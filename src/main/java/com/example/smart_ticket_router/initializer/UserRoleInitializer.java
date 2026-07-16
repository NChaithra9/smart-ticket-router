package com.example.smart_ticket_router.initializer;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.smart_ticket_router.entity.Role;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.repository.RoleRepository;
import com.example.smart_ticket_router.repository.UserRepository;

/**
 * Backfills the default {@code ROLE_USER} role onto any user record
 * that does not yet have a role assigned.
 *
 * <p>
 * The default administrator account is provisioned directly by
 * {@link com.example.smart_ticket_router.config.AdminInitializer} with
 * {@code ROLE_ADMIN} already assigned, so by the time this initializer
 * runs, any remaining user without a role is a regular end user.
 * </p>
 *
 * <p>
 * <b>Startup ordering:</b> annotated with {@code @Order(3)} so it runs
 * last, after
 * {@link com.example.smart_ticket_router.initializer.RolePermissionInitializer}
 * (creates the roles) and
 * {@link com.example.smart_ticket_router.config.AdminInitializer}
 * (creates and assigns the administrator).
 * </p>
 */
@Component
@Order(3)
public class UserRoleInitializer implements CommandLineRunner {

    /**
     * Logger.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(UserRoleInitializer.class);

    /**
     * User repository.
     */
    private final UserRepository userRepository;

    /**
     * Role repository.
     */
    private final RoleRepository roleRepository;

    /**
     * Constructor.
     *
     * @param userRepository user repository
     * @param roleRepository role repository
     */
    public UserRoleInitializer(UserRepository userRepository,
                               RoleRepository roleRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Assigns default roles to users.
     *
     * @param args application arguments
     */
    @Override
    public void run(String... args) {

        logger.info("Assigning roles to existing users...");

        Role userRole = roleRepository
                .findByRoleName("ROLE_USER")
                .orElseThrow(() -> {

                    logger.error("ROLE_USER not found. Ensure RolePermissionInitializer "
                            + "runs before UserRoleInitializer.");

                    return new IllegalStateException("ROLE_USER role not found.");
                });

        int assignedCount = 0;

        for (User user : userRepository.findAll()) {

            if (!user.getRoles().isEmpty()) {

                // Already has a role (e.g. the administrator account,
                // assigned by AdminInitializer). Nothing to do.
                continue;
            }

            logger.info("Assigning ROLE_USER to {}", user.getEmail());

            user.setRoles(Set.of(userRole));

            userRepository.save(user);

            assignedCount++;
        }

        logger.info("User role initialization completed. {} user(s) updated.", assignedCount);
    }
}