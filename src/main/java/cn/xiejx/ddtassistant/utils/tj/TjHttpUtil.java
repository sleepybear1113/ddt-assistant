package cn.xiejx.ddtassistant.utils.tj;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.logic.CaptchaLogic;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.Cacher;
import cn.xiejx.ddtassistant.utils.cacher.CacherBuilder;
import cn.xiejx.ddtassistant.utils.cacher.cache.ExpireWayEnum;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpRequestMaker;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.springframework.util.CollectionUtils;

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

    public static final Random R = new Random();

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
        log.info(responseBody);
        TjResponse tjResponse = JSON.parseObject(responseBody, TjResponse.class);
        TjResponse response = TjResponse.buildResponse(tjResponse);
        response.setCost(end - start);
        return response;
    }

    public static TjResponse waitToGetChoice(long maxDelay, long afterDisappearDelay, TjPredictDto tjPredictDto) {
        int id = R.nextInt(100000);
        CACHER.set(id, TjResponse.buildWaitingResponse(), 1000L * 60);
        GlobalVariable.THREAD_POOL.execute(() -> {
            TjResponse tjResponse = getTjResponse(tjPredictDto);
            tjResponse = TjResponse.buildResponse(tjResponse);
            log.info("[{}] 请求结束，平台识别时间耗时 {} 毫秒", id, tjResponse.getCost());

            CACHER.set(id, tjResponse, 1000L * 60);
        });

        TjResponse response = TjResponse.buildEmptyResponse();
        long startTime = System.currentTimeMillis();
        while (true) {
            CaptchaLogic.TIME_CACHER.set(CaptchaLogic.HAS_FOUND_KEY, System.currentTimeMillis(), afterDisappearDelay, ExpireWayEnum.AFTER_UPDATE);

            if (System.currentTimeMillis() - startTime > maxDelay) {
                log.warn("[{}] 验证码识别超时，超时时间：{} 毫秒", id, maxDelay);
                break;
            }

            Util.sleep(100);
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
        log.info("[{}] 识别耗时 {} 毫秒，答案为：{}，全部结果为：{}", id, cost, response.getChoiceEnum().getChoice(), response);
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
        HttpResponseHelper responseHelper = httpHelper.request();
        log.info("报错 id = {}，{}", id, responseHelper.getResponseBody());
    }

}
