package com.example.smart_ticket_router.initializer;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.smart_ticket_router.entity.Permission;
import com.example.smart_ticket_router.entity.Role;
import com.example.smart_ticket_router.repository.PermissionRepository;
import com.example.smart_ticket_router.repository.RoleRepository;

/**
 * Initializes application roles and permissions.
 *
 * <p>
 * Executes once during application startup.
 * Creates default roles and permissions if they
 * are not already present in the database.
 * </p>
 *
 * <p>
 * <b>Startup ordering:</b> annotated with {@code @Order(1)} so that it
 * always runs first, before
 * {@link com.example.smart_ticket_router.config.AdminInitializer} and
 * {@link UserRoleInitializer}, both of which depend on the roles created
 * here already existing in the database.
 * </p>
 */
@Component
@Order(1)
public class RolePermissionInitializer implements CommandLineRunner {

    /**
     * Logger.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(RolePermissionInitializer.class);

    /**
     * Role repository.
     */
    private final RoleRepository roleRepository;

    /**
     * Permission repository.
     */
    private final PermissionRepository permissionRepository;

    /**
     * Constructor.
     *
     * @param roleRepository role repository
     * @param permissionRepository permission repository
     */
    public RolePermissionInitializer(RoleRepository roleRepository,
                                     PermissionRepository permissionRepository) {

        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Initializes roles and permissions.
     *
     * @param args application arguments
     */
    @Override
    public void run(String... args) {

        logger.info("Initializing roles and permissions...");

        createPermission("CREATE_TICKET");
        createPermission("VIEW_HISTORY");
        createPermission("VIEW_ALL_TICKETS");
        createPermission("DELETE_TICKET");
        createPermission("ASSIGN_TEAM");

        Permission createTicket =
                permissionRepository.findByPermissionName("CREATE_TICKET").get();

        Permission history =
                permissionRepository.findByPermissionName("VIEW_HISTORY").get();

        Permission allTickets =
                permissionRepository.findByPermissionName("VIEW_ALL_TICKETS").get();

        Permission delete =
                permissionRepository.findByPermissionName("DELETE_TICKET").get();

        Permission assign =
                permissionRepository.findByPermissionName("ASSIGN_TEAM").get();

        createRole(
                "ROLE_USER",
                Set.of(createTicket, history)
        );

        createRole(
                "ROLE_ADMIN",
                Set.of(
                        createTicket,
                        history,
                        allTickets,
                        delete,
                        assign
                )
        );

        logger.info("Roles and permissions initialized successfully.");
    }

    /**
     * Creates a permission if it does not exist.
     *
     * @param permissionName permission name
     */
    private void createPermission(String permissionName) {

        if (permissionRepository.findByPermissionName(permissionName).isEmpty()) {

            logger.info("Creating permission: {}", permissionName);

            permissionRepository.save(
                    new Permission(permissionName)
            );
        }
    }

    /**
     * Creates a role if it does not exist.
     *
     * @param roleName role name
     * @param permissions assigned permissions
     */
    private void createRole(String roleName,
                            Set<Permission> permissions) {

        if (roleRepository.findByRoleName(roleName).isEmpty()) {

            logger.info("Creating role: {}", roleName);

            Role role = new Role();

            role.setRoleName(roleName);

            role.setPermissions(permissions);

            roleRepository.save(role);
        }
    }

}