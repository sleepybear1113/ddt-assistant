package cn.sleepybear.ddtassistant.dto;

import cn.sleepybear.ddtassistant.base.EmailConfig;
import cn.sleepybear.ddtassistant.exception.FrontException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2023/01/28 10:59
 */
@Data
public class EmailConfigDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 6519878058926088392L;

    private Long id;

    private String title;
    private String body;

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

    public void generateId() {
        this.id = System.currentTimeMillis();
    }

    public EmailConfig toEmailConfig() {
        EmailConfig emailConfig = new EmailConfig();
        BeanUtils.copyProperties(this, emailConfig);
        return emailConfig;
    }

    public static EmailConfigDto toEmailConfigDto(EmailConfig emailConfig) {
        if (emailConfig == null) {
            return null;
        }
        EmailConfigDto emailConfigDto = new EmailConfigDto();
        BeanUtils.copyProperties(emailConfig, emailConfigDto);
        return emailConfigDto;
    }

    public static EmailConfigDto merge(EmailConfigDto remote, EmailConfig local) {
        if (local == null) {
            throw new FrontException("远程机器未配置邮箱信息");
        }
        if (remote == null) {
            throw new FrontException("未获取到发送邮件的信息");
        }
        if (!Boolean.TRUE.equals(local.getAllowRemoteConnect())) {
            throw new FrontException("远程机器未开启远程发送邮件功能");
        }

        remote.setAllowRemoteConnect(local.getAllowRemoteConnect());
        remote.setEnableRemoteSender(local.getEnableRemoteSender());
        remote.setRemoteSenderAddr(local.getRemoteSenderAddr());
        remote.setUseRemoteLocalConfigFirst(local.getUseRemoteLocalConfigFirst());
        if (!Boolean.TRUE.equals(remote.useRemoteLocalConfigFirst)) {
            return remote;
        }

        if (StringUtils.isBlank(remote.getEmailFrom())) {
            remote.setEmailFrom(local.getEmailFrom());
        }
        if (StringUtils.isBlank(remote.getEmailTo())) {
            remote.setEmailTo(local.getEmailTo());
        }
        if (StringUtils.isBlank(remote.getEmailPassword())) {
            remote.setEmailPassword(local.getEmailPassword());
        }
        if (StringUtils.isBlank(remote.getHostName())) {
            remote.setHostName(local.getHostName());
        }
        if (!remote.toEmailConfig().valid()) {
            throw new FrontException("邮箱配置错误");
        }

        return remote;
    }
}
