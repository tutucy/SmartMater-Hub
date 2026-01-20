package org.example.service.impl;

import org.example.entity.PurchaseOrder;
import org.example.mapper.PurchaseOrderMapper;
import org.example.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public PurchaseOrder getById(Integer id) {
        return purchaseOrderMapper.selectById(id);
    }

    @Override
    public PurchaseOrder getByOrderNo(String orderNo) {
        return purchaseOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<PurchaseOrder> getList(String orderNo, Integer supplierId, Integer status) {
        return purchaseOrderMapper.selectList(orderNo, supplierId, status);
    }

    @Override
    public int add(PurchaseOrder order) {
        return purchaseOrderMapper.insert(order);
    }

    @Override
    public int update(PurchaseOrder order) {
        return purchaseOrderMapper.update(order);
    }

    @Override
    public int delete(Integer id) {
        return purchaseOrderMapper.deleteById(id);
    }
}
