package cn.xiejx.ddtassistant.utils.captcha.way;

import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.pc.PcPredictDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 21:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PcCaptcha extends BaseCaptchaWay implements Serializable {
    private static final long serialVersionUID = 3701573224665113223L;

    private String username;
    private String password;
    private String code;

    @Override
    public boolean validUserInfo() {
        return true;
    }

    @Override
    public BasePredictDto getBasePredictDto() {
        return new PcPredictDto();
    }
}