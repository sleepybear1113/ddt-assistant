package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.base.OfflineDetectionConfig;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.exception.FrontException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 02:19
 */
@Component
public class OfflineDetectionLogic {
    public static final String OFFLINE_DIALOG_CLASS_NAME = "Static";

    public static final String OFFLINE_DIALOG_MSG = "对不起，断线了请刷新页面，重新登录！";
    public static final String OFFSITE_LOGIN_MSG = "您的帐号在别处登陆...";
    @Resource
    private DmDdt defaultDm;

    @Resource
    private OfflineDetectionConfig offlineDetectionConfig;

    public OfflineDetectionConfig get() {
        return this.offlineDetectionConfig;
    }

    public Boolean update(OfflineDetectionConfig offlineDetectionConfig) {
        if (offlineDetectionConfig == null) {
            throw new FrontException("参数为空");
        }

        this.offlineDetectionConfig.update(offlineDetectionConfig);
        this.offlineDetectionConfig.save();
        return true;
    }

    public List<Integer> getOfflineAlerts() {
        return getAlerts(OFFLINE_DIALOG_MSG);
    }

    public List<Integer> getOffsiteAlerts() {
        return getAlerts(OFFSITE_LOGIN_MSG);
    }

    public List<Integer> getAlerts(String s) {
        ArrayList<Integer> list = new ArrayList<>();

        int[] offsiteHwnds = defaultDm.enumWindow(0, s, OFFLINE_DIALOG_CLASS_NAME, 3);
        if (offsiteHwnds != null) {
            for (int hwnd : offsiteHwnds) {
                list.add(hwnd);
            }
        }
        return list;
    }
}
