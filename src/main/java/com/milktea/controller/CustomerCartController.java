package com.milktea.controller;

import com.milktea.entity.*;
import com.milktea.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.Date;
import java.util.List;

@Controller
public class CustomerCartController {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderDetailRepository customerOrderDetailRepository;

    public CustomerCartController(
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderDetailRepository customerOrderDetailRepository) {

        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderDetailRepository = customerOrderDetailRepository;
    }

    @GetMapping("/customer-cart/add/{customerId}/{productId}")
    public String addToCart(
            @PathVariable Integer customerId,
            @PathVariable Integer productId) {


        Customer customer =
                customerRepository.findById(customerId)
                        .orElse(null);

        Product product =
                productRepository.findById(productId)
                        .orElse(null);

        if (customer == null || product == null) {

            return "redirect:/customer/menu";
        }

        CustomerOrder order;

        List<CustomerOrder> carts =
                customerOrderRepository
                        .findByCustomerCustomerIdAndStatus(
                                customerId,
                                "CART"
                        );

        if (carts.isEmpty()) {

            order = new CustomerOrder();

            order.setCustomer(customer);

            order.setOrderDate(new Date());

            order.setStatus("CART");

            order.setTotalAmount(0.0);

            order = customerOrderRepository.save(order);

        }
        else {

            order = carts.get(0);

        }

        CustomerOrderDetail detail = null;

        List<CustomerOrderDetail> details =
                customerOrderDetailRepository
                        .findByCustomerOrderOrderId(
                                order.getOrderId());

        for(CustomerOrderDetail d : details){

            if(d.getProduct().getProductId()
                    .equals(productId)){

                detail = d;
                break;
            }

        }

        if(detail != null){

            detail.setQuantity(
                    detail.getQuantity()+1);

            detail.setSubtotal(
                    detail.getQuantity()
                            * detail.getPrice());

        }
        else{

            detail = new CustomerOrderDetail();

            detail.setCustomerOrder(order);

            detail.setProduct(product);

            detail.setQuantity(1);

            detail.setPrice(product.getPrice());

            detail.setSubtotal(product.getPrice());

        }

        customerOrderDetailRepository.save(detail);

        double total = 0;

        List<CustomerOrderDetail> allDetails =
                customerOrderDetailRepository
                        .findByCustomerOrderOrderId(
                                order.getOrderId());

        for(CustomerOrderDetail d : allDetails){

            total += d.getSubtotal();

        }

        order.setTotalAmount(total);

        customerOrderRepository.save(order);

        String productName =
                URLEncoder.encode(
                        product.getProductName(),
                        StandardCharsets.UTF_8);

        return "redirect:/customer-menu/"
                + customerId
                + "?success="
                + productName;
    }
    @GetMapping("/customer-cart")
    public String customerCart(Model model) {

        Integer customerId = 1;

        List<CustomerOrder> carts =
                customerOrderRepository
                        .findByCustomerCustomerIdAndStatus(
                                customerId,
                                "CART");

        if (!carts.isEmpty()) {

            CustomerOrder order = carts.get(0);

            List<CustomerOrderDetail> details =
                    customerOrderDetailRepository
                            .findByCustomerOrderOrderId(
                                    order.getOrderId());

            model.addAttribute(
                    "order",
                    order);

            model.addAttribute(
                    "details",
                    details);

        }

        return "customer-cart";
    }
    @GetMapping("/customer-cart/increase/{id}")
    public String increaseQuantity(
            @PathVariable Integer id){

        CustomerOrderDetail detail =
                customerOrderDetailRepository
                        .findById(id)
                        .orElse(null);

        if(detail != null){

            detail.setQuantity(
                    detail.getQuantity()+1);

            detail.setSubtotal(
                    detail.getQuantity()
                            * detail.getPrice());

            customerOrderDetailRepository.save(detail);

        }

        return "redirect:/customer-cart";

    }
    @GetMapping("/customer-cart/decrease/{id}")
    public String decreaseQuantity(
            @PathVariable Integer id){

        CustomerOrderDetail detail =
                customerOrderDetailRepository
                        .findById(id)
                        .orElse(null);

        if(detail != null){

            if(detail.getQuantity() > 1){

                detail.setQuantity(
                        detail.getQuantity()-1);

                detail.setSubtotal(
                        detail.getQuantity()
                                * detail.getPrice());

                customerOrderDetailRepository.save(detail);

            }
            else{

                customerOrderDetailRepository.delete(detail);

            }

        }

        return "redirect:/customer-cart";

    }
    @GetMapping("/customer-order/checkout")
    public String checkout(){

        Integer customerId = 1;

        List<CustomerOrder> carts =
                customerOrderRepository
                        .findByCustomerCustomerIdAndStatus(
                                customerId,
                                "CART");

        if(!carts.isEmpty()){

            CustomerOrder order = carts.get(0);

            order.setStatus("PENDING");

            customerOrderRepository.save(order);

        }

        return "redirect:/customer/orders";
    }
    @GetMapping("/customer/orders")
    public String myOrders(Model model){

        Integer customerId = 1;

        List<CustomerOrder> orders =
                customerOrderRepository
                        .findByCustomerCustomerId(customerId);

        model.addAttribute(
                "orders",
                orders);

        return "my-orders";
    }
    @GetMapping("/my-orders/{id}")
    public String orderDetail(
            @PathVariable Integer id,
            Model model){

        CustomerOrder order =
                customerOrderRepository
                        .findById(id)
                        .orElse(null);

        if(order == null){

            return "redirect:/customer/orders";

        }

        List<CustomerOrderDetail> details =
                customerOrderDetailRepository
                        .findByCustomerOrderOrderId(id);

        model.addAttribute(
                "order",
                order);

        model.addAttribute(
                "details",
                details);

        return "customer-order-detail";
    }
    @GetMapping("/customer-order/delete/{id}")
    public String deleteOrder(
            @PathVariable Integer id) {

        CustomerOrder order =
                customerOrderRepository
                        .findById(id)
                        .orElse(null);

        if(order != null){

            List<CustomerOrderDetail> details =
                    customerOrderDetailRepository
                            .findByCustomerOrderOrderId(id);

            customerOrderDetailRepository.deleteAll(details);

            customerOrderRepository.delete(order);
        }

        return "redirect:/customer/orders";
    }


}