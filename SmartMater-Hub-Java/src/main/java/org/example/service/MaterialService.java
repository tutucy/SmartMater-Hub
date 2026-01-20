package org.example.service;

import org.example.entity.Material;

import java.util.List;

public interface MaterialService {

    Material getById(Integer id);

    Material getByCode(String code);

    List<Material> getList(String code, String name, Integer categoryId, Integer status);

    int add(Material material);

    int update(Material material);

    int delete(Integer id);
}
