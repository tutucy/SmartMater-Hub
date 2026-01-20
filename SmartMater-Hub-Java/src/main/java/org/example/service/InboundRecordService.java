package org.example.service;

import org.example.entity.InboundRecord;

import java.util.List;

public interface InboundRecordService {

    InboundRecord getById(Integer id);

    InboundRecord getByInboundNo(String inboundNo);

    List<InboundRecord> getList(String inboundNo, String inboundType, Integer supplierId, String startDate, String endDate);

    int add(InboundRecord record);

    int update(InboundRecord record);

    int delete(Integer id);
}
