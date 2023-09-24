package cn.sleepybear.ddtassistant.utils.captcha.way;

import cn.sleepybear.ddtassistant.type.captcha.CaptchaConstants;
import cn.sleepybear.ddtassistant.utils.captcha.BaseCaptchaWay;
import cn.sleepybear.ddtassistant.utils.captcha.BasePredictDto;
import cn.sleepybear.ddtassistant.utils.captcha.tj.TjPredictDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 21:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TjCaptcha extends BaseCaptchaWay implements Serializable {
    @Serial
    private static final long serialVersionUID = 3701573224665113223L;

    public static final String SERVER_URL = "http://api.ttshitu.com";
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

    public String getSoftId() {
        return StringUtils.isEmpty(this.softId) ? DEFAULT_SOFT_ID : this.softId;
    }

    public String getTypeId() {
        return StringUtils.isEmpty(this.typeId) ? DEFAULT_TYPE_ID : this.typeId;
    }

    @Override
    public BaseCaptchaWay convertType() {
        if (getCaptchaTypeEnum() != CaptchaConstants.CaptchaChoiceEnum.TJ) {
            return null;
        }

        List<String> params = super.getParams();
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        switch (params.size()) {
            case 4:
                this.typeId = params.get(3);
            case 3:
                this.softId = params.get(2);
            case 2:
                this.password = params.get(1);
            case 1:
                this.username = params.get(0);
            default:
                break;
        }

        return this;
    }

    @Override
    public boolean validUserInfo() {
        return this.username != null && !this.username.isEmpty() & this.password != null && !this.password.isEmpty();
    }

    @Override
    public BasePredictDto getBasePredictDto() {
        return new TjPredictDto(this);
    }

    @Override
    public Integer getMinAnswerTime() {
        return CaptchaConstants.TJ_MIN_ANSWER_TIME;
    }

    @Override
    public List<String> getServerAddressList() {
        List<String> serverAddressList = super.getServerAddressList();
        if (CollectionUtils.isEmpty(serverAddressList)) {
            return Collections.singletonList(SERVER_URL);
        }
        return serverAddressList;
    }
}