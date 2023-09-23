package cn.sleepybear.ddtassistant.utils.captcha.pc;

import cn.sleepybear.ddtassistant.type.captcha.CaptchaImgInfo;
import cn.sleepybear.ddtassistant.utils.captcha.BaseResponse;
import cn.sleepybear.ddtassistant.utils.captcha.Choice;
import cn.sleepybear.ddtassistant.utils.captcha.ChoiceEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/14 21:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PcResponse extends BaseResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -154939544804116995L;

    private String message;
    private String data;
    private String time;

    private Boolean success;
    private ChoiceEnum choiceEnum;
    private Long cost;
    private String balance;

    public PcResponse() {
    }

    public static void reportError(CaptchaImgInfo captchaImgInfo) {

    }

    public static boolean validChoice(PcResponse pcResponse) {
        if (pcResponse == null) {
            return false;
        }

        String result = pcResponse.getData();
        if (result == null) {
            return false;
        }

        for (String validChoice : Choice.VALID_CHOICES) {
            boolean b = validChoice.equalsIgnoreCase(result);
            if (b) {
                return true;
            }
        }

        return false;
    }

    public static PcResponse buildEmptyResponse() {
        PcResponse res = new PcResponse();
        res.setSuccess(false);
        res.setMessage("结果为空");
        res.setChoiceEnum(ChoiceEnum.UNDEFINED);
        res.setCost(-1L);
        res.setData(null);
        return res;
    }

    public static PcResponse buildWaitingResponse() {
        PcResponse res = new PcResponse();
        res.setSuccess(false);
        res.setMessage("识别中");
        res.setChoiceEnum(ChoiceEnum.WAITING);
        return res;
    }

    @Override
    public void buildResponse() {
        if ("success".equalsIgnoreCase(getMessage())) {
            setSuccess(true);
        } else {
            setMessage(getMessage() + "-" + getData());
        }
        if (!validChoice(this)) {
            setChoiceEnum(ChoiceEnum.UNDEFINED);
        }

        String choice = getData();
        try {
            if (StringUtils.isBlank(choice)) {
                setChoiceEnum(ChoiceEnum.UNDEFINED);
            } else {
                setChoiceEnum(ChoiceEnum.getChoice(choice));
            }
        } catch (Exception ignored) {
            setChoiceEnum(ChoiceEnum.UNDEFINED);
        }
    }

}
