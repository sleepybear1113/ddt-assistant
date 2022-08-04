package cn.xiejx.ddtassistant;

import cn.xiejx.ddtassistant.config.AppProperties;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author sleepybear
 */
@SpringBootApplication
public class DdtAssistantApplication {

    public static void main(String[] args) {
        System.setProperty("com.jacob.autogc", "true");

        System.out.println("==========================================");
        System.out.println("应用启动中............");
        System.out.println("==========================================");
        try {
            DmDdt dmDdt = DmDdt.createInstance(null);
            String version = dmDdt.getVersion();
            System.out.println("大漠插件版本号： " + version);
            dmDdt.release();
            if (StringUtils.isBlank(version)) {
                throw new RuntimeException("未能获取大漠版本号");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("启动失败！大漠插件未找到，请注册！");
        }

        SpringApplicationBuilder builder = new SpringApplicationBuilder(DdtAssistantApplication.class);
        ConfigurableApplicationContext context = builder.headless(false).run(args);

        SpringContextUtil.setApplicationContext(context);

        Environment environment = context.getBean(Environment.class);
        AppProperties appProperties = context.getBean(AppProperties.class);
        String port = environment.getProperty("server.port");

        System.out.println("==================================================");
        System.out.println("交流群：QQ：879596615。");
        System.out.println("作者：sleepybear，保留所有 Java 程序的版权。");
        System.out.println("==================================================");
        System.out.printf("项目启动完成，版本号：%s，请保持持续运行。%n", appProperties.getAppVersion());
        System.out.println("==================================================");
        System.out.println("若想复制这个控制台命令行的内容，请勿使用 Ctrl + C 快捷键。选择文本之后直接点击右键即可复制。Ctrl + C 在命令行中有其他默认效果（中止程序运行）。");
        System.out.printf("请用浏览器打开下述网址进行相关功能开启：http://127.0.0.1:%s%n", port);
        System.out.println("浏览器请不要用糖果、IE 等上古浏览器，使用 Edge、QQ 浏览器、火狐、Chrome、2345、360 等主流浏览网页的浏览器。");
        System.out.println("若要停止可以直接关闭窗口或者使用 Ctrl + C 发送中止指令。");
        System.out.println("==================以下为运行日志======================");
    }

}
