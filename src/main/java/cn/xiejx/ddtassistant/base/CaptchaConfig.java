package cn.xiejx.ddtassistant.base;

import cn.xiejx.ddtassistant.utils.captcha.CaptchaChoiceEnum;
import cn.xiejx.ddtassistant.utils.captcha.way.PcCaptcha;
import cn.xiejx.ddtassistant.utils.captcha.way.TjCaptcha;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/15 08:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CaptchaConfig extends BaseConfig implements Serializable {
    private static final long serialVersionUID = 4417449558288421370L;

    private TjCaptcha tj;

    private PcCaptcha pc;

    /**
     * 是否开启低余额提醒
     */
    private Boolean lowBalanceRemind;
    /**
     * 低余额提醒余额值
     */
    private Double lowBalanceNum;

    private List<Integer> captchaWay;

    @Override
    public String getFileName() {
        return "验证码配置.json";
    }

    @Override
    public BaseConfig defaultConfig() {
        CaptchaConfig captchaConfig = new CaptchaConfig();

        List<Integer> way = new ArrayList<>();
        way.add(CaptchaChoiceEnum.PC.getChoice());
        way.add(CaptchaChoiceEnum.TJ.getChoice());
        way.add(CaptchaChoiceEnum.TJ.getChoice());
        way.add(CaptchaChoiceEnum.NONE.getChoice());
        way.add(CaptchaChoiceEnum.NONE.getChoice());
        way.add(CaptchaChoiceEnum.NONE.getChoice());
        way.add(CaptchaChoiceEnum.NONE.getChoice());
        captchaConfig.setCaptchaWay(way);

        TjCaptcha tjCaptcha = new TjCaptcha();
        tjCaptcha.setTypeId(TjCaptcha.DEFAULT_TYPE_ID);
        tjCaptcha.setSoftId(TjCaptcha.DEFAULT_SOFT_ID);
        captchaConfig.setTj(tjCaptcha);

        PcCaptcha pcCaptcha = new PcCaptcha();
        captchaConfig.setPc(pcCaptcha);

        return captchaConfig;
    }

    public void userConfigToCaptchaConfig(UserConfig userConfig) {
        if (new File(getFilePath()).exists()) {
            return;
        }
        log.info("迁移验证码配置...");

        setLowBalanceNum(userConfig.getLowBalanceNum());
        setLowBalanceRemind(userConfig.getLowBalanceRemind());
        this.tj.setSoftId(userConfig.getSoftId());
        this.tj.setPassword(userConfig.getPassword());
        this.tj.setUsername(userConfig.getUsername());
        this.tj.setTypeId(userConfig.getTypeId());
        save();
        log.info("验证码配置完成...");
    }
}
