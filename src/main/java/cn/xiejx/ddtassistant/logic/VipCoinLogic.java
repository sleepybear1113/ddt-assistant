package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.base.VipCoinConfig;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.type.BaseType;
import cn.xiejx.ddtassistant.type.vip.AutoVipCoinOpen;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/04/03 21:13
 */
@Component
public class VipCoinLogic {

    @Resource
    private VipCoinConfig vipCoinConfig;

    public VipCoinConfig getConfig() {
        return vipCoinConfig;
    }

    public void resetConfig() {
        vipCoinConfig = vipCoinConfig.defaultConfig();
        vipCoinConfig.save();
    }

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
            baseType.forceStop();
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

    public Boolean addNewVipCoinConfig(String newName) {
        vipCoinConfig.getVipCoinOneConfigList().add(VipCoinConfig.VipCoinOneConfig.defaultConfig(newName));
        vipCoinConfig.save();
        return true;
    }

    public Boolean updateOneConfig(VipCoinConfig.VipCoinOneConfig vipCoinOneConfig) {
        int index = -1;
        for (int i = 0; i < this.vipCoinConfig.getVipCoinOneConfigList().size(); i++) {
            VipCoinConfig.VipCoinOneConfig coinOneConfig = this.vipCoinConfig.getVipCoinOneConfigList().get(i);
            String name = vipCoinOneConfig.getName();
            if (coinOneConfig.getName().equals(name)) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            throw new FrontException("没有该配置名");
        }
        vipCoinOneConfig.setName(vipCoinOneConfig.getNewName());
        this.vipCoinConfig.getVipCoinOneConfigList().set(index, vipCoinOneConfig);
        this.vipCoinConfig.save();
        return true;
    }

    public Boolean deleteThisConfig(String name) {
        boolean b = this.vipCoinConfig.getVipCoinOneConfigList().removeIf(vipCoinOneConfig -> vipCoinOneConfig.getName().equals(name));
        if (!b) {
            throw new FrontException("没有该配置");
        }
        this.vipCoinConfig.save();
        return true;
    }
}
