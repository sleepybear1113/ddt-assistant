package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.annotation.LogAppInfo;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.logic.MonitorLogic;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.captcha.CaptchaUtil;
import cn.xiejx.ddtassistant.vo.MyString;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author sleepybear
 */
@RestController
@Slf4j
public class TestController {
    @Resource
    private DmDdt defaultDm;

    @LogAppInfo("键盘按键")
    @RequestMapping("/test/keyPress")
    public TestRes testKeyPress(String key, Long delay) {
        if (StringUtils.isBlank(key)) {
            return TestRes.buildFail("没有输入内容！");
        }
        Util.sleep(delay);
        defaultDm.keyPressChar(key);
        return TestRes.buildSuccess("按下按键：" + key);
    }

    @LogAppInfo("鼠标点击测试")
    @RequestMapping("/test/click")
    public TestRes testClick(Integer x, Integer y, Long delay) {
        TestRes testRes = new TestRes();
        Collection<DmDdt> dmDdts = GlobalVariable.DM_DDT_MAP.values();
        if (CollectionUtils.isEmpty(dmDdts)) {
            testRes.setMsg("没有正在运行的线程！");
            testRes.setSuccess(false);
            return testRes;
        }

        if (x == null || x < 0) {
            x = 100;
        }
        if (y == null || y < 0) {
            y = 100;
        }

        Util.sleep(delay);
        for (DmDdt dm : dmDdts) {
            dm.leftClick(x, y, 100);
        }
        testRes.setSuccess(true);
        testRes.setMsg(String.format("一共%s线程，点击位置为 x = %s，y = %s", dmDdts.size(), x, y));
        return testRes;
    }

    @LogAppInfo("查看缓存大小")
    @RequestMapping("/test/getCacherSize")
    public MyString getCacherSize() {
        int monitorTimeSize = MonitorLogic.TIME_CACHER.size();
        int monitorBonusSize = MonitorLogic.FLOP_BONUS_CACHER.size();
        int captchaSize = CaptchaUtil.CACHER.size();
        String s = "monitorTimeSize = " + monitorTimeSize + ", monitorBonusSize = " + monitorBonusSize + ", captchaSize = " + captchaSize;
        log.info(s);
        return new MyString(s);
    }

    @Data
    static class TestRes implements Serializable {

        private static final long serialVersionUID = 8119678605818501101L;

        private String msg;
        private Boolean success;

        public TestRes() {
        }

        public TestRes(String msg, Boolean success) {
            this.msg = msg;
            this.success = success;
        }

        public static TestRes buildSuccess(String msg) {
            return new TestRes(msg, true);

        }

        public static TestRes buildFail(String msg) {
            return new TestRes(msg, false);
        }
    }

}
