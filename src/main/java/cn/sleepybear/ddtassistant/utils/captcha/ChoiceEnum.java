package cn.sleepybear.ddtassistant.utils.captcha;

import cn.sleepybear.ddtassistant.type.captcha.CaptchaConstants;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author sleepybear
 */

@Getter
public enum ChoiceEnum {
    /**
     * 选项
     */
    A("A", CaptchaConstants.ANSWER_CHOICE_POINT_A),
    B("B", CaptchaConstants.ANSWER_CHOICE_POINT_B),
    C("C", CaptchaConstants.ANSWER_CHOICE_POINT_C),
    D("D", CaptchaConstants.ANSWER_CHOICE_POINT_D),
    WAITING("识别中", null),
    UNDEFINED("未知", null),
    ;
    private final String choice;
    private final int[] xy;

    ChoiceEnum(String i, int[] choiceXy) {
        this.choice = i;
        this.xy = choiceXy;
    }

    public static ChoiceEnum getChoice(String choice) {
        if (choice == null) {
            return UNDEFINED;
        }

        for (ChoiceEnum value : ChoiceEnum.values()) {
            if (value.choice.equalsIgnoreCase(choice)) {
                return value;
            }
        }
        return UNDEFINED;
    }

    @Override
    public String toString() {
        return "Choice {" +
                "choice='" + choice + '\'' +
                ", xy=" + Arrays.toString(xy) +
                '}';
    }
}
