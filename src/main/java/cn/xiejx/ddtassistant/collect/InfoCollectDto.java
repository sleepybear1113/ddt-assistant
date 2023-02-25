package cn.xiejx.ddtassistant.collect;

import cn.xiejx.ddtassistant.config.AppProperties;
import cn.xiejx.ddtassistant.utils.SpringContextUtil;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.captcha.CaptchaChoiceEnum;
import lombok.Data;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/02/19 22:47
 */
@Data
public class InfoCollectDto implements Serializable {
    private static final long serialVersionUID = 2972846734288837995L;

    private boolean init = false;

    private String hostName;
    private String ip;
    private String mac;
    private Integer hwndNum;
    private String appVersion;
    private Integer version;
    private Long startTime;
    private Integer captchaCountPc;
    private Integer captchaCountTj;
    private Integer captchaCountTotalPc;
    private Integer captchaCountTotalTj;

    public InfoCollectDto() {
        this.startTime = System.currentTimeMillis();
        captchaCountPc = 0;
        captchaCountTj = 0;
        captchaCountTotalPc = 0;
        captchaCountTotalTj = 0;
        hwndNum = 0;
    }

    public void addCaptchaCount(CaptchaChoiceEnum captchaChoiceEnum) {
        if (CaptchaChoiceEnum.PC.equals(captchaChoiceEnum)) {
            captchaCountPc++;
            captchaCountTotalPc++;
        } else if (CaptchaChoiceEnum.TJ.equals(captchaChoiceEnum)) {
            captchaCountTj++;
            captchaCountTotalTj++;
        }
    }

    public void clearCount() {
        captchaCountPc = 0;
        captchaCountTj = 0;
    }

    public void init() {
        AppProperties appProperties = SpringContextUtil.getBean(AppProperties.class);
        init = true;
        this.appVersion = appProperties.getAppVersion();
        this.version = appProperties.getVersion();

        try {
            this.hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ignored) {
        }

        this.mac = Util.getMacAddress();
    }
}
