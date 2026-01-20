package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {

    User selectById(@Param("id") Integer id);

    User selectByUsername(@Param("username") String username);

    List<User> selectList(@Param("username") String username, @Param("realName") String realName, @Param("roleId") Integer roleId, @Param("status") Integer status);

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Integer id);
}
