package com.milktea.controller;

import com.milktea.entity.*;
import com.milktea.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
public class TablePaymentController {

    private final OrdersRepository ordersRepository;
    private final TableCafeRepository tableCafeRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductIngredientRepository productIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;

    public TablePaymentController(
            OrdersRepository ordersRepository,
            TableCafeRepository tableCafeRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            OrderDetailRepository orderDetailRepository,
            ProductIngredientRepository productIngredientRepository,
            IngredientRepository ingredientRepository,
            InventoryTransactionRepository inventoryTransactionRepository) {

        this.ordersRepository = ordersRepository;
        this.tableCafeRepository = tableCafeRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productIngredientRepository = productIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
    }

    @GetMapping("/table-payment/{orderId}")
    public String payment(@PathVariable Integer orderId) {

        Orders order = ordersRepository.findById(orderId).orElse(null);

        if (order == null) {
            return "redirect:/tables";
        }

        // 1. Mark standard order status as PAID
        order.setStatus("PAID");
        ordersRepository.save(order);

        // 2. Free up table cafe status
        tableCafeRepository.updatePaid(order.getTableCafe().getTableId());

        // 3. Automatically generate corresponding Invoice record
        Invoice invoice = new Invoice();
        invoice.setInvoiceDate(new Date());
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setOrders(order);
        invoice = invoiceRepository.save(invoice);

        // 4. Automatically generate corresponding Payment transaction record
        Payment payment = new Payment();
        payment.setPaymentMethod("CASH");
        payment.setPaymentStatus("COMPLETED");
        payment.setPaymentDate(new Date());
        payment.setInvoice(invoice);
        paymentRepository.save(payment);

        // 5. Recipe-based Inventory Stock Subtraction
        List<OrderDetail> details = orderDetailRepository.findByOrdersOrderId(orderId);
        for (OrderDetail detail : details) {
            if (detail.getProduct() != null) {
                List<ProductIngredient> recipes = productIngredientRepository.findByProductProductId(detail.getProduct().getProductId());
                for (ProductIngredient recipe : recipes) {
                    Ingredient ingredient = recipe.getIngredient();
                    if (ingredient != null) {
                        int neededQty = (int) Math.round(recipe.getQuantityUsed() * detail.getQuantity());
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
                        transaction.setSupplier("Bán hàng tại bàn - Đơn #" + orderId);
                        transaction.setNote("Tự động xuất kho dựa trên công thức pha chế món: " + detail.getProduct().getProductName());
                        inventoryTransactionRepository.save(transaction);
                    }
                }
            }
        }

        // 6. Award Customer Loyalty Points (1 point per 10,000 VNĐ)
        if (order.getCustomer() != null && order.getTotalAmount() != null) {
            Customer customer = order.getCustomer();
            int earnedPoints = (int) (order.getTotalAmount() / 10000);
            if (earnedPoints > 0) {
                int currentPoints = customer.getPoint() != null ? customer.getPoint() : 0;
                customer.setPoint(currentPoints + earnedPoints);
            }
        }

        return "redirect:/tables?success";
    }
}