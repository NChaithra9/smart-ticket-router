package com.example.smart_ticket_router.config;

import com.example.smart_ticket_router.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {

    DaoAuthenticationProvider provider =
            new DaoAuthenticationProvider(userDetailsService);

    provider.setPasswordEncoder(passwordEncoder());

    return provider;
}
   @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {

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
        .formLogin(Customizer.withDefaults())
        .logout(Customizer.withDefaults());

    return http.build();
}

    
}