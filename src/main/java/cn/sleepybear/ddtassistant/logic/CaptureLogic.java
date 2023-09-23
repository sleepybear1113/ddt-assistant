package cn.sleepybear.ddtassistant.logic;

import cn.sleepybear.ddtassistant.constant.Constants;
import cn.sleepybear.ddtassistant.dm.DmDdt;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/24 16:26
 */
@Component
public class CaptureLogic {

    public static final String DESKTOP_SCREEN_SHOT_DIR = Constants.TEMP_DIR + "screenshot/";
    public static final String GAME_SCREEN_SHOT_DIR = Constants.TEMP_DIR + "screenshot/game/";

    @Resource
    private DmDdt defaultDm;

    @Resource
    private DmLogic dmLogic;

    public String captureScreen(Integer imgQuality) {
        return dmLogic.captureScreen(imgQuality);
    }
}
