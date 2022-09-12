package cn.xiejx.ddtassistant.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 01:35
 */
@Data
public class EmailConfig implements Serializable {
    private static final long serialVersionUID = 6460329356645762654L;

    private String emailFrom;
    private String emailPassword;
    private String emailTo;
    private String hostName;

    public void update(EmailConfig emailConfig) {
        BeanUtils.copyProperties(emailConfig, this);
    }

    @JsonIgnore
    public String[] getEmailToList() {
        if (StringUtils.isBlank(emailTo)) {
            return null;
        }
        String emailToNew = emailTo.replace("，", ",").replace("；", ",").replace(";", ",");
        return emailToNew.split(",");
    }

    public boolean valid() {
        return !StringUtils.isBlank(emailFrom) && !StringUtils.isBlank(emailPassword) && !StringUtils.isBlank(emailTo);
    }

    public String getHostName() {
        try {
            return StringUtils.isEmpty(hostName) ? InetAddress.getLocalHost().getHostName() : hostName;
        } catch (UnknownHostException e) {
            return "未知主机";
        }
    }
}
