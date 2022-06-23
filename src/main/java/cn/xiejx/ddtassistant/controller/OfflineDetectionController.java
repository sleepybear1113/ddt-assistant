package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.base.OfflineDetectionConfig;
import cn.xiejx.ddtassistant.logic.OfflineDetectionLogic;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 02:18
 */
@RestController
public class OfflineDetectionController {
    @Resource
    private OfflineDetectionLogic offlineDetectionLogic;

    @RequestMapping("offlineDetection/get")
    public OfflineDetectionConfig get() {
        return offlineDetectionLogic.get();
    }

    @RequestMapping("offlineDetection/update")
    public Boolean update(@RequestBody OfflineDetectionConfig offlineDetectionConfig) {
        return offlineDetectionLogic.update(offlineDetectionConfig);
    }
}
