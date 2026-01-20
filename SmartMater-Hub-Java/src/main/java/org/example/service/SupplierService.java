package org.example.service;

import org.example.entity.Supplier;

import java.util.List;

public interface SupplierService {

    Supplier getById(Integer id);

    List<Supplier> getList(String name, String contact, Integer status);

    int add(Supplier supplier);

    int update(Supplier supplier);

    int delete(Integer id);
}
