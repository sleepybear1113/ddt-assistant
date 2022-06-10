package cn.xiejx.ddtassistant.exception;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/10 11:21
 */
public class MyInterruptException extends RuntimeException{
    private static final long serialVersionUID = 7297493405569768199L;

    public MyInterruptException() {
    }

    public MyInterruptException(String message) {
        super(message);
    }
}
