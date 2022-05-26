package cn.xiejx.ddtassistant.test;

import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.type.Captcha;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.tj.ChoiceEnum;
import cn.xiejx.ddtassistant.utils.tj.TjHttpUtil;
import cn.xiejx.ddtassistant.utils.tj.TjPredictDto;
import cn.xiejx.ddtassistant.utils.tj.TjResponse;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {
        captureFlopBonus();
    }

    public static void captureFlopBonus() {
        int hwnd = 329146;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.capturePicByRegion("test/" + Captcha.TEMPLATE_FLOP_BONUS_PREFIX + "1.bmp", Captcha.FLOP_BONUS_SAMPLE_RECT);
    }

    public static void captureFull() {
        int hwnd = 329146;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.capturePicByRegion("test/full-1.bmp", Captcha.GAME_FULL_REACT);
    }

    public static void uploadFile(String path) {
        Util.uploadToServer(path, ChoiceEnum.C);
    }

    public static void deleteFile(String path) {
        Util.deleteFileFromServer(path);
    }

    public static void toBmp() {
        DmDdt dm = DmDdt.createInstance(null);

        dm.imageToBmp("test/count-down-bright-2.png", "test/captcha-sample-bright-2.bmp");
    }

    public static void c() {
        int hwnd = 4264830;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.capturePicByRegion("test/template-dark-1.bmp", Captcha.CAPTCHA_COUNTDOWN_SAMPLE_REACT);
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
