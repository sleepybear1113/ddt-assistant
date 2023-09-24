package cn.sleepybear.ddtassistant.advice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author XJX
 * @date 2021/8/10 0:33
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResultCode<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -2938642554402365880L;

    private Integer code;
    private String message;
    private T result;

    public ResultCode(T result) {
        this.code = ResultCodeConstant.CodeEnum.SUCCESS.getCode();
        this.message = null;
        this.result = result;
    }

    public static ResultCode<String> buildMsg(String s) {
        return new ResultCode<>(ResultCodeConstant.CodeEnum.SUCCESS.getCode(), s);
    }

    public static ResultCode<String> buildResult(String s) {
        ResultCode<String> resultCode = new ResultCode<>(ResultCodeConstant.CodeEnum.SUCCESS.getCode(), null);
        resultCode.setResult(s);
        return resultCode;
    }

    public ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.result = null;
    }

    public ResultCode(String message) {
        this.code = ResultCodeConstant.CodeEnum.COMMON_ERROR.getCode();
        this.message = message;
        this.result = null;
    }
}
