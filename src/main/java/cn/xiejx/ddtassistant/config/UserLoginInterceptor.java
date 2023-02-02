package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.base.LoginConfig;
import cn.xiejx.ddtassistant.base.SettingConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/02 12:22
 */
@Component
public class UserLoginInterceptor implements HandlerInterceptor {

    @Resource
    private SettingConfig settingConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!settingConfig.enableLogin()) {
            return true;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            response.sendRedirect("/login.html");
            return false;
        }

        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            String value = cookie.getValue();

            if (name.equals(LoginConfig.COOKIE_NAME)) {
                if (settingConfig.getLoginConfig().login(value)) {
                    return true;
                }
            }
        }

        response.sendRedirect("/login.html");
        return false;
    }

    /***
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /***
     * 整个请求结束之后被调用，也就是在DispatchServlet渲染了对应的视图之后执行（主要用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
