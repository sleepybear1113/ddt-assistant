package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.base.CaptchaConfig;
import cn.sleepybear.ddtassistant.logic.CaptchaLogic;
import cn.sleepybear.ddtassistant.utils.captcha.BaseResponse;
import cn.sleepybear.ddtassistant.vo.BindResultVo;
import cn.sleepybear.ddtassistant.vo.StringRet;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sleepybear
 */
@RestController
public class CaptchaController {
    @Resource
    private CaptchaLogic captchaLogic;

    @RequestMapping("/captcha/test")
    public BaseResponse testCaptcha(Integer captchaInfoId) {
        return captchaLogic.testCaptcha(captchaInfoId);
    }

    @RequestMapping("/captcha/getAccountInfo")
    public StringRet getAccountInfo(Integer captchaInfoId) {
        return captchaLogic.getAccountInfo(captchaInfoId);
    }

    @RequestMapping("/captcha/addNewCaptcha")
    public Boolean addNewBind(Long delay) {
        return captchaLogic.addNewBind(delay);
    }

    @RequestMapping("/captcha/addAllCaptcha")
    public BindResultVo bindAll() {
        return captchaLogic.bindAll();
    }

    @RequestMapping("/captcha/unbindAllCaptcha")
    public BindResultVo unbindAll() {
        return captchaLogic.unbindAll();
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
