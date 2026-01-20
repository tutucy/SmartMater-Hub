package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.SystemConfig;

import java.util.List;

@Mapper
public interface SystemConfigMapper {

    SystemConfig selectById(@Param("id") Integer id);

    SystemConfig selectByKey(@Param("configKey") String configKey);

    List<SystemConfig> selectList();

    int insert(SystemConfig config);

    int update(SystemConfig config);

    int deleteById(@Param("id") Integer id);
}
