package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Role;

import java.util.List;

@Mapper
public interface RoleMapper {

    Role selectById(@Param("id") Integer id);

    List<Role> selectList(@Param("name") String name, @Param("status") Integer status);

    int insert(Role role);

    int update(Role role);

    int deleteById(@Param("id") Integer id);
}
