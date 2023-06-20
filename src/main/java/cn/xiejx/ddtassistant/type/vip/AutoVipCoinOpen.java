package cn.xiejx.ddtassistant.type.vip;

import cn.xiejx.ddtassistant.base.VipCoinConfig;
import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.dm.DmDomains;
import cn.xiejx.ddtassistant.type.BaseType;
import cn.xiejx.ddtassistant.type.captcha.CaptchaConstants;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class AutoVipCoinOpen extends BaseType {

    private static final long serialVersionUID = 3496760745860466985L;

    private int[] vipCoinPosition = null;

    public static List<String> templatePathList = new ArrayList<>();

    private String configName;

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

        // VIP 币配置文件
        VipCoinConfig vipCoinConfig = SpringContextUtil.getBean(VipCoinConfig.class);
        List<VipCoinConfig.VipCoinOneConfig> vipCoinOneConfigList = vipCoinConfig.getVipCoinOneConfigList();

        getDm().bind();
        setToRunningStatus();

        // 激活窗口
        getDm().clickCorner();
        getDm().clickCorner();
        Util.sleep(100L);

        vipCoinPosition = null;
        int count = 0;
        // 开始刷盘子
        while (true) {
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

            if (!hasOpenVipBoard()) {
                // 双击 VIP 币打开盘子
                openVipCoin();
            }

            // 获取配置文件
            VipCoinConfig.VipCoinOneConfig vipCoinOneConfig = null;
            for (VipCoinConfig.VipCoinOneConfig coinOneConfig : vipCoinOneConfigList) {
                if (coinOneConfig.getName().equals(this.configName)) {
                    vipCoinOneConfig = coinOneConfig;
                }
            }
            if (vipCoinOneConfig == null) {
                log.info("[{}] 没有选择可用的配置文件", hwnd);
                continue;
            }

            // 获取盘子物品
            List<String> matchThingList = match();

            log.info("[{}] 匹配到数量 {}", hwnd, CollectionUtils.size(matchThingList));

            boolean matched = matchConfig(vipCoinOneConfig, matchThingList);
            if (!matched) {
                closeVipB();
            } else {

            }


        }

        setToNotRunningStatus();
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

    public boolean matchConfig(VipCoinConfig.VipCoinOneConfig vipCoinOneConfig, List<String> matchThingList) {
        List<VipCoinConfig.VipCoinCondition> vipCoinConditionList = vipCoinOneConfig.getVipCoinConditionList();
        if (CollectionUtils.isEmpty(vipCoinConditionList)) {
            return false;
        }

        List<VipCoinConfig.VipCoinThingScore> vipCoinThingScoreList = vipCoinOneConfig.getVipCoinThingScoreList();
        if (CollectionUtils.isEmpty(vipCoinThingScoreList)) {
            return false;
        }

        // 匹配到的所有物品，包括未启用的
        List<VipCoinConfig.VipCoinThingScore> matchedThingScoreList = new ArrayList<>();
        for (VipCoinConfig.VipCoinThingScore vipCoinThingScore : vipCoinThingScoreList) {
            for (String s : matchThingList) {
                if (vipCoinThingScore.getName().contains(s)) {
                    matchedThingScoreList.add(vipCoinThingScore);
                }
            }
        }

        for (VipCoinConfig.VipCoinCondition vipCoinCondition : vipCoinConditionList) {
            List<VipCoinConfig.SingleCondition> singleConditionList = vipCoinCondition.getSingleConditionList();
            if (CollectionUtils.isEmpty(singleConditionList)) {
                continue;
            }

            int totalMatchSize = singleConditionList.size();
            int matchSize = 0;
            for (VipCoinConfig.SingleCondition singleCondition : singleConditionList) {
                // 开始遍历每个小项条件，需要全部符合才可以

                int matchCount = 0;
                for (VipCoinConfig.VipCoinThingScore vipCoinThingScore : matchedThingScoreList) {
                    // 遍历匹配到的列表
                    if (vipCoinThingScore.matchName(singleCondition.getName())) {
                        // 如果名字匹配了，那么先增加匹配的数量
                        matchCount++;
                    }
                }

                // 是否符合数量匹配条件
                boolean numMatched = false;
                // 设置的最低匹配数量
                Integer num = singleCondition.getNum();
                num = num == null ? 1 : num;
                switch (singleCondition.getCompareType()) {
                    case "大于":
                        numMatched = matchCount > num;
                        break;
                    case "大于等于":
                    case "包含":
                        numMatched = matchCount >= num;
                        break;
                    case "等于":
                        numMatched = matchCount == num;
                        break;
                    case "小于等于":
                        numMatched = matchCount <= num;
                        break;
                    case "小于":
                        numMatched = matchCount < num;
                        break;
                }

                if (!Boolean.TRUE.equals(singleCondition.getContains())) {
                    // 如果不是 包含 条件
                    numMatched = !numMatched;
                }
                if (numMatched) {
                    matchSize++;
                }
            }

            if (matchSize >= totalMatchSize) {
                return true;
            }
        }

        return false;
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

        auctionType.forceStop();
        remove(hwnd, AutoVipCoinOpen.class);
        return true;
    }

    public static boolean startThread(int hwnd, String name) {
        AutoVipCoinOpen instance = AutoVipCoinOpen.createInstance(hwnd, AutoVipCoinOpen.class, false);
        instance.setConfigName(name);
        if (instance.isRunning()) {
            return false;
        }
        instance.startThread(instance::loop);
        return true;
    }
}
