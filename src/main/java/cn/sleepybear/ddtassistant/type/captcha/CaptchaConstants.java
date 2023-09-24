package cn.sleepybear.ddtassistant.type.captcha;

import cn.sleepybear.cacher.Cacher;
import cn.sleepybear.cacher.CacherBuilder;
import cn.sleepybear.ddtassistant.base.CaptchaConfig;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.utils.SpringContextUtil;
import cn.sleepybear.ddtassistant.utils.captcha.BasePredictDto;
import cn.sleepybear.ddtassistant.utils.captcha.CaptchaInfo;
import cn.sleepybear.ddtassistant.utils.captcha.BaseCaptchaWay;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sleepybear
 */
@Slf4j
public class CaptchaConstants {

    /**
     * 倒计时三个字的模板区域，“验证码提示”字样最小区域
     */
    public final static int[] CAPTCHA_COUNTDOWN_SAMPLE_REACT = {625, 175, 708, 205};

    /**
     * 验证码寻找匹配区域，需要在这块区域匹配 CAPTCHA_COUNTDOWN_SAMPLE_REACT
     */
    public final static int[] CAPTCHA_COUNTDOWN_FIND_REACT = {590, 155, 770, 240};

    /**
     * 找图的模板，“验证码提示”字样最小区域
     */
    public final static int[] CAPTCHA_TEMPLATE_REACT = {265, 135, 365, 160};

    /**
     * 验证码提问区域
     */
    public final static int[] CAPTCHA_QUESTION_REACT = {314, 180, 630, 430};
    /**
     * 验证码裁剪中下部，土黄色区域，来验证是不是验证码图片
     */
    public final static int[] CAPTCHA_SUB_VALID_RECT = {100, 190, 200, 250};
    /**
     * 验证码合法颜色
     */
    public static final int[] CAPTCHA_VALID_COLOR = {199, 159, 103};
    /**
     * 验证码合法颜色容差
     */
    public static final int[] CAPTCHA_VALID_DELTA_COLOR = {10, 10, 10};

    /**
     * 验证码倒计时区域
     */
    public final static int[] CAPTCHA_COUNT_DOWN_REACT = {708, 185, 732, 205};

    /**
     * 验证码整个图片区域
     */
    public final static int[] CAPTCHA_FULL_REACT = {258, 132, 761, 498};

    /**
     * 副本验证码下方提交区域灰色纯色背景坐标范围
     */
    public static final int[] CAPTCHA_BOTTOM_DETECT_AREA_1 = {300, 435, 315, 450};
    public static final int[] CAPTCHA_BOTTOM_DETECT_AREA_2 = {700, 435, 715, 450};

    /**
     * 副本验证码下方提交区域灰色纯色背景颜色数量
     */
    public static final int CAPTCHA_BOTTOM_COLOR_NUM = 200;

    /**
     * 副本验证码下方提交区域灰色纯色背景颜色
     */
    public static final int[] CAPTCHA_BUTTON_DETECT_COLOR = {121, 92, 55};
    /**
     * 底部验证码合法颜色容差
     */
    public static final int[] CAPTCHA_BOTTOM_VALID_DELTA_COLOR = {4, 4, 4};

    /**
     * 副本大翻牌检测区域
     */
    public static final int[] FLOP_BONUS_DETECT_RECT = {120, 0, 420, 120};

    /**
     * 副本大翻牌模板区域
     */
    public static final int[] FLOP_BONUS_SAMPLE_RECT = {160, 10, 365, 75};

    /**
     * 验证码选项坐标
     */
    public final static int[] ANSWER_CHOICE_POINT = {350, 373, 568, 410};

    /**
     * 点击验证码提交答案坐标
     */
    public static final int[] SUBMIT_BUTTON_POINT = {510, 457};

    /**
     * 倒计时秒数的区域
     */
    public static final int[] COUNT_DOWN_NUMBER_RECT = {708, 185, 745, 205};
    public final static int[] ANSWER_CHOICE_POINT_A = {ANSWER_CHOICE_POINT[0], ANSWER_CHOICE_POINT[1]};
    public final static int[] ANSWER_CHOICE_POINT_B = {ANSWER_CHOICE_POINT[2], ANSWER_CHOICE_POINT[1]};
    public final static int[] ANSWER_CHOICE_POINT_C = {ANSWER_CHOICE_POINT[0], ANSWER_CHOICE_POINT[3]};
    public final static int[] ANSWER_CHOICE_POINT_D = {ANSWER_CHOICE_POINT[2], ANSWER_CHOICE_POINT[3]};

    public static final double DEFAULT_BRIGHT_PIC_THRESHOLD = 0.7;
    public static final double DEFAULT_FLOP_BONUS_PIC_THRESHOLD = 0.6;

    /**
     * 图鉴最低打码所需时间
     */
    public static final int TJ_MIN_ANSWER_TIME = 6;
    /**
     * 平川最低打码所需时间
     */
    public static final int PC_MIN_ANSWER_TIME = 2;

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
    public enum CaptchaChoiceEnum {
        /**
         * 图鉴打码
         */
        TJ(1, "图鉴打码", CaptchaConstants.TJ_MIN_ANSWER_TIME),
        /**
         * 平川打码
         */
        PC(2, "平川服务器打码", CaptchaConstants.PC_MIN_ANSWER_TIME),
        ;
        private final Integer choice;
        private final String name;
        private final Integer minAnswerTime;

        CaptchaChoiceEnum(Integer choice, String name, Integer minAnswerTime) {
            this.choice = choice;
            this.name = name;
            this.minAnswerTime = minAnswerTime;
        }

        public static CaptchaChoiceEnum getChoice(Integer choice) {
            if (choice == null) {
                return null;
            }
            for (CaptchaChoiceEnum captchaChoiceEnum : values()) {
                if (captchaChoiceEnum.getChoice().equals(choice)) {
                    return captchaChoiceEnum;
                }
            }
            return null;
        }
    }
}
