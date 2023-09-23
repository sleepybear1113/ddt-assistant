package cn.sleepybear.ddtassistant.config;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.web.server.PortInUseException;

/**
 * 启动失败端口占用的提示
 *
 * @author sleepybear
 * @date 2022/12/17 23:39
 */
public class PortFailureAnalyzer extends AbstractFailureAnalyzer<PortInUseException> {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, PortInUseException cause) {
        return new FailureAnalysis(cause.getMessage(), String.format("程序启动的端口 %s 已被占用，请关闭另外的正在运行的程序！", cause.getPort()), cause);
    }
}
