package com.example.smart_ticket_router.controller;

import com.example.smart_ticket_router.model.RegisterRequest;
import com.example.smart_ticket_router.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller responsible for handling user authentication-related requests.
 *
 * <p>This controller provides endpoints for:
 * <ul>
 *     <li>Displaying the user registration page.</li>
 *     <li>Registering new users.</li>
 * </ul>
 */
@Controller
public class AuthController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    /**
     * Constructs an AuthController.
     *
     * @param userService service responsible for user registration
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the user registration page.
     *
     * @param request registration request object used for form binding
     * @return the registration view
     */
    @GetMapping("/register")
    public String registerPage(RegisterRequest request) {

        logger.info("Registration page requested.");

        return "register";
    }

    /**
     * Registers a new user and redirects to the login page.
     *
     * @param request registration details submitted by the user
     * @return redirect to the login page after successful registration
     */
    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request) {

        logger.info("Registering new user with email: {}", request.getEmail());

        userService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        logger.info("User registered successfully: {}", request.getEmail());

        return "redirect:/login";
    }
    @GetMapping("/login")
public String loginPage() {

    logger.info("Login page requested.");

    return "login";
}
}