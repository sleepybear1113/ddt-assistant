package cn.xiejx.ddtassistant.utils.captcha.way;

import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;

import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 21:05
 */
public abstract class BaseCaptchaWay implements Serializable {
    private static final long serialVersionUID = -5900674422451222626L;

    public abstract boolean validUserInfo();

    public abstract BasePredictDto getBasePredictDto();
}
