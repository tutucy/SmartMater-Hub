package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.PurchaseOrderItem;

import java.util.List;

@Mapper
public interface PurchaseOrderItemMapper {

    PurchaseOrderItem selectById(@Param("id") Integer id);

    List<PurchaseOrderItem> selectList(@Param("orderId") Integer orderId);

    List<PurchaseOrderItem> selectByOrderId(@Param("orderId") Integer orderId);

    int insert(PurchaseOrderItem item);

    int update(PurchaseOrderItem item);

    int deleteById(@Param("id") Integer id);

    int deleteByOrderId(@Param("orderId") Integer orderId);
}
