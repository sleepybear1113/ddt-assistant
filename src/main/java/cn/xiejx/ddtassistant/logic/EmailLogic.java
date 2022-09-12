package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.base.EmailConfig;
import cn.xiejx.ddtassistant.base.SettingConfig;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.utils.MailUtil;
import cn.xiejx.ddtassistant.utils.Util;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.Random;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 01:44
 */
@Component
public class EmailLogic {
    public static final Random RANDOM = new Random();

    @Resource
    private SettingConfig settingConfig;

    public Boolean sendTestEmail() {
        EmailConfig email = settingConfig.getEmail();
        if (!email.valid()) {
            throw new FrontException("邮箱设置错误！");
        }
        String subject = "测试邮件主题-" + RANDOM.nextInt(1000);
        String body = "测试邮件内容-" + RANDOM.nextInt(1000) + "\n主机名：%s\n这个是由 ddt-assistant 发出的测试邮件。";
        try {
            MailUtil.sendMail(email.getEmailFrom(), email.getEmailPassword(), email.getEmailToList(), subject, String.format(body, email.getHostName()));
        } catch (MessagingException ignored) {
        }
        return true;
    }

    public Boolean sendOfflineRemindEmail(Integer offlineNum, Integer offsiteNum) {
        EmailConfig email = settingConfig.getEmail();
        valid(email);
        String subject = "掉线提醒";
        String body = "主机名：%s\n时间：%s\n掉线数量：%s, 异地数量：%s\n这个是由 ddt-assistant 发出的掉线提醒邮件。";
        String timeString = Util.getTimeString(Util.TIME_ALL_FORMAT_EASY);
        try {
            MailUtil.sendMail(email.getEmailFrom(), email.getEmailPassword(), email.getEmailToList(), subject, String.format(body, email.getHostName(), timeString, offlineNum, offsiteNum));
        } catch (MessagingException ignored) {
        }
        return true;
    }

    public Boolean sendLowBalanceNotify(double balance) {
        EmailConfig email = settingConfig.getEmail();
        return sendLowBalanceNotify(email, balance);
    }

    public static Boolean sendLowBalanceNotify(EmailConfig emailConfig, double balance) {
        valid(emailConfig);
        String subject = "低余额提醒";
        String body = "时间：%s\n余额：%s，请注意使用情况。\n这个是由 ddt-assistant 发出的低余额提醒邮件。";
        String timeString = Util.getTimeString(Util.TIME_ALL_FORMAT_EASY);
        try {
            MailUtil.sendMail(emailConfig.getEmailFrom(), emailConfig.getEmailPassword(), emailConfig.getEmailToList(), subject, String.format(body, timeString, balance));
        } catch (MessagingException ignored) {
        }
        return true;
    }

    public static void valid(EmailConfig email) {
        if (email == null) {
            throw new FrontException("邮箱未设置！");
        }
        if (!email.valid()) {
            throw new FrontException("邮箱设置错误！");
        }
    }
}
