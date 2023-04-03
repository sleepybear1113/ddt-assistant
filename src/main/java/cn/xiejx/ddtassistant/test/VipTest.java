package cn.xiejx.ddtassistant.test;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.dm.DmDomains;
import cn.xiejx.ddtassistant.type.captcha.CaptchaConstants;
import cn.xiejx.ddtassistant.type.vip.AutoVipCoinOpen;
import cn.xiejx.ddtassistant.utils.Util;

import java.io.File;
import java.util.*;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/27 21:52
 */
public class VipTest {

    public static String vipTemplateDir = "test/vip";

    public static DmDdt dmDdt = DmDdt.createInstance(331892);


    public static void main(String[] args) {
        buildClick();
        dmDdt.captureFullGamePic("test/full-vip.bmp");
    }

    public static void buildClick() {
        dmDdt.bind();
        dmDdt.clickCorner();
        Util.sleep(100L);
    }

    public static void captureAll() {
        dmDdt.bind();
        dmDdt.clickCorner();
        Util.sleep(100L);

//        dmDdt.capturePicByRegion("test/vip/VIP币.bmp", new int[]{819, 165, 849, 185});

//        test();

//        for (int i = 0; i < 18; i++) {
//            captureEachPic(i, 1051762);
//        }

        List<DmDomains.PicEx> vipPicEx = dmDdt.findPicExInFullGame(Collections.singletonList(Constants.VIP_COIN_TEMPLATE_DIR + "VIP币.bmp"), "101010", 0.7);
        DmDomains.PicEx vipPositionP = vipPicEx.get(0);
        int[] vipPosition = new int[]{vipPositionP.getX() + 30, vipPositionP.getY() + 30};
        while (true) {
            openVipB(vipPosition);
            Util.sleep(200L);

            match(false);
            if (true) {
//                break;
            }

            closeVipB();
            Util.sleep(50L);
        }


//        match(1051762);
    }

    public static void openVipB(int[] p) {
        dmDdt.leftClick(p);
        Util.sleep(80L);
        dmDdt.leftClick(p);
    }

    public static void closeVipB() {
        dmDdt.leftClick(708, 39);
        Util.sleep(50L);
        dmDdt.leftClick(435, 345);
    }


    public static void testExist(String path) {
        dmDdt.clickCorner();
        Util.sleep(100L);
        String filename = Constants.TEMPLATE_PICTURE_DIR + "VIP币/" + path;
        System.out.println(new File(filename).length());
        List<DmDomains.PicEx> picEx = dmDdt.findPicEx(CaptchaConstants.VIP_COIN_SEARCH_RECT, Collections.singletonList(filename), "101010", 0.7);
        dmDdt.capturePicByRegion("test/a.bmp", CaptchaConstants.VIP_COIN_SEARCH_RECT);
        System.out.println(picEx);
    }

    public static void match(boolean add) {
        File dir = new File(vipTemplateDir);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        List<String> existTemplates = new ArrayList<>();
        for (File file : files) {
            existTemplates.add(vipTemplateDir + "/" + file.getName());
        }

        Set<Integer> allSet = new HashSet<>();
        for (int i = 0; i < 18; i++) {
            allSet.add(i);
        }


        List<DmDomains.PicEx> picEx = null;
        for (int i = 0; i < 5; i++) {
            dmDdt.clickCorner();

            picEx = dmDdt.findPicEx(CaptchaConstants.VIP_COIN_SEARCH_RECT, existTemplates, "111111", 0.7);

            for (DmDomains.PicEx ex : picEx) {
                System.out.println(ex.getPicName());
                Integer position = AutoVipCoinOpen.getPosition(ex);
                if (position == null) {
                    continue;
                }

                allSet.remove(position);
            }
            System.out.println(picEx.size());
            System.out.println("----------------------");

            if (allSet.size() == 0) {
                return;
            }
            Util.sleep(50L);
        }

        if (add) {
            for (Integer integer : allSet) {
                System.out.println("添加" + integer);
                captureEachPic(integer);
            }
        }

    }

    public static void captureEachPic(int i) {
        dmDdt.clickCorner();
        Util.sleep(100L);

        int x = CaptchaConstants.VIP_COIN_SEARCH_RECT[0] + CaptchaConstants.VIP_COIN_POSITION[i][1] * (CaptchaConstants.VIP_COIN_GRID_LENGTH + CaptchaConstants.VIP_COIN_GAP);
        int y = CaptchaConstants.VIP_COIN_SEARCH_RECT[1] + CaptchaConstants.VIP_COIN_POSITION[i][0] * (CaptchaConstants.VIP_COIN_GRID_LENGTH + CaptchaConstants.VIP_COIN_GAP);

        String path = "test/vip/g" + i + ".bmp";
        Util.ensureParentDir(path);
//        dmDdt.capturePicByRegion(path, new int[]{x, y, x + CaptchaConstants.VIP_B_GRID_LENGTH, y + CaptchaConstants.VIP_B_GRID_LENGTH});

        x += CaptchaConstants.VIP_COIN_INNER_GAP;
        y += CaptchaConstants.VIP_COIN_INNER_GAP;
        int fileCount = new File(vipTemplateDir).listFiles().length;
        path = "test/vip/" + fileCount + ".bmp";
        System.out.println("截图第" + fileCount);
        dmDdt.capturePicByRegion(path, new int[]{x, y, x + CaptchaConstants.VIP_COIN_GRID_LENGTH - 2 * CaptchaConstants.VIP_COIN_INNER_GAP, y + CaptchaConstants.VIP_COIN_GRID_LENGTH - 2 * CaptchaConstants.VIP_COIN_INNER_GAP});
    }

    public static void test() {
        List<DmDomains.PicEx> a = dmDdt.findPicExInFullGame(Collections.singletonList("test/vip/14.bmp"), "15", .6);
        System.out.println(a);
        throw new RuntimeException();
    }
}
