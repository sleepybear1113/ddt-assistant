package cn.xiejx.ddtassistant.utils.captcha.way;

import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.pc.PcPredictDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

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

    private String serverAddr;

    private String cami;
    private String author;

    @Override
    public boolean validUserInfo() {
        return StringUtils.isNotBlank(cami) && StringUtils.isNotBlank(serverAddr);
    }

    public String getAuthor() {
        return StringUtils.isBlank(author) ? "sleepy" : author;
    }

    @Override
    public BasePredictDto getBasePredictDto() {
        return new PcPredictDto();
    }
}