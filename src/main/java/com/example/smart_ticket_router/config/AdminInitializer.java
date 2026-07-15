package com.example.smart_ticket_router.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.enums.Role;
import com.example.smart_ticket_router.repository.UserRepository;

/**
 * Initializes the default administrator account during application startup.
 *
 * <p>
 * This component checks whether a default administrator account already
 * exists in the database. If it does not exist, a new administrator
 * account is created with a securely encrypted password.
 * </p>
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    /**
     * Logger for AdminInitializer.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(AdminInitializer.class);

    /**
     * Repository used to perform user-related database operations.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder used to securely encrypt the administrator password.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs an AdminInitializer.
     *
     * @param userRepository repository for user persistence
     * @param passwordEncoder password encoder for encrypting passwords
     */
    public AdminInitializer(UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates the default administrator account if it does not already exist.
     *
     * @param args command-line arguments
     */
    @Override
    public void run(String... args) {

        logger.info("Checking for default administrator account.");

        String adminEmail = "admin@example.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {

            logger.info("Default administrator account not found. Creating administrator.");

            User admin = new User();

            admin.setName("Administrator");
            admin.setEmail(adminEmail);

            // Encrypt the administrator password before saving.
            admin.setPassword(passwordEncoder.encode("admin123"));

            // Assign administrator role.
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            logger.info("Default administrator account created successfully.");

        } else {

            logger.info("Default administrator account already exists.");

        }
    }
}