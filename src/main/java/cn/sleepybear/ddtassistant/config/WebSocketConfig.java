package cn.sleepybear.ddtassistant.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/11 16:26
 */
@Slf4j
@Configuration
public class WebSocketConfig {

    /**
     * 服务器节点
     * <p>
     * 如果使用独立的servlet容器，而不是直接使用springboot 的内置容器，就不要注入ServerEndPoint
     *
     * @return ServerEndpointExporter
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        log.info("启动 WebSocket ...");
        return new ServerEndpointExporter();
    }
}