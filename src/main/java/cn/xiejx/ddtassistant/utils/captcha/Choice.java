package cn.xiejx.ddtassistant.utils.captcha;

import lombok.Data;

/**
 * @author sleepybear
 */
@Data
public class Choice {
    public static final String[] VALID_CHOICES = {"A", "B", "C", "D"};

    private ChoiceEnum choiceEnum;
    private boolean success;
    private String message;
}
