package cn.xiejx.ddtassistant.type.captcha;

import cn.xiejx.ddtassistant.base.UserConfig;
import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmConstants;
import cn.xiejx.ddtassistant.logic.MonitorLogic;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.type.BaseType;
import cn.xiejx.ddtassistant.utils.OcrUtil;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.cache.ExpireWayEnum;
import cn.xiejx.ddtassistant.utils.tj.ChoiceEnum;
import cn.xiejx.ddtassistant.utils.tj.TjHttpUtil;
import cn.xiejx.ddtassistant.utils.tj.TjPredictDto;
import cn.xiejx.ddtassistant.utils.tj.TjResponse;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sleepybear
 */
@Slf4j
public class Captcha extends BaseType {

    private static final long serialVersionUID = -2259938240133602111L;
    public static List<String> captchaTemplateNameList = new ArrayList<>();
    public static List<String> captchaTemplateNameDarkList = new ArrayList<>();
    public static List<String> flopBonusTemplateNameList = new ArrayList<>();

    private long lastCaptchaTime;
    private String lastRemoteCaptchaId;
    private String lastCaptchaFilePath;

    private final long[] errorTimeRange = {1000, 10000};

    private static boolean hasGetUserInfo = false;

    public Captcha() {
    }

    private Captcha(DmDdt dm) {
        init(dm);
    }

    @Override
    public void init(DmDdt dm) {
        super.init(dm);
        lastCaptchaTime = 0;
        lastRemoteCaptchaId = null;
        lastCaptchaFilePath = null;
    }

    public void captureCaptchaAllRegionPic(String path) {
        getDm().capturePicByRegion(path, CaptchaConstants.CAPTCHA_FULL_REACT);
    }

    public boolean findCaptcha(String templatePath, double threshold) {
        return findPicture(templatePath, threshold, CaptchaConstants.CAPTCHA_COUNTDOWN_FIND_REACT);
    }

    public boolean findFlopBonus(String templatePath, double threshold) {
        return findPicture(templatePath, threshold, CaptchaConstants.FLOP_BONUS_DETECT_RECT);
    }

    private boolean findPicture(String templatePath, double threshold, int[] rect) {
        if (StringUtils.isBlank(templatePath)) {
            return false;
        }
        int[] pic = getDm().findPic(rect, templatePath, "010101", threshold, DmConstants.SearchWay.LEFT2RIGHT_UP2DOWN);
        return pic[0] > 0;
    }

    public void captureCaptchaQuestionPic(String path) {
        getDm().capturePicByRegion(path, CaptchaConstants.CAPTCHA_QUESTION_REACT);
    }

    public void captureCaptchaCountDownPic(String path) {
        getDm().capturePicByRegion(path, CaptchaConstants.CAPTCHA_COUNT_DOWN_REACT);
    }

    public void captureCountDownSampleRegion(String path) {
        getDm().capturePicByRegion(path, CaptchaConstants.CAPTCHA_COUNTDOWN_SAMPLE_REACT);
    }

    public void captureCountDownNumberRegion(String path) {
        getDm().capturePicByRegion(path, CaptchaConstants.COUNT_DOWN_NUMBER_RECT);
    }

    public void identifyCaptchaLoop(UserConfig userConfig) {
        Integer hwnd = getHwnd();
        if (isRunning()) {
            log.info("[{}] 线程已经在运行中了", hwnd);
            return;
        }

        // 判断是否 flash
        boolean isFlashWindow = getDm().isWindowClassFlashPlayerActiveX();
        if (!isFlashWindow) {
            unbindAndRemove();
            return;
        }

        // 绑定
        getDm().bind(userConfig);
        setRunning(true);
        log.info("绑定 flash 窗口，句柄：{}", hwnd);

        long lastLogTime = System.currentTimeMillis();
        // 开始运行
        while (isRunning()) {
            // 输出日志
            long now = System.currentTimeMillis();
            Long logPrintInterval = userConfig.getLogPrintInterval();
            if (logPrintInterval != null && logPrintInterval > 0) {
                if (now - lastLogTime > logPrintInterval) {
                    log.info("[{}] 窗口正在运行", hwnd);
                    lastLogTime = System.currentTimeMillis();
                }
            }

            // 每次识屏间隔
            Util.sleep(userConfig.getCaptureInterval());
            // 判断是否还是 flash
            if (!getDm().isWindowClassFlashPlayerActiveX()) {
                log.info("[{}] 当前句柄不为 flash 窗口，解绑！", hwnd);
                unbindAndRemove();
                break;
            }

            // 检测副本大翻牌
            identifyPveFlopBonus(userConfig);
            // 检测验证码的代码
            identifyCaptcha(userConfig);
        }
        setRunning(false);
        remove();
    }

    public void identifyCaptcha(UserConfig userConfig) {
        // 没找到
        if (!findCaptcha(getTemplateBmpNames(), CaptchaConstants.DEFAULT_BRIGHT_PIC_THRESHOLD)) {
            return;
        }
        log.info("[{}] 发现副本验证码！", getHwnd());

        // 上报错误，如果有
        reportErrorResult(this.lastRemoteCaptchaId, false);

        // 设置按钮缓存
        MonitorLogic.TIME_CACHER.set(MonitorLogic.CAPTCHA_FOUND_KEY, System.currentTimeMillis(), MonitorLogic.CAPTCHA_DELAY, ExpireWayEnum.AFTER_UPDATE);

        log.info("[{}] 点亮屏幕", getHwnd());
        getDm().leftClick(100, 100, 100);
        getDm().leftClick(100, 100, 100);
        Util.sleep(300L);
        getDm().leftClick(100, 100, 100);
        Util.sleep(100L);

        // 验证码保存路径
        String captchaDir = Constants.CAPTCHA_DIR + Util.getTimeString(Util.TIME_YMD_FORMAT) + "/";
        String captchaName = captchaDir + getHwnd() + "-" + Util.getTimeString(Util.TIME_HMS_FORMAT) + ".png";
        this.lastCaptchaFilePath = captchaName;
        // 倒计时保存路径
        String countDownName = Constants.CAPTCHA_COUNT_DOWN_DIR + getHwnd() + ".png";

        // 截屏
        captureCaptchaQuestionPic(captchaName);
        //倒计时
        captureCountDownNumberRegion(countDownName);
        log.info("[{}] 验证码保存到本地，文件名为：{}", getHwnd(), captchaName);
        Integer countDown = OcrUtil.ocrCountDownPic(countDownName);
        long startCaptchaTime = System.currentTimeMillis();

        // 提交平台识别
        TjResponse response = new TjResponse();
        if (countDown != null && countDown <= CaptchaConstants.MIN_ANSWER_TIME) {
            response.setChoiceEnum(ChoiceEnum.UNDEFINED);
            log.info("[{}] 验证码倒计时剩下 {} 秒，来不及提交打码，进行自定义选择", getHwnd(), countDown);
        } else if (userConfig.validUserInfo()) {
            TjPredictDto tjPredictDto = TjPredictDto.build(userConfig, captchaName);
            log.info("[{}] 提交平台识别...倒计时还剩下 {} 秒", getHwnd(), countDown);
            long countDownTime = countDown == null ? userConfig.getTimeout() : (countDown - 2) * 1000L;
            response = TjHttpUtil.waitToGetChoice(countDownTime, userConfig.getKeyPressDelayAfterCaptchaDisappear(), tjPredictDto);
            if (ChoiceEnum.UNDEFINED.equals(response.getChoiceEnum())) {
                long leftTime = countDownTime - (System.currentTimeMillis() - startCaptchaTime);
                if (leftTime > CaptchaConstants.MIN_ANSWER_TIME * 1000) {
                    reportErrorResult(response.getResult().getId(), true);
                    log.info("[{}] 平台返回结果有误，但倒计时仍有 {} 毫秒，再次请求平台", getHwnd(), leftTime);
                    response = TjHttpUtil.waitToGetChoice(leftTime - 2000, userConfig.getKeyPressDelayAfterCaptchaDisappear(), tjPredictDto);
                }
            }
        } else {
            log.info("[{}] 用户名或密码为空，无法提交打码平台", getHwnd());
            response.setChoiceEnum(ChoiceEnum.UNDEFINED);
        }
        this.lastRemoteCaptchaId = response.getResult().getId();

        // 获取结果
        ChoiceEnum choiceEnum = response.getChoiceEnum();
        // 判断是否还是 flash
        if (!getDm().isWindowClassFlashPlayerActiveX()) {
            return;
        }

        if (ChoiceEnum.UNDEFINED.equals(choiceEnum)) {
            // 报错
            reportErrorResult(response.getResult().getId(), true);

            // 识别错误，那么走用户自定义
            String defaultChoiceAnswer = userConfig.getDefaultChoiceAnswer();
            if (defaultChoiceAnswer == null || defaultChoiceAnswer.length() == 0) {
                log.info("[{}] 用户没有设置默认选项，跳过选择，等待 5000 毫秒继续下一轮检测", getHwnd());
                Util.sleep(5000L);
                return;
            }
            choiceEnum = ChoiceEnum.getChoice(defaultChoiceAnswer);
            if (ChoiceEnum.UNDEFINED.equals(choiceEnum)) {
                // 用户选了 abcd 之外的，默认赋值 a
                choiceEnum = ChoiceEnum.A;
            }
            log.info("[{}] 平台返回结果格式不正确，进行用户自定义选择，{}", getHwnd(), choiceEnum);
        } else {
            log.info("[{}] 选择结果 {}", getHwnd(), choiceEnum.getChoice());

            // 上传结果到服务器
            final ChoiceEnum answer = choiceEnum;
            GlobalVariable.THREAD_POOL.execute(() -> Util.uploadToServer(captchaName, answer));
        }

        // 点击选项
        getDm().leftClick(choiceEnum.getXy(), 100);
        Util.sleep(300L);
        getDm().leftClick(choiceEnum.getXy(), 100);

        // 提交答案
        Util.sleep(500L);
        getDm().leftClick(CaptchaConstants.SUBMIT_BUTTON_POINT, 100);

        this.lastCaptchaTime = System.currentTimeMillis();
        Util.sleep(1000L);
    }

    public void identifyPveFlopBonus(UserConfig userConfig) {
        Long pveFlopBonusAppearDelay = userConfig.getPveFlopBonusAppearDelay();
        if (pveFlopBonusAppearDelay == null || pveFlopBonusAppearDelay <= 0) {
            return;
        }
        if (!findFlopBonus(getFlopBonusTemplateBmpNames(), CaptchaConstants.DEFAULT_FLOP_BONUS_PIC_THRESHOLD)) {
            return;
        }
        if (MonitorLogic.FLOP_BONUS_CACHER.get(MonitorLogic.FLOP_BONUS_FOUND_KEY) == null) {
            log.info("[{}] 发现副本大翻牌！", getHwnd());
        }

        MonitorLogic.FLOP_BONUS_CACHER.set(MonitorLogic.FLOP_BONUS_FOUND_KEY, new Pair<>(pveFlopBonusAppearDelay, getDm()), userConfig.getPveFlopBonusDisappearDelay(), ExpireWayEnum.AFTER_UPDATE);
    }

    public void reportErrorResult(String id, boolean force) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        if (!force) {
            long timeSub = System.currentTimeMillis() - this.lastCaptchaTime;
            if (timeSub < this.errorTimeRange[0] || timeSub > this.errorTimeRange[1]) {
                return;
            }
        }

        log.info("[{}] [报错] 对上一次错误打码报错给平台，id = {}", getHwnd(), id);

        GlobalVariable.THREAD_POOL.execute(() -> {
            try {
                TjHttpUtil.reportError(id);
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        });

        GlobalVariable.THREAD_POOL.execute(() -> Util.deleteFileFromServer(this.lastCaptchaFilePath));

        this.lastRemoteCaptchaId = null;
        this.lastCaptchaFilePath = null;
    }

    public static boolean startIdentifyCaptcha(Integer hwnd, UserConfig userConfig) {
        GlobalVariable.THREAD_POOL.execute(() -> {
            if (!hasGetUserInfo) {
                hasGetUserInfo = true;
                String accountInfo = TjHttpUtil.getAccountInfo(userConfig.getUsername(), userConfig.getPassword(), userConfig.getLowBalanceRemind(), userConfig.getLowBalanceNum());
                log.info(accountInfo);
            }
        });

        if (isRunning(hwnd, Captcha.class)) {
            return false;
        }
        GlobalVariable.THREAD_POOL.execute(() -> Captcha.createInstance(DmDdt.createInstance(hwnd), Captcha.class).identifyCaptchaLoop(userConfig));
        return true;
    }

    public static String getFlopBonusTemplateBmpNames() {
        return StringUtils.join(Captcha.flopBonusTemplateNameList, "|");
    }

    public static String getTemplateBmpNames() {
        return StringUtils.join(Captcha.captchaTemplateNameList, "|");
    }

    public static String getTemplateDarkBmpNames() {
        return StringUtils.join(Captcha.captchaTemplateNameDarkList, "|");
    }
}
