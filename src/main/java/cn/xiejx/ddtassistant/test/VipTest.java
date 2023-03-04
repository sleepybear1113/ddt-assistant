package cn.xiejx.ddtassistant.test;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.dm.DmDomains;
import cn.xiejx.ddtassistant.type.captcha.CaptchaConstants;
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

    public static DmDdt dmDdt = DmDdt.createInstance(1575522);


    public static void main(String[] args) {
        dmDdt.bind();
        dmDdt.clickCorner();
        Util.sleep(100L);

//        test();

//        for (int i = 0; i < 18; i++) {
//            captureEachPic(i, 1051762);
//        }

        while (true) {
            openVipB();
            Util.sleep(150L);

            match();
            if (true) {
//                break;
            }

            closeVipB();
            Util.sleep(50L);
        }



//        match(1051762);
    }

    public static void openVipB() {
        int[] p = {849, 195};
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
        List<DmDomains.PicEx> picEx = dmDdt.findPicEx(CaptchaConstants.VIP_B_SEARCH_RECT, Collections.singletonList(filename), "101010", 0.7);
        dmDdt.capturePicByRegion("test/a.bmp", CaptchaConstants.VIP_B_SEARCH_RECT);
        System.out.println(picEx);
    }

    public static void match() {
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

            picEx = dmDdt.findPicEx(CaptchaConstants.VIP_B_SEARCH_RECT, existTemplates, "111111", 0.7);

            for (DmDomains.PicEx ex : picEx) {
                Integer position = getPosition(ex);
                if (position == null) {
                    continue;
                }

                allSet.remove(position);
            }

            if (allSet.size() == 0) {
                return;
            }
            Util.sleep(50L);
        }
        for (Integer integer : allSet) {
            System.out.println("添加" + integer);
            captureEachPic(integer);
        }
    }

    public static Integer getPosition(DmDomains.PicEx picEx) {
        for (int i = 0; i < CaptchaConstants.VIP_B_POSITION.length; i++) {
            int[] position = CaptchaConstants.VIP_B_POSITION[i];
            int startX = CaptchaConstants.VIP_B_SEARCH_RECT[0];
            int startY = CaptchaConstants.VIP_B_SEARCH_RECT[1];

            int x = startX + position[1] * (CaptchaConstants.VIP_B_GRID_LENGTH + CaptchaConstants.VIP_B_GAP);
            int y = startY + position[0] * (CaptchaConstants.VIP_B_GRID_LENGTH + CaptchaConstants.VIP_B_GAP);

            int[] p = {x, y};

            if (p[0] < picEx.getX() && p[0] + CaptchaConstants.VIP_B_GRID_LENGTH > picEx.getX() && p[1] < picEx.getY() && p[1] + CaptchaConstants.VIP_B_GRID_LENGTH > picEx.getY()) {
                return i;
            }
        }
        return null;
    }

    public static void captureEachPic(int i) {
        dmDdt.clickCorner();
        Util.sleep(100L);

        int x = CaptchaConstants.VIP_B_SEARCH_RECT[0] + CaptchaConstants.VIP_B_POSITION[i][1] * (CaptchaConstants.VIP_B_GRID_LENGTH + CaptchaConstants.VIP_B_GAP);
        int y = CaptchaConstants.VIP_B_SEARCH_RECT[1] + CaptchaConstants.VIP_B_POSITION[i][0] * (CaptchaConstants.VIP_B_GRID_LENGTH + CaptchaConstants.VIP_B_GAP);

        String path = "test/vip/g" + i + ".bmp";
        Util.ensureParentDir(path);
//        dmDdt.capturePicByRegion(path, new int[]{x, y, x + CaptchaConstants.VIP_B_GRID_LENGTH, y + CaptchaConstants.VIP_B_GRID_LENGTH});

        x += CaptchaConstants.VIP_B_INNER_GAP;
        y += CaptchaConstants.VIP_B_INNER_GAP;
        int fileCount = new File(vipTemplateDir).listFiles().length;
        path = "test/vip/" + fileCount + ".bmp";
        System.out.println("截图第" + fileCount);
        dmDdt.capturePicByRegion(path, new int[]{x, y, x + CaptchaConstants.VIP_B_GRID_LENGTH - 2 * CaptchaConstants.VIP_B_INNER_GAP, y + CaptchaConstants.VIP_B_GRID_LENGTH - 2 * CaptchaConstants.VIP_B_INNER_GAP});
    }

    public static void test() {
        List<DmDomains.PicEx> a = dmDdt.findPicExInFullGame(Collections.singletonList("test/vip/14.bmp"), "15", .6);
        System.out.println(a);
        throw new RuntimeException();
    }
}
