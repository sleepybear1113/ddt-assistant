package cn.sleepybear.ddtassistant.base;

import cn.sleepybear.ddtassistant.dm.Dm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class UserConfig extends BaseConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 4172087034701104358L;

    /**
     * 是否开启低余额提醒
     */
    private Boolean lowBalanceRemind;
    /**
     * 低余额提醒余额值
     */
    private Double lowBalanceNum;
    /**
     * 识屏时间间隔
     */
    private Long captureInterval;
    /**
     * 验证码出现后等待的毫秒
     */
    private Long captchaAppearDelay;
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
     * 副本大翻牌出现后的延迟毫秒
     */
    private Long pveFlopBonusAppearDelay;
    /**
     * 副本大翻牌出现后的键盘按键
     */
    private String keyPressAfterPveFlopBonus;
    /**
     * 副本大翻牌消失后延迟的毫秒
     */
    private Long pveFlopBonusDisappearDelay;
    /**
     * 副本大翻牌消失后的键盘按键
     */
    private String keyPressAfterPveFlopBonusDisappear;
    /**
     * 副本大翻牌是否截屏
     */
    private Boolean pveFlopBonusCapture;
    private Boolean pveFlopBonusCaptureMosaic;
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

    @Override
    @JsonIgnore
    public String getFileName() {
        return "基本配置.json";
    }

    @Override
    public UserConfig defaultConfig() {
        UserConfig userConfig = new UserConfig();
        userConfig.setDefaultChoiceAnswer("A");
        userConfig.setCaptchaAppearDelay(null);
        userConfig.setKeyPressAfterCaptchaShow(null);
        userConfig.setKeyPressAfterCaptchaDisappear(null);
        userConfig.setKeyPressDelayAfterCaptchaDisappear(null);
        userConfig.setPveFlopBonusAppearDelay(null);
        userConfig.setPveFlopBonusDisappearDelay(null);
        userConfig.setKeyPressAfterPveFlopBonus(null);
        userConfig.setKeyPressAfterPveFlopBonusDisappear(null);
        userConfig.setPveFlopBonusCapture(false);
        userConfig.setCaptureInterval(2000L);
        userConfig.setDetectNewWindowInterval(20000L);
        userConfig.setTimeout(20000L);
        userConfig.setLogPrintInterval(60000L);
        userConfig.setMouseMode(Dm.DEFAULT_MOUSE_MODE);
        userConfig.setKeyPadMode(Dm.DEFAULT_KEY_PAD_MODE);
        return userConfig;
    }

    public void setUserConfig(UserConfig userConfig) {
        BeanUtils.copyProperties(userConfig, this);
    }
}
