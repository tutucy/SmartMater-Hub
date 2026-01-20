package org.example.service.impl;

import org.example.entity.Category;
import org.example.mapper.CategoryMapper;
import org.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Category getById(Integer id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<Category> getList(Integer parentId, String name, Integer status) {
        return categoryMapper.selectList(parentId, name, status);
    }

    @Override
    public List<Category> getTree() {
        return categoryMapper.selectTree();
    }

    @Override
    public int add(Category category) {
        return categoryMapper.insert(category);
    }

    @Override
    public int update(Category category) {
        return categoryMapper.update(category);
    }

    @Override
    public int delete(Integer id) {
        return categoryMapper.deleteById(id);
    }
}
