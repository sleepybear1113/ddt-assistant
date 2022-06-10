package cn.xiejx.ddtassistant.controller2;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.utils.Util;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/10 02:00
 */
@RestController
public class RemoteController {
    public static final String DESKTOP_SCREEN_SHOT_DIR = Constants.TEMP_DIR + "screenshot/";
    public static final String GAME_SCREEN_SHOT_DIR = Constants.TEMP_DIR + "screenshot/game/";

    @Resource
    private DmDdt defaultDm;

    @RequestMapping(value = "/screen/capture", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getScreenCapture(int x, int y) {
        long now = System.currentTimeMillis();
        String path = DESKTOP_SCREEN_SHOT_DIR + now + ".png";
        int[] region = {0, 0, x, y};
        defaultDm.capturePicByRegion(path, region);

        return Util.fileToBytes(path);
    }

    @RequestMapping(value = "/game/capture", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getGameCapture(int n) {
        Collection<DmDdt> values = GlobalVariable.DM_DDT_MAP.values();
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }

        DmDdt dm = null;
        int i = 0;
        for (DmDdt value : values) {
            if (i++ == n) {
                dm = value;
                break;
            }
        }
        if (dm == null) {
            return null;
        }

        long now = System.currentTimeMillis();
        String path = GAME_SCREEN_SHOT_DIR + now + ".png";
        dm.captureFullGamePic(path);

        return Util.fileToBytes(path);
    }
}
