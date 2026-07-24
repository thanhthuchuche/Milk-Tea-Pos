package com.milktea.controller;

import com.milktea.entity.Invoice;
import com.milktea.entity.OrderDetail;
import com.milktea.service.BankSettingService;
import com.milktea.service.InvoiceService;
import com.milktea.service.OrderDetailService;
import com.milktea.service.OrdersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final OrdersService ordersService;
    private final OrderDetailService orderDetailService;
    private final BankSettingService bankSettingService;

    public InvoiceController(
            InvoiceService invoiceService,
            OrdersService ordersService,
            OrderDetailService orderDetailService,
            BankSettingService bankSettingService) {

        this.invoiceService = invoiceService;
        this.ordersService = ordersService;
        this.orderDetailService = orderDetailService;
        this.bankSettingService = bankSettingService;
    }

    @GetMapping("/invoices")
    public String getAllInvoices(Model model) {
        model.addAttribute("invoices", invoiceService.getAllInvoices());
        return "invoice-list";
    }

    @GetMapping("/invoices/add")
    public String addInvoice(Model model) {
        model.addAttribute("invoice", new Invoice());
        model.addAttribute("orders", ordersService.getAllOrders());
        return "invoice-form";
    }

    @GetMapping("/invoices/edit/{id}")
    public String editInvoice(@PathVariable Integer id, Model model) {
        model.addAttribute("invoice", invoiceService.getInvoiceById(id));
        model.addAttribute("orders", ordersService.getAllOrders());
        return "invoice-form";
    }

    @PostMapping("/invoices/save")
    public String saveInvoice(Invoice invoice) {
        invoiceService.saveInvoice(invoice);
        return "redirect:/invoices";
    }

    @GetMapping("/invoices/delete/{id}")
    public String deleteInvoice(@PathVariable Integer id) {
        invoiceService.deleteInvoice(id);
        return "redirect:/invoices";
    }

    @GetMapping("/invoice/{id}")
    public String viewInvoice(@PathVariable Integer id, Model model) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        if (invoice != null) {
            model.addAttribute("invoice", invoice);
            if (invoice.getOrders() != null) {
                List<OrderDetail> details = orderDetailService.getByOrderId(invoice.getOrders().getOrderId());
                model.addAttribute("orderDetails", details);
            }
            model.addAttribute("bankSetting", bankSettingService.getBankSetting());
        }
        return "invoice-detail";
    }
}