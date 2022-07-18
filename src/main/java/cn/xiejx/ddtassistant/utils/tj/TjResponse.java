package cn.xiejx.ddtassistant.utils.tj;

import cn.xiejx.ddtassistant.utils.Util;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sleepybear
 */
@Data
public class TjResponse implements Serializable {
    public static final String[] VALID_CHOICES = {"A", "B", "C", "D"};

    private static final long serialVersionUID = -5428070169210657190L;

    private Boolean success;
    private String code;
    private String message;
    private String data;
    private TjPicResult result;


    private ChoiceEnum choiceEnum;
    private Long cost;

    public TjResponse() {
        this.result = new TjPicResult();
    }

    public static boolean validChoice(TjResponse tjResponse) {
        if (tjResponse == null) {
            return false;
        }

        TjPicResult picResult = tjResponse.getResult();
        if (picResult == null) {
            return false;
        }

        String result = picResult.getResult();
        if (result == null) {
            return false;
        }

        for (String validChoice : VALID_CHOICES) {
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
        return res;
    }

    public static TjResponse buildWaitingResponse() {
        TjResponse res = new TjResponse();
        res.setSuccess(false);
        res.setMessage("识别中");
        res.setChoiceEnum(ChoiceEnum.WAITING);
        return res;
    }

    public static TjResponse buildResponse(TjResponse tjResponse) {
        if (tjResponse == null) {
            return buildEmptyResponse();
        }
        if (!validChoice(tjResponse)) {
            tjResponse.setChoiceEnum(ChoiceEnum.UNDEFINED);
        }

        String data = tjResponse.getData();
        if (data == null || data.length() == 0) {
            tjResponse.setResult(new TjPicResult());
            tjResponse.setChoiceEnum(ChoiceEnum.UNDEFINED);
        }else {
            TjPicResult tjPicResult = null;
            try {
                tjPicResult = Util.parseJsonToObject(data, TjPicResult.class);
                if (tjPicResult == null) {
                    tjResponse.setChoiceEnum(ChoiceEnum.UNDEFINED);
                } else {
                    tjResponse.setChoiceEnum(ChoiceEnum.getChoice(tjPicResult.getResult()));
                }
            } catch (Exception ignored) {
                tjResponse.setChoiceEnum(ChoiceEnum.UNDEFINED);
            }
            tjResponse.setResult(tjPicResult);
        }
        return tjResponse;
    }
}
