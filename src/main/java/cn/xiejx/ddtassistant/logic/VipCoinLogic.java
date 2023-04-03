package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.type.BaseType;
import cn.xiejx.ddtassistant.type.vip.AutoVipCoinOpen;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/04/03 21:13
 */
@Component
public class VipCoinLogic {

    public Boolean start(int hwnd) {
        boolean running = AutoVipCoinOpen.isRunning(hwnd, AutoVipCoinOpen.class);
        if (running) {
            return false;
        }
        return AutoVipCoinOpen.startThread(hwnd);
    }

    public Boolean stopAll() {
        List<AutoVipCoinOpen> baseTypes = BaseType.getBaseTypes(AutoVipCoinOpen.class);
        if (CollectionUtils.isEmpty(baseTypes)) {
            return false;
        }

        for (AutoVipCoinOpen baseType : baseTypes) {
            baseType.stop();
        }
        return true;
    }

    public void stop(Integer hwnd) {
        BaseType baseType = BaseType.getBaseType(hwnd, AutoVipCoinOpen.class);
        baseType.forceStop();
    }

    public void suspend(Integer hwnd) {
        BaseType baseType = BaseType.getBaseType(hwnd, AutoVipCoinOpen.class);
        baseType.suspend();
    }

    public void resume(Integer hwnd) {
        BaseType baseType = BaseType.getBaseType(hwnd, AutoVipCoinOpen.class);
        baseType.resume();
    }
}
