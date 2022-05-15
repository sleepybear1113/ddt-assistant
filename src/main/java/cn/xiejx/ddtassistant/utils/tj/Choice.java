package cn.xiejx.ddtassistant.utils.tj;

import lombok.Data;

/**
 * @author sleepybear
 */
@Data
public class Choice {
    private ChoiceEnum choiceEnum;
    private boolean success;
    private String message;
}
