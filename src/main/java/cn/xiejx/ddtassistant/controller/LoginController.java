package cn.xiejx.ddtassistant.controller;

import cn.xiejx.ddtassistant.base.LoginConfig;
import cn.xiejx.ddtassistant.base.SettingConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/02 14:19
 */
@Controller
public class LoginController {
    @Resource
    private SettingConfig settingConfig;

    @RequestMapping("/login")
    public String login(String username, String password, HttpServletResponse response) {
        if (!settingConfig.enableLogin()) {
            response.addCookie(LoginConfig.loginExpireCookie());
            return "redirect:/";
        }

        LoginConfig loginConfig = settingConfig.getLoginConfig();
        if (!loginConfig.login(username, password)) {
            response.addCookie(LoginConfig.loginExpireCookie());
            return "redirect:login.html?page=login-fail";
        }

        response.addCookie(loginConfig.loginSuccessCookie());
        return "redirect:/";
    }

    @RequestMapping("/logout")
    @ResponseBody
    public Boolean logout(HttpServletResponse response) {
        response.addCookie(LoginConfig.loginExpireCookie());
        return true;
    }
}
