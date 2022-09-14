package cn.xiejx.ddtassistant.utils.captcha;

import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.captcha.pc.PcResponse;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjResponse;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpRequestMaker;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;

import java.io.Serializable;
import java.util.List;
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

    private String url;

    private Boolean success;
    private ChoiceEnum choiceEnum;
    private Long cost;

    private String message;

    public void buildResponse(){
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
