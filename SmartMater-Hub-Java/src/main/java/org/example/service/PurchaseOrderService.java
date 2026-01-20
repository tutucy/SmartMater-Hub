package org.example.service;

import org.example.entity.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderService {

    PurchaseOrder getById(Integer id);

    PurchaseOrder getByOrderNo(String orderNo);

    List<PurchaseOrder> getList(String orderNo, Integer supplierId, Integer status);

    int add(PurchaseOrder order);

    int update(PurchaseOrder order);

    int delete(Integer id);
}
