package com.milktea.service;

import com.milktea.entity.Role;
import java.util.List;

public interface RoleService {

    List<Role> getAllRoles();

    Role getRoleById(Integer id);

    Role saveRole(Role role);

    void deleteRole(Integer id);
}