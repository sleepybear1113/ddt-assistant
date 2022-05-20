package cn.xiejx.ddtassistant.utils.tj;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
public class TjAccountInfo implements Serializable {
    private static final long serialVersionUID = 7234944473288741084L;

    private Boolean success;
    private String code;
    private String message;
    private String data;
}
