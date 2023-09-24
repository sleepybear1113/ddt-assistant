package cn.sleepybear.ddtassistant.utils.captcha.way;

import cn.sleepybear.ddtassistant.type.captcha.CaptchaConstants;
import cn.sleepybear.ddtassistant.utils.captcha.BaseCaptchaWay;
import cn.sleepybear.ddtassistant.utils.captcha.BasePredictDto;
import cn.sleepybear.ddtassistant.utils.captcha.pc.PcPredictDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 21:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PcCaptcha extends BaseCaptchaWay implements Serializable {
    @Serial
    private static final long serialVersionUID = 3701573224665113223L;

    private String cami;
    private String author;

    @Override
    public BaseCaptchaWay convertType() {
        if (getCaptchaTypeEnum() != CaptchaConstants.CaptchaChoiceEnum.PC) {
            return null;
        }

        List<String> params = super.getParams();
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        this.cami = params.get(0);
        if (params.size() > 1) {
            this.author = params.get(1);
        }
        return this;
    }

    @Override
    public boolean validUserInfo() {
        return StringUtils.isNotBlank(this.cami);
    }

    @Override
    public BasePredictDto getBasePredictDto() {
        return new PcPredictDto(this);
    }

    @Override
    public Integer getMinAnswerTime() {
        return CaptchaConstants.PC_MIN_ANSWER_TIME;
    }

    public String getAuthor() {
        return StringUtils.isBlank(this.author) ? "sleepy" : this.author;
    }
}