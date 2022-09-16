package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.base.CaptchaConfig;
import cn.xiejx.ddtassistant.base.UserConfig;
import cn.xiejx.ddtassistant.dm.DmDdt;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.type.BaseType;
import cn.xiejx.ddtassistant.type.TypeConstants;
import cn.xiejx.ddtassistant.type.captcha.Captcha;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.BaseResponse;
import cn.xiejx.ddtassistant.utils.captcha.CaptchaChoiceEnum;
import cn.xiejx.ddtassistant.utils.captcha.CaptchaUtil;
import cn.xiejx.ddtassistant.utils.captcha.pc.PcPredictDto;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjHttpUtil;
import cn.xiejx.ddtassistant.utils.captcha.tj.TjPredictDto;
import cn.xiejx.ddtassistant.vo.BindResultVo;
import cn.xiejx.ddtassistant.vo.StringRet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author sleepybear
 */
@Component
@Slf4j
public class CaptchaLogic {
    private static final Random RANDOM = new Random();

    @Resource
    private UserConfig userConfig;
    @Resource
    private CaptchaConfig captchaConfig;
    @Resource
    private DmDdt defaultDm;

    public BaseResponse testCaptcha(Integer captchaWay) {
        BasePredictDto basePredictDto;
        String picPath = "资源图片/验证码测试/test-1.bmp";
        CaptchaChoiceEnum choiceEnum = CaptchaChoiceEnum.getChoice(captchaWay);
        log.info("开始测试{}", choiceEnum.getName());
        if (CaptchaChoiceEnum.PC.equals(choiceEnum)) {
            basePredictDto = new PcPredictDto();
        } else if (CaptchaChoiceEnum.TJ.equals(choiceEnum)) {
            basePredictDto = new TjPredictDto();
        } else {
            return BaseResponse.buildEmptyResponse();
        }
        basePredictDto.build(captchaConfig, picPath);

        BaseResponse response = CaptchaUtil.getResponse(basePredictDto);
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

        boolean create = Captcha.startIdentifyCaptcha(pointWindowHwnd, captchaConfig);
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
                Captcha.startIdentifyCaptcha(hwnd, captchaConfig);
            }
        }
        Util.sleep(300L);
        log.info(bindResultVo.buildInfo());
        return bindResultVo;
    }

    public StringRet captureCaptchaSampleRegion() {
        Collection<Captcha> captchaList = BaseType.getBaseTypes(Captcha.class);
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

    public StringRet getTjAccountInfo() {
        String username = captchaConfig.getTj().getUsername();
        String password = captchaConfig.getTj().getPassword();
        Boolean lowBalanceRemind = captchaConfig.getLowBalanceRemind();
        Double lowBalanceNum = captchaConfig.getLowBalanceNum();
        return StringRet.buildSuccess(TjHttpUtil.getAccountInfo(username, password, lowBalanceRemind, lowBalanceNum));
    }

    public CaptchaConfig getCaptchaConfig() {
        return captchaConfig;
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
}
