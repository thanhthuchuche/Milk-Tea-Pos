package com.milktea.service.impl;

import com.milktea.entity.User;
import com.milktea.repository.UserRepository;
import com.milktea.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    @Override
    public void saveUser(User user) {

        if (user.getUserId() == null) {

            user.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );
        }
        else {

            User oldUser =
                    userRepository.findById(
                            user.getUserId()
                    ).orElse(null);

            if (oldUser != null) {

                if (user.getPassword() == null
                        || user.getPassword().trim().isEmpty()) {

                    user.setPassword(
                            oldUser.getPassword()
                    );
                }
                else {

                    user.setPassword(
                            passwordEncoder.encode(
                                    user.getPassword()
                            )
                    );
                }
            }
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}