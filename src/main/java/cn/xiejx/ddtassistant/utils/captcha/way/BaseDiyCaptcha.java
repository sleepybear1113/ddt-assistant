package cn.xiejx.ddtassistant.utils.captcha.way;

import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.pc.PcPredictDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 21:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseDiyCaptcha extends BaseCaptchaWay implements Serializable {
    private static final long serialVersionUID = 3701573224665113223L;

    /**
     * 后续都使用这个
     */
    private List<String> serverAddrList;

    private String cami;
    private String author;

    @Override
    public boolean validUserInfo() {
        return StringUtils.isNotBlank(cami);
    }

    public void setServerAddr(List<String> serverAddrList) {
        this.serverAddrList = serverAddrList;
        if (CollectionUtils.isNotEmpty(serverAddrList)) {
            for (int i = 0; i < this.serverAddrList.size(); i++) {
                String serverAddr = this.serverAddrList.get(i);
                if (StringUtils.isNotBlank(serverAddr)) {
                    this.serverAddrList.set(i, serverAddr.trim());
                }
            }
        }
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public BasePredictDto getBasePredictDto() {
        return new PcPredictDto();
    }
}