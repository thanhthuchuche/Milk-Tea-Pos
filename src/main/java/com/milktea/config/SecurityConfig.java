package com.milktea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/login",
                                "/menu/**",
                                "/order-at-table/**",
                                "/decrease-order/**",
                                "/table-order/**",
                                "/edit-note",
                                "/save-note"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/products",
                                "/products/search"
                        ).hasAnyRole("ADMIN", "STAFF")

                        .requestMatchers(
                                "/products/add",
                                "/products/save",
                                "/products/edit/**",
                                "/products/delete/**"
                        ).hasAnyRole("ADMIN", "STAFF")

                        .requestMatchers(
                                "/vouchers/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/customer/menu",
                                "/customer-menu/**",
                                "/customer-cart/**",
                                "/customer-order/**",
                                "/customer/orders",
                                "/my-orders/**"
                        ).hasRole("CUSTOMER")

                        .requestMatchers(
                                "/users/**",
                                "/roles/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                "/",
                                "/categories/**"
                        ).hasAnyRole("ADMIN", "STAFF")

                        .requestMatchers(
                                "/ingredients/**",
                                "/customers/**",
                                "/orders/**",
                                "/invoices/**",
                                "/invoice/**",
                                "/orderdetails/**",
                                "/payments/**",
                                "/tables/**",
                                "/transactions/**",
                                "/product-ingredients/**",
                                "/table-payment/**"
                        ).hasAnyRole("ADMIN", "STAFF")

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form

                        .loginPage("/login")

                        .usernameParameter("username")

                        .passwordParameter("password")

                        .successHandler((request, response, authentication) -> {
                            var authorities = authentication.getAuthorities();
                            boolean isCustomer = authorities.stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
                            if (isCustomer) {
                                response.sendRedirect("/customer/menu");
                            } else {
                                response.sendRedirect("/");
                            }
                        })

                        .failureUrl("/login?error")

                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login")

                        .permitAll()
                );

        return http.build();
    }
}