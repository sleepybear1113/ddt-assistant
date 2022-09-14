package cn.xiejx.ddtassistant.utils.captcha.pc;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.logic.MonitorLogic;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.Cacher;
import cn.xiejx.ddtassistant.utils.cacher.CacherBuilder;
import cn.xiejx.ddtassistant.utils.cacher.cache.ExpireWayEnum;
import cn.xiejx.ddtassistant.utils.captcha.ChoiceEnum;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpRequestMaker;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/14 21:51
 */
@Slf4j
public class PcHttpUtil {
    private static final String PC_PREDICT_URL = "http://139.155.237.55:21000/predict";
    public static final Random RANDOM = new Random();

    private static final Cacher<Integer, PcResponse> CACHER = new CacherBuilder<Integer, PcResponse>()
            .scheduleName("pcCacher")
            .delay(20, TimeUnit.SECONDS)
            .build();

    public static PcResponse getPcResponse(PcPredictDto pcPredictDto) {
        PcResponse res = new PcResponse();
        res.setSuccess(false);
        res.setChoiceEnum(ChoiceEnum.UNDEFINED);

        if (pcPredictDto == null) {
            res.setMessage("用户信息缺失！");
            return res;
        }
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000 * 5)
                .setConnectTimeout(1000 * 5)
                .setSocketTimeout(1000 * 20)
                .build();

        HttpRequestMaker requestMaker = HttpRequestMaker.makePostHttpHelper(PC_PREDICT_URL);
        requestMaker.setConfig(requestConfig);

        HttpHelper httpHelper = new HttpHelper(requestMaker);

        List<NameValuePair> pairs = pcPredictDto.buildPair();
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
        PcResponse pcResponse = Util.parseJsonToObject(responseBody, PcResponse.class);
        PcResponse response = new PcResponse();
        response.setCost(end - start);
        return response;
    }

    public static PcResponse waitToGetChoice(long maxDelay, Long afterDisappearDelay, PcPredictDto pcPredictDto) {
        int id = RANDOM.nextInt(100000);
        CACHER.set(id, PcResponse.buildWaitingResponse(), 1000L * 60);
        GlobalVariable.THREAD_POOL.execute(() -> {
            PcResponse pcResponse = getPcResponse(pcPredictDto);
            pcResponse = new PcResponse();

            if (pcResponse.getSuccess()) {
                log.info("[{}] 请求结束，平台识别时间耗时 {} 毫秒", id, pcResponse.getCost());
            } else {
                log.info("[{}] 请求失败！ 原因：{}", id, pcResponse.getMessage());
            }

            CACHER.set(id, pcResponse, 1000L * 60);
        });

        PcResponse response = PcResponse.buildEmptyResponse();
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
            PcResponse cache = CACHER.get(id);
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

    public static void main(String[] args) {
        for (int i = 0; i < 1; i++) {
            new Thread(PcHttpUtil::aa).start();
        }
    }

    public static void aa() {
        PcPredictDto t = new PcPredictDto();
        String ff = "C:\\Users\\xjx\\Desktop\\ddt\\captcha\\20220524\\" + "A@67232-17_20_44.png";
        t.setImgFile(ff);
        String s = t.imgToBase64();
        System.out.println(s);

        ArrayList<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("file", s));

        String rr = "http://139.155.237.55:21000/predict";
        HttpRequestMaker requestMaker = HttpRequestMaker.makePostHttpHelper(rr);
        requestMaker.setConfig(t.getRequestConfig());
        HttpHelper httpHelper = new HttpHelper(requestMaker);
        httpHelper.setUrlEncodedFormPostBody(pairs);
        long start = System.currentTimeMillis();
        HttpResponseHelper responseHelper = httpHelper.request();
        long end = System.currentTimeMillis();
        String responseBody = responseHelper.getResponseBody();
        System.out.println(responseBody + (end - start));
    }
}
