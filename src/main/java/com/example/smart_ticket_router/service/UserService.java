package com.example.smart_ticket_router.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.smart_ticket_router.entity.Role;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.exception.UserAlreadyExistsException;
import com.example.smart_ticket_router.repository.RoleRepository;
import com.example.smart_ticket_router.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for user registration and retrieval.
 *
 * <p>
 * This service handles user-related business logic such as
 * registering new users, validating email uniqueness,
 * encrypting passwords, assigning default roles,
 * and retrieving users by email.
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
     * Repository for role persistence.
     */
    private final RoleRepository roleRepository;

    /**
     * Password encoder used to encrypt user passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a UserService.
     *
     * @param userRepository repository for user operations
     * @param roleRepository repository for role operations
     * @param passwordEncoder password encoder for securing passwords
     */
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user.
     *
     * <p>
     * Validates that the email is unique, encrypts the user's
     * password, assigns the default ROLE_USER role,
     * and persists the user.
     * </p>
     *
     * <p>
     * The whole operation runs inside a single database transaction:
     * if role assignment or the final save fails, the transaction is
     * rolled back and no partial user record is left behind. See
     * {@code TransactionLoggingAspect} for how the rollback is logged.
     * </p>
     *
     * @param name user's name
     * @param email user's email
     * @param password user's password
     * @return saved user
     * @throws UserAlreadyExistsException if the email already exists
     * @throws RuntimeException if ROLE_USER is missing
     */
    @Transactional
    public User registerUser(String name,
                             String email,
                             String password) {

        logger.info("Registering new user with email: {}", email);

        if (userRepository.findByEmail(email).isPresent()) {

            logger.warn("Registration failed. Email already exists: {}", email);

            throw new UserAlreadyExistsException("Email already exists: " + email);
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);

        logger.debug("Encrypting password.");

        user.setPassword(passwordEncoder.encode(password));

        logger.info("Assigning default ROLE_USER.");

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> {

                    logger.error("ROLE_USER not found in database.");

                    return new RuntimeException("Default role not found.");
                });

        user.setRoles(Set.of(userRole));

        logger.debug("ROLE_USER assigned successfully.");

        User savedUser = userRepository.save(user);

        logger.info("User registered successfully with ID: {}",
                savedUser.getId());

        return savedUser;
    }

    /**
     * Retrieves a user by email.
     *
     * @param email user email
     * @return matching user or null
     */
    public User findByEmail(String email) {

        logger.info("Searching user by email: {}", email);

        return userRepository.findByEmail(email)
                .orElse(null);
    }
}