package com.milktea.controller;

import com.milktea.entity.User;
import com.milktea.service.RoleService;
import com.milktea.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(
            UserService userService,
            RoleService roleService) {

        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {

        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "user-list";
    }

    @GetMapping("/users/add")
    public String addUser(Model model) {

        model.addAttribute(
                "user",
                new User()
        );

        model.addAttribute(
                "roles",
                roleService.getAllRoles()
        );

        return "user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user) {

        userService.saveUser(user);

        return "redirect:/users";
    }
    @GetMapping("/users/edit/{id}")
    public String editUser(
            @PathVariable Integer id,
            Model model) {

        User user =
                userService.getUserById(id);

        user.setPassword("");

        model.addAttribute(
                "user",
                user
        );

        model.addAttribute(
                "roles",
                roleService.getAllRoles()
        );

        return "user-form";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(
            @PathVariable Integer id) {

        userService.deleteUser(id);

        return "redirect:/users";
    }
}