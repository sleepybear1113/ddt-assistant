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

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 校验用户信息
     *
     * @return boolean
     */
    public abstract boolean validUserInfo();

    /**
     * 获取基本配置
     *
     * @return BasePredictDto
     */
    public abstract BasePredictDto getBasePredictDto();
}
