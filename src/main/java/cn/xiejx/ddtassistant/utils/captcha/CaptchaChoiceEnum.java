package cn.xiejx.ddtassistant.utils.captcha;

import cn.xiejx.ddtassistant.type.captcha.CaptchaConstants;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 20:59
 */
public enum CaptchaChoiceEnum {
    /**
     * 不打码
     */
    NONE(0, "无", 0),
    /**
     * 图鉴打码
     */
    TJ(1,"图鉴打码",CaptchaConstants.TJ_MIN_ANSWER_TIME),
    /**
     * 平川打码
     */
    PC(2, "平川服务器打码", CaptchaConstants.PC_MIN_ANSWER_TIME),
    /**
     * 服务器打码1
     */
    PC1(3, "服务器打码1", CaptchaConstants.PC_MIN_ANSWER_TIME),
    /**
     * 服务器打码2
     */
    PC2(4, "服务器打码2", CaptchaConstants.PC_MIN_ANSWER_TIME),
    ;
    private final Integer choice;
    private final String name;
    private final Integer minAnswerTime;

    CaptchaChoiceEnum(Integer choice,String name, Integer minAnswerTime) {
        this.choice = choice;
        this.name = name;
        this.minAnswerTime = minAnswerTime;
    }

    public Integer getChoice() {
        return choice;
    }

    public static CaptchaChoiceEnum getChoice(Integer choice) {
        if (choice == null) {
            return NONE;
        }
        for (CaptchaChoiceEnum captchaChoiceEnum : values()) {
            if (captchaChoiceEnum.getChoice().equals(choice)) {
                return captchaChoiceEnum;
            }
        }
        return NONE;
    }

    public Integer getMinAnswerTime() {
        return minAnswerTime;
    }

    public String getName() {
        return name;
    }
}
