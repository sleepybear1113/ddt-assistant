package cn.xiejx.ddtassistant.exception;

/**
 * @author sleepybear
 */
public class FrontException extends RuntimeException {
    private static final long serialVersionUID = -850477151980098414L;

    public FrontException() {
    }

    public FrontException(String message) {
        super(message);
    }
}
