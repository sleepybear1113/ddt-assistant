package cn.xiejx.ddtassistant.utils.captcha.tj;

import lombok.Data;

import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
public class TjConsumption implements Serializable {
    private static final long serialVersionUID = 7975027875311113244L;

    /**
     * 实时余额
     */
    private String balance;
    /**
     * 实时总消费
     */
    private String consumed;
    /**
     * 实时总识别成功的次数
     */
    private String successNum;
    /**
     * 实时总识别错误数
     */
    private String failNum;
}
