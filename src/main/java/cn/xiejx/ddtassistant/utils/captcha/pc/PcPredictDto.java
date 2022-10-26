package cn.xiejx.ddtassistant.utils.captcha.pc;

import cn.xiejx.ddtassistant.base.CaptchaConfig;
import cn.xiejx.ddtassistant.base.SettingConfig;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.logic.EmailLogic;
import cn.xiejx.ddtassistant.type.captcha.Captcha;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.BaseResponse;
import cn.xiejx.ddtassistant.utils.captcha.CaptchaChoiceEnum;
import cn.xiejx.ddtassistant.utils.captcha.way.PcCaptcha;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpRequestMaker;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
public class PcPredictDto extends BasePredictDto implements Serializable {
    private static final long serialVersionUID = -3828083082254095381L;

    private static final String ACCOUNT_INFO_SUFFIX_URL = "/balance?cami=%s&author=%s";

    private String cami;
    private String author;

    public PcPredictDto() {
    }

    public PcPredictDto(String imgFile) {
        setImgFile(imgFile);
    }

    public String getAuthor() {
        return StringUtils.isBlank(author) ? "sleepy" : author;
    }

    public String getHost(boolean throwException) {
        String host = SpringContextUtil.getBean(CaptchaConfig.class).getPc().getServerAddr();
        if (StringUtils.isBlank(host) || (!host.startsWith("http://") && !host.startsWith("https://"))) {
            if (throwException) {
                throw new FrontException("服务器地址填写错误");
            } else {
                log.warn("服务器地址填写错误");
            }
        }
        return host;
    }

    @Override
    public String getPredictUrl() {
        return getHost(true) + "/predict2";
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
        pairs.add(new BasicNameValuePair("cami", this.cami));
        pairs.add(new BasicNameValuePair("author", this.author));
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
        PcCaptcha pc = captchaConfig.getPc();
        if (!pc.validUserInfo()) {
            throw new FrontException("用户卡密信息错误！");
        }
        getHost(true);
        setAuthor(pc.getAuthor());
        setCami(pc.getCami());
    }

    @Override
    public <T extends BaseResponse> Class<T> getResponseClass() {
        return (Class<T>) PcResponse.class;
    }

    @Override
    public boolean testConnection() {
        HttpRequestMaker requestMaker = HttpRequestMaker.makeGetHttpHelper(getHost(true) + "/test");
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
        Runnable runnable = () -> getAccountInfo(captchaConfig);
        GlobalVariable.THREAD_POOL.execute(runnable);
    }

    @Override
    public String getAccountInfo(CaptchaConfig captchaConfig) {
        String cami = captchaConfig.getPc().getCami();
        String author = captchaConfig.getPc().getAuthor();
        Boolean lowBalanceRemind = captchaConfig.getLowBalanceRemind();
        Double lowBalanceNum = captchaConfig.getLowBalanceNum();

        StringBuilder sb = new StringBuilder(CaptchaChoiceEnum.PC.getName());

        if (StringUtils.isBlank(cami)) {
            return sb.append("卡密为空，无可用用户信息").toString();
        }
        try {
            String url = String.format(getHost(true) + ACCOUNT_INFO_SUFFIX_URL, cami, author);
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
                    log.warn(CaptchaChoiceEnum.PC.getName() + "当前余额：{}，低于设定值 {}，请注意！", realtimeBalance, lowBalanceNum);
                    SettingConfig settingConfig = SpringContextUtil.getBean(SettingConfig.class);
                    EmailLogic.sendLowBalanceNotify(settingConfig.getEmail(), realtimeBalance);
                    Captcha.hasSendLowBalanceEmail = true;
                }
            }
            return String.format(CaptchaChoiceEnum.PC.getName() + " 当前余额：%s", balance);
        } catch (Exception e) {
            String s = sb.append("获取用户信息失败：").append(e.getMessage()).toString();
            log.warn(s, e);
            return s;
        }
    }

    public static void main(String[] args) {
        PcPredictDto pcPredictDto = new PcPredictDto("test/A@66142-12_54_44.png");
        CaptchaConfig captchaConfig = new CaptchaConfig();
        PcCaptcha pc = new PcCaptcha();
        pc.setAuthor("sleepy");
        pc.setCami("YGc6Jb0W5jzu72e0");
        captchaConfig.setPc(pc);

        pcPredictDto.build(captchaConfig, "test/A@66142-12_54_44.png");
        pcPredictDto.getAccountInfo(captchaConfig);
    }
}
