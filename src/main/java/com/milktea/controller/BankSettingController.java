package com.milktea.controller;

import com.milktea.entity.BankSetting;
import com.milktea.service.BankSettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BankSettingController {

    private final BankSettingService bankSettingService;

    public BankSettingController(BankSettingService bankSettingService) {
        this.bankSettingService = bankSettingService;
    }

    @GetMapping("/admin/bank-settings")
    public String showBankSettingsForm(Model model) {
        BankSetting setting = bankSettingService.getBankSetting();
        model.addAttribute("bankSetting", setting);
        model.addAttribute("qrPreviewUrl", bankSettingService.generateVietQrUrl(50000.0, "THANH TOAN DEMO"));
        return "bank-setting-form";
    }

    @PostMapping("/admin/bank-settings")
    public String updateBankSettings(@ModelAttribute("bankSetting") BankSetting bankSetting, RedirectAttributes redirectAttributes) {
        bankSettingService.saveBankSetting(bankSetting);
        redirectAttributes.addFlashAttribute("successMessage", "Cấu hình tài khoản ngân hàng & VietQR đã được lưu thành công!");
        return "redirect:/admin/bank-settings";
    }
}
