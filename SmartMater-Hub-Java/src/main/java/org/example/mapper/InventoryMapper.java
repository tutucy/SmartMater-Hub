package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Inventory;

import java.util.List;

@Mapper
public interface InventoryMapper {

    Inventory selectById(@Param("id") Integer id);

    Inventory selectByMaterialId(@Param("materialId") Integer materialId);

    List<Inventory> selectList(@Param("materialId") Integer materialId, @Param("warehouse") String warehouse);

    List<Inventory> selectWarningList();

    int insert(Inventory inventory);

    int update(Inventory inventory);

    int updateQuantity(@Param("materialId") Integer materialId, @Param("quantity") Integer quantity);

    int deleteById(@Param("id") Integer id);
}
