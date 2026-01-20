package org.example.service;

import org.example.entity.User;

import java.util.List;

public interface UserService {

    User getById(Integer id);

    User getByUsername(String username);

    List<User> getList(String username, String realName, Integer roleId, Integer status);

    int add(User user);

    int update(User user);

    int delete(Integer id);
}
