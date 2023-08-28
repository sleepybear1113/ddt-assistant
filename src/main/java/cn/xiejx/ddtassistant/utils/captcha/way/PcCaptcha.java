package cn.xiejx.ddtassistant.utils.captcha.way;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/16 21:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PcCaptcha extends BaseDiyCaptcha implements Serializable {
    private static final long serialVersionUID = 3701573224665113223L;

    /**
     * 服务器地址，旧版本仅有一个，新版本有多个。废弃
     */
    private String serverAddr;

    @Override
    public boolean validUserInfo() {
        return StringUtils.isNotBlank(getCami());
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
        if (serverAddr != null) {
            this.serverAddr = serverAddr.trim();
        }
    }

    @Override
    public List<String> getServerAddrList() {
        if (CollectionUtils.isNotEmpty(super.getServerAddrList())) {
            return super.getServerAddrList();
        }
        return new ArrayList<>(Collections.singletonList(this.serverAddr));
    }

    @Override
    public void setServerAddr(List<String> serverAddrList) {
        this.setServerAddrList(serverAddrList);
        if (CollectionUtils.isNotEmpty(serverAddrList)) {
            for (int i = 0; i < this.getServerAddrList().size(); i++) {
                String serverAddr = this.getServerAddrList().get(i);
                if (StringUtils.isNotBlank(serverAddr)) {
                    this.getServerAddrList().set(i, serverAddr.trim());
                }
            }
        }
    }

    @Override
    public String getAuthor() {
        return StringUtils.isBlank(super.getAuthor()) ? "sleepy" : super.getAuthor();
    }
}