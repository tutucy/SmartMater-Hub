package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.PurchaseOrderItem;

import java.util.List;

@Mapper
public interface PurchaseOrderItemMapper {

    PurchaseOrderItem selectById(@Param("id") Integer id);

    List<PurchaseOrderItem> selectByOrderId(@Param("orderId") Integer orderId);

    int insert(PurchaseOrderItem item);

    int deleteByOrderId(@Param("orderId") Integer orderId);
}
