package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.advice.ResultCode;
import cn.sleepybear.ddtassistant.annotation.LogAppInfo;
import cn.sleepybear.ddtassistant.constant.Constants;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.dm.DmDdt;
import cn.sleepybear.ddtassistant.exception.FrontException;
import cn.sleepybear.ddtassistant.logic.MonitorLogic;
import cn.sleepybear.ddtassistant.utils.Util;
import cn.sleepybear.ddtassistant.utils.captcha.CaptchaUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serial;
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
    public ResultCode<String> getCacherSize() {
        int monitorTimeSize = MonitorLogic.TIME_CACHER.size();
        int monitorBonusSize = MonitorLogic.FLOP_BONUS_CACHER.size();
        int captchaSize = CaptchaUtil.CACHER.size();
        String s = "monitorTimeSize = " + monitorTimeSize + ", monitorBonusSize = " + monitorBonusSize + ", captchaSize = " + captchaSize;
        log.info(s);
        return ResultCode.buildResult(s);
    }

    @LogAppInfo("测试截图")
    @RequestMapping("/test/capture")
    public ResultCode<String> capture(Integer hwnd) {
        if (hwnd == null) {
            throw new FrontException("没有输入句柄！");
        }
        DmDdt dmDdt = GlobalVariable.DM_DDT_MAP.get(hwnd);
        if (dmDdt == null) {
            throw new FrontException("没有找到句柄为[" + hwnd + "]的窗口！");
        }
        dmDdt.bind();
        long l = System.currentTimeMillis();
        dmDdt.captureFullGamePic(Constants.TEST_CAPTURE_DIR + l + ".bmp");
        return ResultCode.buildResult(String.valueOf(l));
    }

    @Data
    public static class TestRes implements Serializable {

        @Serial
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
