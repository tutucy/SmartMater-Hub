package org.example.service.impl;

import org.example.entity.PurchaseOrderItem;
import org.example.mapper.PurchaseOrderItemMapper;
import org.example.service.PurchaseOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseOrderItemServiceImpl implements PurchaseOrderItemService {

    @Autowired
    private PurchaseOrderItemMapper purchaseOrderItemMapper;

    @Override
    public List<PurchaseOrderItem> getList(Integer orderId) {
        return purchaseOrderItemMapper.selectList(orderId);
    }

    @Override
    public PurchaseOrderItem getById(Integer id) {
        return purchaseOrderItemMapper.selectById(id);
    }

    @Override
    public int add(PurchaseOrderItem item) {
        return purchaseOrderItemMapper.insert(item);
    }

    @Override
    public int update(PurchaseOrderItem item) {
        return purchaseOrderItemMapper.update(item);
    }

    @Override
    public int delete(Integer id) {
        return purchaseOrderItemMapper.deleteById(id);
    }
}
