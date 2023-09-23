package cn.sleepybear.ddtassistant.type.common;

import cn.sleepybear.ddtassistant.dm.Dm;
import cn.sleepybear.ddtassistant.dm.DmConstants;
import cn.sleepybear.ddtassistant.type.BaseType;
import lombok.extern.slf4j.Slf4j;

/**
 * 仅会被别人传参调用
 *
 * @author sleepybear
 * @date 2022/06/10 11:59
 */
@Slf4j
public class Common {

    public boolean openMore(BaseType baseType) {
        if (!Common.findMorePic(baseType.getDm(), "", 0.6)) {
            log.info("[{}] 未找到 更多 按钮", baseType.getHwnd());
            return false;
        }
        Common.clickMore(baseType.getDm());
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
        int[] pic = dm.findPic(CommonConstants.MORE_FIND_RECT, templatePath, "010101", threshold, DmConstants.SearchWay.LEFT2RIGHT_UP2DOWN);
        if (pic == null) {
            return false;
        }
        return pic[0] > 0;
    }

    public static void clickMore(Dm dm) {
        dm.leftClick(CommonConstants.MORE_POINT[0], CommonConstants.MORE_POINT[1]);
    }
}
