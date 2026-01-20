package org.example.service.impl;

import org.example.entity.Material;
import org.example.mapper.MaterialMapper;
import org.example.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Override
    public Material getById(Integer id) {
        return materialMapper.selectById(id);
    }

    @Override
    public Material getByCode(String code) {
        return materialMapper.selectByCode(code);
    }

    @Override
    public List<Material> getList(String code, String name, Integer categoryId, Integer status) {
        return materialMapper.selectList(code, name, categoryId, status);
    }

    @Override
    public int add(Material material) {
        return materialMapper.insert(material);
    }

    @Override
    public int update(Material material) {
        return materialMapper.update(material);
    }

    @Override
    public int delete(Integer id) {
        return materialMapper.deleteById(id);
    }
}
