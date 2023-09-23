package cn.sleepybear.ddtassistant.utils.captcha.tj;

import cn.sleepybear.ddtassistant.base.CaptchaConfig;
import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.logic.EmailLogic;
import cn.sleepybear.ddtassistant.type.captcha.Captcha;
import cn.sleepybear.ddtassistant.utils.SpringContextUtil;
import cn.sleepybear.ddtassistant.utils.Util;
import cn.sleepybear.ddtassistant.utils.captcha.BasePredictDto;
import cn.sleepybear.ddtassistant.utils.captcha.BaseResponse;
import cn.sleepybear.ddtassistant.utils.captcha.way.TjCaptcha;
import cn.sleepybear.ddtassistant.utils.http.HttpHelper;
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
 * @date 2022/05/10 09:55
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class TjPredictDto extends BasePredictDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 4340261310989122096L;
    public static final String HOST = "http://api.ttshitu.com";
    private static final String TJ_ACCOUNT_INFO_URL = HOST + "/queryAccountInfo.json?username=%s&password=%s";

    public TjPredictDto() {
    }

    public TjPredictDto(TjCaptcha baseCaptcha) {
        super.setBaseCaptchaWay(baseCaptcha);
    }

    public TjCaptcha getCaptchaWay() {
        return (TjCaptcha) super.getBaseCaptchaWay();
    }

    @Override
    public String getPredictUrl() {
        String serverUrl = getServerUrl(true);
        if (StringUtils.isBlank(serverUrl)) {
            return null;
        }

        return serverUrl + "/predict";
    }

    @Override
    public List<NameValuePair> buildPair() {
        String base64Img = imgToBase64();
        if (base64Img == null) {
            log.warn("图片转 base64 失败");
            return null;
        }
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("username", getCaptchaWay().getUsername()));
        pairs.add(new BasicNameValuePair("password", getCaptchaWay().getPassword()));
        pairs.add(new BasicNameValuePair("typeid", getCaptchaWay().getTypeId()));
        pairs.add(new BasicNameValuePair("softid", getCaptchaWay().getSoftId()));
        pairs.add(new BasicNameValuePair("image", base64Img));
        return pairs;
    }

    @Override
    public RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(1000 * 5)
                .setConnectTimeout(1000 * 5)
                .setSocketTimeout(1000 * 62)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BaseResponse> Class<T> getResponseClass() {
        return (Class<T>) TjResponse.class;
    }

    @Override
    public boolean testConnection(String s) {
        return true;
    }

    @Override
    public void lowBalanceRemind(CaptchaConfig captchaConfig) {
        Runnable runnable = () -> getAccountInfo(captchaConfig);
        GlobalVariable.THREAD_POOL.execute(runnable);
    }

    @Override
    public String getAccountInfo(CaptchaConfig captchaConfig) {
        String username = getCaptchaWay().getUsername();
        String password = getCaptchaWay().getPassword();
        Boolean lowBalanceRemind = captchaConfig.getLowBalanceRemind();
        Double lowBalanceNum = captchaConfig.getLowBalanceNum();

        StringBuilder sb = new StringBuilder(getBaseCaptchaWay().getCaptchaName());

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return sb.append("无可用用户信息").toString();
        }
        try {
            String url = String.format(TJ_ACCOUNT_INFO_URL, username, password);
            HttpHelper httpHelper = HttpHelper.makeDefaultGetHttpHelper(url);
            HttpResponseHelper responseHelper = httpHelper.request();
            String responseBody = responseHelper.getResponseBody();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            TjAccountInfo tjAccountInfo = Util.parseJsonToObject(responseBody, TjAccountInfo.class, mapper);
            if (tjAccountInfo == null) {
                return sb.append("解析平台返回内容失败").toString();
            }
            if (!Boolean.TRUE.equals(tjAccountInfo.getSuccess())) {
                return sb.append("获取用户信息失败，").append(tjAccountInfo.getMessage()).toString();
            }
            TjConsumption tjConsumption = tjAccountInfo.getData();
            if (tjConsumption == null) {
                return sb.append("解析平台返回内容失败").toString();
            }

            // 低余额提醒
            String balance = tjConsumption.getBalance();
            if (Util.isNumber(balance) && Boolean.TRUE.equals(lowBalanceRemind) && lowBalanceNum != null && lowBalanceNum > 0) {
                double realtimeBalance = Double.parseDouble(balance);
                if (realtimeBalance < lowBalanceNum) {
                    log.warn(getBaseCaptchaWay().getCaptchaName() + "当前余额：{}，低于设定值 {}，请注意！", realtimeBalance, lowBalanceNum);
                    SettingConfig settingConfig = SpringContextUtil.getBean(SettingConfig.class);
                    GlobalVariable.THREAD_POOL.execute(() -> EmailLogic.sendLowBalanceNotify(settingConfig.getEmail(), realtimeBalance));
                    Captcha.hasSendLowBalanceEmail = true;
                }
            }
            return String.format(getBaseCaptchaWay().getCaptchaName() + " 当前余额：%s，总消费：%s，总成功：%s，总失败：%s", balance, tjConsumption.getConsumed(), tjConsumption.getSuccessNum(), tjConsumption.getFailNum());
        } catch (Exception e) {
            String s = sb.append("获取用户信息失败：").append(e.getMessage()).toString();
            log.warn(s, e);
            return "";
        }
    }

    @Override
    public String decryptHost(String host) {
        return host;
    }

}
