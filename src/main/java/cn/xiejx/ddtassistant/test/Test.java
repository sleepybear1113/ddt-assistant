package cn.xiejx.ddtassistant.test;

import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.tj.TjHttpUtil;
import cn.xiejx.ddtassistant.utils.tj.TjPredictDto;
import cn.xiejx.ddtassistant.utils.tj.TjResponse;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {
        DmDdt dm = DmDdt.createInstance(null);
        int hwnd = 1709470;
        System.out.println(dm.getWindowTitle(hwnd) + "-" + dm.getWindowClass(hwnd) + "-" + Arrays.toString(dm.getWindowRect(hwnd)));
        dm.setWindowState(hwnd, 5);
    }

    public static void testKey() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_F5);
        Util.sleep(50L);
        robot.keyRelease(KeyEvent.VK_F5);
    }

    public static void r() {
        String captchaDir = "captcha/" + Util.getTimeString(Util.TIME_YMD_FORMAT);
        File file = new File(captchaDir);
        if (!file.isDirectory()) {
            System.out.println(file.mkdirs());
        }

    }

    public static void testYzm() {
        TjPredictDto tjPredictDto = new TjPredictDto("sleepybear", "tj123456", "7", null, null, "captcha-sample.jpg");
        TjResponse tjResponse = TjHttpUtil.getTjResponse(tjPredictDto);

        System.out.println(tjResponse);
    }

    public static void sleep(long t) {
        try {
            TimeUnit.MILLISECONDS.sleep(t);
        } catch (InterruptedException ignored) {

        }
    }
}
