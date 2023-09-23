package cn.sleepybear.ddtassistant.type.captcha;

import cn.sleepybear.cacher.cache.ExpireWayEnum;
import cn.sleepybear.ddtassistant.base.BindWindowConfig;
import cn.sleepybear.ddtassistant.base.CaptchaConfig;
import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.base.UserConfig;
import cn.sleepybear.ddtassistant.constant.Constants;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.dm.DmConstants;
import cn.sleepybear.ddtassistant.dm.DmDdt;
import cn.sleepybear.ddtassistant.dm.DmDomains;
import cn.sleepybear.ddtassistant.logic.MonitorLogic;
import cn.sleepybear.ddtassistant.type.BaseType;
import cn.sleepybear.ddtassistant.type.TypeConstants;
import cn.sleepybear.ddtassistant.utils.ImgUtil;
import cn.sleepybear.ddtassistant.utils.OcrUtil;
import cn.sleepybear.ddtassistant.utils.SpringContextUtil;
import cn.sleepybear.ddtassistant.utils.Util;
import cn.sleepybear.ddtassistant.utils.captcha.*;
import cn.sleepybear.ddtassistant.utils.captcha.tj.TjResponse;
import cn.sleepybear.ddtassistant.utils.captcha.way.BaseCaptchaWay;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serial;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author sleepybear
 */
@Slf4j
public class Captcha extends BaseType {
    @Serial
    private static final long serialVersionUID = -2259938240133602111L;
    private static final Logger logMore = LoggerFactory.getLogger("MORE");

    private final Map<CaptchaChoiceEnum, CaptchaImgInfo> captchaInfoMap = new HashMap<>();

    private static boolean hasGetUserInfo = false;

    /**
     * 上一张验证码图
     */
    private final LastCaptchaImg lastCaptchaImg = new LastCaptchaImg();

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
        if (!GlobalVariable.ocrSuccess) {
            return null;
        }
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
        SettingConfig settingConfig = SpringContextUtil.getBean(SettingConfig.class);
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
        getDm().bind(settingConfig.getBindWindowConfig().getMouseTypeEnum(), settingConfig.getBindWindowConfig().getKeypadTypeEnum());
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
                        logMore.info("[{}] 窗口正在运行", hwnd);
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
        Integer choice = captchaConfig.getCaptchaWay().get(0);
        if (choice == null || choice == 0) {
            // 未设置打码
            return;
        }

        if (!findCaptcha()) {
            // 没找到验证码
            return;
        }

        // 验证码保存路径
        String captchaDir = Constants.CAPTCHA_DIR + Util.getTimeString(Util.TIME_YMD_FORMAT) + "/";
        String captchaName = captchaDir + getHwnd() + "-" + Util.getTimeString(Util.TIME_HMS_FORMAT) + ".png";

        // 截屏
        captureCaptchaQuestionPic(captchaName);
        log.info("[{}] 验证码保存到本地，文件名为：{}", getHwnd(), captchaName);
        if (!captchaValid(captchaName)) {
            getDm().clickCorner();
            log.info("[{}] 保存验证码图片为非验证码内容，不予上传！", getHwnd());
            return;
        }
        log.info("[{}] 发现副本验证码！", getHwnd());

        // 读取发现的验证码图片到内存
        BufferedImage current = ImgUtil.read(captchaName);
        // 判断是否为与上一次重复的验证码
        if (isSameCaptcha(lastCaptchaImg, current)) {
            log.info("[{}] 验证码第[{}]次重复！！请检查程序点击功能是否正常！！请打开网页进入“测试功能正常”中进行点击功能检查！！", getHwnd(), lastCaptchaImg.getSameTimes());
            lastCaptchaImg.addSameTimes();

            int sameTimes = 5;
            if (lastCaptchaImg.getSameTimes() % sameTimes != 0) {
                // 如果重复次数不是 5 的倍数，那么点击坐标之后，直接跳过上传到平台
                clickCaptchaAnswer(lastCaptchaImg.getChoiceEnum(), userConfig.getDefaultChoiceAnswer());
                Util.sleep(1000L);
                return;
            }
            // 如果是 5 的倍数，那么继续走原有逻辑，上传到平台、获取结果、打码
        } else {
            // 与上一次不为同一张验证码，那么清空重复次数
            lastCaptchaImg.setSameTimes(0);
        }
        lastCaptchaImg.setImg(current);
        lastCaptchaImg.setChoiceEnum(ChoiceEnum.UNDEFINED);

        // 设置验证码出现的按钮缓存
        MonitorLogic.TIME_CACHER.set(MonitorLogic.CAPTCHA_FOUND_KEY, System.currentTimeMillis(), MonitorLogic.CAPTCHA_DELAY, ExpireWayEnum.AFTER_UPDATE);

        // 上报错误，如果有
        CaptchaImgInfo lastErrorCaptchaImgInfo = null;
        if (!captchaInfoMap.isEmpty()) {
            for (CaptchaImgInfo captchaImgInfo : captchaInfoMap.values()) {
                if (lastErrorCaptchaImgInfo == null) {
                    lastErrorCaptchaImgInfo = captchaImgInfo;
                    continue;
                }

                if (lastErrorCaptchaImgInfo.getLastCaptchaTime() < captchaImgInfo.getLastCaptchaTime()) {
                    lastErrorCaptchaImgInfo = captchaImgInfo;
                }
            }
            BaseResponse.reportError(getHwnd(), lastErrorCaptchaImgInfo, false);
        }

        // 获取打码结果
        BaseResponse response = getCaptchaAnswer(captchaConfig, userConfig, captchaName);
        if (response == null) {
            // 内部非验证码返回 null，跳出
            return;
        }

        // 获取结果
        ChoiceEnum choiceEnum = response.getChoiceEnum();
        lastCaptchaImg.setChoiceEnum(choiceEnum);
        ChoiceEnum choiceEnumResult = clickCaptchaAnswer(choiceEnum, userConfig.getDefaultChoiceAnswer());

        // 上传服务器验证码结果
        GlobalVariable.THREAD_POOL.execute(() -> Util.uploadToServer(captchaName, choiceEnumResult));

        Util.sleep(1000L);
    }

    /**
     * 遍历所有的打码方式，返回打码结果
     *
     * @param captchaConfig captchaConfig
     * @param userConfig    userConfig
     * @param captchaName   captchaName
     * @return 如果不为 null 那么就结果，否则上层方法跳出循环
     */
    private BaseResponse getCaptchaAnswer(CaptchaConfig captchaConfig, UserConfig userConfig, String captchaName) {
        // 空结果
        BaseResponse response = TjResponse.buildEmptyResponse();
        // 遍历所有的打码方式
        boolean first = true;
        for (Integer captchaInfoId : captchaConfig.getCaptchaWay()) {
            // 未选择直接退出
            if (captchaInfoId == null || captchaInfoId == 0) {
                break;
            }

            List<CaptchaInfo> captchaInfoList = captchaConfig.getCaptchaInfoList();
            CaptchaInfo captchaInfo = null;
            if (CollectionUtils.isNotEmpty(captchaInfoList)) {
                for (CaptchaInfo info : captchaInfoList) {
                    if (captchaInfoId.equals(info.getId())) {
                        captchaInfo = info;
                        break;
                    }
                }
            }

            if (captchaInfo == null) {
                // 这里为空的情况一般是有删除打码导致获取不到，那么走下一个打码
                continue;
            }

            if (!first && !findCaptcha()) {
                // 没找到验证码（二次验证是否有验证码）
                return null;
            }
            first = false;

            // 倒计时时间
            Integer countDown = captureAndOcrCountDown();

            // 判断倒计时剩余时间
            CaptchaChoiceEnum captchaChoiceEnum = CaptchaChoiceEnum.getChoice(captchaInfo.getCaptchaType());
            Integer minAnswerTime = captchaChoiceEnum.getMinAnswerTime();
            if (countDown != null && countDown <= minAnswerTime) {
                response.setChoiceEnum(ChoiceEnum.UNDEFINED);
                log.info("[{}] 验证码倒计时剩下 {} 秒，[{}]来不及提交打码", getHwnd(), countDown, captchaChoiceEnum.getName());
                continue;
            }

            // 倒计时剩余时间
            long countDownTime = countDown == null ? 18 * 1000 : countDown * 1000L;
            log.info("[{}] 准备提交[{}]识别...倒计时还剩下 {} 秒", getHwnd(), captchaChoiceEnum.getName(), countDown);

            // 单个打码
            BaseCaptchaWay baseCaptchaWay = BaseCaptchaWay.buildBaseCaptchaWay(captchaInfo);
            // 验证身份
            if (!baseCaptchaWay.validUserInfo()) {
                log.info("[{}] 用户名或密码填写错误，无法提交打码平台", captchaChoiceEnum.getName());
                response.setChoiceEnum(ChoiceEnum.UNDEFINED);
                continue;
            }

            // 打码请求体
            BasePredictDto basePredictDto = baseCaptchaWay.getBasePredictDto();
            basePredictDto.setImgFile(captchaName);
            response = CaptchaUtil.waitToGetChoice(getHwnd(), countDownTime - 2000L, userConfig.getKeyPressDelayAfterCaptchaDisappear(), basePredictDto);
            if (!basePredictDto.getResponseClass().isInstance(response)) {
                log.info("[{}] 解析返回结果失败，可能是网络波动或者平台暂时出现问题等", getHwnd());
                // 解析错误直接返回
                continue;
            }

            // 打码 id
            String captchaId = response.getCaptchaId();
            CaptchaImgInfo captchaImgInfo = captchaInfoMap.get(captchaChoiceEnum);
            if (captchaImgInfo == null) {
                captchaImgInfo = new CaptchaImgInfo(captchaChoiceEnum, System.currentTimeMillis(), captchaId, captchaName);
                captchaInfoMap.put(captchaChoiceEnum, captchaImgInfo);
            } else {
                captchaImgInfo.renew(System.currentTimeMillis(), captchaId, captchaName);
            }

            if (!ChoiceEnum.UNDEFINED.equals(response.getChoiceEnum())) {
                if (captchaImgInfo.getCount() % 10 == 0) {
                    // 每 10 次验证码请求一次余额
                    basePredictDto.lowBalanceRemind(captchaConfig);
                }

                // 验证码次数收集
                GlobalVariable.INFO_COLLECT_DTO.addCaptchaCount(captchaChoiceEnum);
                // 获取到正常选项，退出
                break;
            }
            // 非 ABCD 选项，报错
            BaseResponse.reportError(getHwnd(), captchaImgInfo, true);
        }
        return response;
    }

    private ChoiceEnum clickCaptchaAnswer(ChoiceEnum choiceEnum, String defaultChoiceAnswer) {
        ChoiceEnum resChoice = ChoiceEnum.UNDEFINED;
        // 判断是否还是 flash
        if (!getDm().isWindowClassFlashPlayerActiveX()) {
            return resChoice;
        }

        if (ChoiceEnum.UNDEFINED.equals(choiceEnum)) {
            // 识别错误，那么走用户自定义
            if (defaultChoiceAnswer == null || defaultChoiceAnswer.isEmpty()) {
                log.info("[{}] 用户没有设置默认选项，跳过选择，等待 5000 毫秒继续下一轮检测", getHwnd());
                Util.sleep(5000L);
                return resChoice;
            }
            choiceEnum = ChoiceEnum.getChoice(defaultChoiceAnswer);
            if (ChoiceEnum.UNDEFINED.equals(choiceEnum)) {
                // 用户选了 abcd 之外的，默认赋值 a
                choiceEnum = ChoiceEnum.A;
            }
            log.info("[{}] 平台返回结果格式不正确，进行用户自定义选择，{}", getHwnd(), choiceEnum);
        } else {
            log.info("[{}] 选择结果 {}, 坐标={}", getHwnd(), choiceEnum.getChoice(), choiceEnum.getXy());

            // 上传结果到服务器
            resChoice = choiceEnum;
        }

        // 点击选项
        getDm().leftClick(choiceEnum.getXy(), 100);
        Util.sleep(300L);
        getDm().leftClick(choiceEnum.getXy(), 100);

        // 提交答案
        Util.sleep(300L);
        getDm().leftClick(CaptchaConstants.SUBMIT_BUTTON_POINT, 100);
        return resChoice;
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

        MonitorLogic.FLOP_BONUS_CACHER.set(MonitorLogic.FLOP_BONUS_FOUND_KEY, Pair.of(pveFlopBonusAppearDelay, getDm()), userConfig.getPveFlopBonusDisappearDelay(), ExpireWayEnum.AFTER_UPDATE);
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

    public static boolean isSameCaptcha(LastCaptchaImg last, BufferedImage current) {
        if (last == null || last.getImg() == null) {
            return false;
        }
        if (current == null) {
            return true;
        }

        int w = 30;
        int h = 30;
        int[][] points = {{30, 30}, {30, 90}, {30, 180}, {120, 30}, {120, 90}, {120, 180}};
        int delta = 5;
        int repeatCount = 0;
        int totalDelta = 0;
        for (int[] point : points) {
            int[] avgColor0 = ImgUtil.getAvgColor(last.getImg().getSubimage(point[0], point[1], w, h));
            int[] avgColor1 = ImgUtil.getAvgColor(current.getSubimage(point[0], point[1], w, h));
            for (int i = 0; i < avgColor0.length; i++) {
                int abs = Math.abs(avgColor0[i] - avgColor1[i]);
                if (abs < delta) {
                    repeatCount++;
                }
                totalDelta += abs;
            }
        }
        return repeatCount >= (points.length * 3 - 5) && totalDelta < delta * 3;
    }

    public static boolean startIdentifyCaptcha(Integer hwnd, CaptchaConfig captchaConfig, BindWindowConfig bindWindowConfig) {
        GlobalVariable.THREAD_POOL.execute(() -> {
            if (!hasGetUserInfo) {
                hasGetUserInfo = true;
                for (Integer captchaInfoId : new HashSet<>(captchaConfig.getCaptchaWay())) {
                    CaptchaInfo captchaInfo = captchaConfig.getByCaptchaInfoId(captchaInfoId);
                    if (captchaInfo == null) {
                        continue;
                    }

                    BasePredictDto basePredictDto = BaseCaptchaWay.buildBaseCaptchaWay(captchaInfo).getBasePredictDto();
                    if (basePredictDto != null) {
                        String accountInfo = basePredictDto.getAccountInfo(captchaConfig);
                        log.info(accountInfo);
                    }
                }
            }
        });

        if (isRunning(hwnd, Captcha.class)) {
            return false;
        }
        GlobalVariable.THREAD_POOL.execute(() -> Captcha.createInstance(hwnd, Captcha.class, false, bindWindowConfig).identifyCaptchaLoop());
        return true;
    }
}
