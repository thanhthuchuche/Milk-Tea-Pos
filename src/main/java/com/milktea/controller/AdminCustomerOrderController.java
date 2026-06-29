package com.milktea.controller;

import com.milktea.entity.*;
import com.milktea.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/customer-orders")
public class AdminCustomerOrderController {

    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderDetailRepository customerOrderDetailRepository;
    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    public AdminCustomerOrderController(
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderDetailRepository customerOrderDetailRepository,
            OrdersRepository ordersRepository,
            OrderDetailRepository orderDetailRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            ProductIngredientRepository productIngredientRepository,
            IngredientRepository ingredientRepository,
            InventoryTransactionRepository inventoryTransactionRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderDetailRepository = customerOrderDetailRepository;
        this.ordersRepository = ordersRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }

    @GetMapping
    public String listSubmittedOrders(Model model) {
        // Find all customer orders that are submitted (not in CART status)
        List<CustomerOrder> submittedOrders = customerOrderRepository.findByStatusNot("CART");
        model.addAttribute("orders", submittedOrders);
        return "admin-customer-order-list";
    }

    @GetMapping("/detail/{id}")
    public String viewOrderDetail(@PathVariable Integer id, Model model) {
        CustomerOrder order = customerOrderRepository.findById(id).orElse(null);
        if (order == null) {
            return "redirect:/admin/customer-orders";
        }
        List<CustomerOrderDetail> details = customerOrderDetailRepository.findByCustomerOrderOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("details", details);
        return "admin-customer-order-detail";
    }

    @GetMapping("/approve/{id}")
    public String approveOrder(@PathVariable Integer id) {
        CustomerOrder customerOrder = customerOrderRepository.findById(id).orElse(null);
        if (customerOrder != null && "PENDING".equals(customerOrder.getStatus())) {
            // 1. Mark CustomerOrder as COMPLETED
            customerOrder.setStatus("COMPLETED");
            customerOrderRepository.save(customerOrder);

            // 2. Create and Save standard Orders record
            Orders orders = new Orders();
            orders.setCustomer(customerOrder.getCustomer());
            orders.setOrderDate(new Date());
            orders.setStatus("PAID");
            orders.setTotalAmount(customerOrder.getTotalAmount());
            orders = ordersRepository.save(orders);

            // 3. Copy CustomerOrderDetail list to standard OrderDetail list and perform stock subtraction
            List<CustomerOrderDetail> customerDetails = customerOrderDetailRepository.findByCustomerOrderOrderId(id);
            for (CustomerOrderDetail cd : customerDetails) {
                OrderDetail od = new OrderDetail();
                od.setOrders(orders);
                od.setProduct(cd.getProduct());
                od.setQuantity(cd.getQuantity());
                od.setPrice(cd.getPrice());
                od.setSubtotal(cd.getSubtotal());
                od.setNote("Đơn đặt hàng Online");
                orderDetailRepository.save(od);

                // Perform stock subtraction based on recipe
                if (cd.getProduct() != null) {
                    List<ProductIngredient> recipes = productIngredientRepository.findByProductProductId(cd.getProduct().getProductId());
                    for (ProductIngredient recipe : recipes) {
                        Ingredient ingredient = recipe.getIngredient();
                        if (ingredient != null) {
                            double neededQty = recipe.getQuantityUsed() * cd.getQuantity();
                            double updatedQty = ingredient.getQuantity() - neededQty;
                            ingredient.setQuantity(updatedQty);
                            ingredientRepository.save(ingredient);

                            // Log EXPORT inventory transaction
                            InventoryTransaction transaction = new InventoryTransaction();
                            transaction.setTransactionDate(new Date());
                            transaction.setIngredient(ingredient);
                            transaction.setQuantity(neededQty);
                            transaction.setTransactionType("EXPORT");
                            transaction.setSupplier("Đơn hàng Online - Đơn #" + customerOrder.getOrderId());
                            transaction.setNote("Tự động xuất kho dựa trên công thức pha chế món: " + cd.getProduct().getProductName());
                            inventoryTransactionRepository.save(transaction);
                        }
                    }
                }
            }

            // 4. Generate and Save Invoice automatically
            Invoice invoice = new Invoice();
            invoice.setInvoiceDate(new Date());
            invoice.setTotalAmount(customerOrder.getTotalAmount());
            invoice.setOrders(orders);
            invoice = invoiceRepository.save(invoice);

            // 5. Generate and Save Payment transaction automatically
            Payment payment = new Payment();
            payment.setPaymentMethod("E-WALLET");
            payment.setPaymentStatus("COMPLETED");
            payment.setPaymentDate(new Date());
            payment.setInvoice(invoice);
            paymentRepository.save(payment);
        }
        return "redirect:/admin/customer-orders";
    }

    @GetMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Integer id) {
        CustomerOrder customerOrder = customerOrderRepository.findById(id).orElse(null);
        if (customerOrder != null && "PENDING".equals(customerOrder.getStatus())) {
            customerOrder.setStatus("CANCELLED");
            customerOrderRepository.save(customerOrder);
        }
        return "redirect:/admin/customer-orders";
    }
}
