package cn.xiejx.ddtassistant.utils.captcha;

import cn.xiejx.ddtassistant.type.captcha.CaptchaInfo;
import cn.xiejx.ddtassistant.utils.captcha.pc.PcResponse;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Random;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/14 22:56
 */
@Data
@Slf4j
public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 1120471332408173687L;
    public static final Random RANDOM = new Random();

    public static long[] errorTimeRange = {1000, 10000};

    private Boolean success;
    private ChoiceEnum choiceEnum;
    private Long cost;
    private String captchaId;

    private String message;
    private String balance;

    public void buildResponse() {
    }

    public static void reportError(Integer hwnd, CaptchaInfo captchaInfo, boolean force) {
        if (captchaInfo == null) {
            return;
        }

        String lastCaptchaId = captchaInfo.getLastCaptchaId();
        if (StringUtils.isBlank(lastCaptchaId)) {
            return;
        }

        if (!force) {
            long timeSub = System.currentTimeMillis() - captchaInfo.getLastCaptchaTime();
            if (timeSub < BaseResponse.errorTimeRange[0] || timeSub > BaseResponse.errorTimeRange[1]) {
                return;
            }
        }

        log.info("[{}] [报错] 对上一次错误打码报错给[{}]平台，id = {}", hwnd, captchaInfo.getCaptchaChoiceEnum().getName(), lastCaptchaId);

        CaptchaChoiceEnum captchaChoiceEnum = captchaInfo.getCaptchaChoiceEnum();
        if (CaptchaChoiceEnum.TJ.equals(captchaChoiceEnum)) {
            TjResponse.reportError(captchaInfo);
        } else if (CaptchaChoiceEnum.PC.equals(captchaChoiceEnum)) {
            PcResponse.reportError(captchaInfo);
        }
    }

    public static BaseResponse buildWaitingResponse() {
        BaseResponse res = new BaseResponse();
        res.setSuccess(false);
        res.setMessage("识别中");
        res.setChoiceEnum(ChoiceEnum.WAITING);
        return res;
    }

    public static BaseResponse buildEmptyResponse() {
        BaseResponse res = new BaseResponse();
        res.setSuccess(false);
        res.setMessage("结果为空");
        res.setChoiceEnum(ChoiceEnum.UNDEFINED);
        res.setCost(-1L);
        return res;
    }
}
