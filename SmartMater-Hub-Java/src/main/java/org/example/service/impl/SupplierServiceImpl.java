package org.example.service.impl;

import org.example.entity.Supplier;
import org.example.mapper.SupplierMapper;
import org.example.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public Supplier getById(Integer id) {
        return supplierMapper.selectById(id);
    }

    @Override
    public List<Supplier> getList(String name, String contact, Integer status) {
        return supplierMapper.selectList(name, contact, status);
    }

    @Override
    public int add(Supplier supplier) {
        return supplierMapper.insert(supplier);
    }

    @Override
    public int update(Supplier supplier) {
        return supplierMapper.update(supplier);
    }

    @Override
    public int delete(Integer id) {
        return supplierMapper.deleteById(id);
    }
}
