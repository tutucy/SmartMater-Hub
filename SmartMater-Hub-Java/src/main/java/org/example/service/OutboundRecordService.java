package org.example.service;

import org.example.entity.OutboundRecord;

import java.util.List;

public interface OutboundRecordService {

    OutboundRecord getById(Integer id);

    OutboundRecord getByOutboundNo(String outboundNo);

    List<OutboundRecord> getList(String outboundNo, String outboundType, String recipient, String department, String startDate, String endDate);

    int add(OutboundRecord record);

    int update(OutboundRecord record);

    int delete(Integer id);
}
