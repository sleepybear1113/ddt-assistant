package cn.xiejx.ddtassistant.utils.captcha.pc;

import cn.xiejx.ddtassistant.base.CaptchaConfig;
import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.BaseResponse;
import cn.xiejx.ddtassistant.utils.captcha.CaptchaChoiceEnum;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpRequestMaker;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/14 22:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@NoArgsConstructor
public class PcPredictDto extends BasePredictDto implements Serializable {
    private static final long serialVersionUID = -3828083082254095381L;

    public static final String HOST = "http://139.155.237.55:21000";

    public PcPredictDto(String imgFile) {
        setImgFile(imgFile);
    }

    @Override
    public String getUrl() {
        return HOST + "/predict";
    }

    @Override
    public List<NameValuePair> buildPair() {
        String base64Img = imgToBase64();
        if (base64Img == null) {
            log.warn("图片转 base64 失败");
            return null;
        }
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("file", base64Img));
        return pairs;
    }

    @Override
    public RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(1000 * 3)
                .setConnectTimeout(1000 * 3)
                .setSocketTimeout(1000 * 10)
                .build();
    }

    @Override
    public void build(CaptchaConfig captchaConfig, String imgFilePath) {
        setImgFile(imgFilePath);
    }

    @Override
    public <T extends BaseResponse> Class<T> getResponseClass() {
        return (Class<T>) PcResponse.class;
    }

    @Override
    public boolean testConnection() {
        HttpRequestMaker requestMaker = HttpRequestMaker.makeGetHttpHelper(HOST + "/test");
        requestMaker.setConfig(RequestConfig.custom()
                .setConnectionRequestTimeout(2000)
                .setConnectTimeout(2000)
                .setSocketTimeout(2000)
                .build());
        HttpHelper httpHelper = new HttpHelper(requestMaker);
        HttpResponseHelper request = httpHelper.request();
        String responseBody = request.getResponseBody();
        return !StringUtils.isBlank(responseBody) && responseBody.contains("OK");
    }

    @Override
    public void lowBalanceRemind(CaptchaConfig captchaConfig) {

    }

    @Override
    public String getAccountInfo(CaptchaConfig captchaConfig) {
        return CaptchaChoiceEnum.PC.getName() + "平台无余额功能";
    }
}
