package cn.sleepybear.ddtassistant.utils.captcha.tj;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
public class TjAccountInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 7234944473288741084L;

    private Boolean success;
    private String code;
    private String message;
    private TjConsumption data;
}
