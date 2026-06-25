package com.milktea.service;

import com.milktea.entity.User;
import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User getUserById(Integer id);

    void saveUser(User user);

    void deleteUser(Integer id);
}