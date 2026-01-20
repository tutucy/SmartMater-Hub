package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Category;

import java.util.List;

@Mapper
public interface CategoryMapper {

    Category selectById(@Param("id") Integer id);

    List<Category> selectList(@Param("parentId") Integer parentId, @Param("name") String name, @Param("status") Integer status);

    List<Category> selectTree();

    int insert(Category category);

    int update(Category category);

    int deleteById(@Param("id") Integer id);
}
