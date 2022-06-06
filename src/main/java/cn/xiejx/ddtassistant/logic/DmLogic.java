package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.utils.Util;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    public List<Integer> getDdtHwnds() {
        List<Integer> ddtWindowHwnd = defaultDm.enumDdtWindowHwnd();
        if (CollectionUtils.isEmpty(ddtWindowHwnd)) {
            throw new FrontException("没有 flash 窗口");
        }
        return ddtWindowHwnd;
    }

    public String getGameScreenPath(int hwnd) {
        String path = Constants.TEMP_GAME_SCREEN_SHOT_DIR + hwnd + "-" + System.currentTimeMillis() + ".png";
//        DmDdt dmDdt = GlobalVariable.DM_DDT_MAP.get(hwnd);
        DmDdt dmDdt = DmDdt.createInstance(hwnd);
        dmDdt.bind();
        dmDdt.captureFullGamePic(path);
        Util.delayDeleteFile(path, 5000L);
        return path.replace(Constants.TEMP_DIR, "file/");
    }
}
