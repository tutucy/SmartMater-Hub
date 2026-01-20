package org.example.service;

import org.example.entity.PurchaseOrderItem;

import java.util.List;

public interface PurchaseOrderItemService {

    List<PurchaseOrderItem> getList(Integer orderId);

    PurchaseOrderItem getById(Integer id);

    int add(PurchaseOrderItem item);

    int update(PurchaseOrderItem item);

    int delete(Integer id);
}
