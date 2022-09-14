package cn.xiejx.ddtassistant.utils.captcha.tj;

import cn.xiejx.ddtassistant.utils.captcha.BaseResponse;
import cn.xiejx.ddtassistant.utils.captcha.Choice;
import cn.xiejx.ddtassistant.utils.captcha.ChoiceEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author sleepybear
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TjResponse extends BaseResponse implements Serializable {
    private static final long serialVersionUID = -5428070169210657190L;

    private String code;
    private TjPicResult data;

    public TjResponse() {
    }

    public static boolean validChoice(TjResponse tjResponse) {
        if (tjResponse == null) {
            return false;
        }

        if (tjResponse.getData() == null) {
            return false;
        }

        String result = tjResponse.getData().getResult();
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

    public static TjResponse buildEmptyResponse() {
        TjResponse res = new TjResponse();
        res.setSuccess(false);
        res.setMessage("结果为空");
        res.setChoiceEnum(ChoiceEnum.UNDEFINED);
        res.setCost(-1L);
        res.setData(new TjPicResult());
        return res;
    }

    public static TjResponse buildWaitingResponse() {
        TjResponse res = new TjResponse();
        res.setSuccess(false);
        res.setMessage("识别中");
        res.setChoiceEnum(ChoiceEnum.WAITING);
        return res;
    }

    @Override
    public void buildResponse() {
        if (!validChoice(this)) {
            setChoiceEnum(ChoiceEnum.UNDEFINED);
        }

        TjPicResult tjPicResult = getData();
        try {
            if (tjPicResult == null) {
                setChoiceEnum(ChoiceEnum.UNDEFINED);
            } else {
                setChoiceEnum(ChoiceEnum.getChoice(tjPicResult.getResult()));
            }
        } catch (Exception ignored) {
            setChoiceEnum(ChoiceEnum.UNDEFINED);
        }
        setData(tjPicResult);
    }
}
