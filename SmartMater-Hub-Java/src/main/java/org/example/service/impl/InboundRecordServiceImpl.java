package org.example.service.impl;

import org.example.entity.InboundRecord;
import org.example.mapper.InboundRecordMapper;
import org.example.service.InboundRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InboundRecordServiceImpl implements InboundRecordService {

    @Autowired
    private InboundRecordMapper inboundRecordMapper;

    @Override
    public InboundRecord getById(Integer id) {
        return inboundRecordMapper.selectById(id);
    }

    @Override
    public InboundRecord getByInboundNo(String inboundNo) {
        return inboundRecordMapper.selectByInboundNo(inboundNo);
    }

    @Override
    public List<InboundRecord> getList(String inboundNo, String inboundType, Integer supplierId, String startDate, String endDate) {
        return inboundRecordMapper.selectList(inboundNo, inboundType, supplierId, startDate, endDate);
    }

    @Override
    public int add(InboundRecord record) {
        return inboundRecordMapper.insert(record);
    }

    @Override
    public int update(InboundRecord record) {
        return inboundRecordMapper.update(record);
    }

    @Override
    public int delete(Integer id) {
        return inboundRecordMapper.deleteById(id);
    }
}
