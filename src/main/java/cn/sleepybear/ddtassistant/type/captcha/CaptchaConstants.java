package cn.sleepybear.ddtassistant.type.captcha;

/**
 * @author sleepybear
 */
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
    public static final int TJ_MIN_ANSWER_TIME = 7;
    /**
     * 平川最低打码所需时间
     */
    public static final int PC_MIN_ANSWER_TIME = 3;
    public static final int DIY_1_MIN_ANSWER_TIME = 3;
    public static final int DIY_2_MIN_ANSWER_TIME = 6;
}
