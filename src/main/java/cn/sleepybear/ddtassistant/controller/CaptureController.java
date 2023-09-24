package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.logic.CaptureLogic;
import cn.sleepybear.ddtassistant.advice.ResultCode;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/24 16:26
 */
@RestController
public class CaptureController {

    @Resource
    private CaptureLogic captureLogic;

    @RequestMapping("/capture/getScreenshotPath")
    public ResultCode<String> captureScreen(Integer imgQuality) {
        String path = captureLogic.captureScreen(imgQuality);
        return ResultCode.buildResult(path);
    }
}
