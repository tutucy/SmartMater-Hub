package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.PurchaseOrder;

import java.util.List;

@Mapper
public interface PurchaseOrderMapper {

    PurchaseOrder selectById(@Param("id") Integer id);

    PurchaseOrder selectByOrderNo(@Param("orderNo") String orderNo);

    List<PurchaseOrder> selectList(@Param("orderNo") String orderNo, @Param("supplierId") Integer supplierId, @Param("status") Integer status);

    int insert(PurchaseOrder order);

    int update(PurchaseOrder order);

    int deleteById(@Param("id") Integer id);
}
