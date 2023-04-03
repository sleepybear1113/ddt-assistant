package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.logic.VipCoinLogic;
import cn.xiejx.ddtassistant.type.BaseType;
import cn.xiejx.ddtassistant.type.vip.AutoVipCoinOpen;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/04/03 21:12
 */
@RestController
public class VipCoinController {
    @Resource
    private VipCoinLogic vipCoinLogic;

    @RequestMapping("/vipCoin/start")
    public Boolean startVipCoinLoop(Integer hwnd) {
        vipCoinLogic.start(hwnd);
        return true;
    }

    @RequestMapping("/vipCoin/stopAll")
    public Boolean stopAll() {
        vipCoinLogic.stopAll();
        return true;
    }

    @RequestMapping("/vipCoin/stop")
    public void stop(Integer hwnd) {
        vipCoinLogic.stop(hwnd);
    }

    @RequestMapping("/vipCoin/suspend")
    public void suspend(Integer hwnd) {
        GlobalVariable.THREAD_POOL.execute(() -> vipCoinLogic.suspend(hwnd));
    }

    @RequestMapping("/vipCoin/resume")
    public void resume(Integer hwnd) {
        vipCoinLogic.resume(hwnd);
    }
}
