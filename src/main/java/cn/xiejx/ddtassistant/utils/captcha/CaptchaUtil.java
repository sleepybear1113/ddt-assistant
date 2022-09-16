package cn.xiejx.ddtassistant.utils.captcha;

import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.logic.MonitorLogic;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.Cacher;
import cn.xiejx.ddtassistant.utils.cacher.CacherBuilder;
import cn.xiejx.ddtassistant.utils.cacher.cache.ExpireWayEnum;
import cn.xiejx.ddtassistant.utils.captcha.pc.PcPredictDto;
import cn.xiejx.ddtassistant.utils.captcha.pc.PcResponse;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjPredictDto;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import cn.xiejx.ddtassistant.utils.http.HttpRequestMaker;
import cn.xiejx.ddtassistant.utils.http.HttpResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.NameValuePair;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/14 22:47
 */
@Slf4j
public class CaptchaUtil {
    public static final Random RANDOM = new Random();

    private static final Cacher<Integer, BaseResponse> CACHER = new CacherBuilder<Integer, BaseResponse>()
            .scheduleName("cacher")
            .delay(20, TimeUnit.SECONDS)
            .build();


    public static ChoiceEnum getChoice(long maxDelay, Long afterDisappearDelay, BasePredictDto basePredictDto) {
        BaseResponse baseResponse = new PcResponse();
//        BaseResponse response = baseResponse.getResponse(basePredictDto);
        return null;
    }

    public static <T extends BaseResponse> BaseResponse getResponse(BasePredictDto basePredictDto) {
        BaseResponse res = BaseResponse.buildEmptyResponse();
        res.setSuccess(false);
        res.setChoiceEnum(ChoiceEnum.UNDEFINED);

        if (basePredictDto == null) {
            res.setMessage("用户信息缺失！");
            return res;
        }

        // 构建 httpHelper
        HttpRequestMaker requestMaker = HttpRequestMaker.makePostHttpHelper(basePredictDto.getUrl());
        requestMaker.setConfig(basePredictDto.getRequestConfig());
        HttpHelper httpHelper = new HttpHelper(requestMaker);

        // 构建 body
        List<NameValuePair> pairs = basePredictDto.buildPair();
        if (CollectionUtils.isEmpty(pairs)) {
            res.setMessage("验证码请求构建失败！");
            return null;
        }
        httpHelper.setUrlEncodedFormPostBody(pairs);

        // 发起请求
        long start = System.currentTimeMillis();
        HttpResponseHelper responseHelper = httpHelper.request();
        long end = System.currentTimeMillis();
        String responseBody = responseHelper.getResponseBody();
        if (responseBody == null || responseBody.length() == 0) {
            res.setMessage("验证码获取结果为空");
            return res;
        }

        try {
            T response = Util.parseJsonToObject(responseBody.replace("\"\"", "null"), basePredictDto.getResponseClass());
            if (response != null) {
                response.buildResponse();
                response.setCost(end - start);
                return response;
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        return res;
    }

    public static <T extends BaseResponse> BaseResponse waitToGetChoice(Integer hwnd, long maxDelay, Long afterDisappearDelay, BasePredictDto basePredictDto) {
        final int hwndFinal = hwnd == null ? 0 : hwnd;
        // 识别 id
        int id = RANDOM.nextInt(100000);
        // 设置初始缓存
        CACHER.set(id, BaseResponse.buildWaitingResponse(), 1000L * 60);

        // 异步发送请求
        GlobalVariable.THREAD_POOL.execute(() -> {
            if (!basePredictDto.testConnection()) {
                log.info("[{}] 请求失败！ 原因：{}", hwndFinal, "无法连接到打码平台！");
                CACHER.set(id, BaseResponse.buildEmptyResponse(), 1000L * 60);
                return;
            }

            BaseResponse response = getResponse(basePredictDto);
            if (response == null) {
                log.info("[{}] 请求失败！ 原因：{}", hwndFinal, "结果为空！");
                CACHER.set(id, BaseResponse.buildEmptyResponse(), 1000L * 60);
            } else {
                if (Boolean.TRUE.equals(response.getSuccess())) {
                    log.info("[{}] 请求结束，平台识别时间耗时 {} 毫秒", hwndFinal, response.getCost());
                } else {
                    log.info("[{}] 请求失败！ 原因：{}", hwndFinal, response.getMessage());
                }
                CACHER.set(id, response, 1000L * 60);
            }
        });

        BaseResponse response = BaseResponse.buildWaitingResponse();
        long startTime = System.currentTimeMillis();
        while (true) {
            if (afterDisappearDelay != null && afterDisappearDelay > 0) {
                MonitorLogic.TIME_CACHER.set(MonitorLogic.CAPTCHA_FOUND_KEY, System.currentTimeMillis(), afterDisappearDelay, ExpireWayEnum.AFTER_UPDATE);
            }

            if (System.currentTimeMillis() - startTime > maxDelay) {
                log.warn("[{}] 验证码识别超时，超时时间：{} 毫秒", hwndFinal, maxDelay);
                response.setChoiceEnum(ChoiceEnum.UNDEFINED);
                response.setMessage("超时未返回答案");
                break;
            }

            Util.sleep(100L);
            BaseResponse cache = CACHER.get(id);
            if (cache == null) {
                break;
            }
            if (!ChoiceEnum.WAITING.equals(cache.getChoiceEnum())) {
                response = cache;
                break;
            }
        }

        long cost = System.currentTimeMillis() - startTime;
        log.info("[{}] 总识别耗时 {} 毫秒，答案为：{}，全部结果为：{}", hwndFinal, cost, response.getChoiceEnum().getChoice(), response);
        return response;
    }

    public static void main(String[] args) {
//        String file = "C:\\Users\\xjx\\Desktop\\ddt\\captcha\\20220527\\B@132252-21_21_10.png";
        String file = "图片/A@67200-12_59_39.png";
        tj2(file);
    }

    public static void pc(String file) {
        PcPredictDto basePredictDto = new PcPredictDto();
        basePredictDto.setImgFile(file);
        BaseResponse response = getResponse(basePredictDto);
        System.out.println(response);
    }

    public static void pc2(String file) {
        PcPredictDto basePredictDto = new PcPredictDto();
        basePredictDto.setImgFile(file);
        BaseResponse response = waitToGetChoice(null, 10000L, null, basePredictDto);
        System.out.println(response);
    }

    public static void tj(String file) {
        TjPredictDto basePredictDto = new TjPredictDto();
        basePredictDto.setImgFile(file);
        basePredictDto.setUsername("sleepybear1");
        basePredictDto.setPassword("tj123456");
        basePredictDto.setTypeId("7");
        basePredictDto.setSoftId("");
        BaseResponse response = getResponse(basePredictDto);
        System.out.println(response);
    }

    public static void tj2(String file) {
        TjPredictDto basePredictDto = new TjPredictDto();
        basePredictDto.setImgFile(file);
        basePredictDto.setUsername("sleepybear1");
        basePredictDto.setPassword("");
        basePredictDto.setTypeId("7");
        basePredictDto.setSoftId("");
        BaseResponse response = waitToGetChoice(null, 10000L, null, basePredictDto);
        System.out.println(basePredictDto.getResponseClass().isInstance(response));
        System.out.println(response);
    }

}
