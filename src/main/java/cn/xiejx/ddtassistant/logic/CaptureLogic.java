package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.dm.DmDdt;
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

    public static final String DESKTOP_SCREEN_SHOT_DIR = Constants.TEMP_DIR + "screenshot/";
    public static final String GAME_SCREEN_SHOT_DIR = Constants.TEMP_DIR + "screenshot/game/";

    @Resource
    private DmDdt defaultDm;

    @Resource
    private DmLogic dmLogic;

    public String captureScreen() {
        return dmLogic.captureScreen();
    }
}
