package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.utils.Util;
import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
public class UserConfig implements Serializable {
    private static final long serialVersionUID = 4172087034701104358L;
    public static final String RANDOM_ANSWER_KEY = "X";

    public static final String PATH = "user-config.json";

    private String username;
    private String password;
    /**
     * 推荐码
     */
    private String softId;
    /**
     * 识屏时间间隔
     */
    private Long captureInterval;
    /**
     * 出现验证码后按下的按键
     */
    private String keyPressAfterCaptchaShow;
    /**
     * 验证码消失后按下的按键
     */
    private String keyPressAfterCaptchaDisappear;
    /**
     * 验证码消失后按下的按键的延时
     */
    private Long keyPressDelayAfterCaptchaDisappear;
    /**
     * 默认选项
     */
    private String defaultChoiceAnswer;
    /**
     * 验证码超时时间
     */
    private Long timeout;
    /**
     * 控制台日志输出间隔，-1 为不输出
     */
    private Long logPrintInterval;
    /**
     * 每隔时间检测有没有新的号上线，-1 为不检测
     */
    private Long detectNewWindowInterval;

    public static UserConfig defaultConfig() {
        UserConfig userConfig = new UserConfig();
        userConfig.setDefaultChoiceAnswer("A");
        userConfig.setKeyPressAfterCaptchaShow("F7");
        userConfig.setKeyPressAfterCaptchaDisappear("F7");
        userConfig.setSoftId("3b995690b1794ff08bad1abb88a3e451");
        userConfig.setCaptureInterval(500L);
        userConfig.setDetectNewWindowInterval(null);
        userConfig.setTimeout(20000L);
        userConfig.setLogPrintInterval(15000L);
        userConfig.setKeyPressDelayAfterCaptchaDisappear(5000L);
        return userConfig;
    }

    public void setUserConfig(UserConfig userConfig) {
        this.username = userConfig.getUsername();
        this.password = userConfig.getPassword();
        this.softId = userConfig.getSoftId();
        this.keyPressAfterCaptchaDisappear = userConfig.getKeyPressAfterCaptchaDisappear();
        this.keyPressAfterCaptchaShow = userConfig.getKeyPressAfterCaptchaShow();
        this.keyPressDelayAfterCaptchaDisappear = userConfig.getKeyPressDelayAfterCaptchaDisappear();
        this.captureInterval = userConfig.getCaptureInterval();
        this.detectNewWindowInterval = userConfig.getDetectNewWindowInterval();
        this.logPrintInterval = userConfig.getLogPrintInterval();
        this.timeout = userConfig.getTimeout();
        this.defaultChoiceAnswer = userConfig.getDefaultChoiceAnswer();
    }

    public static UserConfig readFromFile() {
        UserConfig defaultConfig = UserConfig.defaultConfig();
        if (!new File(PATH).exists()) {
            return defaultConfig;
        }
        String s = Util.readFile(PATH);
        if (s == null || s.length() == 0) {
            return defaultConfig;
        }

        try {
            return JSON.parseObject(s, UserConfig.class);
        } catch (Exception e) {
            return defaultConfig;
        }
    }

    public void writeFile() {
        Util.writeFile(JSON.toJSONString(this), PATH);
    }

    public boolean validUserInfo() {
        return this.username != null && this.username.length() > 0 & this.password != null && this.password.length() > 0;
    }

    public boolean invalidUserInfo() {
        return !validUserInfo();
    }
}
