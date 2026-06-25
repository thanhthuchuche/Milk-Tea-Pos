package com.milktea.controller;
import com.milktea.entity.OrderDetail;
import com.milktea.entity.Orders;
import com.milktea.entity.Product;
import com.milktea.entity.TableCafe;
import com.milktea.repository.OrderDetailRepository;
import com.milktea.repository.OrdersRepository;
import com.milktea.repository.ProductRepository;
import com.milktea.repository.TableCafeRepository;
import com.milktea.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;
import java.util.Date;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class OrderQRController {

    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final TableCafeRepository tableCafeRepository;
    private final CustomerRepository customerRepository;

    public OrderQRController(
            OrdersRepository ordersRepository,
            OrderDetailRepository orderDetailRepository,
            ProductRepository productRepository,
            TableCafeRepository tableCafeRepository,
            CustomerRepository customerRepository) {


        this.ordersRepository = ordersRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.tableCafeRepository = tableCafeRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/order-at-table/{tableId}/{productId}")
    public String orderAtTable(
            @PathVariable Integer tableId,
            @PathVariable Integer productId,
            RedirectAttributes ra) {

        TableCafe table =
                tableCafeRepository.findById(tableId).orElse(null);

        Product product =
                productRepository.findById(productId).orElse(null);

        if (table == null || product == null) {
            return "redirect:/menu/" + tableId;
        }

        Orders order;

        List<Orders> pendingOrders =
                ordersRepository.findPendingOrderByTable(tableId);

        if (pendingOrders.isEmpty()) {

            order = new Orders();

            order.setOrderDate(new Date());

            order.setStatus("PENDING");

            order.setTableCafe(table);

            order.setTotalAmount(0.0);

            order = ordersRepository.save(order);

            tableCafeRepository.updateUnpaid(tableId);

        }
        else {

            // lấy đơn mới nhất
            order = pendingOrders.get(0);

        }

        Optional<OrderDetail> optionalDetail =
                orderDetailRepository.findProductInOrder(
                        order.getOrderId(),
                        productId
                );

        // Nếu món đã có trong đơn
        if (optionalDetail.isPresent()) {

            OrderDetail detail = optionalDetail.get();

            detail.setQuantity(
                    detail.getQuantity() + 1
            );

            detail.setSubtotal(
                    detail.getQuantity() * detail.getPrice()
            );

            orderDetailRepository.save(detail);

        }

        // Nếu món chưa có trong đơn
        else {

            OrderDetail detail = new OrderDetail();

            detail.setOrders(order);

            detail.setProduct(product);

            detail.setQuantity(1);

            detail.setPrice(product.getPrice());

            detail.setSubtotal(product.getPrice());

            orderDetailRepository.save(detail);

        }

        // cập nhật tổng tiền đơn hàng
        double total = 0;

        List<OrderDetail> details =
                orderDetailRepository.findAllByOrderId(order.getOrderId());

        for (OrderDetail d : details) {

            total += d.getSubtotal();

        }

        order.setTotalAmount(total);

        ordersRepository.save(order);

        ra.addFlashAttribute(
                "successMessage",
                "✓ Đã thêm " + product.getProductName()
        );
        return "redirect:/menu/" + tableId;
    }

    @GetMapping("/decrease-order/{tableId}/{productId}")
    public String decreaseOrder(
            @PathVariable Integer tableId,
            @PathVariable Integer productId) {

        List<Orders> pendingOrders =
                ordersRepository.findPendingOrderByTable(tableId);

        if (pendingOrders.isEmpty()) {

            return "redirect:/menu/" + tableId;

        }

        Orders order = pendingOrders.get(0);

        Optional<OrderDetail> optionalDetail =
                orderDetailRepository.findProductInOrder(
                        order.getOrderId(),
                        productId);

        if (optionalDetail.isPresent()) {

            OrderDetail detail = optionalDetail.get();

            if (detail.getQuantity() > 1) {

                detail.setQuantity(
                        detail.getQuantity() - 1);

                detail.setSubtotal(
                        detail.getQuantity()
                                * detail.getPrice());

                orderDetailRepository.save(detail);

            }
            else {

                orderDetailRepository.delete(detail);

            }

        }

        // cập nhật lại tổng tiền
        double total = 0;

        List<OrderDetail> details =
                orderDetailRepository.findAllByOrderId(
                        order.getOrderId());

        for (OrderDetail d : details) {

            total += d.getSubtotal();

        }

        order.setTotalAmount(total);

        ordersRepository.save(order);

        return "redirect:/menu/" + tableId;

    }
    @PostMapping("/update-note/{id}")
    public String updateNote(
            @PathVariable Integer id,
            @RequestParam String note) {

        OrderDetail detail =
                orderDetailRepository.findById(id)
                        .orElse(null);

        if (detail != null) {

            detail.setNote(note);

            orderDetailRepository.save(detail);

            Integer tableId =
                    detail.getOrders()
                            .getTableCafe()
                            .getTableId();

            return "redirect:/table-order/" + tableId;
        }

        return "redirect:/tables";
    }
}