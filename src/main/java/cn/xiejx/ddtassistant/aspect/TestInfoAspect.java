package cn.xiejx.ddtassistant.aspect;

import cn.xiejx.ddtassistant.annotation.LogAppInfo;
import cn.xiejx.ddtassistant.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author sleepybear
 */
@Aspect
@Component
@Slf4j
public class TestInfoAspect {
    @Resource
    private AppProperties appProperties;

    @Around("@annotation(logAppInfo)")
    public Object aroundMethod(ProceedingJoinPoint pjd, LogAppInfo logAppInfo) throws Throwable {
        log.info("[{}] 测试方法 {}", appProperties.getAppVersion(), logAppInfo.value());
        return pjd.proceed();
    }
}
