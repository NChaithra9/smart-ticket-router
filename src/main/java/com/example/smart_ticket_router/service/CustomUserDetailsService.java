package com.example.smart_ticket_router.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.example.smart_ticket_router.entity.Permission;
import com.example.smart_ticket_router.entity.Role;
import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.repository.UserRepository;

/**
 * Custom implementation of {@link UserDetailsService}
 * used by Spring Security.
 *
 * <p>
 * Loads users from the database and converts their
 * roles and permissions into Spring Security authorities.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Logger.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    /**
     * User repository.
     */
    private final UserRepository userRepository;

    /**
     * Constructor.
     *
     * @param userRepository repository
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by email.
     *
     * @param email user email
     * @return authenticated user
     * @throws UsernameNotFoundException if user is absent
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        logger.info("Authenticating user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {

                    logger.warn("User not found: {}", email);

                    return new UsernameNotFoundException("User not found");
                });

        logger.info("Building authorities for user.");

        List<GrantedAuthority> authorities =
                user.getRoles()
                        .stream()
                        .flatMap(role -> {

                            List<GrantedAuthority> permissionAuthorities =
                                    role.getPermissions()
                                            .stream()
                                            .map(Permission::getPermissionName)
                                            .map(SimpleGrantedAuthority::new)
                                            .collect(Collectors.toList());

                            permissionAuthorities.add(
                                    new SimpleGrantedAuthority(role.getRoleName())
                            );

                            return permissionAuthorities.stream();
                        })
                        .collect(Collectors.toList());

        logger.info("Authentication successful.");

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}