package com.milktea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**",
                                "/login"
                        ).permitAll()

                        .requestMatchers(
                                "/users/**",
                                "/categories/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers("/ingredients/**")
                        .hasAnyRole("ADMIN","STAFF")

                        .requestMatchers(
                                "/vouchers/**"
                        )
                        .hasAnyRole("ADMIN","CUSTOMER")

                        .requestMatchers(
                                "/products/**"
                        )
                        .hasAnyRole("ADMIN","STAFF","CUSTOMER")

                        .requestMatchers(
                                "/customers/**",
                                "/orders/**",
                                "/invoices/**",
                                "/payments/**",
                                "/tables/**"
                        )
                                .hasAnyRole("ADMIN","STAFF")

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form

                        .loginPage("/login")

                        .usernameParameter("username")

                        .passwordParameter("password")

                        .defaultSuccessUrl("/", true)

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