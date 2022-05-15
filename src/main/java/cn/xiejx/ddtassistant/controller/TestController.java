package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.config.AppProperties;
import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.Dm;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
    private AppProperties appProperties;

    @RequestMapping("/test/click")
    public TestRes testClick(Integer x, Integer y) {
        log.info("程序版本号：{}，调试接口", appProperties.getAppVersion());
        TestRes testRes = new TestRes();
        Collection<Dm> dms = Constants.HWND_DM_MAP.values();
        if (CollectionUtils.isEmpty(dms)) {
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

        for (Dm dm : dms) {
            dm.leftClick(x, y, 100);
        }
        testRes.setMsg(String.format("一共%s线程，点击位置为 x = %s，y = %s", dms.size(), x, y));
        return testRes;
    }

    @RequestMapping("/test/invoke")
    public Object testInvoke(String method, String params) {
        log.info("程序版本号：{}，调试接口", appProperties.getAppVersion());
        TestRes testRes = new TestRes();
        Collection<Dm> dms = Constants.HWND_DM_MAP.values();
        if (CollectionUtils.isEmpty(dms)) {
            testRes.setMsg("没有正在运行的线程！");
            testRes.setSuccess(false);
            return testRes;
        }

        if (params == null) {
            for (Dm dm : dms) {
                GlobalVariable.THREAD_POOL.execute(() -> System.out.println(dm.invoke(method, (String) null)));
            }
        } else {
            String[] split = params.split(",");
            for (Dm dm : dms) {
                GlobalVariable.THREAD_POOL.execute(() -> System.out.println(dm.invoke(method, split)));
            }
        }

        testRes.setMsg("操作完成");
        return false;
    }

    @Data
    static class TestRes implements Serializable {

        private static final long serialVersionUID = 8119678605818501101L;

        private String msg;
        private Boolean success;
    }

}
