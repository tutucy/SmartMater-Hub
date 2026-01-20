package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.OutboundDetail;

import java.util.List;

@Mapper
public interface OutboundDetailMapper {

    OutboundDetail selectById(@Param("id") Integer id);

    List<OutboundDetail> selectByOutboundId(@Param("outboundId") Integer outboundId);

    int insert(OutboundDetail detail);

    int deleteByOutboundId(@Param("outboundId") Integer outboundId);
}
