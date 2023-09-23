package cn.sleepybear.ddtassistant.utils.captcha.way;

import cn.sleepybear.ddtassistant.utils.captcha.BasePredictDto;
import cn.sleepybear.ddtassistant.utils.captcha.CaptchaChoiceEnum;
import cn.sleepybear.ddtassistant.utils.captcha.CaptchaInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 21:05
 */
@Getter
public abstract class BaseCaptchaWay extends CaptchaInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = -5900674422451222626L;

    public CaptchaChoiceEnum getCaptchaTypeEnum() {
        return CaptchaChoiceEnum.getChoice(super.getCaptchaType());
    }

    public abstract BaseCaptchaWay convertType();

    public abstract void fillParams();

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
    @JsonIgnore
    public abstract BasePredictDto getBasePredictDto();

    public BaseCaptchaWay setCaptchaInfo(CaptchaInfo captchaInfo) {
        super.setParams(captchaInfo.getParams());
        super.setId(captchaInfo.getId());
        super.setCaptchaName(captchaInfo.getCaptchaName());
        super.setCaptchaType(captchaInfo.getCaptchaType());
        super.setValidTimeList(captchaInfo.getValidTimeList());
        super.setServerAddressList(captchaInfo.getServerAddressList());

        return convertType();
    }

    public static BaseCaptchaWay buildBaseCaptchaWay(CaptchaInfo captchaInfo) {
        BaseCaptchaWay baseCaptchaWay = null;
        if (CaptchaChoiceEnum.TJ.getChoice().equals(captchaInfo.getCaptchaType())) {
            baseCaptchaWay = new TjCaptcha().setCaptchaInfo(captchaInfo);
        } else if (CaptchaChoiceEnum.PC.getChoice().equals(captchaInfo.getCaptchaType())) {
            baseCaptchaWay = new PcCaptcha().setCaptchaInfo(captchaInfo);
        }

        return baseCaptchaWay;
    }
}
