package com.milktea.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        model.addAttribute("storeName", "Cô Đào Quán");
        model.addAttribute("storeAddress", "369/25 Lý Thái Tổ, Phường 9, Quận 10, TP.HCM");
        model.addAttribute("storePhone", "0912 345 678 - 0987 654 321");
        model.addAttribute("storeEmail", "contact@codaoquan.com");
        model.addAttribute("openingHours", "07:00 - 22:30 Hằng Ngày (Thứ 2 - Chủ Nhật)");
        return "contact";
    }

    @PostMapping("/contact/send")
    public String handleContactSubmit(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("message") String message,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("successMessage", 
                "Cảm ơn " + fullName + "! Ý kiến đóng góp của bạn đã được gửi tới ban quản lý Cô Đào Quán.");
        return "redirect:/contact";
    }
}
