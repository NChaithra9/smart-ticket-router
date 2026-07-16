package com.example.smart_ticket_router.config;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.smart_ticket_router.entity.Role;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.initializer.RolePermissionInitializer;
import com.example.smart_ticket_router.repository.RoleRepository;
import com.example.smart_ticket_router.repository.UserRepository;

/**
 * Initializes the default administrator account during application startup.
 *
 * <p>
 * This component checks whether a default administrator account already
 * exists in the database. If it does not exist, a new administrator
 * account is created with a securely encrypted password and is assigned
 * the {@code ROLE_ADMIN} role from the role/permission system.
 * </p>
 *
 * <p>
 * <b>Startup ordering:</b> this runner is annotated with
 * {@code @Order(2)} so that it always executes after
 * {@link RolePermissionInitializer} (ordered first), which is
 * responsible for creating the {@code ROLE_ADMIN} role in the database.
 * Without this ordering, the role lookup performed here could fail on a
 * fresh database.
 * </p>
 */
@Component
@Order(2)
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
     * Repository used to look up the ROLE_ADMIN role.
     */
    private final RoleRepository roleRepository;

    /**
     * Password encoder used to securely encrypt the administrator password.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs an AdminInitializer.
     *
     * @param userRepository repository for user persistence
     * @param roleRepository repository for role persistence
     * @param passwordEncoder password encoder for encrypting passwords
     */
    public AdminInitializer(UserRepository userRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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

            // Assign the ROLE_ADMIN role, which must already have been
            // created by RolePermissionInitializer at this point.
            Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                    .orElseThrow(() -> {

                        logger.error("ROLE_ADMIN not found. Ensure RolePermissionInitializer "
                                + "runs before AdminInitializer.");

                        return new IllegalStateException(
                                "ROLE_ADMIN role not found. Cannot create default administrator.");
                    });

            admin.setRoles(Set.of(adminRole));

            userRepository.save(admin);

            logger.info("Default administrator account created successfully with ROLE_ADMIN.");

        } else {

            logger.info("Default administrator account already exists.");

        }
    }
}