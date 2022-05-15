package cn.xiejx.ddtassistant.type;

import cn.xiejx.ddtassistant.config.UserConfig;
import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.dm.React;
import cn.xiejx.ddtassistant.logic.CaptchaLogic;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.cache.ExpireWayEnum;
import cn.xiejx.ddtassistant.utils.tj.ChoiceEnum;
import cn.xiejx.ddtassistant.utils.tj.TjHttpUtil;
import cn.xiejx.ddtassistant.utils.tj.TjPredictDto;
import cn.xiejx.ddtassistant.utils.tj.TjResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author sleepybear
 */
@Slf4j
public class Captcha {
    public final static int[] CAPTCHA_TITLE_REACT = {240, 110, 400, 180};
    /**
     * 找图的模板，“验证码提示”字样最小区域
     */
    public final static int[] CAPTCHA_TEMPLATE_REACT = {265, 135, 365, 160};
    /**
     * 验证码提问区域
     */
    public final static int[] CAPTCHA_QUESTION_REACT = {314, 180, 630, 430};
    /**
     * 验证码倒计时区域
     */
    public final static int[] CAPTCHA_COUNT_DOWN_REACT = {708, 185, 732, 205};
    /**
     * 验证码整个图片区域
     */
    public final static int[] CAPTCHA_FULL_REACT = {258, 132, 761, 498};
    /**
     * 验证码选项坐标
     */
    public final static int[] ANSWER_CHOICE_POINT = {350, 373, 568, 410};

    public static final int[] SUBMIT_BUTTON_POINT = {510, 457};
    public final static int[] ANSWER_CHOICE_POINT_A = {ANSWER_CHOICE_POINT[0], ANSWER_CHOICE_POINT[1]};
    public final static int[] ANSWER_CHOICE_POINT_B = {ANSWER_CHOICE_POINT[2], ANSWER_CHOICE_POINT[1]};
    public final static int[] ANSWER_CHOICE_POINT_C = {ANSWER_CHOICE_POINT[0], ANSWER_CHOICE_POINT[3]};
    public final static int[] ANSWER_CHOICE_POINT_D = {ANSWER_CHOICE_POINT[2], ANSWER_CHOICE_POINT[3]};

    public static final String TEMPLATE_PIC_BRIGHT = Constants.RESOURCE_DIR + "template-bright.bmp";
    public static final String TEMPLATE_PIC_DARK = Constants.RESOURCE_DIR + "template-dark.bmp";
    public static final String TEMPLATE_PIC_ALL = TEMPLATE_PIC_BRIGHT + "|" + TEMPLATE_PIC_DARK;

    private final DmDdt dm;

    private boolean identifyCaptchaRunning;
    private long lastCaptchaTime;
    private String lastRemoteCaptchaId;

    private final long[] errorTimeRange = {1000, 10000};

    private Captcha(DmDdt dm) {
        this.dm = dm;
        this.identifyCaptchaRunning = false;
        lastCaptchaTime = 0;
        lastRemoteCaptchaId = null;
    }

    public static Captcha createInstance(DmDdt dm) {
        Captcha captcha = GlobalVariable.CAPTCHA_MAP.get(dm.getHwnd());
        if (captcha != null) {
            return captcha;
        }

        captcha = new Captcha(dm);
        GlobalVariable.CAPTCHA_MAP.put(dm.getHwnd(), captcha);
        return captcha;
    }

    public static boolean isRunning(Integer hwnd) {
        Captcha captcha = GlobalVariable.CAPTCHA_MAP.get(hwnd);
        if (captcha == null) {
            return false;
        }

        return captcha.identifyCaptchaRunning;
    }

    public void captureCaptchaAllRegionPic(String path) {
        dm.capturePicByRegion(path, CAPTCHA_FULL_REACT);
    }

    public boolean findCaptcha(String templatePath) {
        int[] pic = dm.findPic(CAPTCHA_TITLE_REACT[0], CAPTCHA_TITLE_REACT[1], CAPTCHA_TITLE_REACT[2], CAPTCHA_TITLE_REACT[3], templatePath, "050505", 0.9, 0);
        return pic[0] > 0;
    }

    public React findCaptchaReact(String templatePath) {
        return dm.findPicS(CAPTCHA_TITLE_REACT[0], CAPTCHA_TITLE_REACT[1], CAPTCHA_TITLE_REACT[2], CAPTCHA_TITLE_REACT[3], templatePath, "050505", 0.9, 0);
    }

    public void captureCaptchaQuestionPic(String path) {
        dm.capturePicByRegion(path, CAPTCHA_QUESTION_REACT);
    }

    public void captureCaptchaCountDownPic(String path) {
        dm.capturePicByRegion(path, CAPTCHA_COUNT_DOWN_REACT);
    }

    public void identifyCaptchaLoop(UserConfig userConfig) {
        if (identifyCaptchaRunning) {
            log.info("[{}] 线程已经在运行中了", dm.getHwnd());
            return;
        }

        boolean isFlashWindow = dm.isWindowClassFlashPlayerActiveX();
        if (!isFlashWindow) {
            dm.unbind();
            return;
        }

        dm.bind();

        identifyCaptchaRunning = true;
        log.info("绑定 flash 窗口，句柄：{}", dm.getHwnd());
        long lastLogTime = System.currentTimeMillis();
        do {
            // 输出日志
            long now = System.currentTimeMillis();
            Long logPrintInterval = userConfig.getLogPrintInterval();
            if (logPrintInterval != null && logPrintInterval > 0) {
                if (now - lastLogTime > logPrintInterval) {
                    log.info("[{}] 窗口正在运行", dm.getHwnd());
                    lastLogTime = System.currentTimeMillis();
                }
            }

            // 真正检测代码
        } while (identifyCaptcha(userConfig));
    }

    public boolean identifyCaptcha(UserConfig userConfig) {
        // 每次识屏间隔
        Util.sleep(userConfig.getCaptureInterval());
        // 判断是否还是 flash
        if (!dm.isWindowClassFlashPlayerActiveX()) {
            log.info("[{}] 当前句柄不为 flash 窗口，解绑！", dm.getHwnd());
            dm.unBindWindow();
            return false;
        }

        // 没找到
        if (!findCaptcha(TEMPLATE_PIC_ALL)) {
            return true;
        }
        log.info("[{}] 发现验证码！", dm.getHwnd());

        // 上报错误，如果有
        reportErrorResult(this.lastRemoteCaptchaId);

        // 设置按钮缓存
        CaptchaLogic.TIME_CACHER.set(CaptchaLogic.HAS_FOUND_KEY, System.currentTimeMillis(), CaptchaLogic.S, ExpireWayEnum.AFTER_UPDATE);

        log.info("点亮屏幕");
        dm.leftClick(100, 100, 100);
        dm.leftClick(100, 100, 100);
        Util.sleep(300);
        dm.leftClick(100, 100, 100);
        Util.sleep(100);

        // 验证码保存路径
        String captchaDir = "captcha/" + Util.getTimeString(Util.TIME_YMD_FORMAT) + "/";
        String captchaName = captchaDir + dm.getHwnd() + "-" + Util.getTimeString(Util.TIME_HMS_FORMAT) + ".png";
        File file = new File(captchaDir);
        if (!file.isDirectory()) {
            boolean mkdirs = file.mkdirs();
        }

        // 截屏
        captureCaptchaQuestionPic(captchaName);
        log.info("[{}] 验证码保存到本地，文件名为：{}", dm.getHwnd(), captchaName);

        // 提交平台识别
        TjResponse response = new TjResponse();
        if (userConfig.validUserInfo()) {
            TjPredictDto tjPredictDto = TjPredictDto.build(userConfig, captchaName);
            log.info("[{}] 提交识别...", dm.getHwnd());
            response = TjHttpUtil.waitToGetChoice(userConfig.getTimeout(), userConfig.getKeyPressDelayAfterCaptchaDisappear(), tjPredictDto);
        } else {
            log.info("[{}] 用户名或密码为空，无法提交打码平台", dm.getHwnd());
            response.setChoiceEnum(ChoiceEnum.UNDEFINED);
        }
        this.lastRemoteCaptchaId = response.getResult().getId();

        // 获取结果
        ChoiceEnum choiceEnum = response.getChoiceEnum();
        // 判断是否还是 flash
        if (!dm.isWindowClassFlashPlayerActiveX()) {
            log.info("[{}] 当前句柄不为 flash 窗口，解绑！", dm.getHwnd());
            dm.unBindWindow();
            return false;
        }

        log.info("[{}] 选择结果 {}", dm.getHwnd(), response);
        if (ChoiceEnum.UNDEFINED.equals(choiceEnum)) {
            // 报错
            reportErrorResult(response.getResult().getId());

            // 识别错误，那么走用户自定义
            String defaultChoiceAnswer = userConfig.getDefaultChoiceAnswer();
            if (defaultChoiceAnswer == null || defaultChoiceAnswer.length() == 0) {
                log.info("[{}] 用户没有设置默认选项，跳过选择，等待 5000 毫秒继续下一轮检测", dm.getHwnd());
                Util.sleep(5000);
                return true;
            }
            choiceEnum = ChoiceEnum.getChoice(defaultChoiceAnswer);
            if (ChoiceEnum.UNDEFINED.equals(choiceEnum)) {
                // 用户选了 abcd 之外的，默认赋值 a
                choiceEnum = ChoiceEnum.A;
            }
            log.info("[{}] 平台返回结果格式不正确，进行用户自定义选择，{}", dm.getHwnd(), choiceEnum);
        }

        // 点击选项
        dm.leftClick(choiceEnum.getXy(), 100);
        Util.sleep(300);
        dm.leftClick(choiceEnum.getXy(), 100);

        // 提交答案
        Util.sleep(500);
        dm.leftClick(SUBMIT_BUTTON_POINT, 100);

        this.lastCaptchaTime = System.currentTimeMillis();
        Util.sleep(1000);
        return true;
    }

    public void reportErrorResult(String id) {
        long timeSub = System.currentTimeMillis() - this.lastCaptchaTime;
        if (timeSub < this.errorTimeRange[0] || timeSub > this.errorTimeRange[1]) {
            return;
        }
        if (StringUtils.isBlank(id)) {
            return;
        }

        log.info("[{}] 对上一次错误打码报错给平台，id = {}", dm.getHwnd(), id);
        this.lastRemoteCaptchaId = null;
        GlobalVariable.THREAD_POOL.execute(() -> {
            try {
                TjHttpUtil.reportError(id);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        });
    }

    public static boolean startIdentifyCaptcha(Integer hwnd, UserConfig userConfig) {
        if (Captcha.isRunning(hwnd)) {
            return false;
        }
        GlobalVariable.THREAD_POOL.execute(() -> Captcha.createInstance(DmDdt.createInstance(hwnd)).identifyCaptchaLoop(userConfig));
        return true;
    }
}
