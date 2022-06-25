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
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
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
        // 大漠插件的 programId
        String programId = "dm.dmsoft";
        // 使用单线程的方式初始化，3.1233 大漠不支持 MTA
        ComThread.InitSTA();
        // 实例化大漠组件对象
        ActiveXComponent dm = new ActiveXComponent(programId);
        Dispatch dispatch = dm.getObject();
        // 通过 Dispatch.call() 方法来调用 COM 组件的方法，返回值为 Variant 类型，可以使用 getString、getInt 来获取对应返回值
        // Ver 为获取大漠版本号的方法
        Variant variant = Dispatch.call(dispatch, "Ver");
        System.out.println(variant.getString());
        // 释放 Variant 对象
        variant.safeRelease();
        // 释放资源
        ComThread.Release();
    }

    public static void ocr() {
        String filename = "tmp/auction/790348-item_name.png";
        String s = OcrUtil.ocrAuctionItemName(filename);
        System.out.println(s);
    }

    public static void testAuction() {
        int hwnd = 790348;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.leftClick(10, 10);
        Util.sleep(100L);
        Auction auction = Auction.createInstance(dm);
        auction.go(1);
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
