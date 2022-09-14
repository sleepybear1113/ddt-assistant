package cn.xiejx.ddtassistant.utils.captcha.tj;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
public class TjPicResult implements Serializable {
    private static final long serialVersionUID = 5947809593343797675L;

    private String result;
    private String id;
}
