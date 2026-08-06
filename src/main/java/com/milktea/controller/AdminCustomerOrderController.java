package com.milktea.controller;

import com.milktea.entity.*;
import com.milktea.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
    private final CustomerRepository customerRepository;
    private final CustomerNotificationRepository notificationRepository;
    private final CustomerVoucherRepository customerVoucherRepository;

    public AdminCustomerOrderController(
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderDetailRepository customerOrderDetailRepository,
            OrdersRepository ordersRepository,
            OrderDetailRepository orderDetailRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            ProductIngredientRepository productIngredientRepository,
            IngredientRepository ingredientRepository,
            InventoryTransactionRepository inventoryTransactionRepository,
            CustomerRepository customerRepository,
            CustomerNotificationRepository notificationRepository,
            CustomerVoucherRepository customerVoucherRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderDetailRepository = customerOrderDetailRepository;
        this.ordersRepository = ordersRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.customerRepository = customerRepository;
        this.notificationRepository = notificationRepository;
        this.customerVoucherRepository = customerVoucherRepository;
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
    @Transactional
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
            orders.setVoucher(customerOrder.getVoucher());
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
                            int neededQty = (int) Math.round(recipe.getQuantityUsed() * cd.getQuantity());
                            int currentQty = ingredient.getQuantity() != null ? ingredient.getQuantity() : 0;
                            int updatedQty = currentQty - neededQty;
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

            // 6. Award Customer Loyalty Points (1 point per 10,000 VNĐ)
            if (customerOrder.getCustomer() != null && customerOrder.getTotalAmount() != null) {
                Customer customer = customerOrder.getCustomer();
                int earnedPoints = (int) (customerOrder.getTotalAmount() / 10000);
                if (earnedPoints > 0) {
                    int currentPoints = customer.getPoint() != null ? customer.getPoint() : 0;
                    customer.setPoint(currentPoints + earnedPoints);
                    customerRepository.save(customer);
                }
            }
            createNotification(customerOrder, "Đơn hàng đã được duyệt", "Đơn #" + customerOrder.getOrderId() + " đã được duyệt và chuyển sang trạng thái hoàn tất.");
        }
        return "redirect:/admin/customer-orders";
    }

    @GetMapping("/cancel/{id}")
    @Transactional
    public String cancelOrder(@PathVariable Integer id) {
        CustomerOrder customerOrder = customerOrderRepository.findById(id).orElse(null);
        if (customerOrder != null && "PENDING".equals(customerOrder.getStatus())) {
            customerOrder.setStatus("CANCELLED");
            customerOrderRepository.save(customerOrder);
            if (customerOrder.getVoucher() != null && customerOrder.getCustomer() != null) {
                customerVoucherRepository.findByCustomerCustomerIdAndVoucherVoucherId(
                                customerOrder.getCustomer().getCustomerId(), customerOrder.getVoucher().getVoucherId())
                        .ifPresent(customerVoucher -> {
                            customerVoucher.setStatus("AVAILABLE");
                            customerVoucherRepository.save(customerVoucher);
                        });
            }
            createNotification(customerOrder, "Đơn hàng đã bị hủy", "Đơn #" + customerOrder.getOrderId() + " đã được quán hủy. Vui lòng liên hệ quán nếu cần hỗ trợ.");
        }
        return "redirect:/admin/customer-orders";
    }

    private void createNotification(CustomerOrder order, String title, String message) {
        if (order.getCustomer() == null) return;
        CustomerNotification notification = new CustomerNotification();
        notification.setCustomer(order.getCustomer());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setOrderId(order.getOrderId());
        notification.setCreatedAt(new Date());
        notification.setRead(false);
        notificationRepository.save(notification);
    }
}
