package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.OutboundRecord;

import java.util.List;

@Mapper
public interface OutboundRecordMapper {

    OutboundRecord selectById(@Param("id") Integer id);

    OutboundRecord selectByOutboundNo(@Param("outboundNo") String outboundNo);

    List<OutboundRecord> selectList(@Param("outboundNo") String outboundNo,
                                     @Param("outboundType") String outboundType,
                                     @Param("recipient") String recipient,
                                     @Param("department") String department,
                                     @Param("startDate") String startDate,
                                     @Param("endDate") String endDate);

    int insert(OutboundRecord record);

    int update(OutboundRecord record);

    int deleteById(@Param("id") Integer id);
}
