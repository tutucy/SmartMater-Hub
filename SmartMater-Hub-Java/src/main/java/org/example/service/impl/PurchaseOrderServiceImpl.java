package org.example.service.impl;

import org.example.dto.PurchaseOrderDTO;
import org.example.dto.PurchaseOrderItemDTO;
import org.example.entity.PurchaseOrder;
import org.example.entity.PurchaseOrderItem;
import org.example.mapper.PurchaseOrderItemMapper;
import org.example.mapper.PurchaseOrderMapper;
import org.example.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private PurchaseOrderItemMapper purchaseOrderItemMapper;

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
    @Transactional
    public int addWithItems(PurchaseOrderDTO orderDTO) {
        int orderId = purchaseOrderMapper.insertDTO(orderDTO);
        List<PurchaseOrderItemDTO> items = orderDTO.getDetails() != null ? orderDTO.getDetails() : orderDTO.getItems();
        if (items != null && !items.isEmpty()) {
            for (PurchaseOrderItemDTO itemDTO : items) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setOrderId(orderId);
                item.setMaterialId(itemDTO.getMaterialId());
                item.setQuantity(itemDTO.getQuantity());
                item.setPrice(itemDTO.getPrice());
                item.setAmount(itemDTO.getAmount());
                purchaseOrderItemMapper.insert(item);
            }
        }
        return orderId;
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
