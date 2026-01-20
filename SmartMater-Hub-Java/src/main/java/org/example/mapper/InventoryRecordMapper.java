package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.InventoryRecord;

import java.util.List;

@Mapper
public interface InventoryRecordMapper {

    InventoryRecord selectById(@Param("id") Integer id);

    List<InventoryRecord> selectList(@Param("recordNo") String recordNo, @Param("materialId") Integer materialId, @Param("recordType") Integer recordType);

    int insert(InventoryRecord record);

    int deleteById(@Param("id") Integer id);
}
