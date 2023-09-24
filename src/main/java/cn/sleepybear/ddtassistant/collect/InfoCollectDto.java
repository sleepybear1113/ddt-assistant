package cn.sleepybear.ddtassistant.collect;

import cn.sleepybear.ddtassistant.config.AppProperties;
import cn.sleepybear.ddtassistant.constant.GlobalVariable;
import cn.sleepybear.ddtassistant.type.captcha.CaptchaConstants;
import cn.sleepybear.ddtassistant.utils.SpringContextUtil;
import cn.sleepybear.ddtassistant.utils.Util;
import lombok.Data;

import java.io.Serial;
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
    @Serial
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

    public void refreshHwndNum() {
        this.hwndNum = GlobalVariable.DM_DDT_MAP.size();
    }

    public void addCaptchaCount(Integer captchaType) {
        if (CaptchaConstants.CaptchaChoiceEnum.PC.getChoice().equals(captchaType)) {
            captchaCountPc++;
            captchaCountTotalPc++;
        } else if (CaptchaConstants.CaptchaChoiceEnum.TJ.getChoice().equals(captchaType)) {
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
