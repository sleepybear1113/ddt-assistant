package cn.sleepybear.ddtassistant.exception;

import java.io.Serial;

/**
 * @author sleepybear
 */
public class FrontException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -850477151980098414L;

    public FrontException() {
    }

    public FrontException(String message) {
        super(message);
    }
}
