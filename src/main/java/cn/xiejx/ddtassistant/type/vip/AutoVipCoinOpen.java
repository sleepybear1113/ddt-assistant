package cn.xiejx.ddtassistant.type.vip;

import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDomains;
import cn.xiejx.ddtassistant.type.BaseType;
import cn.xiejx.ddtassistant.type.captcha.CaptchaConstants;
import cn.xiejx.ddtassistant.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/03/05 22:16
 */
@Slf4j
public class AutoVipCoinOpen extends BaseType {

    private static final long serialVersionUID = 3496760745860466985L;

    private int[] vipCoinPosition = null;

    public static List<String> templatePathList = new ArrayList<>();

    static {
        loadVipTemplate();
        if (CollectionUtils.isEmpty(templatePathList)) {
            log.warn("本地VIP模板文件不存在，请前往设置->更新设置处下载");
        }
    }

    public static void loadVipTemplate() {
        File dir = new File(Constants.VIP_COIN_TEMPLATE_DIR);
        File[] files = dir.listFiles();
        templatePathList.clear();
        if (files != null) {
            for (File file : files) {
                templatePathList.add(Constants.VIP_COIN_TEMPLATE_DIR + file.getName());
            }
            templatePathList.removeIf(s -> !s.contains("-"));
        }
    }

    public boolean findVipCoinPosition(int n) {
        getDm().clickCorner();
        Util.sleep(100L);
        for (int i = 0; i < n; i++) {
            boolean b = findVipCoinPosition();
            if (b) {
                return true;
            }
            Util.sleep(600L);
        }
        return false;
    }

    public boolean findVipCoinPosition() {
        List<DmDomains.PicEx> vipCoinPicEx = getDm().findPicExInFullGame(Collections.singletonList(Constants.VIP_COIN_TEMPLATE_DIR + "VIP币.bmp"), "101010", 0.7);
        if (CollectionUtils.isEmpty(vipCoinPicEx)) {
            log.info("[{}] 找不到VIP币，请检查是否打开背包或者切换到“道具”栏！", getHwnd());
            return false;
        }
        DmDomains.PicEx vipCoinPositionPicEx = vipCoinPicEx.get(0);
        vipCoinPosition = new int[]{vipCoinPositionPicEx.getX() + 30, vipCoinPositionPicEx.getY() + 30};
        return true;
    }

    public void openVipCoin() {
        if (vipCoinPosition == null) {
            return;
        }

        getDm().leftClick(vipCoinPosition);
        Util.sleep(50L);
        getDm().leftClick(vipCoinPosition);
    }

    public boolean hasOpenVipBoard() {
        int[] colorInt = getDm().getColorInt(290, 70);
        int[] a2 = {98, 163, 80};
        if (Arrays.equals(colorInt, a2)) {
            log.info("[{}] 转盘已经打开", getHwnd());
            return true;
        }
        return false;
    }

    public void closeVipB() {
        getDm().leftClick(708, 39);
        Util.sleep(50L);
        getDm().leftClick(435, 345);
    }

    public void loop() {
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName());
        Integer hwnd = getHwnd();
        if (isRunning()) {
            log.info("[{}] 线程已经在运行中了", hwnd);
            return;
        }

        // 判断是否 flash
        boolean isFlashWindow = getDm().isWindowClassFlashPlayerActiveX();
        if (!isFlashWindow) {
            unbindAndRemove();
            return;
        }

        getDm().bind();
        setRunning(true);

        // 激活窗口
        getDm().clickCorner();
        getDm().clickCorner();
        Util.sleep(100L);

        vipCoinPosition = null;
        int count = 0;
        // 开始刷盘子
        while (true) {
            stopOrPause();

            count++;

            // 找 VIP 币的位置，或者每刷 10 次重新设置坐标
            if (vipCoinPosition == null) {
                if (!hasOpenVipBoard()) {
                    // 没有打开 VIP 转盘的话，判断有没有币
                    if (!findVipCoinPosition(4)) {
                        // 没找到币的位置
                        break;
                    }
                }
            }

            // 双击 VIP 币打开盘子
            openVipCoin();

            List<String> matchThingList = match();

            log.info("[{}] 匹配到数量 {}", hwnd, CollectionUtils.size(matchThingList));

            closeVipB();
        }

        setRunning(false);
    }

    public List<String> match() {
        List<String> res = new ArrayList<>();

        if (CollectionUtils.isEmpty(templatePathList)) {
            log.warn("本地VIP模板文件不存在，请前往设置->更新设置处下载");
            return res;
        }

        List<DmDomains.PicEx> picExList = null;
        for (int i = 0; i < 10; i++) {
            if (!hasOpenVipBoard()) {
                // 是否打开盘子
                Util.sleep(50L);
                continue;
            }

            // 找图
            picExList = getDm().findPicEx(CaptchaConstants.VIP_COIN_SEARCH_RECT, templatePathList, "111111", 0.7);
            if (CollectionUtils.isEmpty(picExList) || picExList.size() < 14) {
                // 匹配的图片小于 14 张，说明可能因为网络加载慢，还没显示出来，那么延迟
                Util.sleep(50L);
                continue;
            }
            break;
        }

        if (CollectionUtils.isEmpty(picExList)) {
            return res;
        }

        for (DmDomains.PicEx picEx : picExList) {
            res.add(picEx.getPicName().replace(Constants.VIP_COIN_TEMPLATE_DIR, ""));
        }
        return res;
    }

    public static Integer getPosition(DmDomains.PicEx picEx) {
        for (int i = 0; i < CaptchaConstants.VIP_COIN_POSITION.length; i++) {
            int[] position = CaptchaConstants.VIP_COIN_POSITION[i];
            int startX = CaptchaConstants.VIP_COIN_SEARCH_RECT[0];
            int startY = CaptchaConstants.VIP_COIN_SEARCH_RECT[1];

            int x = startX + position[1] * (CaptchaConstants.VIP_COIN_GRID_LENGTH + CaptchaConstants.VIP_COIN_GAP);
            int y = startY + position[0] * (CaptchaConstants.VIP_COIN_GRID_LENGTH + CaptchaConstants.VIP_COIN_GAP);

            int[] p = {x, y};

            if (p[0] < picEx.getX() && p[0] + CaptchaConstants.VIP_COIN_GRID_LENGTH > picEx.getX() && p[1] < picEx.getY() && p[1] + CaptchaConstants.VIP_COIN_GRID_LENGTH > picEx.getY()) {
                return i;
            }
        }
        return null;
    }

    public static boolean stopAuction(int hwnd) {
        BaseType auctionType = getBaseType(hwnd, AutoVipCoinOpen.class);
        if (auctionType == null || !auctionType.isRunning()) {
            return false;
        }

        auctionType.stop();
        remove(hwnd, AutoVipCoinOpen.class);
        return true;
    }

    public static boolean startThread(int hwnd) {
        if (isRunning(hwnd, AutoVipCoinOpen.class)) {
            return false;
        }
        AutoVipCoinOpen instance = AutoVipCoinOpen.createInstance(hwnd, AutoVipCoinOpen.class, false);
        Runnable runnable = instance::loop;
        Thread thread = new Thread(runnable);
        instance.setThread(thread);
        thread.start();
        return true;
    }
}
