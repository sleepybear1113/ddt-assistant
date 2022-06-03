package cn.xiejx.ddtassistant;

import cn.xiejx.ddtassistant.config.AppProperties;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
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
        System.out.println("==========================================");
        System.out.println("应用启动中............");
        System.out.println("如果启动后最后没有显示中文的输出，显示一串英文代码，那么可能是大漠插件没有注册，请打开“大漠插件注册”文件夹进行注册。");
        System.out.println("==========================================");

        SpringApplicationBuilder builder = new SpringApplicationBuilder(DdtAssistantApplication.class);
        ConfigurableApplicationContext context = builder.headless(false).run(args);

        SpringContextUtil.setApplicationContext(context);

        Environment environment = context.getBean(Environment.class);
        AppProperties appProperties = context.getBean(AppProperties.class);
        String port = environment.getProperty("server.port");

        System.out.println("==========================================");
        System.out.println("以下是一些程序相关介绍：");
        System.out.println("如果你会使用 Java，可以在当前文件夹下找到 jar 包，，直接使用 java -jar ddt-assistant.jar 来直接运行，需要使用 Java 8 x86。");
        System.out.println("该项目基于 Spring Boot 2.x 构建，使用 com 调用大漠插件执行相关窗口绑定和操作");
        System.out.println("==================================================");
        System.out.println("该程序的路径结构如下：");
        System.out.println("captcha 文件夹路径下是识别到验证码的相关截图，文件夹按照每天新建。图片命名格式为[句柄号-时间]。");
        System.out.println("img 文件夹路径下是相关自带图片的资源，template-bright.bmp 和 template-dark.bmp 两个文件请不要更改删除移动。");
        System.out.println("logs 文件夹路径下是项目的日志文件。");
        System.out.println("jre 文件夹路径下是运行 Java 所需的必要环境。如果你懂 Java 那么可以不需要该环境自行配置。");
        System.out.println("user-config.json 文件为用户的个人配置文件，里面包含明文用户名密码、相关找图的时间配置等。");
        System.out.println("ddt-assistant.jar 则为该 Java 的主程序，后续若有更新，仅更新该文件。");
        System.out.println("==================================================");
        System.out.println("复制本程序的时候，请不要复制 captcha、logs、user-config.json 文件夹！！！");
        System.out.println("==================================================");
        System.out.println("如果这个对你有帮助，那么可以请我吃个冰棍，喝个可乐，喝杯奶茶，下面的网址用浏览器打开。");
        System.out.printf("微信：http://127.0.0.1:%s/images/wx.jpg%n", port);
        System.out.printf("支付宝：http://127.0.0.1:%s/images/zfb.jpg%n", port);
        System.out.println("交流群：QQ：879596615。");
        System.out.println("作者：sleepybear，保留所有 Java 程序的版权。");
        System.out.println("==================================================");
        System.out.printf("项目启动完成，版本号：%s，请保持持续运行。%n", appProperties.getAppVersion());
        System.err.println("若想复制这个控制台命令行的内容，请勿使用 Ctrl + C 快捷键。选择文本之后直接点击右键即可复制。Ctrl + C 在命令行中有其他默认效果。");
        System.out.printf("请用浏览器打开下述网址进行相关功能开启：http://127.0.0.1:%s/index.html%n", port);
        System.out.println("浏览器请不要用糖果、IE 等上古浏览器，使用 Edge、QQ 浏览器、火狐、Chrome、2345、360 等主流浏览网页的浏览器。");
        System.err.println("若要停止可以直接关闭窗口或者使用 Ctrl + C 发送中止指令。");
    }

}
