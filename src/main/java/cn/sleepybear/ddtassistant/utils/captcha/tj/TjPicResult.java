package cn.sleepybear.ddtassistant.utils.captcha.tj;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
public class TjPicResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 5947809593343797675L;

    private String result;
    private String id;
}
