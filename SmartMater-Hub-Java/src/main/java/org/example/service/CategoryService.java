package org.example.service;

import org.example.entity.Category;

import java.util.List;

public interface CategoryService {

    Category getById(Integer id);

    List<Category> getList(Integer parentId, String name, Integer status);

    List<Category> getTree();

    int add(Category category);

    int update(Category category);

    int delete(Integer id);
}
