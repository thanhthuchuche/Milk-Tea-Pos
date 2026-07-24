package com.milktea.controller;

import com.milktea.entity.Payment;
import com.milktea.service.InvoiceService;
import com.milktea.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final InvoiceService invoiceService;
    private final com.milktea.service.BankSettingService bankSettingService;

    public PaymentController(
            PaymentService paymentService,
            InvoiceService invoiceService,
            com.milktea.service.BankSettingService bankSettingService) {

        this.paymentService = paymentService;
        this.invoiceService = invoiceService;
        this.bankSettingService = bankSettingService;
    }

    @GetMapping("/payments")
    public String getAllPayments(Model model) {

        model.addAttribute("payments", paymentService.getAllPayments());
        model.addAttribute("bankSetting", bankSettingService.getBankSetting());

        return "payment-list";
    }

    @GetMapping("/payments/add")
    public String addPayment(Model model) {

        model.addAttribute(
                "payment",
                new Payment()
        );

        model.addAttribute(
                "invoices",
                invoiceService.getAllInvoices()
        );

        return "payment-form";
    }

    @GetMapping("/payments/edit/{id}")
    public String editPayment(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "payment",
                paymentService.getPaymentById(id)
        );

        model.addAttribute(
                "invoices",
                invoiceService.getAllInvoices()
        );

        return "payment-form";
    }

    @PostMapping("/payments/save")
    public String savePayment(Payment payment) {

        paymentService.savePayment(payment);

        return "redirect:/payments";
    }

    @GetMapping("/payments/delete/{id}")
    public String deletePayment(
            @PathVariable Integer id) {

        paymentService.deletePayment(id);

        return "redirect:/payments";
    }
}