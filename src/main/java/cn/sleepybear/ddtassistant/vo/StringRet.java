package cn.sleepybear.ddtassistant.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
public class StringRet implements Serializable {
    @Serial
    private static final long serialVersionUID = -6502138426028695970L;

    private Boolean success;
    private String msg;

    public static StringRet buildSuccess(String msg) {
        StringRet stringRet = new StringRet();
        stringRet.setSuccess(true);
        stringRet.setMsg(msg);
        return stringRet;
    }

    public static StringRet buildFail(String msg) {
        StringRet stringRet = new StringRet();
        stringRet.setSuccess(false);
        stringRet.setMsg(msg);
        return stringRet;
    }
}
