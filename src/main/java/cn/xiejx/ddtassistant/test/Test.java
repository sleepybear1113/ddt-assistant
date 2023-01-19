package cn.xiejx.ddtassistant.test;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmConstants;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.dm.DmDomains;
import cn.xiejx.ddtassistant.type.TypeConstants;
import cn.xiejx.ddtassistant.type.auction.Auction;
import cn.xiejx.ddtassistant.type.auction.AuctionConstants;
import cn.xiejx.ddtassistant.type.captcha.CaptchaConstants;
import cn.xiejx.ddtassistant.utils.ImgUtil;
import cn.xiejx.ddtassistant.utils.OcrUtil;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.captcha.ChoiceEnum;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {
        whiteTest();
    }

    public static void whiteTest() throws IOException {
        Util.sleep(500L);
        for (int i = 0; i < 4; i++) {
            long t0 = System.currentTimeMillis();
            ImgUtil.MajorPixelInfo majorColor = ImgUtil.getMajorColor("test/white-screen.png");
            long t1 = System.currentTimeMillis();
            System.out.println(majorColor);
            System.out.println(t1 - t0);
        }

    }

    public static void searchLeaveGame() {
        DmDdt dm = DmDdt.createInstance(null);
        dm.capture(0, 0, 5000, 5000, "test/aaa.jpg");
        int[] hwnds = dm.enumWindow(0, "离开此页(&L)", "", DmConstants.EnumWindowFilter.TITLE.getType());
        System.out.println(Arrays.toString(hwnds));

        hwnds = dm.enumWindow(0, "留在此页(&S)", "", DmConstants.EnumWindowFilter.TITLE.getType());
        System.out.println(Arrays.toString(hwnds));

        for (int hwnd : hwnds) {
            DmDdt dmDdt = DmDdt.createInstance(hwnd);

            dmDdt.bind();
            dmDdt.captureFullGamePic("test/" + hwnd + ".jpg");
        }
    }

    public static void angle() {
        int hwnd = 594094;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.clickCorner();
        System.out.println("等待1秒，然后向下-10度");
        Util.sleep(1000L);
        for (int i = 0; i < 10; i++) {
            dm.keyPressChar("down");
        }

        System.out.println("等待1秒，然后向上+20度");
        Util.sleep(1000L);
        for (int i = 0; i < 20; i++) {
            dm.keyPressChar("up");
        }
    }

    public static void capWind() {
        int[] region = new int[]{480, 20, 520, 43};
        int hwnd = 329468;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.clickCorner();
        dm.capturePicByRegion(Constants.TEMP_DIR + "wind2/" + System.currentTimeMillis() + ".png", region);
    }

    public static void ocrWind() throws IOException, TesseractException {
        int[] blue = {60, 60, 60};
        int[][][] colors = new int[][][]{
                {blue, ImgUtil.WHITE, blue},
//                {ImgUtil.WHITE, ImgUtil.BLACK, ImgUtil.COLOR_40},
        };
        BufferedImage bufferedImage = ImgUtil.changeImgColor(Constants.TEMP_DIR + "wind/1658028220368.png", colors, ImgUtil.DeltaInOut.DELTA_IN);

        String ocr = OcrUtil.ocr(bufferedImage);
        System.out.println(ocr);
    }

    public static void find1() {
        int hwnd = 329468;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.clickCorner();
        List<String> templateImgList = GlobalVariable.getTemplateImgList(TypeConstants.TemplatePrefix.AUCTION_NUM_BOX_CONFIRM_CANCEL_BUTTON);
        int[] found = dm.findPic(AuctionConstants.NUM_INPUT_OR_DROP_CONFIRM_CANCEL_FIND_RECT, StringUtils.join(templateImgList, "|"), "010101", 0.5, DmConstants.SearchWay.LEFT2RIGHT_UP2DOWN);
        System.out.println(found[0] > 0);
    }

    public static void testOcrNumTotal() {
        int hwnd = 67702;
        Auction auction = Auction.createInstance(hwnd, Auction.class, true);
        auction.setRunning(true);
        auction.getDm().clickCorner();
        for (int i = 1; i <= 999; i++) {
            Integer num = auction.ocrItemNum();
            if (Objects.equals(num, i)) {
                System.out.println(i + ", num = " + num + " is ok");
            } else {
                System.err.println(i + ", num = " + num + " !!!!!!!!!!!!!!!!!!");
            }
            auction.getDm().leftClick(AuctionConstants.NUM_INPUT_BOX_NUM_ADD_POINT);
            Util.sleep(100L);
            Util.sleep(100L);
        }
    }

    public static void ocr() throws Exception {
        String filename = Constants.TEMP_DIR + "ocr/b/1657729494540.png";
//        ImgUtil.cleanImg(filename, Constants.TEMP_DIR + "ocr/b/1.png", ImageClean.Type.CAPTCHA_WHITE_CHAR);
        String s = OcrUtil.ocrAuctionItemName(filename, Constants.TEMP_DIR + "ocr/b/3.png");
        System.out.println(s);
    }

    public static void auctionNameCap() {
        int hwnd = 393942;
        Auction auction = Auction.createInstance(hwnd, Auction.class, true);
        auction.setRunning(true);
//        auction.getDm().leftDoubleClick(AuctionConstants.NUM_INPUT_BOX_NUM_POINT);
//        Util.sleep(100L);
        auction.getDm().clickCorner();
        Util.sleep(100L);
        String path = Constants.TEMP_DIR + "ocr/raw/" + System.currentTimeMillis() + ".png";
        auction.captureItemNameOcrRect(path);
        String s = OcrUtil.ocrAuctionItemName(path, path.replace("raw", "b"));
        System.out.println(s);
//        System.out.println(auction.ocrItemMouthfulPrice());
//        int[] picInFullGame = auction.getDm().findPicInFullGame("资源图片/模板/拍卖场-是否需要出售-1.bmp", "030303", 0.7);
//        System.out.println(Arrays.toString(picInFullGame));
//        auction.getDm().findPicExInFullGame("资源图片/模板/副本-翻牌-游戏结算-1.bmp|资源图片/模板/拍卖场-是否需要出售-1.bmp", "030303", 0.7);
    }

    public static void testAuction() {
        int hwnd = 329448;
        Auction auction = Auction.createInstance(hwnd, Auction.class, true);
        auction.setRunning(true);
//        auction.getDm().leftDoubleClick(AuctionConstants.NUM_INPUT_BOX_NUM_POINT);
//        Util.sleep(100L);
        auction.getDm().clickCorner();
        Util.sleep(100L);
        auction.putBackItem();
//        System.out.println(auction.ocrItemMouthfulPrice());
//        int[] picInFullGame = auction.getDm().findPicInFullGame("资源图片/模板/拍卖场-是否需要出售-1.bmp", "030303", 0.7);
//        System.out.println(Arrays.toString(picInFullGame));
//        auction.getDm().findPicExInFullGame("资源图片/模板/副本-翻牌-游戏结算-1.bmp|资源图片/模板/拍卖场-是否需要出售-1.bmp", "030303", 0.7);
    }

    public static void captureAuctionSample() {
        AuctionConstants.AuctionPosition.getIndexPic("test/bag.png", 16);
    }

    public static void captureCountDownNumber() {
        String countDownName = Constants.CAPTCHA_COUNT_DOWN_DIR + 662382 + ".png";
        Integer countDown = OcrUtil.ocrCountDownPic("test/113.png");
        System.out.println(countDown);
    }

    public static void capture() {
        int hwnd = 921548;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.clickCorner();
        Util.sleep(200L);
        dm.capturePicByRegion(TypeConstants.TemplatePrefix.getFullPathWithFullName(TypeConstants.TemplatePrefix.AUCTION_DROP_CONFIRM_CANCEL_BUTTON, 1), AuctionConstants.NUM_INPUT_OR_DROP_CONFIRM_CANCEL_SAMPLE_RECT);
    }

    public static void findPic() {
        int hwnd = 526516;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.leftClick(10, 10);
        Util.sleep(100L);

        Map<String, List<String>> templateImgMap = TypeConstants.TemplatePrefix.getTemplateImgMap();
        List<DmDomains.PicEx> picExInFullGame = dm.findPicExInFullGame(templateImgMap.get(TypeConstants.TemplatePrefix.AUCTION_SOLD_OUT_INTRO), "020202", 0.7);
//        dm.capturePicByRegion("test/full-a-1.png", DmDdt.GAME_FULL_REACT);
        System.out.println(picExInFullGame);
    }

    public static void captureFull() {
        int hwnd = 4330002;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        System.out.println(Arrays.toString(dm.getWindowRect(hwnd)));
        dm.leftClick(1000,600);
        Util.sleep(100L);
        dm.capturePicByRegion("test/white-225.png", DmDdt.GAME_FULL_REACT);

    }

    public static void click() {
        int hwnd = 921548;
        DmDdt dm = DmDdt.createInstance(hwnd);
        dm.bind();
        dm.leftClick(179, 73);
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
        dm.capturePicByRegion("test/template-dark-1.bmp", CaptchaConstants.CAPTCHA_COUNTDOWN_SAMPLE_REACT);
    }

    public static void testKey() throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_F5);
        Util.sleep(50L);
        robot.keyRelease(KeyEvent.VK_F5);
    }

    public static void r() {
        String captchaDir = Constants.CAPTCHA_DIR + Util.getTimeString(Util.TIME_YMD_FORMAT);
        File file = new File(captchaDir);
        if (!file.isDirectory()) {
            System.out.println(file.mkdirs());
        }

    }

    public static void sleep(long t) {
        try {
            TimeUnit.MILLISECONDS.sleep(t);
        } catch (InterruptedException ignored) {

        }
    }
}
