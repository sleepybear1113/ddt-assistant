package cn.sleepybear.ddtassistant.base;

import cn.sleepybear.ddtassistant.utils.captcha.CaptchaChoiceEnum;
import cn.sleepybear.ddtassistant.utils.captcha.CaptchaInfo;
import cn.sleepybear.ddtassistant.utils.captcha.way.BaseCaptchaWay;
import cn.sleepybear.ddtassistant.utils.captcha.way.PcCaptcha;
import cn.sleepybear.ddtassistant.utils.captcha.way.TjCaptcha;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = 4417449558288421370L;

    /**
     * 使用平川打码数据类型的自定义打码
     */
    private List<CaptchaInfo> captchaInfoList;

    /**
     * 是否开启低余额提醒
     */
    private Boolean lowBalanceRemind;
    /**
     * 低余额提醒余额值
     */
    private Double lowBalanceNum;

    private List<Integer> captchaWay;

    public void setCaptchaInfoList(List<CaptchaInfo> captchaInfoList) {
        this.captchaInfoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(captchaInfoList)) {
            return;
        }

        for (CaptchaInfo captchaInfo : captchaInfoList) {
            if (captchaInfo instanceof TjCaptcha || captchaInfo instanceof PcCaptcha) {
                this.captchaInfoList.add(captchaInfo);
            } else {
                this.captchaInfoList.add(BaseCaptchaWay.buildBaseCaptchaWay(captchaInfo));
            }
        }
    }

    public void setCaptchaInfoListPure(List<CaptchaInfo> captchaInfoList) {
        this.captchaInfoList = captchaInfoList;
    }

    @Override
    public String getFileName() {
        return "验证码配置.json";
    }

    @Override
    public BaseConfig defaultConfig() {
        CaptchaConfig captchaConfig = new CaptchaConfig();

        List<Integer> way = new ArrayList<>();
        way.add(2);
        way.add(1);
        way.add(1);
        way.add(0);
        way.add(0);
        way.add(0);
        way.add(0);
        captchaConfig.setCaptchaWay(way);

        captchaInfoList = defaultCaptchaList();

        return captchaConfig;
    }

    public List<CaptchaInfo> defaultCaptchaList() {
        List<CaptchaInfo> captchaInfoList = new ArrayList<>();
        captchaInfoList.add(defaultTj());
        captchaInfoList.add(defaultPc());
        return captchaInfoList;
    }

    public CaptchaInfo defaultTj() {
        CaptchaInfo tjCaptcha = new CaptchaInfo();
        tjCaptcha.setId(1);
        tjCaptcha.setCaptchaName("图鉴打码");
        tjCaptcha.setCaptchaType(CaptchaChoiceEnum.TJ.getChoice());
        tjCaptcha.setParams(List.of(new String[]{"", "", TjCaptcha.DEFAULT_SOFT_ID, TjCaptcha.DEFAULT_TYPE_ID}));
        return tjCaptcha;
    }

    public CaptchaInfo defaultPc() {
        CaptchaInfo captchaInfo = new CaptchaInfo();
        captchaInfo.setId(2);
        captchaInfo.setCaptchaName("平川打码");
        captchaInfo.setCaptchaType(CaptchaChoiceEnum.PC.getChoice());
        captchaInfo.setParams(List.of(new String[]{"", "sleepy"}));
        captchaInfo.setServerAddressList(List.of("http://sq3.sleepybear.cn:34775","http://sq1.sleepybear.cn:33080","https://pc1.sleepybear1113.cn"));
        return captchaInfo;
    }

    public CaptchaInfo getByCaptchaInfoId(Integer captchaInfoId) {
        if (captchaInfoId == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(captchaInfoList)) {
            return null;
        }
        for (CaptchaInfo captchaInfo : captchaInfoList) {
            if (captchaInfoId.equals(captchaInfo.getId())) {
                return captchaInfo;
            }
        }
        return null;
    }
}
