package cn.xiejx.ddtassistant.type.captcha;

import cn.xiejx.ddtassistant.base.CaptchaConfig;
import cn.xiejx.ddtassistant.base.UserConfig;
import cn.xiejx.ddtassistant.constant.Constants;
import cn.xiejx.ddtassistant.constant.GlobalVariable;
import cn.xiejx.ddtassistant.dm.DmConstants;
import cn.xiejx.ddtassistant.dm.DmDomains;
import cn.xiejx.ddtassistant.logic.MonitorLogic;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.type.BaseType;
import cn.xiejx.ddtassistant.type.TypeConstants;
import cn.xiejx.ddtassistant.utils.ImgUtil;
import cn.xiejx.ddtassistant.utils.OcrUtil;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.cache.ExpireWayEnum;
import cn.xiejx.ddtassistant.utils.captcha.BaseResponse;
import cn.xiejx.ddtassistant.utils.captcha.CaptchaUtil;
import cn.xiejx.ddtassistant.utils.captcha.ChoiceEnum;
import cn.xiejx.ddtassistant.utils.captcha.pc.PcPredictDto;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjHttpUtil;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjPredictDto;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjResponse;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * @author sleepybear
 */
@Slf4j
public class Captcha extends BaseType {

    private static final long serialVersionUID = -2259938240133602111L;

    private long lastTjCaptchaTime;
    private String lastTjRemoteCaptchaId;
    private String lastCaptchaFilePath;

    private final long[] errorTimeRange = {1000, 10000};

    private static boolean hasGetUserInfo = false;

    /**
     * 请求验证码次数
     */
    public static int captchaCount = 0;
    /**
     * 是否发送过低余额邮件通知
     */
    public static boolean hasSendLowBalanceEmail = false;

    public Captcha() {
    }

    private Captcha(DmDdt dm) {
        init(dm);
    }

    @Override
    public void init(DmDdt dm) {
        super.init(dm);
        lastTjCaptchaTime = 0;
        lastTjRemoteCaptchaId = null;
        lastCaptchaFilePath = null;
    }

    public void captureCaptchaAllRegionPic(String path) {
        getDm().capturePicByRegion(path, CaptchaConstants.CAPTCHA_FULL_REACT);
    }

    public boolean findCaptcha() {
        List<String> templateImgList = GlobalVariable.getTemplateImgList(TypeConstants.TemplatePrefix.PVE_CAPTCHA_COUNT_DOWN);
        List<DmDomains.PicEx> picExList = getDm().findPicEx(CaptchaConstants.CAPTCHA_COUNTDOWN_FIND_REACT, templateImgList, "020202", 0.7);
        return CollectionUtils.isNotEmpty(picExList);
    }

    public boolean findFlopBonus() {
        List<String> templateImgList = GlobalVariable.getTemplateImgList(TypeConstants.TemplatePrefix.PVE_FLOP_BONUS);
        List<DmDomains.PicEx> picExList = getDm().findPicEx(CaptchaConstants.FLOP_BONUS_DETECT_RECT, templateImgList, "020202", 0.7);
        return CollectionUtils.isNotEmpty(picExList);
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

    public void capturePveFlopBonusSampleRegion(String path) {
        getDm().capturePicByRegion(path, CaptchaConstants.FLOP_BONUS_SAMPLE_RECT);
    }

    public void captureCountDownSampleRegion(String path) {
        getDm().capturePicByRegion(path, CaptchaConstants.CAPTCHA_COUNTDOWN_SAMPLE_REACT);
    }

    public void captureCountDownNumberRegion(String path) {
        getDm().capturePicByRegion(path, CaptchaConstants.COUNT_DOWN_NUMBER_RECT);
    }

    public Integer captureAndOcrCountDown() {
        // 倒计时保存路径
        String countDownName = Constants.CAPTCHA_COUNT_DOWN_DIR + getHwnd() + "-" + System.currentTimeMillis() + ".png";
        // 截屏倒计时
        captureCountDownNumberRegion(countDownName);
        Integer countDown = OcrUtil.ocrCountDownPic(countDownName);
        Util.delayDeleteFile(countDownName, null);
        return countDown;
    }

    public void identifyCaptchaLoop() {
        UserConfig userConfig = SpringContextUtil.getBean(UserConfig.class);
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
        getDm().bind(userConfig.getMouseMode(), userConfig.getKeyPadMode());
        setRunning(true);

        long lastLogTime = System.currentTimeMillis();
        try {
            // 开始运行
            while (isRunning()) {
                // 每次识屏间隔
                Long captureInterval = userConfig.getCaptureInterval();
                if (captureInterval == null || captureInterval <= 0) {
                    // 未设置则不管
                    Util.sleep(500L);
                    continue;
                }

                // 输出日志
                long now = System.currentTimeMillis();
                Long logPrintInterval = userConfig.getLogPrintInterval();
                if (logPrintInterval != null && logPrintInterval > 0) {
                    if (now - lastLogTime > logPrintInterval) {
                        log.info("[{}] 窗口正在运行", hwnd);
                        lastLogTime = System.currentTimeMillis();
                    }
                }

                Util.sleep(captureInterval);
                // 判断是否还是 flash
                if (!getDm().isWindowClassFlashPlayerActiveX()) {
                    log.info("[{}] 当前句柄不为 flash 窗口，解绑！", hwnd);
                    unbindAndRemove();
                    break;
                }

                // 检测副本大翻牌
                identifyPveFlopBonus();
                // 检测验证码的代码
                identifyCaptcha();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        setRunning(false);
        remove();
    }

    public void identifyCaptcha() {
        UserConfig userConfig = SpringContextUtil.getBean(UserConfig.class);
        CaptchaConfig captchaConfig = SpringContextUtil.getBean(CaptchaConfig.class);
        CaptchaConfig.CaptchaChoiceEnum captchaChoiceEnumTest = CaptchaConfig.CaptchaChoiceEnum.getChoice(captchaConfig.getCaptchaWay().get(0));
        if (CaptchaConfig.CaptchaChoiceEnum.NONE.equals(captchaChoiceEnumTest)) {
            // 未设置打码
            return;
        }

        // 没找到
        if (!findCaptcha()) {
            return;
        }
        log.info("[{}] 发现副本验证码！", getHwnd());

        // 上报错误，如果有
        reportErrorResult(this.lastTjRemoteCaptchaId, false);

        // 设置按钮缓存
        MonitorLogic.TIME_CACHER.set(MonitorLogic.CAPTCHA_FOUND_KEY, System.currentTimeMillis(), MonitorLogic.CAPTCHA_DELAY, ExpireWayEnum.AFTER_UPDATE);

        // 验证码保存路径
        String captchaDir = Constants.CAPTCHA_DIR + Util.getTimeString(Util.TIME_YMD_FORMAT) + "/";
        String captchaName = captchaDir + getHwnd() + "-" + Util.getTimeString(Util.TIME_HMS_FORMAT) + ".png";
        this.lastCaptchaFilePath = captchaName;

        // 截屏
        captureCaptchaQuestionPic(captchaName);
        log.info("[{}] 验证码保存到本地，文件名为：{}", getHwnd(), captchaName);
        if (!captchaValid(captchaName)) {
            getDm().clickCorner();
            log.info("[{}] 保存验证码图片为非验证码内容，不予上传！", getHwnd());
            return;
        }

        // 空结果
        BaseResponse response = TjResponse.buildEmptyResponse();

        // 遍历所有的打码方式
        for (Integer captchaWay : captchaConfig.getCaptchaWay()) {
            CaptchaConfig.CaptchaChoiceEnum captchaChoiceEnum = CaptchaConfig.CaptchaChoiceEnum.getChoice(captchaWay);

            // 未选择直接退出
            if (CaptchaConfig.CaptchaChoiceEnum.NONE.equals(captchaChoiceEnum)) {
                break;
            }

            // 倒计时时间
            Integer countDown = captureAndOcrCountDown();

            // 判断倒计时剩余时间
            Integer minAnswerTime = captchaChoiceEnum.getMinAnswerTime();
            if (countDown != null && countDown <= minAnswerTime) {
                response.setChoiceEnum(ChoiceEnum.UNDEFINED);
                log.info("[{}] 验证码倒计时剩下 {} 秒，[{}]来不及提交打码", getHwnd(), countDown, captchaChoiceEnum.getName());
                continue;
            }

            // 倒计时剩余时间
            long countDownTime = countDown == null ? 20 * 1000 : countDown * 1000L;
            log.info("[{}] 准备提交[{}]识别...倒计时还剩下 {} 秒", getHwnd(), captchaChoiceEnum.getName(), countDown);

            // 单个打码
            // 图鉴打码
            if (CaptchaConfig.CaptchaChoiceEnum.TJ.equals(captchaChoiceEnum)) {
                CaptchaConfig.Tj configTj = captchaConfig.getTj();
                if (!configTj.validUserInfo()) {
                    log.info("[{}] 用户名或密码填写错误，无法提交打码平台", captchaChoiceEnum.getName());
                    response.setChoiceEnum(ChoiceEnum.UNDEFINED);
                    continue;
                }

                TjPredictDto tjPredictDto = TjPredictDto.build(captchaConfig, captchaName);
                response = CaptchaUtil.waitToGetChoice(getHwnd(), countDownTime, userConfig.getKeyPressDelayAfterCaptchaDisappear(), tjPredictDto);
                if (!(response instanceof TjResponse)) {
                    // 解析错误直接返回
                    continue;
                }
                String captchaId = ((TjResponse) response).getData().getId();

                if (!ChoiceEnum.UNDEFINED.equals(response.getChoiceEnum())) {
                    if (!hasSendLowBalanceEmail && ++captchaCount % 10 == 0) {
                        // 每 10 次验证码请求一次余额
                        Runnable runnable = () -> TjHttpUtil.getAccountInfo(captchaConfig.getTj().getUsername(), captchaConfig.getTj().getPassword(), captchaConfig.getLowBalanceRemind(), captchaConfig.getLowBalanceNum());
                        GlobalVariable.THREAD_POOL.execute(runnable);
                    }
                    // 设置图鉴的报错
                    this.lastTjRemoteCaptchaId = captchaId;
                    this.lastTjCaptchaTime = System.currentTimeMillis();
                    // 获取到正常选项，退出
                    break;
                }
                // 非 ABCD 选项，报错
                reportErrorResult(captchaId, true);
                continue;
            }

            // 平川打码
            if (CaptchaConfig.CaptchaChoiceEnum.PC.equals(captchaChoiceEnum)) {
                PcPredictDto basePredictDto = new PcPredictDto(captchaName);
                response = CaptchaUtil.waitToGetChoice(getHwnd(), countDownTime, userConfig.getKeyPressDelayAfterCaptchaDisappear(), basePredictDto);
                if (!ChoiceEnum.UNDEFINED.equals(response.getChoiceEnum())) {
                    // 获取到正常选项，退出
                    break;
                }
            }
        }

        // 获取结果
        ChoiceEnum choiceEnum = response.getChoiceEnum();
        // 判断是否还是 flash
        if (!getDm().isWindowClassFlashPlayerActiveX()) {
            return;
        }

        if (ChoiceEnum.UNDEFINED.equals(choiceEnum)) {
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
        Util.sleep(300L);
        getDm().leftClick(CaptchaConstants.SUBMIT_BUTTON_POINT, 100);

        this.lastTjCaptchaTime = System.currentTimeMillis();
        Util.sleep(1000L);
    }

    public void identifyPveFlopBonus() {
        UserConfig userConfig = SpringContextUtil.getBean(UserConfig.class);
        Long pveFlopBonusAppearDelay = userConfig.getPveFlopBonusAppearDelay();
        if (pveFlopBonusAppearDelay == null || pveFlopBonusAppearDelay <= 0) {
            if (!Boolean.TRUE.equals(userConfig.getPveFlopBonusCapture())) {
                return;
            }
        }
        if (!findFlopBonus()) {
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
            long timeSub = System.currentTimeMillis() - this.lastTjCaptchaTime;
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

        this.lastTjRemoteCaptchaId = null;
        this.lastCaptchaFilePath = null;
    }

    public static boolean captchaValid(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            int[] rect = CaptchaConstants.CAPTCHA_SUB_VALID_RECT;
            BufferedImage subImage = img.getSubimage(rect[0], rect[1], rect[2] - rect[0], rect[3] - rect[1]);
            int[] avgColor = ImgUtil.getAvgColor(subImage);
            for (int i = 0; i < CaptchaConstants.CAPTCHA_VALID_COLOR.length; i++) {
                if (Math.abs(CaptchaConstants.CAPTCHA_VALID_COLOR[i] - avgColor[i]) > CaptchaConstants.CAPTCHA_VALID_DELTA_COLOR[i]) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean startIdentifyCaptcha(Integer hwnd, CaptchaConfig captchaConfig) {
        GlobalVariable.THREAD_POOL.execute(() -> {
            if (!hasGetUserInfo) {
                hasGetUserInfo = true;
                String accountInfo = TjHttpUtil.getAccountInfo(captchaConfig.getTj().getUsername(), captchaConfig.getTj().getPassword(), captchaConfig.getLowBalanceRemind(), captchaConfig.getLowBalanceNum());
                log.info(accountInfo);
            }
        });

        if (isRunning(hwnd, Captcha.class)) {
            return false;
        }
        GlobalVariable.THREAD_POOL.execute(() -> Captcha.createInstance(hwnd, Captcha.class, false).identifyCaptchaLoop());
        return true;
    }
}
