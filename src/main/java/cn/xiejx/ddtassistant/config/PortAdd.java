package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.base.UserConfig;
import cn.xiejx.ddtassistant.utils.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sleepybear
 */
@Configuration
@Slf4j
public class PortAdd {

    @Resource
    private UserConfig userConfig;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        List<Connector> connectors = this.additionalConnector();
        if (CollectionUtils.isEmpty(connectors)) {
            return tomcat;
        }
        for (Connector connector : connectors) {
            tomcat.addAdditionalTomcatConnectors(connector);
        }
        return tomcat;
    }

    private List<Connector> additionalConnector() {
        List<Integer> ports = userConfig.getPortArray();
        if (CollectionUtils.isEmpty(ports)) {
            return null;
        }

        if (!isSelf()) {
            return null;
        }

        // 端口按,分割
        List<Connector> result = new ArrayList<>();
        for (int port : ports) {
            if (port < 0 || port > 65535) {
                log.error("======================================");
                log.error("======================================");
                log.error("端口开启失败，端口号：{} 不在 [0, 65535]之间，输入错误，无法开启", port);
                log.error("======================================");
                log.error("======================================");
                continue;
            }
            if (!Util.portAvailable(port)) {
                log.error("======================================");
                log.error("======================================");
                log.error("端口开启失败，端口号：{} 被占用，无法开启", port);
                log.error("======================================");
                log.error("======================================");
                continue;
            }
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setScheme("http");
            connector.setPort(port);
            result.add(connector);
        }
        return result;
    }

    public static boolean isSelf() {
        File file = new File("sleepybear.txt");
        if (!file.exists()) {
            return false;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String s = bufferedReader.readLine();
            if (StringUtils.isBlank(s)) {
                return false;
            }
            return s.contains("1113");
        } catch (IOException ignored) {
            return false;
        }
    }
}