package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.logic.CaptchaLogic;
import cn.xiejx.ddtassistant.utils.tj.TjResponse;
import cn.xiejx.ddtassistant.vo.BindResultVo;
import cn.xiejx.ddtassistant.vo.StringRet;
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
    public TjResponse testCaptcha() {
        return captchaLogic.testCaptcha();
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
}
