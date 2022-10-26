package cn.xiejx.ddtassistant.utils.captcha.way;

import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjPredictDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 21:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TjCaptcha extends BaseCaptchaWay implements Serializable {
    private static final long serialVersionUID = 3701573224665113223L;

    public static final String DEFAULT_SOFT_ID = "3b995690b1794ff08bad1abb88a3e451";
    public static final String DEFAULT_TYPE_ID = "7";

    private String username;
    private String password;
    /**
     * 推荐码
     */
    private String softId;
    /**
     * 打码类型
     */
    private String typeId;

    @Override
    public String getHost() {
        return "http://api.ttshitu.com";
    }

    @Override
    public boolean validUserInfo() {
        return this.username != null && this.username.length() > 0 & this.password != null && this.password.length() > 0;
    }

    @Override
    public BasePredictDto getBasePredictDto() {
        return new TjPredictDto();
    }
}