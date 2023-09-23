package cn.sleepybear.ddtassistant.controller;

import cn.sleepybear.ddtassistant.base.LoginConfig;
import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.annotation.WithoutLogin;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @WithoutLogin
    @RequestMapping("/login")
    public String login(String username, String password, HttpServletResponse response) {
        if (!settingConfig.enableLogin()) {
            response.addCookie(LoginConfig.loginExpireCookie());
            return "redirect:/";
        }

        LoginConfig loginConfig = settingConfig.getLoginConfig();
        if (!loginConfig.login(username, password)) {
            response.addCookie(LoginConfig.loginExpireCookie());
            return "redirect:/login.html?page=login-fail";
        }

        response.addCookie(loginConfig.loginSuccessCookie());
        return "redirect:/";
    }

    @WithoutLogin
    @RequestMapping("/logout")
    @ResponseBody
    public Boolean logout(HttpServletResponse response) {
        response.addCookie(LoginConfig.loginExpireCookie());
        return true;
    }
}
