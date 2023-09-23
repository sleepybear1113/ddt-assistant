package cn.sleepybear.ddtassistant.utils.captcha.pc;

import cn.sleepybear.ddtassistant.base.CaptchaConfig;
import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.exception.FrontException;
import cn.sleepybear.ddtassistant.logic.EmailLogic;
import cn.sleepybear.ddtassistant.type.captcha.Captcha;
import cn.sleepybear.ddtassistant.utils.EncryptUtil;
import cn.sleepybear.ddtassistant.utils.SpringContextUtil;
import cn.sleepybear.ddtassistant.utils.Util;
import cn.sleepybear.ddtassistant.utils.captcha.BasePredictDto;
import cn.sleepybear.ddtassistant.utils.captcha.BaseResponse;
import cn.sleepybear.ddtassistant.utils.captcha.CaptchaChoiceEnum;
import cn.sleepybear.ddtassistant.utils.captcha.way.PcCaptcha;
import cn.sleepybear.ddtassistant.utils.http.HttpHelper;
import cn.sleepybear.ddtassistant.utils.http.HttpRequestMaker;
import cn.sleepybear.ddtassistant.utils.http.HttpResponseHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serial;
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
public class PcPredictDto extends BasePredictDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -3828083082254095381L;
    public static final String ENCRYPT_PREFIX = "c" + "a" + "p" + "t" + "c" + "h" + "a" + ":" + "/" + "/";
    public static final String ENCRYPT_SUFFIX = "." + "c" + "o" + "m";

    public static final String AES_KEY = "1q2w3e4r5t6y7u8i";

    private static final String ACCOUNT_INFO_SUFFIX_URL = "/balance?cami=%s&author=%s";

    public PcPredictDto() {
    }

    public PcPredictDto(PcCaptcha baseCaptchaWay) {
        super.setBaseCaptchaWay(baseCaptchaWay);
    }

    public PcCaptcha getCaptchaWay() {
        return (PcCaptcha) super.getBaseCaptchaWay();
    }

    @Override
    public String getPredictUrl() {
        String serverUrl = getServerUrl(true);
        if (StringUtils.isBlank(serverUrl)) {
            return null;
        }

        serverUrl = decryptHost(serverUrl);
        return serverUrl + "/predict2";
    }

    @Override
    public boolean testConnection(String host) {
        if (StringUtils.isBlank(host)) {
            return false;
        }

        host = decryptHost(host);
        try {
            HttpRequestMaker requestMaker = HttpRequestMaker.makeGetHttpHelper(host + "/test");
            requestMaker.setConfig(RequestConfig.custom()
                    .setConnectionRequestTimeout(2000)
                    .setConnectTimeout(2000)
                    .setSocketTimeout(2000)
                    .build());
            HttpHelper httpHelper = new HttpHelper(requestMaker);
            HttpResponseHelper request = httpHelper.request();
            String responseBody = request.getResponseBody();
            return !StringUtils.isBlank(responseBody) && responseBody.contains("OK");
        } catch (Exception e) {
            log.warn("测试服务器连接失败", e);
            return false;
        }
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
        pairs.add(new BasicNameValuePair("cami", getCaptchaWay().getCami()));
        pairs.add(new BasicNameValuePair("author", getCaptchaWay().getAuthor()));
        return pairs;
    }

    @Override
    public RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(1000 * 3)
                .setConnectTimeout(1000 * 3)
                .setSocketTimeout(1000 * 15)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseResponse> Class<T> getResponseClass() {
        return (Class<T>) PcResponse.class;
    }

    @Override
    public void lowBalanceRemind(CaptchaConfig captchaConfig) {
        Runnable runnable = () -> getAccountInfo(captchaConfig);
        GlobalVariable.THREAD_POOL.execute(runnable);
    }

    @Override
    public String getAccountInfo(CaptchaConfig captchaConfig) {
        String cami = getCaptchaWay().getCami();
        String author = getCaptchaWay().getAuthor();
        Boolean lowBalanceRemind = captchaConfig.getLowBalanceRemind();
        Double lowBalanceNum = captchaConfig.getLowBalanceNum();

        StringBuilder sb = new StringBuilder(CaptchaChoiceEnum.PC.getName());

        if (StringUtils.isBlank(cami)) {
            return sb.append("卡密为空，无可用用户信息").toString();
        }
        try {
            String url = String.format(getServerUrl(true) + ACCOUNT_INFO_SUFFIX_URL, cami, author);
            HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
            ArrayList<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("cami", cami));
            pairs.add(new BasicNameValuePair("author", author));
            httpHelper.setUrlEncodedFormPostBody(pairs);
            HttpResponseHelper responseHelper = httpHelper.request();
            String responseBody = responseHelper.getResponseBody();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            PcAccountInfo pcAccountInfo = Util.parseJsonToObject(responseBody, PcAccountInfo.class, mapper);
            if (pcAccountInfo == null) {
                return sb.append("解析平台返回内容失败").toString();
            }

            // 低余额提醒
            String balance = pcAccountInfo.getBalance();
            if (Util.isNumber(balance) && Boolean.TRUE.equals(lowBalanceRemind) && lowBalanceNum != null && lowBalanceNum > 0) {
                double realtimeBalance = Double.parseDouble(balance);
                if (realtimeBalance / 100 < lowBalanceNum) {
                    log.warn(getBaseCaptchaWay().getCaptchaName() + "当前余额：{}，低于设定值 {}，请注意！", realtimeBalance, lowBalanceNum);
                    SettingConfig settingConfig = SpringContextUtil.getBean(SettingConfig.class);
                    GlobalVariable.THREAD_POOL.execute(() -> EmailLogic.sendLowBalanceNotify(settingConfig.getEmail(), realtimeBalance));
                    Captcha.hasSendLowBalanceEmail = true;
                }
            }
            return String.format(getBaseCaptchaWay().getCaptchaName() + " 当前余额：%s", balance);
        } catch (Exception e) {
            String s = sb.append("获取用户信息失败：").append(e.getMessage()).toString();
            log.warn(s, e);
            return s;
        }
    }

    @Override
    public String decryptHost(String host) {
        if (StringUtils.isBlank(host)) {
            return host;
        }
        if (!host.startsWith(ENCRYPT_PREFIX) && !host.endsWith(ENCRYPT_SUFFIX)) {
            return host;
        }
        host = host.substring(ENCRYPT_PREFIX.length()).replace(ENCRYPT_SUFFIX, "");
        if (StringUtils.isBlank(host)) {
            throw new FrontException("加密服务器地址填写错误");
        }
        String s = EncryptUtil.aesDecrypt(host, AES_KEY);
        if (s == null) {
            throw new FrontException("服务器地址解密失败");
        }
        return s;
    }

    public static void main(String[] args) {
        PcPredictDto pcPredictDto = new PcPredictDto();
        CaptchaConfig captchaConfig = new CaptchaConfig();
        PcCaptcha pc = new PcCaptcha();
        pc.setAuthor("sleepy");
        pc.setCami("YGc6Jb0W5jzu72e0");

        pcPredictDto.setImgFile("test/A@66142-12_54_44.png");
        pcPredictDto.getAccountInfo(captchaConfig);
    }
}
