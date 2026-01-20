package org.example.service.impl;

import org.example.entity.Role;
import org.example.mapper.RoleMapper;
import org.example.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Role getById(Integer id) {
        return roleMapper.selectById(id);
    }

    @Override
    public List<Role> getList(String name, Integer status) {
        return roleMapper.selectList(name, status);
    }

    @Override
    public int add(Role role) {
        return roleMapper.insert(role);
    }

    @Override
    public int update(Role role) {
        return roleMapper.update(role);
    }

    @Override
    public int delete(Integer id) {
        return roleMapper.deleteById(id);
    }
}
