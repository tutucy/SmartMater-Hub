package org.example.controller;

import org.example.common.Result;
import org.example.entity.OutboundRecord;
import org.example.service.OutboundRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outbound")
public class OutboundRecordController {

    @Autowired
    private OutboundRecordService outboundRecordService;

    @GetMapping("/{id}")
    public Result<OutboundRecord> getById(@PathVariable Integer id) {
        OutboundRecord record = outboundRecordService.getById(id);
        return Result.success(record);
    }

    @GetMapping("/list")
    public Result<List<OutboundRecord>> getList(
            @RequestParam(required = false) String outboundNo,
            @RequestParam(required = false) String outboundType,
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<OutboundRecord> list = outboundRecordService.getList(outboundNo, outboundType, recipient, department, startDate, endDate);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody OutboundRecord record) {
        outboundRecordService.add(record);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody OutboundRecord record) {
        outboundRecordService.update(record);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        outboundRecordService.delete(id);
        return Result.success("删除成功");
    }

    @PutMapping("/audit/{id}")
    public Result<String> audit(@PathVariable Integer id, @RequestParam Integer status, @RequestParam(required = false) String remark) {
        OutboundRecord record = new OutboundRecord();
        record.setId(id);
        record.setStatus(status);
        outboundRecordService.update(record);
        return Result.success("审核成功");
    }
}
