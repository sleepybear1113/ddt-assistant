package cn.sleepybear.ddtassistant.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = 6460329356645762654L;

    /**
     * 发件人
     */
    private String emailFrom;

    /**
     * 发件人密码/授权码
     */
    private String emailPassword;

    /**
     * 收件人列表
     */
    private String emailTo;

    /**
     * 发件主机名
     */
    private String hostName;

    /**
     * 是否允许远程方连接到本机
     */
    private Boolean allowRemoteConnect;

    /**
     * 使用的发送方的地址
     */
    private String remoteSenderAddr;

    /**
     * 启动远程发送方功能
     */
    private Boolean enableRemoteSender;

    /**
     * 优先使用发送方的邮箱配置发送邮件
     */
    private Boolean useRemoteLocalConfigFirst;

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
