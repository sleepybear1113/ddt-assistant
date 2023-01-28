package cn.xiejx.ddtassistant.logic;

import cn.xiejx.ddtassistant.base.EmailConfig;
import cn.xiejx.ddtassistant.base.SettingConfig;
import cn.xiejx.ddtassistant.dto.AbnormalDetectionCountDto;
import cn.xiejx.ddtassistant.dto.EmailConfigDto;
import cn.xiejx.ddtassistant.exception.FrontException;
import cn.xiejx.ddtassistant.utils.MailUtil;
import cn.xiejx.ddtassistant.utils.Util;
import cn.xiejx.ddtassistant.utils.cacher.Cacher;
import cn.xiejx.ddtassistant.utils.cacher.CacherBuilder;
import cn.xiejx.ddtassistant.utils.http.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/23 01:44
 */
@Component
@Slf4j
public class EmailLogic {
    public static final Random RANDOM = new Random();

    public static final Cacher<Long, Integer> EMAIL_RECEIVER_CACHER = new CacherBuilder<Long, Integer>()
            .scheduleName("EMAIL_RECEIVER_CACHER")
            .delay(300, TimeUnit.SECONDS)
            .build();

    @Resource
    private SettingConfig settingConfig;

    public Boolean sendTestEmail() {
        EmailConfig email = settingConfig.getEmail();
        if (!email.valid()) {
            throw new FrontException("邮箱设置错误！");
        }
        String subject = "测试邮件主题-" + RANDOM.nextInt(1000);
        String body = "测试邮件内容-" + RANDOM.nextInt(1000) + "\n主机名：%s\n这个是由 ddt-assistant 发出的测试邮件。";
        sendEmail(subject, body);
        return true;
    }

    public Boolean sendEmailByRemote(EmailConfigDto emailConfigDto) {
        EmailConfig emailConfig = settingConfig.getEmail();
        EmailConfigDto merge = EmailConfigDto.merge(emailConfigDto, emailConfig);

        Integer times = EMAIL_RECEIVER_CACHER.get(merge.getId());
        if (times == null) {
            times = 0;
        }
        EMAIL_RECEIVER_CACHER.put(merge.getId(), ++times);
        if (times >= 5) {
            log.warn("邮件发送出现内部地址循环，已停止!");
            return false;
        }

        sendEmail(merge.toEmailConfig(), merge.getTitle(), merge.getBody());
        return true;
    }

    public Boolean sendOfflineRemindEmail(AbnormalDetectionCountDto abnormalDetectionCountDto) {
        EmailConfig email = settingConfig.getEmail();
        valid(email);
        String subject = "掉线提醒";
        String bodyFormat = "主机名：%s\n时间：%s\n%s\n这个是由 ddt-assistant 发出的掉线提醒邮件。";
        String timeString = Util.getTimeString(Util.TIME_ALL_FORMAT_EASY);
        String body = String.format(bodyFormat, email.getHostName(), timeString, abnormalDetectionCountDto.toString());
        sendEmail(subject, body);
        return true;
    }

    public Boolean sendEmail(String title, String body) {
        EmailConfig email = settingConfig.getEmail();
        return sendEmail(email, title, body);
    }

    public static Boolean sendEmail(EmailConfig email, String title, String body) {
        valid(email);
        try {
            if (!Boolean.TRUE.equals(email.getEnableRemoteSender())) {
                MailUtil.sendMail(email.getEmailFrom(), email.getEmailPassword(), email.getEmailToList(), title, body);
            } else {

            }
        } catch (MessagingException e) {
            log.warn("邮件发送失败：{}", e.getMessage());
        }
        return true;
    }

    public Boolean sendLowBalanceNotify(double balance) {
        EmailConfig email = settingConfig.getEmail();
        return sendLowBalanceNotify(email, balance);
    }

    public static void sendRemoteEmail(EmailConfig email) {
        HttpHelper httpHelper = new HttpHelper(email.getRemoteSenderAddr());
    }

    public static Boolean sendLowBalanceNotify(EmailConfig emailConfig, double balance) {
        valid(emailConfig);
        String subject = "低余额提醒";
        String bodyFormat = "时间：%s\n余额：%s，请注意使用情况。\n这个是由 ddt-assistant 发出的低余额提醒邮件。";
        String timeString = Util.getTimeString(Util.TIME_ALL_FORMAT_EASY);
        String body = String.format(bodyFormat, timeString, balance);
        return sendEmail(emailConfig, subject, body);
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
