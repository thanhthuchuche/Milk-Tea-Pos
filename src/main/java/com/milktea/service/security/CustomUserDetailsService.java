package com.milktea.service.security;

import com.milktea.entity.User;
import com.milktea.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("User not found"));

        System.out.println("USER = " + user.getUsername());

        System.out.println("ROLE = " + user.getRole().getRoleName());

        return new org.springframework.security.core.userdetails.User(

                user.getUsername(),

                user.getPassword(),

                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().getRoleName()
                        )
                )
        );
    }
}