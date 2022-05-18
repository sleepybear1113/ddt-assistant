package cn.xiejx.ddtassistant.config;

import cn.xiejx.ddtassistant.utils.Util;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sleepybear
 */
@Data
public class UserConfig implements Serializable {
    private static final long serialVersionUID = 4172087034701104358L;

    public static final String PATH = "user-config.json";

    public static final long DEFAULT_CAPTURE_INTERVAL = 500L;
    public static final long DEFAULT_TIMEOUT = 20000L;
    public static final String DEFAULT_MOUSE_MODE = "windows";
    public static final String DEFAULT_KEY_PAD_MODE = "windows";

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

    private String mouseMode;
    private String keyPadMode;

    private String extraPorts;

    public static UserConfig defaultConfig() {
        UserConfig userConfig = new UserConfig();
        userConfig.setDefaultChoiceAnswer("A");
        userConfig.setKeyPressAfterCaptchaShow("F7");
        userConfig.setKeyPressAfterCaptchaDisappear("F7");
        userConfig.setSoftId("3b995690b1794ff08bad1abb88a3e451");
        userConfig.setCaptureInterval(DEFAULT_CAPTURE_INTERVAL);
        userConfig.setDetectNewWindowInterval(null);
        userConfig.setTimeout(DEFAULT_TIMEOUT);
        userConfig.setLogPrintInterval(15000L);
        userConfig.setKeyPressDelayAfterCaptchaDisappear(5000L);
        userConfig.setMouseMode(DEFAULT_MOUSE_MODE);
        userConfig.setKeyPadMode(DEFAULT_KEY_PAD_MODE);
        userConfig.setExtraPorts(null);
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
        this.mouseMode = userConfig.mouseMode;
        this.keyPadMode = userConfig.keyPadMode;
        this.extraPorts = userConfig.extraPorts;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSoftId() {
        return softId;
    }

    public void setSoftId(String softId) {
        this.softId = softId;
    }

    public Long getCaptureInterval() {
        if (captureInterval == null || captureInterval <= 0) {
            captureInterval = DEFAULT_CAPTURE_INTERVAL;
        }
        return captureInterval;
    }

    public void setCaptureInterval(Long captureInterval) {
        this.captureInterval = captureInterval;
    }

    public String getKeyPressAfterCaptchaShow() {
        return keyPressAfterCaptchaShow;
    }

    public void setKeyPressAfterCaptchaShow(String keyPressAfterCaptchaShow) {
        this.keyPressAfterCaptchaShow = keyPressAfterCaptchaShow;
    }

    public String getKeyPressAfterCaptchaDisappear() {
        return keyPressAfterCaptchaDisappear;
    }

    public void setKeyPressAfterCaptchaDisappear(String keyPressAfterCaptchaDisappear) {
        this.keyPressAfterCaptchaDisappear = keyPressAfterCaptchaDisappear;
    }

    public Long getKeyPressDelayAfterCaptchaDisappear() {
        return keyPressDelayAfterCaptchaDisappear;
    }

    public void setKeyPressDelayAfterCaptchaDisappear(Long keyPressDelayAfterCaptchaDisappear) {
        this.keyPressDelayAfterCaptchaDisappear = keyPressDelayAfterCaptchaDisappear;
    }

    public String getDefaultChoiceAnswer() {
        return defaultChoiceAnswer;
    }

    public void setDefaultChoiceAnswer(String defaultChoiceAnswer) {
        this.defaultChoiceAnswer = defaultChoiceAnswer;
    }

    public Long getTimeout() {
        if (timeout == null || timeout <= 0) {
            timeout = DEFAULT_TIMEOUT;
        }
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Long getLogPrintInterval() {
        return logPrintInterval;
    }

    public void setLogPrintInterval(Long logPrintInterval) {
        this.logPrintInterval = logPrintInterval;
    }

    public Long getDetectNewWindowInterval() {
        return detectNewWindowInterval;
    }

    public void setDetectNewWindowInterval(Long detectNewWindowInterval) {
        this.detectNewWindowInterval = detectNewWindowInterval;
    }

    public String getMouseMode() {
        if (StringUtils.isBlank(mouseMode)) {
            mouseMode = DEFAULT_MOUSE_MODE;
        }
        return mouseMode;
    }

    public void setMouseMode(String mouseMode) {
        this.mouseMode = mouseMode;
    }

    public String getKeyPadMode() {
        if (StringUtils.isBlank(keyPadMode)) {
            keyPadMode = DEFAULT_KEY_PAD_MODE;
        }
        return keyPadMode;
    }

    public void setKeyPadMode(String keyPadMode) {
        this.keyPadMode = keyPadMode;
    }

    public String getExtraPorts() {
        return extraPorts;
    }

    public List<Integer> getPortArray() {
        if (StringUtils.isBlank(this.extraPorts)) {
            return null;
        }

        List<Integer> res = new ArrayList<>();
        String[] ps = extraPorts.replace("，", ",").split(",");
        for (String p : ps) {
            if (StringUtils.isNumeric(p)) {
                res.add(Integer.valueOf(p));
            }
        }

        return res;
    }

    public void setExtraPorts(String extraPorts) {
        if (StringUtils.isBlank(extraPorts)) {
            this.extraPorts = null;
            return;
        }

        StringBuilder s = new StringBuilder();
        String[] ps = extraPorts.replace("，", ",").split(",");
        for (String p : ps) {
            if (StringUtils.isNumeric(p)) {
                s.append(p).append(",");
            }
        }
        this.extraPorts = s.toString();
    }
}
