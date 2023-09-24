package cn.sleepybear.ddtassistant.type.captcha;

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
public class CaptchaImgInfo {
    private Integer captchaId;
    private Integer captchaType;
    private String captchaName;
    private long lastCaptchaTime;
    private String lastCaptchaId;
    private String lastCaptchaFilePath;
    private Integer count;

    public CaptchaImgInfo(Integer captchaId, Integer captchaType, String captchaName, long lastCaptchaTime, String lastCaptchaId, String lastCaptchaFilePath) {
        this.captchaId = captchaId;
        this.captchaType = captchaType;
        this.captchaName = captchaName;
        this.lastCaptchaTime = lastCaptchaTime;
        this.lastCaptchaId = lastCaptchaId;
        this.lastCaptchaFilePath = lastCaptchaFilePath;
        this.count = 0;
    }

    public void renew(long lastCaptchaTime, String lastCaptchaId, String lastCaptchaFilePath) {
        this.count++;
        this.lastCaptchaTime = lastCaptchaTime;
        this.lastCaptchaId = lastCaptchaId;
        this.lastCaptchaFilePath = lastCaptchaFilePath;
    }

    public void clear() {
        lastCaptchaId = null;
        lastCaptchaFilePath = null;
    }
}
