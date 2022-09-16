package cn.xiejx.ddtassistant.type.captcha;

import cn.xiejx.ddtassistant.utils.captcha.CaptchaChoiceEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 20:45
 */
@Data
@NoArgsConstructor
public class CaptchaInfo {
    private CaptchaChoiceEnum captchaChoiceEnum;
    private long lastCaptchaTime;
    private String lastCaptchaId;
    private String lastCaptchaFilePath;

    public CaptchaInfo(CaptchaChoiceEnum captchaChoiceEnum, long lastCaptchaTime, String lastCaptchaId, String lastCaptchaFilePath) {
        this.captchaChoiceEnum = captchaChoiceEnum;
        this.lastCaptchaTime = lastCaptchaTime;
        this.lastCaptchaId = lastCaptchaId;
        this.lastCaptchaFilePath = lastCaptchaFilePath;
    }

    public void clear() {
        lastCaptchaId = null;
        lastCaptchaFilePath = null;
    }
}
