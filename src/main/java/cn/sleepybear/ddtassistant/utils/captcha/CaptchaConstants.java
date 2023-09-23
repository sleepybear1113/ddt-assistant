package cn.sleepybear.ddtassistant.utils.captcha;

import cn.sleepybear.cacher.Cacher;
import cn.sleepybear.cacher.CacherBuilder;
import cn.sleepybear.ddtassistant.base.CaptchaConfig;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.utils.SpringContextUtil;
import cn.sleepybear.ddtassistant.utils.captcha.way.BaseCaptchaWay;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * There is description
 * @author sleepybear
 * @date 2023/09/20 22:34
 */
@Slf4j
public class CaptchaConstants {
    /**
     * key 是 CaptchaImgInfo serverList 的 url@id，value 是请求失败的时间
     */
    public static final Cacher<String, Long> CAPTCHA_SERVER_CONNECT_FAIL_CACHER = new CacherBuilder<String, Long>()
            .scheduleName("CAPTCHA_SERVER_CONNECT_FAIL_CACHER")
            .delay(30, TimeUnit.SECONDS)
            .allowNullKey(String.valueOf(System.currentTimeMillis()))
            .build();

    /**
     * 默认的 testConnection 的 fail 时间
     */
    public static final long DEFAULT_EXPIRE_TIME = 1000L * 60 * 15;

    static {
        // 设置过期后的操作，重新测试连接，如果连接失败，再次设置过期时间
        CAPTCHA_SERVER_CONNECT_FAIL_CACHER.setExpireAction((key, value, useExpireAction) -> {
            CaptchaConfig captchaConfig = SpringContextUtil.getBean(CaptchaConfig.class);
            List<CaptchaInfo> captchaInfoList = captchaConfig.getCaptchaInfoList();
            if (CollectionUtils.isEmpty(captchaInfoList)) {
                return;
            }

            String[] split = key.split("@");
            String url = split[0];
            int id = Integer.parseInt(split[1]);

            CaptchaInfo expiredCaptchaInfo = null;
            for (CaptchaInfo captchaInfo : captchaInfoList) {
                if (captchaInfo.getId().equals(id)) {
                    expiredCaptchaInfo = captchaInfo;
                    break;
                }
            }

            if (expiredCaptchaInfo == null) {
                return;
            }

            BaseCaptchaWay baseCaptchaWay = BaseCaptchaWay.buildBaseCaptchaWay(expiredCaptchaInfo);
            BasePredictDto basePredictDto = baseCaptchaWay.getBasePredictDto();

            final CaptchaInfo expiredCaptchaInfo2 = expiredCaptchaInfo;
            GlobalVariable.THREAD_POOL.execute(() -> {
                if (!basePredictDto.testConnection(url)) {
                    log.info("打码平台[{}]的服务器地址[{}]连接重试再次失败", expiredCaptchaInfo2.getCaptchaName(), url);
                    CAPTCHA_SERVER_CONNECT_FAIL_CACHER.set(key, System.currentTimeMillis(), DEFAULT_EXPIRE_TIME);
                }
            });
        });
    }

    @Getter
    public enum CaptchaTypeEnum {
        /**
         * 图鉴
         */
        TJ(1),
        /**
         * 平川
         */
        PC(2),
        ;
        private final Integer type;

        CaptchaTypeEnum(Integer type) {
            this.type = type;
        }

        public static CaptchaTypeEnum getType(Integer type) {
            if (type == null) {
                return null;
            }
            for (CaptchaTypeEnum captchaTypeEnum : values()) {
                if (captchaTypeEnum.getType().equals(type)) {
                    return captchaTypeEnum;
                }
            }
            return null;
        }
    }
}
