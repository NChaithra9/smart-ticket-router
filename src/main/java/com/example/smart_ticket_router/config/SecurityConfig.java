package com.example.smart_ticket_router.config;

import com.example.smart_ticket_router.service.CustomUserDetailsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for the Smart Ticket Router application.
 *
 * <p>This configuration defines:
 * <ul>
 *     <li>Password encoding using BCrypt.</li>
 *     <li>Authentication provider backed by the application's user database.</li>
 *     <li>Authorization rules for application endpoints.</li>
 *     <li>Form-based login and logout support.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    private static final Logger logger =
            LoggerFactory.getLogger(SecurityConfig.class);

    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructs a SecurityConfig.
     *
     * @param userDetailsService service responsible for loading user details
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Creates the password encoder used for encoding and
     * verifying user passwords.
     *
     * @return BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        logger.info("Creating BCryptPasswordEncoder bean.");

        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication provider that authenticates
     * users using the application's {@link CustomUserDetailsService}.
     *
     * @return configured authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        logger.info("Configuring DaoAuthenticationProvider.");

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        logger.debug("Password encoder configured for authentication provider.");

        return provider;
    }

    /**
     * Configures the application's security filter chain.
     *
     * <p>Security rules:
     * <ul>
     *     <li>Allows anonymous access to registration and login pages.</li>
     *     <li>Restricts <code>/admin/**</code> endpoints to ADMIN users.</li>
     *     <li>Requires authentication for all other requests.</li>
     *     <li>Enables default form login and logout.</li>
     * </ul>
     *
     * @param http HttpSecurity configuration object
     * @return configured security filter chain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        logger.info("Configuring Spring Security filter chain.");

        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login")
                        .permitAll()

                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )
                .formLogin(form -> form
        .loginPage("/login")
        .defaultSuccessUrl("/", true)
        .permitAll()
)
.logout(logout -> logout
        .logoutSuccessUrl("/login?logout")
        .permitAll()
);

        logger.info("Spring Security configuration completed.");

        return http.build();
    }
}