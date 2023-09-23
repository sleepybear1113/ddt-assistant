package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.base.OfflineDetectionConfig;
import cn.sleepybear.ddtassistant.logic.OfflineDetectionLogic;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
