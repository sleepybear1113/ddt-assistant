package cn.xiejx.ddtassistant.type.common;

import cn.xiejx.ddtassistant.dm.Dm;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.type.auction.AuctionConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * 仅会被别人传参调用
 *
 * @author sleepybear
 * @date 2022/06/10 11:59
 */
@Slf4j
public class Common {

    public boolean openMore(DmDdt dm) {
        if (!Common.findMorePic(dm, "", 0.6)) {
            log.info("[{}] 未找到 更多 按钮", dm.getHwnd());
            return false;
        }
        Common.clickMore(dm);
        return true;
    }


    /**
     * 是否在拍卖场
     *
     * @param dm           dm
     * @param templatePath templatePath
     * @param threshold    threshold
     * @return boolean
     */
    public static boolean findMorePic(Dm dm, String templatePath, double threshold) {
        int[] pic = dm.findPic(CommonConstants.MORE_FIND_RECT, templatePath, "010101", threshold, 0);
        if (pic == null) {
            return false;
        }
        return pic[0] > 0;
    }

    public static void clickMore(Dm dm) {
        dm.leftClick(CommonConstants.MORE_POINT[0], CommonConstants.MORE_POINT[1]);
    }
}
