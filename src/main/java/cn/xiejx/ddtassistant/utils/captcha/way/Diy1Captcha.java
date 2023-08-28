package cn.xiejx.ddtassistant.utils.captcha.way;

import cn.xiejx.ddtassistant.utils.captcha.BasePredictDto;
import cn.xiejx.ddtassistant.utils.captcha.diy1.Diy1PredictDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 21:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Diy1Captcha extends BaseDiyCaptcha implements Serializable {
    private static final long serialVersionUID = 3701573224665113223L;

    private List<String> serverAddrList;

    private String cami;
    private String author;

    @Override
    public boolean validUserInfo() {
        return StringUtils.isNotBlank(cami);
    }

    @Override
    public List<String> getServerAddrList() {
        if (CollectionUtils.isEmpty(this.serverAddrList)) {
            this.serverAddrList = new ArrayList<>();
        }
        return this.serverAddrList;
    }

    @Override
    public String getAuthor() {
        return StringUtils.isBlank(super.getAuthor()) ? "sleepy" : super.getAuthor();
    }

    @Override
    public BasePredictDto getBasePredictDto() {
        return new Diy1PredictDto();
    }
}