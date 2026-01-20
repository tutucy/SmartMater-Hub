package org.example.controller;

import org.example.common.Result;
import org.example.entity.SystemLog;
import org.example.entity.SystemConfig;
import org.example.mapper.SystemLogMapper;
import org.example.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private SystemLogMapper systemLogMapper;

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @GetMapping("/log/list")
    public Result<List<SystemLog>> getLogList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<SystemLog> list = systemLogMapper.selectList(username, operation, startDate, endDate);
        return Result.success(list);
    }

    @GetMapping("/config")
    public Result<Map<String, Object>> getConfig() {
        List<SystemConfig> configs = systemConfigMapper.selectList();
        Map<String, Object> configMap = new HashMap<>();
        for (SystemConfig config : configs) {
            if ("boolean".equals(config.getConfigType())) {
                configMap.put(config.getConfigKey(), "1".equals(config.getConfigValue()));
            } else if ("number".equals(config.getConfigType())) {
                configMap.put(config.getConfigKey(), Integer.parseInt(config.getConfigValue()));
            } else {
                configMap.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        return Result.success(configMap);
    }

    @PutMapping("/config")
    public Result<String> updateConfig(@RequestBody Map<String, Object> configMap) {
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            SystemConfig config = systemConfigMapper.selectByKey(key);
            if (config != null) {
                String strValue = value.toString();
                config.setConfigValue(strValue);
                systemConfigMapper.update(config);
            }
        }
        return Result.success("更新成功");
    }
}
