package cn.sleepybear.ddtassistant.config;

import cn.sleepybear.ddtassistant.annotation.WithoutLogin;
import cn.sleepybear.ddtassistant.base.LoginConfig;
import cn.sleepybear.ddtassistant.base.SettingConfig;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            WithoutLogin withoutLogin = ((HandlerMethod) handler).getMethod().getAnnotation(WithoutLogin.class);
            if (withoutLogin != null) {
                return true;
            }
        }

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

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, ModelAndView modelAndView) {
    }

    /**
     * 整个请求结束之后被调用，也就是在DispatchServlet渲染了对应的视图之后执行（主要用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler, Exception ex) {
    }
}
