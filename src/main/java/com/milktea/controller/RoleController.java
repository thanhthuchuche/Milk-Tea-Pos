package com.milktea.controller;

import com.milktea.entity.Role;
import com.milktea.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    public String getAllRoles(Model model) {

        model.addAttribute(
                "roles",
                roleService.getAllRoles()
        );

        return "role-list";
    }

    @GetMapping("/roles/add")
    public String addRole(Model model) {

        model.addAttribute(
                "role",
                new Role()
        );

        return "role-form";
    }

    @PostMapping("/roles/save")
    public String saveRole(Role role) {

        roleService.saveRole(role);

        return "redirect:/roles";
    }

    @GetMapping("/roles/delete/{id}")
    public String deleteRole(@PathVariable Integer id) {

        try {
            roleService.deleteRole(id);
        } catch (Exception e) {
            System.out.println("Role đang được sử dụng, không thể xóa");
        }

        return "redirect:/roles";
    }
}