package org.example.controller;

import org.example.common.Result;
import org.example.entity.InboundRecord;
import org.example.service.InboundRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inbound")
public class InboundRecordController {

    @Autowired
    private InboundRecordService inboundRecordService;

    @GetMapping("/{id}")
    public Result<InboundRecord> getById(@PathVariable Integer id) {
        InboundRecord record = inboundRecordService.getById(id);
        return Result.success(record);
    }

    @GetMapping("/list")
    public Result<List<InboundRecord>> getList(
            @RequestParam(required = false) String inboundNo,
            @RequestParam(required = false) String inboundType,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<InboundRecord> list = inboundRecordService.getList(inboundNo, inboundType, supplierId, startDate, endDate);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody InboundRecord record) {
        inboundRecordService.add(record);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody InboundRecord record) {
        inboundRecordService.update(record);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        inboundRecordService.delete(id);
        return Result.success("删除成功");
    }

    @PutMapping("/audit/{id}")
    public Result<String> audit(@PathVariable Integer id, @RequestParam Integer status, @RequestParam(required = false) String remark) {
        InboundRecord record = new InboundRecord();
        record.setId(id);
        record.setStatus(status);
        inboundRecordService.update(record);
        return Result.success("审核成功");
    }
}
