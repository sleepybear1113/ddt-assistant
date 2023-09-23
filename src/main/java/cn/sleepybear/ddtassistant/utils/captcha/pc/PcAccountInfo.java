package cn.sleepybear.ddtassistant.utils.captcha.pc;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/25 18:37
 */
@Data
public class PcAccountInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6671212718305543689L;

    private String message;
    private String author;
    private String balance;
}
