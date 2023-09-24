package cn.sleepybear.ddtassistant.logic;

import cn.sleepybear.ddtassistant.base.BindWindowConfig;
import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.constant.Constants;
import cn.sleepybear.ddtassistant.dm.DmDdt;
import cn.sleepybear.ddtassistant.exception.FrontException;
import cn.sleepybear.ddtassistant.utils.ImgUtil;
import cn.sleepybear.ddtassistant.utils.Util;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/06 15:17
 */
@Component
public class DmLogic {
    @Resource
    private DmDdt defaultDm;
    @Resource
    private SettingConfig settingConfig;

    public List<Integer> getDdtHwnds() {
        List<Integer> ddtWindowHwnd = defaultDm.enumDdtWindowHwnd();
        if (CollectionUtils.isEmpty(ddtWindowHwnd)) {
            throw new FrontException("没有 flash 窗口");
        }
        return ddtWindowHwnd;
    }

    public String getGameScreenPath(int hwnd, Integer imgQuality) {
        String path = Constants.TEMP_GAME_SCREEN_SHOT_DIR + hwnd + "-" + System.currentTimeMillis() + ".png";
        if (!defaultDm.isWindowClassFlashPlayerActiveX(hwnd)) {
            throw new FrontException(String.format("该句柄[%s]不是 flash 游戏句柄", hwnd));
        }
        BindWindowConfig bindWindowConfig = settingConfig.getBindWindowConfig();
        DmDdt dmDdt = DmDdt.createInstance(hwnd);
        dmDdt.bind(bindWindowConfig.getMouseTypeEnum(), bindWindowConfig.getKeypadTypeEnum());
        dmDdt.captureFullGamePic(path);
        if (imgQuality != null && imgQuality >= 1 && imgQuality <= 9) {
            ImgUtil.compress(path, path, imgQuality * 1.0f / 10);
        }
        Util.delayDeleteFile(path, 5000L);
        return path;
    }

    public String captureScreen(Integer imgQuality) {
        long now = System.currentTimeMillis();
        String path = Constants.DESKTOP_SCREEN_SHOT_DIR + now + ".png";
        int[] region = {0, 0, 5000, 5000};
        defaultDm.capturePicByRegion(path, region);
        if (imgQuality != null && imgQuality >= 1 && imgQuality <= 9) {
            ImgUtil.compress(path, path, imgQuality * 1.0f / 10);
        }
        Util.delayDeleteFile(path, 3000L);
        return path;
    }
}