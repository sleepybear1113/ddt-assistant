package cn.xiejx.ddtassistant.controller2;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.utils.Util;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/10 02:00
 */
@RestController
public class RemoteController {
    public static final String DESKTOP_SCREEN_SHOT_DIR = Constants.TEMP_DIR + "screenshot/";

    @Resource
    private DmDdt defaultDm;

    @RequestMapping(value = "/screen/capture", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getImage(int x, int y) throws Exception {
        long now = System.currentTimeMillis();
        String path = DESKTOP_SCREEN_SHOT_DIR + now + ".png";
        int[] region = {0, 0, x, y};
        defaultDm.capturePicByRegion(path, region);

        File file = new File(path);
        byte[] bytes;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            bytes = new byte[inputStream.available()];
            int read = inputStream.read(bytes, 0, inputStream.available());
        }

        Util.delayDeleteFile(path,3000L);
        return bytes;
    }
}
