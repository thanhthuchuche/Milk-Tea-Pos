package com.milktea.controller;

import com.milktea.repository.CustomerRepository;
import com.milktea.repository.InvoiceRepository;
import com.milktea.repository.OrdersRepository;
import com.milktea.repository.ProductRepository;
import com.milktea.repository.UserRepository;
import java.util.List;
import com.milktea.dto.ProductSalesDTO;
import com.milktea.repository.IngredientRepository;
import com.milktea.service.InvoiceService;
import com.milktea.repository.OrderDetailRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrdersRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final IngredientRepository ingredientRepository;

    public DashboardController(
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            OrdersRepository orderRepository,
            InvoiceRepository invoiceRepository,
            UserRepository userRepository,
            InvoiceService invoiceService,
            OrderDetailRepository orderDetailRepository,
            IngredientRepository ingredientRepository) {

        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.invoiceService = invoiceService;
        this.orderDetailRepository = orderDetailRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        model.addAttribute(
                "totalProducts",
                productRepository.count()
        );

        model.addAttribute(
                "totalCustomers",
                customerRepository.count()
        );

        model.addAttribute(
                "totalOrders",
                orderRepository.count()
        );

        model.addAttribute(
                "totalInvoices",
                invoiceRepository.count()
        );

        model.addAttribute(
                "totalUsers",
                userRepository.count()
        );
        model.addAttribute(
                "totalRevenue",
                invoiceService.getTotalRevenue()
        );

        model.addAttribute(
                "revenueData",
                invoiceRepository.getRevenueData()
        );

        model.addAttribute(
                "revenueLabels",
                invoiceRepository.getRevenueLabels()
        );
        model.addAttribute(
                "topProducts",
                orderDetailRepository.getTopSellingProducts()
        );
        List<ProductSalesDTO> topProducts =
                orderDetailRepository.getTopSellingProducts();

        model.addAttribute(
                "topProducts",
                topProducts
        );

        model.addAttribute(
                "topProductNames",
                topProducts.stream()
                        .map(ProductSalesDTO::getProductName)
                        .toList()
        );

        model.addAttribute(
                "topProductSold",
                topProducts.stream()
                        .map(ProductSalesDTO::getTotalSold)
                        .toList()
        );
        model.addAttribute(
                "ingredients",
                ingredientRepository.findAll()
        );

        model.addAttribute(
                "totalIngredients",
                ingredientRepository.count()
        );

        model.addAttribute(
                "ingredientNames",
                ingredientRepository.findAll()
                        .stream()
                        .map(i -> i.getIngredientName())
                        .toList()
        );

        model.addAttribute(
                "ingredientQuantities",
                ingredientRepository.findAll()
                        .stream()
                        .map(i -> i.getQuantity())
                        .toList()
        );
        model.addAttribute(
                "pendingOrders",
                orderRepository.countByStatus("PENDING"));

        model.addAttribute(
                "paidOrders",
                orderRepository.countByStatus("PAID"));

        model.addAttribute(
                "revenueMonths",
                invoiceRepository.getRevenueMonths()
        );

        model.addAttribute(
                "revenueByMonth",
                invoiceRepository.getRevenueByMonth()
        );

        return "dashboard";
    }
}