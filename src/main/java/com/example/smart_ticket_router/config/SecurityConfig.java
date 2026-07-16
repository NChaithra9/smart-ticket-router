package com.example.smart_ticket_router.config;

import com.example.smart_ticket_router.service.CustomUserDetailsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration.
 *
 * <p>
 * Configures authentication, authorization,
 * password encoding and login/logout.
 * </p>
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Logger.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * User details service.
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor.
     *
     * @param userDetailsService custom user details service
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Password encoder.
     *
     * @return BCrypt encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        logger.info("Creating BCryptPasswordEncoder.");

        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider.
     *
     * @return provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        logger.info("Configuring authentication provider.");

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * Security filter chain.
     *
     * @param http HttpSecurity
     * @return configured filter chain
     * @throws Exception configuration error
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        logger.info("Configuring Security Filter Chain.");

        http
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/register",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

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
                )

                .httpBasic(Customizer.withDefaults());

        logger.info("Security configuration completed.");

        return http.build();
    }

}