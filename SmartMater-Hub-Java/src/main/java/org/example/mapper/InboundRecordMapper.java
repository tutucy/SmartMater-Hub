package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.InboundRecord;

import java.util.List;

@Mapper
public interface InboundRecordMapper {

    InboundRecord selectById(@Param("id") Integer id);

    InboundRecord selectByInboundNo(@Param("inboundNo") String inboundNo);

    List<InboundRecord> selectList(@Param("inboundNo") String inboundNo, 
                                   @Param("inboundType") String inboundType,
                                   @Param("supplierId") Integer supplierId,
                                   @Param("startDate") String startDate,
                                   @Param("endDate") String endDate);

    int insert(InboundRecord record);

    int update(InboundRecord record);

    int deleteById(@Param("id") Integer id);
}
