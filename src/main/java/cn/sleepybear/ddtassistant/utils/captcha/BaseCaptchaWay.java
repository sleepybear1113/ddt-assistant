package cn.sleepybear.ddtassistant.utils.captcha;

import cn.sleepybear.ddtassistant.type.captcha.CaptchaConstants;
import cn.sleepybear.ddtassistant.utils.captcha.way.PcCaptcha;
import cn.sleepybear.ddtassistant.utils.captcha.way.TjCaptcha;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

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

    public CaptchaConstants.CaptchaChoiceEnum getCaptchaTypeEnum() {
        return CaptchaConstants.CaptchaChoiceEnum.getChoice(super.getCaptchaType());
    }

    public abstract BaseCaptchaWay convertType();

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

    public abstract Integer getMinAnswerTime();

    public static BaseCaptchaWay buildBaseCaptchaWay(CaptchaInfo captchaInfo) {
        BaseCaptchaWay baseCaptchaWay = null;
        if (CaptchaConstants.CaptchaChoiceEnum.TJ.getChoice().equals(captchaInfo.getCaptchaType())) {
            baseCaptchaWay = new TjCaptcha().setCaptchaInfo(captchaInfo);
        } else if (CaptchaConstants.CaptchaChoiceEnum.PC.getChoice().equals(captchaInfo.getCaptchaType())) {
            baseCaptchaWay = new PcCaptcha().setCaptchaInfo(captchaInfo);
        }

        return baseCaptchaWay;
    }

    public boolean inValidTime(long time) {
        if (CollectionUtils.isEmpty(super.getValidTimeList())) {
            return true;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        // 获取小时和分钟
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        for (List<Integer> times : super.getValidTimeList()) {
            if (times.size() <= 4) {
                continue;
            }
            Integer startHour = times.get(0);
            Integer startMinute = times.get(1);
            Integer endHour = times.get(2);
            Integer endMinute = times.get(3);

            if (startHour == null || startMinute == null || endHour == null || endMinute == null) {
                continue;
            }

            if (hour >= startHour && hour <= endHour) {
                if (hour == startHour && minute < startMinute) {
                    continue;
                }
                if (hour == endHour && minute > endMinute) {
                    continue;
                }
                return true;
            }
        }

        return false;
    }
}
