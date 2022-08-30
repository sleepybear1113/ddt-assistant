package cn.xiejx.ddtassistant.utils.tj;

import cn.xiejx.ddtassistant.base.SettingConfig;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.logic.EmailLogic;
import cn.xiejx.ddtassistant.logic.MonitorLogic;
import cn.xiejx.ddtassistant.type.captcha.Captcha;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.Cacher;
import cn.xiejx.ddtassistant.utils.cacher.CacherBuilder;
import cn.xiejx.ddtassistant.utils.cacher.cache.ExpireWayEnum;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpRequestMaker;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/05/10 10:25
 */
@Slf4j
public class TjHttpUtil {
    private static final String TJ_PREDICT_URL = "http://api.ttshitu.com/predict";
    private static final String TJ_REPORT_ERROR_URL = "http://api.ttshitu.com/reporterror.json?id=";
    private static final String TJ_ACCOUNT_INFO_URL = "http://api.ttshitu.com/queryAccountInfo.json?username=%s&password=%s";

    public static final Random RANDOM = new Random();

    private static final Cacher<Integer, TjResponse> CACHER = new CacherBuilder<Integer, TjResponse>()
            .scheduleName("cacher")
            .delay(20, TimeUnit.SECONDS)
            .build();

    public static TjResponse getTjResponse(TjPredictDto tjPredictDto) {
        TjResponse res = new TjResponse();
        res.setSuccess(false);
        res.setChoiceEnum(ChoiceEnum.UNDEFINED);

        if (tjPredictDto == null) {
            res.setMessage("用户信息缺失！");
            return res;
        }
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000 * 5)
                .setConnectTimeout(1000 * 5)
                .setSocketTimeout(1000 * 62)
                .build();

        HttpRequestMaker requestMaker = HttpRequestMaker.makePostHttpHelper(TJ_PREDICT_URL);
        requestMaker.setConfig(requestConfig);

        HttpHelper httpHelper = new HttpHelper(requestMaker);

        List<NameValuePair> pairs = tjPredictDto.buildPair();
        if (CollectionUtils.isEmpty(pairs)) {
            res.setMessage("验证码请求构建失败！");
            return null;
        }
        httpHelper.setUrlEncodedFormPostBody(pairs);
        long start = System.currentTimeMillis();
        HttpResponseHelper responseHelper = httpHelper.request();
        long end = System.currentTimeMillis();
        String responseBody = responseHelper.getResponseBody();
        if (responseBody == null || responseBody.length() == 0) {
            res.setMessage("验证码获取结果为空");
            return res;
        }
        TjResponse tjResponse = Util.parseJsonToObject(responseBody, TjResponse.class);
        TjResponse response = TjResponse.buildResponse(tjResponse);
        response.setCost(end - start);
        return response;
    }

    public static TjResponse waitToGetChoice(long maxDelay, Long afterDisappearDelay, TjPredictDto tjPredictDto) {
        int id = RANDOM.nextInt(100000);
        CACHER.set(id, TjResponse.buildWaitingResponse(), 1000L * 60);
        GlobalVariable.THREAD_POOL.execute(() -> {
            TjResponse tjResponse = getTjResponse(tjPredictDto);
            tjResponse = TjResponse.buildResponse(tjResponse);
            if (tjResponse.getSuccess()) {
                log.info("[{}] 请求结束，平台识别时间耗时 {} 毫秒", id, tjResponse.getCost());
            } else {
                log.info("[{}] 请求失败！ 原因：{}", id, tjResponse.getMessage());
            }

            CACHER.set(id, tjResponse, 1000L * 60);
        });

        TjResponse response = TjResponse.buildEmptyResponse();
        long startTime = System.currentTimeMillis();
        while (true) {
            if (afterDisappearDelay != null && afterDisappearDelay > 0) {
                MonitorLogic.TIME_CACHER.set(MonitorLogic.CAPTCHA_FOUND_KEY, System.currentTimeMillis(), afterDisappearDelay, ExpireWayEnum.AFTER_UPDATE);
            }

            if (System.currentTimeMillis() - startTime > maxDelay) {
                log.warn("[{}] 验证码识别超时，超时时间：{} 毫秒", id, maxDelay);
                break;
            }

            Util.sleep(100L);
            TjResponse cache = CACHER.get(id);
            if (cache == null) {
                break;
            }
            if (!ChoiceEnum.WAITING.equals(cache.getChoiceEnum())) {
                response = cache;
                break;
            }
        }

        long cost = System.currentTimeMillis() - startTime;
        log.info("[{}] 总识别耗时 {} 毫秒，答案为：{}，全部结果为：{}", id, cost, response.getChoiceEnum().getChoice(), response);
        return response;
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

    public static String getAccountInfo(String username, String password, Boolean lowBalanceRemind, Double lowBalanceNum) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return "";
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
                return "解析平台返回内容失败";
            }
            if (!Boolean.TRUE.equals(tjAccountInfo.getSuccess())) {
                return "获取用户信息失败，" + tjAccountInfo.getMessage();
            }
            TjConsumption tjConsumption = tjAccountInfo.getData();
            if (tjConsumption == null) {
                return "解析平台返回内容失败";
            }

            // 低余额提醒
            String balance = tjConsumption.getBalance();
            if (Util.isNumber(balance) && Boolean.TRUE.equals(lowBalanceRemind) && lowBalanceNum != null && lowBalanceNum > 0) {
                double realtimeBalance = Double.parseDouble(balance);
                if (realtimeBalance < lowBalanceNum) {
                    log.warn("当前余额：{}，低于设定值 {}，请注意！", realtimeBalance, lowBalanceNum);
                    SettingConfig settingConfig = SpringContextUtil.getBean(SettingConfig.class);
                    if (settingConfig != null) {
                        EmailLogic.sendLowBalanceNotify(settingConfig.getEmail(), realtimeBalance);
                        Captcha.hasSendLowBalanceEmail = true;
                    }
                }
            }

            return String.format("当前余额：%s，总消费：%s，总成功：%s，总失败：%s", balance, tjConsumption.getConsumed(), tjConsumption.getSuccessNum(), tjConsumption.getFailNum());
        } catch (Exception e) {
            String s = "获取用户信息失败：" + e.getMessage();
            log.warn(s, e);
            return "";
        }
    }

}
