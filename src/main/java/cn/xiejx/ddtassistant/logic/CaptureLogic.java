package cn.xiejx.ddtassistant.logic;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/24 16:26
 */
@Component
public class CaptureLogic {

    @Resource
    private DmLogic dmLogic;

    public String captureScreen(Integer imgQuality) {
        return dmLogic.captureScreen(imgQuality);
    }
}
