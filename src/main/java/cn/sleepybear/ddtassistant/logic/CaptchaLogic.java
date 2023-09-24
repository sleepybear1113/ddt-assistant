package cn.sleepybear.ddtassistant.logic;

import cn.sleepybear.ddtassistant.base.CaptchaConfig;
import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.constant.Constants;
import cn.sleepybear.ddtassistant.dm.DmDdt;
import cn.sleepybear.ddtassistant.exception.FrontException;
import cn.sleepybear.ddtassistant.type.BaseType;
import cn.sleepybear.ddtassistant.type.TypeConstants;
import cn.sleepybear.ddtassistant.type.captcha.Captcha;
import cn.sleepybear.ddtassistant.utils.Util;
import cn.sleepybear.ddtassistant.utils.captcha.BasePredictDto;
import cn.sleepybear.ddtassistant.utils.captcha.BaseResponse;
import cn.sleepybear.ddtassistant.utils.captcha.CaptchaInfo;
import cn.sleepybear.ddtassistant.utils.captcha.CaptchaUtil;
import cn.sleepybear.ddtassistant.utils.captcha.BaseCaptchaWay;
import cn.sleepybear.ddtassistant.vo.BindResultVo;
import cn.sleepybear.ddtassistant.vo.StringRet;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author sleepybear
 */
@Component
@Slf4j
public class CaptchaLogic {
    private static final Random RANDOM = new Random();

    private static final Logger logMore = LoggerFactory.getLogger("MORE");

    @Getter
    @Resource
    private CaptchaConfig captchaConfig;
    @Resource
    private DmDdt defaultDm;
    @Resource
    private SettingConfig settingConfig;

    public BaseResponse testCaptcha(Integer captchaInfoId) {
        if (captchaInfoId == null) {
            throw new FrontException("请选择打码平台");
        }
        String picPath = Constants.CAPTCHA_TEST_DIR + "test-1.png";

        List<CaptchaInfo> captchaInfoList = captchaConfig.getCaptchaInfoList();
        if (CollectionUtils.isEmpty(captchaInfoList)) {
            throw new FrontException("打码平台未设置！请新建并保存！");
        }

        CaptchaInfo captchaInfo = null;
        for (CaptchaInfo c : captchaInfoList) {
            if (c.getId().equals(captchaInfoId)) {
                captchaInfo = c;
                break;
            }
        }

        if (captchaInfo == null) {
            throw new FrontException("打码平台未保存！请保存再测试！");
        }

        log.info("开始测试{}", captchaInfo.getCaptchaName());
        BaseCaptchaWay baseCaptchaWay = BaseCaptchaWay.buildBaseCaptchaWay(captchaInfo);
        BasePredictDto basePredictDto = baseCaptchaWay.getBasePredictDto();
        basePredictDto.setImgFile(picPath);
        if (basePredictDto.getServerUrl(true) == null) {
            throw new FrontException("无法连接到打码平台！");
        }

        BaseResponse response = CaptchaUtil.getResponse(basePredictDto);
        if (response == null) {
            return null;
        }
        if (StringUtils.isNotBlank(response.getBalance())) {
            response.setMessage(response.getMessage() + "-" + response.getBalance());
        }
        log.info(String.valueOf(response));
        return response;
    }

    public Boolean addNewBind(Long delay) {
        if (delay != null && delay > 0) {
            Util.sleep(delay);
        }
        int pointWindowHwnd = defaultDm.getPointWindowHwnd();
        if (!defaultDm.isWindowClassFlashPlayerActiveX(pointWindowHwnd)) {
            throw new FrontException("当前窗口 " + pointWindowHwnd + " 不是 flash 窗口！");
        }

        boolean create = Captcha.startIdentifyCaptcha(pointWindowHwnd, captchaConfig, settingConfig.getBindWindowConfig());
        if (!create) {
            throw new FrontException("当前窗口" + pointWindowHwnd + "已在列表中运行！");
        }

        log.info("当前窗口 hwnd = {}", pointWindowHwnd);
        return true;
    }

    public BindResultVo bindAll() {
        List<Integer> ddtWindowHwnd = defaultDm.enumDdtWindowHwnd();
        if (CollectionUtils.isEmpty(ddtWindowHwnd)) {
            throw new FrontException("没有 flash 窗口");
        }

        BindResultVo bindResultVo = new BindResultVo();
        for (int hwnd : ddtWindowHwnd) {
            Util.sleep(150L);
            boolean running = Captcha.isRunning(hwnd, Captcha.class);
            if (running) {
                bindResultVo.increaseRunningCount();
            } else {
                bindResultVo.increaseNewAddCount();
                Captcha.startIdentifyCaptcha(hwnd, captchaConfig, settingConfig.getBindWindowConfig());
            }
        }
        Util.sleep(300L);
        logMore.info(bindResultVo.buildInfo());
        return bindResultVo;
    }

    public StringRet captureCaptchaSampleRegion() {
        List<Captcha> captchaList = BaseType.getBaseTypes(Captcha.class);
        if (CollectionUtils.isEmpty(captchaList)) {
            return StringRet.buildFail("当前没有正在运行的线程");
        }

        List<String> pathList = new ArrayList<>();
        for (Captcha captcha : captchaList) {
            String path = TypeConstants.TemplatePrefix.getFullPathWithFullName(TypeConstants.TemplatePrefix.PVE_CAPTCHA_COUNT_DOWN, (RANDOM.nextInt(1000) + 100));
            captcha.captureCountDownSampleRegion(path);
            pathList.add(path);
        }

        return StringRet.buildSuccess(StringUtils.join(pathList, "\n"));
    }

    public StringRet getAccountInfo(Integer captchaInfoId) {
        CaptchaInfo captchaInfo = captchaConfig.getByCaptchaInfoId(captchaInfoId);
        if (captchaInfo == null) {
            return StringRet.buildSuccess("无该类型账户信息！请检查确认是否填写并且点击“保存打码设置”！");
        }

        BasePredictDto basePredictDto = BaseCaptchaWay.buildBaseCaptchaWay(captchaInfo).getBasePredictDto();
        return StringRet.buildSuccess(basePredictDto.getAccountInfo(captchaConfig));
    }

    public Boolean updateCaptchaConfig(CaptchaConfig captchaConfig) {
        this.captchaConfig.update(captchaConfig);
        this.captchaConfig.save();
        return true;
    }

    public CaptchaConfig resetCaptchaConfig() {
        CaptchaConfig defaultConfig = (CaptchaConfig) this.captchaConfig.defaultConfig();
        updateCaptchaConfig(defaultConfig);
        return defaultConfig;
    }

    public BindResultVo unbindAll() {
        List<Captcha> captchaList = BaseType.getBaseTypes(Captcha.class);
        if (CollectionUtils.isEmpty(captchaList)) {
            throw new FrontException("当前没有正在运行的线程");
        }

        BindResultVo bindResultVo = new BindResultVo();
        for (Captcha captcha : captchaList) {
            captcha.unbindAndRemove();
            bindResultVo.increaseNewAddCount();
        }

        return bindResultVo;
    }
}
