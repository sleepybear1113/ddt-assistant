package cn.sleepybear.ddtassistant.utils.captcha;

import cn.sleepybear.ddtassistant.type.captcha.CaptchaConstants;
import lombok.Getter;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 20:59
 */
@Getter
public enum CaptchaChoiceEnum {
    /**
     * 图鉴打码
     */
    TJ(1,"图鉴打码", CaptchaConstants.TJ_MIN_ANSWER_TIME),
    /**
     * 平川打码
     */
    PC(2, "平川服务器打码", CaptchaConstants.PC_MIN_ANSWER_TIME),
    ;
    private final Integer choice;
    private final String name;
    private final Integer minAnswerTime;

    CaptchaChoiceEnum(Integer choice,String name, Integer minAnswerTime) {
        this.choice = choice;
        this.name = name;
        this.minAnswerTime = minAnswerTime;
    }

    public static CaptchaChoiceEnum getChoice(Integer choice) {
        if (choice == null) {
            return null;
        }
        for (CaptchaChoiceEnum captchaChoiceEnum : values()) {
            if (captchaChoiceEnum.getChoice().equals(choice)) {
                return captchaChoiceEnum;
            }
        }
        return null;
    }

}
