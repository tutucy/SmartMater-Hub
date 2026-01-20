package org.example.service.impl;

import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<User> getList(String username, String realName, Integer roleId, Integer status) {
        return userMapper.selectList(username, realName, roleId, status);
    }

    @Override
    public int add(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int update(User user) {
        return userMapper.update(user);
    }

    @Override
    public int resetPassword(Integer id, String password, String oldPassword) {
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (oldPassword != null && !oldPassword.isEmpty() && !existingUser.getPassword().equals(oldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        
        User user = new User();
        user.setId(id);
        user.setPassword(password);
        return userMapper.update(user);
    }

    @Override
    public int delete(Integer id) {
        return userMapper.deleteById(id);
    }
}
