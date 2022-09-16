package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.base.CaptchaConfig;
import cn.xiejx.ddtassistant.logic.CaptchaLogic;
import cn.xiejx.ddtassistant.utils.captcha.BaseResponse;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjResponse;
import cn.xiejx.ddtassistant.vo.BindResultVo;
import cn.xiejx.ddtassistant.vo.StringRet;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sleepybear
 */
@RestController
public class CaptchaController {
    @Resource
    private CaptchaLogic captchaLogic;

    @RequestMapping("/captcha/test")
    public BaseResponse testCaptcha(Integer captchaWay) {
        return captchaLogic.testCaptcha(captchaWay);
    }

    @RequestMapping("/captcha/getTjAccountInfo")
    public StringRet getTjAccountInfo() {
        return captchaLogic.getTjAccountInfo();
    }

    @RequestMapping("/captcha/addNewCaptcha")
    public Boolean addNewBind(Long delay) {
        return captchaLogic.addNewBind(delay);
    }

    @RequestMapping("/captcha/addAllCaptcha")
    public BindResultVo bindAll() {
        return captchaLogic.bindAll();
    }

    @RequestMapping("/captcha/captureCaptchaSampleRegion")
    public StringRet captureCaptchaSampleRegion() {
        return captchaLogic.captureCaptchaSampleRegion();
    }

    @RequestMapping("/captchaConfig/get")
    public CaptchaConfig getCaptchaConfig() {
        return captchaLogic.getCaptchaConfig();
    }

    @RequestMapping("/captchaConfig/update")
    public Boolean updateCaptchaConfig(@RequestBody CaptchaConfig captchaConfig) {
        return captchaLogic.updateCaptchaConfig(captchaConfig);
    }

    @RequestMapping("/captchaConfig/reset")
    public CaptchaConfig resetCaptchaConfig() {
        return captchaLogic.resetCaptchaConfig();
    }
}
