package org.example.service.impl;

import org.example.entity.OutboundRecord;
import org.example.mapper.OutboundRecordMapper;
import org.example.service.OutboundRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboundRecordServiceImpl implements OutboundRecordService {

    @Autowired
    private OutboundRecordMapper outboundRecordMapper;

    @Override
    public OutboundRecord getById(Integer id) {
        return outboundRecordMapper.selectById(id);
    }

    @Override
    public OutboundRecord getByOutboundNo(String outboundNo) {
        return outboundRecordMapper.selectByOutboundNo(outboundNo);
    }

    @Override
    public List<OutboundRecord> getList(String outboundNo, String outboundType, String recipient, String department, String startDate, String endDate) {
        return outboundRecordMapper.selectList(outboundNo, outboundType, recipient, department, startDate, endDate);
    }

    @Override
    public int add(OutboundRecord record) {
        return outboundRecordMapper.insert(record);
    }

    @Override
    public int update(OutboundRecord record) {
        return outboundRecordMapper.update(record);
    }

    @Override
    public int delete(Integer id) {
        return outboundRecordMapper.deleteById(id);
    }
}
