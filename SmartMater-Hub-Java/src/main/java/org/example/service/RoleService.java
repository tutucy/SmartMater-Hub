package org.example.service;

import org.example.entity.Role;

import java.util.List;

public interface RoleService {

    Role getById(Integer id);

    List<Role> getList(String name, Integer status);

    int add(Role role);

    int update(Role role);

    int delete(Integer id);
}
