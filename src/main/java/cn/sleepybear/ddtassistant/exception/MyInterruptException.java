package cn.sleepybear.ddtassistant.exception;

import java.io.Serial;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/10 11:21
 */
public class MyInterruptException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 7297493405569768199L;

    public MyInterruptException() {
    }

    public MyInterruptException(String message) {
        super(message);
    }
}
