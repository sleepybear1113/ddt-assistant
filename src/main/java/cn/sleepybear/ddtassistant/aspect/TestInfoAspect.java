package cn.sleepybear.ddtassistant.aspect;

import cn.sleepybear.ddtassistant.config.AppProperties;
import cn.sleepybear.ddtassistant.annotation.LogAppInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

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
