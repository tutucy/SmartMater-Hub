package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.SystemLog;

import java.util.List;

@Mapper
public interface SystemLogMapper {

    SystemLog selectById(@Param("id") Integer id);

    List<SystemLog> selectList(@Param("username") String username,
                                @Param("operation") String operation,
                                @Param("startDate") String startDate,
                                @Param("endDate") String endDate);

    int insert(SystemLog log);

    int deleteById(@Param("id") Integer id);
}
