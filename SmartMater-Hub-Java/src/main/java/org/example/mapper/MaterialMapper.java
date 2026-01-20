package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Material;

import java.util.List;

@Mapper
public interface MaterialMapper {

    Material selectById(@Param("id") Integer id);

    Material selectByCode(@Param("code") String code);

    List<Material> selectList(@Param("code") String code, @Param("name") String name, @Param("categoryId") Integer categoryId, @Param("status") Integer status);

    int insert(Material material);

    int update(Material material);

    int deleteById(@Param("id") Integer id);
}
