package com.example.smart_ticket_router.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.example.smart_ticket_router.entity.User;
import com.example.smart_ticket_router.repository.UserRepository;

/**
 * Custom implementation of {@link UserDetailsService} used by
 * Spring Security for authenticating users.
 * <p>
 * This service retrieves user details from the database based on
 * the user's email address and converts them into a
 * {@link UserDetails} object required by Spring Security.
 * </p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Logger for CustomUserDetailsService.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    /**
     * Repository used to access user information.
     */
    private final UserRepository userRepository;

    /**
     * Constructs a CustomUserDetailsService.
     *
     * @param userRepository repository for user data
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address.
     *
     * @param email the email address of the user
     * @return the authenticated user's details
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        logger.info("Authenticating user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });

        logger.info("User authenticated successfully: {}", email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}