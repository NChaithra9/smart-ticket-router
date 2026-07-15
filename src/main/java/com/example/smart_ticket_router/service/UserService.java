package com.example.smart_ticket_router.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.enums.Role;
import com.example.smart_ticket_router.repository.UserRepository;

/**
 * Service responsible for user registration and retrieval.
 * <p>
 * This service handles user-related business logic such as
 * registering new users, validating email uniqueness,
 * encrypting passwords, and retrieving users by email.
 * </p>
 */
@Service
public class UserService {

    /**
     * Logger for UserService.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);

    /**
     * Repository for user persistence.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder used to encrypt user passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a UserService.
     *
     * @param userRepository repository for user operations
     * @param passwordEncoder password encoder for securing passwords
     */
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     * <p>
     * Validates that the email is unique, encrypts the user's
     * password, assigns the default {@link Role#USER} role,
     * and persists the user.
     * </p>
     *
     * @param name the user's name
     * @param email the user's email address
     * @param password the user's plain-text password
     * @return the saved user
     * @throws RuntimeException if the email already exists
     */
    public User registerUser(String name,
                             String email,
                             String password) {

        logger.info("Registering new user with email: {}", email);

        if (userRepository.findByEmail(email).isPresent()) {

            logger.warn("Registration failed. Email already exists: {}", email);

            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);

        logger.debug("Encrypting password for user.");

        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(password));

        // Every newly registered user is assigned the USER role
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return savedUser;
    }

    /**
     * Retrieves a user by email address.
     *
     * @param email the user's email
     * @return the matching user, or {@code null} if not found
     */
    public User findByEmail(String email) {

        logger.info("Searching for user with email: {}", email);

        return userRepository.findByEmail(email)
                .orElse(null);
    }
}