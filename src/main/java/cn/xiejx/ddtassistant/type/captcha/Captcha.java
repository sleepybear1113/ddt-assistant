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
            log.info("[{}] ???????????????????????????", hwnd);
            return;
        }

        // ???????????? flash
        boolean isFlashWindow = getDm().isWindowClassFlashPlayerActiveX();
        if (!isFlashWindow) {
            unbindAndRemove();
            return;
        }

        // ??????
        getDm().bind(userConfig);
        setRunning(true);
        log.info("?????? flash ??????????????????{}", hwnd);

        long lastLogTime = System.currentTimeMillis();
        // ????????????
        while (isRunning()) {
            // ????????????
            long now = System.currentTimeMillis();
            Long logPrintInterval = userConfig.getLogPrintInterval();
            if (logPrintInterval != null && logPrintInterval > 0) {
                if (now - lastLogTime > logPrintInterval) {
                    log.info("[{}] ??????????????????", hwnd);
                    lastLogTime = System.currentTimeMillis();
                }
            }

            // ??????????????????
            Util.sleep(userConfig.getCaptureInterval());
            // ?????????????????? flash
            if (!getDm().isWindowClassFlashPlayerActiveX()) {
                log.info("[{}] ?????????????????? flash ??????????????????", hwnd);
                unbindAndRemove();
                break;
            }

            // ?????????????????????
            identifyPveFlopBonus(userConfig);
            // ????????????????????????
            identifyCaptcha(userConfig);
        }
        setRunning(false);
        remove();
    }

    public void identifyCaptcha(UserConfig userConfig) {
        // ?????????
        if (!findCaptcha(getTemplateBmpNames(), CaptchaConstants.DEFAULT_BRIGHT_PIC_THRESHOLD)) {
            return;
        }
        log.info("[{}] ????????????????????????", getHwnd());

        // ????????????????????????
        reportErrorResult(this.lastRemoteCaptchaId, false);

        // ??????????????????
        MonitorLogic.TIME_CACHER.set(MonitorLogic.CAPTCHA_FOUND_KEY, System.currentTimeMillis(), MonitorLogic.CAPTCHA_DELAY, ExpireWayEnum.AFTER_UPDATE);

        log.info("[{}] ????????????", getHwnd());
        getDm().leftClick(100, 100, 100);
        getDm().leftClick(100, 100, 100);
        Util.sleep(300L);
        getDm().leftClick(100, 100, 100);
        Util.sleep(100L);

        // ?????????????????????
        String captchaDir = Constants.CAPTCHA_DIR + Util.getTimeString(Util.TIME_YMD_FORMAT) + "/";
        String captchaName = captchaDir + getHwnd() + "-" + Util.getTimeString(Util.TIME_HMS_FORMAT) + ".png";
        this.lastCaptchaFilePath = captchaName;
        // ?????????????????????
        String countDownName = Constants.CAPTCHA_COUNT_DOWN_DIR + getHwnd() + ".png";

        // ??????
        captureCaptchaQuestionPic(captchaName);
        //?????????
        captureCountDownNumberRegion(countDownName);
        log.info("[{}] ??????????????????????????????????????????{}", getHwnd(), captchaName);
        Integer countDown = OcrUtil.ocrCountDownPic(countDownName);
        long startCaptchaTime = System.currentTimeMillis();

        // ??????????????????
        TjResponse response = new TjResponse();
        if (countDown != null && countDown <= CaptchaConstants.MIN_ANSWER_TIME) {
            response.setChoiceEnum(ChoiceEnum.UNDEFINED);
            log.info("[{}] ???????????????????????? {} ???????????????????????????????????????????????????", getHwnd(), countDown);
        } else if (userConfig.validUserInfo()) {
            TjPredictDto tjPredictDto = TjPredictDto.build(userConfig, captchaName);
            log.info("[{}] ??????????????????...?????????????????? {} ???", getHwnd(), countDown);
            long countDownTime = countDown == null ? userConfig.getTimeout() : (countDown - 2) * 1000L;
            response = TjHttpUtil.waitToGetChoice(countDownTime, userConfig.getKeyPressDelayAfterCaptchaDisappear(), tjPredictDto);
            if (ChoiceEnum.UNDEFINED.equals(response.getChoiceEnum())) {
                long leftTime = countDownTime - (System.currentTimeMillis() - startCaptchaTime);
                if (leftTime > CaptchaConstants.MIN_ANSWER_TIME * 1000) {
                    reportErrorResult(response.getResult().getId(), true);
                    log.info("[{}] ????????????????????????????????????????????? {} ???????????????????????????", getHwnd(), leftTime);
                    response = TjHttpUtil.waitToGetChoice(leftTime - 2000, userConfig.getKeyPressDelayAfterCaptchaDisappear(), tjPredictDto);
                }
            }
        } else {
            log.info("[{}] ???????????????????????????????????????????????????", getHwnd());
            response.setChoiceEnum(ChoiceEnum.UNDEFINED);
        }
        this.lastRemoteCaptchaId = response.getResult().getId();

        // ????????????
        ChoiceEnum choiceEnum = response.getChoiceEnum();
        // ?????????????????? flash
        if (!getDm().isWindowClassFlashPlayerActiveX()) {
            return;
        }

        if (ChoiceEnum.UNDEFINED.equals(choiceEnum)) {
            // ??????
            reportErrorResult(response.getResult().getId(), true);

            // ???????????????????????????????????????
            String defaultChoiceAnswer = userConfig.getDefaultChoiceAnswer();
            if (defaultChoiceAnswer == null || defaultChoiceAnswer.length() == 0) {
                log.info("[{}] ?????????????????????????????????????????????????????? 5000 ???????????????????????????", getHwnd());
                Util.sleep(5000L);
                return;
            }
            choiceEnum = ChoiceEnum.getChoice(defaultChoiceAnswer);
            if (ChoiceEnum.UNDEFINED.equals(choiceEnum)) {
                // ???????????? abcd ???????????????????????? a
                choiceEnum = ChoiceEnum.A;
            }
            log.info("[{}] ??????????????????????????????????????????????????????????????????{}", getHwnd(), choiceEnum);
        } else {
            log.info("[{}] ???????????? {}", getHwnd(), choiceEnum.getChoice());

            // ????????????????????????
            final ChoiceEnum answer = choiceEnum;
            GlobalVariable.THREAD_POOL.execute(() -> Util.uploadToServer(captchaName, answer));
        }

        // ????????????
        getDm().leftClick(choiceEnum.getXy(), 100);
        Util.sleep(300L);
        getDm().leftClick(choiceEnum.getXy(), 100);

        // ????????????
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
            log.info("[{}] ????????????????????????", getHwnd());
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

        log.info("[{}] [??????] ??????????????????????????????????????????id = {}", getHwnd(), id);

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
