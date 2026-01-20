package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.Supplier;

import java.util.List;

@Mapper
public interface SupplierMapper {

    Supplier selectById(@Param("id") Integer id);

    List<Supplier> selectList(@Param("name") String name, @Param("contact") String contact, @Param("status") Integer status);

    int insert(Supplier supplier);

    int update(Supplier supplier);

    int deleteById(@Param("id") Integer id);
}
