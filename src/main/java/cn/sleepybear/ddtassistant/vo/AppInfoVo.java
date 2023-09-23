package cn.sleepybear.ddtassistant.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
public class AppInfoVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -2408842202683244847L;

    private String appVersion;
}
