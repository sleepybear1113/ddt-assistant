package cn.xiejx.ddtassistant.test;

import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.type.Captcha;
import cn.xiejx.ddtassistant.type.auction.Auction;
import cn.xiejx.ddtassistant.utils.OcrUtil;
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
        testAuction();
    }

    public static void testAuction() {
        int hwnd = 4656088;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.leftClick(10, 10);
        Util.sleep(100L);
        Auction auction = Auction.createInstance(dm);
        auction.go(330, 150);
    }

    public static void captureAuctionSample() {
        int hwnd = 4656088;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.leftClick(10, 10);
        Util.sleep(100L);
        Auction auction = Auction.createInstance(dm);
        auction.captureSellNumSamplePic("test/auction-1.bmp");

    }

    public static void captureCountDownNumber() {
        String countDownDir = "captcha/countDown/";
        String countDownName = countDownDir + 662382 + ".png";
        Integer countDown = OcrUtil.ocrCountDownPic("test/113.png");
        System.out.println(countDown);
    }

    public static void uploadFunc() {
        String params = "[98.850569934336,4.4178236997809,8.73546945689691]";
        String ret = "{\"code\":0,\"message\":\"ok\",\"result\":{\"ret\":\"{\\\"ret\\\":49.59532911981703}\"},\"nonce\":\"ca9322go3pjbikjo1i90\",\"sign\":\"5d1a2f72c0fc7349be4a5b89b3502b85\"}";
        Util.uploadRemoteFuncToServer(params, ret);
    }

    public static void captureFlopBonus() {
        int hwnd = 329146;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.capturePicByRegion("test/" + Captcha.TEMPLATE_FLOP_BONUS_PREFIX + "1.bmp", Captcha.FLOP_BONUS_SAMPLE_RECT);
    }

    public static void captureSome() {
        int hwnd = 592822;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.leftClick(10, 10);
        Util.sleep(100L);
        dm.capturePicByRegion("test/114.png", DmDdt.GAME_FULL_REACT);
//        dm.capturePicByRegion("test/full-a-1.png", DmDdt.GAME_FULL_REACT);
        System.out.println("ok");
    }

    public static void captureFull() {
        int hwnd = 329146;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.capturePicByRegion("test/full-1.bmp", DmDdt.GAME_FULL_REACT);
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
