package com.example.smart_ticket_router.service;

import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.enums.Role;
import com.example.smart_ticket_router.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String name,
                             String email,
                             String password) {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(password));

        // Every newly registered user is a USER
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElse(null);
    }
}