package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.base.VipCoinConfig;
import cn.xiejx.ddtassistant.logic.VipCoinLogic;
import org.springframework.web.bind.annotation.RequestBody;
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

    @RequestMapping("/vipCoin/getConfig")
    public VipCoinConfig getConfig() {
        return vipCoinLogic.getConfig();
    }

    @RequestMapping("/vipCoin/addNewVipCoinConfig")
    public Boolean addNewVipCoinConfig(String newName) {
        return vipCoinLogic.addNewVipCoinConfig(newName);
    }

    @RequestMapping("/vipCoin/updateOneConfig")
    public Boolean updateOneConfig(@RequestBody VipCoinConfig.VipCoinOneConfig config) {
        return vipCoinLogic.updateOneConfig(config);
    }

    @RequestMapping("/vipCoin/deleteThisConfig")
    public Boolean deleteThisConfig(String name) {
        return vipCoinLogic.deleteThisConfig(name);
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
        vipCoinLogic.suspend(hwnd);
    }

    @RequestMapping("/vipCoin/resume")
    public void resume(Integer hwnd) {
        vipCoinLogic.resume(hwnd);
    }
}
