package cn.sleepybear.ddtassistant.utils.captcha.tj;

import cn.sleepybear.ddtassistant.type.captcha.CaptchaImgInfo;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.utils.Util;
import cn.sleepybear.ddtassistant.utils.captcha.BaseResponse;
import cn.sleepybear.ddtassistant.utils.captcha.Choice;
import cn.sleepybear.ddtassistant.utils.captcha.ChoiceEnum;
import cn.sleepybear.ddtassistant.utils.http.HttpHelper;
import cn.sleepybear.ddtassistant.utils.http.HttpRequestMaker;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author sleepybear
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class TjResponse extends BaseResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -5428070169210657190L;

    private static final String TJ_REPORT_ERROR_URL = TjPredictDto.HOST + "/reporterror.json?id=";


    private String code;
    private TjPicResult data;

    public TjResponse() {
    }

    public static void reportError(CaptchaImgInfo captchaImgInfo) {
        String lastCaptchaId = captchaImgInfo.getLastCaptchaId();
        String lastCaptchaFilePath = captchaImgInfo.getLastCaptchaFilePath();
        GlobalVariable.THREAD_POOL.execute(() -> {
            try {
                reportError(lastCaptchaId);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
            Util.deleteFileFromServer(lastCaptchaFilePath);
        });

        captchaImgInfo.clear();
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
        if (tjPicResult != null) {
            this.setCaptchaId(tjPicResult.getId());
        }
    }

    public static void reportError(String id) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000 * 5)
                .setConnectTimeout(1000 * 5)
                .setSocketTimeout(1000 * 5)
                .build();

        HttpRequestMaker requestMaker = HttpRequestMaker.makeGetHttpHelper(TJ_REPORT_ERROR_URL + id);
        requestMaker.setConfig(requestConfig);
        HttpHelper httpHelper = new HttpHelper(requestMaker);
        httpHelper.request();
    }
}
