package com.project.ecommerce.config;

import com.project.ecommerce.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Disable CSRF for REST API
                .csrf(csrf -> csrf.disable())

                // JWT authentication is stateless
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // AUTH
                        // =========================
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()

                        // =========================
                        // PRODUCTS
                        // =========================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products",
                                "/api/products/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products",
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        // =========================
                        // CATEGORIES
                        // =========================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories",
                                "/api/categories/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories",
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        // =========================
                        // USERS
                        // =========================
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/**"
                        ).hasRole("ADMIN")

                        // =========================
                        // CART
                        // =========================
                        .requestMatchers(
                                "/api/cart/**"
                        ).authenticated()

                        // =========================
                        // ADDRESSES
                        // =========================
                        .requestMatchers(
                                "/api/addresses/**"
                        ).authenticated()

                        // =========================
                        // INVENTORY
                        // =========================
                        .requestMatchers(
                                "/api/inventory/**"
                        ).hasRole("ADMIN")

                        // =========================
                        // ORDERS
                        // =========================
                        .requestMatchers(
                                "/api/orders/status/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/orders/*/status"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/orders/admin/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/orders/**"
                        ).authenticated()

                        // =========================
                        // PAYMENTS
                        // =========================
                        .requestMatchers(
                                "/api/payments/order/*/paid",
                                "/api/payments/order/*/failed"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/api/payments/**"
                        ).authenticated()

                        // =========================
                        // SWAGGER
                        // =========================
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // =========================
                        // EVERYTHING ELSE
                        // =========================
                        .anyRequest().authenticated()
                )

                // JWT filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}