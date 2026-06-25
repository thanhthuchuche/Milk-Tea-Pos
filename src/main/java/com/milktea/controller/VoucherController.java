package com.milktea.controller;

import com.milktea.entity.Voucher;
import com.milktea.service.VoucherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(
            VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping("/vouchers")
    public String getAllVouchers(Model model) {

        model.addAttribute(
                "vouchers",
                voucherService.getAllVouchers()
        );

        return "voucher-list";
    }

    @GetMapping("/vouchers/add")
    public String addVoucher(Model model) {

        model.addAttribute(
                "voucher",
                new Voucher()
        );

        return "voucher-form";
    }

    @PostMapping("/vouchers/save")
    public String saveVoucher(Voucher voucher) {

        voucherService.saveVoucher(voucher);

        return "redirect:/vouchers";
    }

    @GetMapping("/vouchers/edit/{id}")
    public String editVoucher(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "voucher",
                voucherService.getVoucherById(id)
        );

        return "voucher-form";
    }

    @GetMapping("/vouchers/delete/{id}")
    public String deleteVoucher(
            @PathVariable Integer id) {

        voucherService.deleteVoucher(id);

        return "redirect:/vouchers";
    }
}