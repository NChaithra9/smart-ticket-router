package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.model.RegisterRequest;
import com.example.smart_ticket_router.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(RegisterRequest request) {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request) {

        userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        return "redirect:/login";
    }
}