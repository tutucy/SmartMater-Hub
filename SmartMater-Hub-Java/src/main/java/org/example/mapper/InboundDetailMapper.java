package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.InboundDetail;

import java.util.List;

@Mapper
public interface InboundDetailMapper {

    InboundDetail selectById(@Param("id") Integer id);

    List<InboundDetail> selectByInboundId(@Param("inboundId") Integer inboundId);

    int insert(InboundDetail detail);

    int deleteByInboundId(@Param("inboundId") Integer inboundId);
}
