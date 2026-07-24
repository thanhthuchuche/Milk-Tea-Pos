package com.milktea.controller;

import com.milktea.entity.*;
import com.milktea.repository.*;
import com.milktea.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
public class OrdersController {

    private final OrdersService ordersService;
    private final CustomerService customerService;
    private final TableCafeService tableCafeService;
    private final VoucherService voucherService;
    private final UserService userService;
    private final OrderDetailService orderDetailService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final BankSettingService bankSettingService;
    private final ProductIngredientRepository productIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final CustomerRepository customerRepository;
    private final TableCafeRepository tableCafeRepository;

    public OrdersController(
            OrdersService ordersService,
            CustomerService customerService,
            TableCafeService tableCafeService,
            VoucherService voucherService,
            UserService userService,
            OrderDetailService orderDetailService,
            ProductService productService,
            CategoryService categoryService,
            InvoiceService invoiceService,
            PaymentService paymentService,
            BankSettingService bankSettingService,
            ProductIngredientRepository productIngredientRepository,
            IngredientRepository ingredientRepository,
            InventoryTransactionRepository inventoryTransactionRepository,
            CustomerRepository customerRepository,
            TableCafeRepository tableCafeRepository) {

        this.ordersService = ordersService;
        this.customerService = customerService;
        this.tableCafeService = tableCafeService;
        this.voucherService = voucherService;
        this.userService = userService;
        this.orderDetailService = orderDetailService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
        this.bankSettingService = bankSettingService;
        this.productIngredientRepository = productIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.customerRepository = customerRepository;
        this.tableCafeRepository = tableCafeRepository;
    }

    @GetMapping("/orders")
    public String getAllOrders(Model model) {
        model.addAttribute("orders", ordersService.getAllOrders());
        return "order-list";
    }

    @GetMapping("/orders/add")
    public String addOrder(Model model) {
        model.addAttribute("order", new Orders());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("tables", tableCafeService.getAllTables());
        model.addAttribute("vouchers", voucherService.getAllVouchers());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("bankSetting", bankSettingService.getBankSetting());

        return "order-form";
    }

    @PostMapping("/orders/pos-checkout")
    public String posCheckout(
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) Integer tableId,
            @RequestParam(required = false) Integer voucherId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "CASH") String paymentMethod,
            @RequestParam(name = "productIds", required = false) List<Integer> productIds,
            @RequestParam(name = "quantities", required = false) List<Integer> quantities,
            @RequestParam(name = "notes", required = false) List<String> notes,
            RedirectAttributes redirectAttributes
    ) {
        if (productIds == null || productIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn ít nhất 1 sản phẩm để tạo đơn hàng tại quầy!");
            return "redirect:/orders/add";
        }

        // 1. Create Order Entity
        Orders order = new Orders();
        order.setOrderDate(new Date());
        order.setStatus("COMPLETED");

        Customer customer = null;
        if (customerId != null) {
            customer = customerService.getCustomerById(customerId);
            order.setCustomer(customer);
        }
        TableCafe table = null;
        if (tableId != null) {
            table = tableCafeService.getTableById(tableId);
            order.setTableCafe(table);
            // Free up table or mark as occupied
            table.setStatus("OCCUPIED");
            tableCafeRepository.save(table);
        }
        if (userId != null) {
            order.setUser(userService.getUserById(userId));
        }

        ordersService.saveOrder(order);

        // 2. Add Items, Calculate Subtotal & Subtract Recipe Inventory Stock
        double totalAmount = 0.0;
        for (int i = 0; i < productIds.size(); i++) {
            Integer pId = productIds.get(i);
            Integer qty = (quantities != null && i < quantities.size()) ? quantities.get(i) : 1;
            String note = (notes != null && i < notes.size()) ? notes.get(i) : "";

            Product product = productService.getProductById(pId);
            if (product != null) {
                double itemPrice = product.getPrice() != null ? product.getPrice() : 0.0;
                double subtotal = itemPrice * qty;
                totalAmount += subtotal;

                OrderDetail detail = new OrderDetail();
                detail.setOrders(order);
                detail.setProduct(product);
                detail.setQuantity(qty);
                detail.setPrice(itemPrice);
                detail.setSubtotal(subtotal);
                detail.setNote(note);

                orderDetailService.saveOrderDetail(detail);

                // Automatic Recipe-based Ingredient Stock Subtraction
                List<ProductIngredient> recipes = productIngredientRepository.findByProductProductId(pId);
                for (ProductIngredient recipe : recipes) {
                    Ingredient ingredient = recipe.getIngredient();
                    if (ingredient != null) {
                        int neededQty = (int) Math.round(recipe.getQuantityUsed() * qty);
                        int currentQty = ingredient.getQuantity() != null ? ingredient.getQuantity() : 0;
                        int updatedQty = Math.max(0, currentQty - neededQty);
                        ingredient.setQuantity(updatedQty);
                        ingredientRepository.save(ingredient);

                        // Record EXPORT inventory transaction log
                        InventoryTransaction transaction = new InventoryTransaction();
                        transaction.setTransactionDate(new Date());
                        transaction.setIngredient(ingredient);
                        transaction.setQuantity(neededQty);
                        transaction.setTransactionType("EXPORT");
                        transaction.setSupplier("Bán hàng tại quầy POS - Đơn #" + order.getOrderId());
                        transaction.setNote("Tự động xuất kho theo định lượng món: " + product.getProductName());
                        inventoryTransactionRepository.save(transaction);
                    }
                }
            }
        }

        // 3. Apply Voucher Discount
        if (voucherId != null) {
            Voucher v = voucherService.getVoucherById(voucherId);
            if (v != null) {
                order.setVoucher(v);
                if (v.getDiscountPercent() != null && v.getDiscountPercent() > 0) {
                    double discount = v.getDiscountPercent() / 100.0;
                    totalAmount = totalAmount * (1.0 - discount);
                }
            }
        }

        order.setTotalAmount(totalAmount);
        ordersService.saveOrder(order);

        // 4. Award Customer Loyalty Points (1 point for every 10,000 VNĐ)
        if (customer != null) {
            int earnedPoints = (int) (totalAmount / 10000);
            if (earnedPoints > 0) {
                int currentPoints = customer.getPoint() != null ? customer.getPoint() : 0;
                customer.setPoint(currentPoints + earnedPoints);
                customerRepository.save(customer);
            }
        }

        // 5. Create Invoice Entity
        Invoice invoice = new Invoice();
        invoice.setOrders(order);
        invoice.setInvoiceDate(new Date());
        invoice.setTotalAmount(totalAmount);
        invoiceService.saveInvoice(invoice);

        // 6. Create Payment Entity
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaymentDate(new Date());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("COMPLETED");
        paymentService.savePayment(payment);

        // Redirect directly to Thermal Invoice Print view!
        return "redirect:/invoice/" + invoice.getInvoiceId();
    }

    @PostMapping("/orders/save")
    public String saveOrder(
            Orders order,
            @RequestParam(required = false) Integer customer,
            @RequestParam(required = false) Integer tableCafe,
            @RequestParam(required = false) Integer voucher,
            @RequestParam(required = false) Integer user
    ) {
        if (customer != null) {
            order.setCustomer(customerService.getCustomerById(customer));
        }
        if (tableCafe != null) {
            order.setTableCafe(tableCafeService.getTableById(tableCafe));
        }
        if (voucher != null) {
            Voucher v = voucherService.getVoucherById(voucher);
            order.setVoucher(v);
            if (v.getDiscountPercent() != null && order.getTotalAmount() != null) {
                double discount = v.getDiscountPercent() / 100.0;
                order.setTotalAmount(order.getTotalAmount() * (1.0 - discount));
            }
        }
        if (user != null) {
            order.setUser(userService.getUserById(user));
        }

        ordersService.saveOrder(order);
        return "redirect:/orders";
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        ordersService.deleteOrder(id);
        return "redirect:/orders";
    }

    @GetMapping("/orders/detail/{id}")
    public String orderDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("orderDetails", orderDetailService.getByOrderId(id));
        model.addAttribute("orderId", id);
        return "order-detail-list";
    }

    @GetMapping("/orders/search")
    public String searchOrder(@RequestParam String keyword, Model model) {
        model.addAttribute("orders", ordersService.searchOrders(keyword));
        return "order-list";
    }
}