package cn.sleepybear.ddtassistant.logic;

import cn.sleepybear.cacher.Cacher;
import cn.sleepybear.cacher.CacherBuilder;
import cn.sleepybear.ddtassistant.base.EmailConfig;
import cn.sleepybear.ddtassistant.base.SettingConfig;
import cn.sleepybear.ddtassistant.dto.AbnormalDetectionCountDto;
import cn.sleepybear.ddtassistant.dto.EmailConfigDto;
import cn.sleepybear.ddtassistant.exception.FrontException;
import cn.sleepybear.ddtassistant.utils.MailUtil;
import cn.sleepybear.ddtassistant.utils.Util;
import cn.sleepybear.ddtassistant.utils.http.HttpHelper;
import cn.sleepybear.ddtassistant.utils.http.HttpResponseHelper;
import cn.sleepybear.ddtassistant.utils.http.enumeration.MethodEnum;
import cn.sleepybear.ddtassistant.advice.ResultCode;
import cn.sleepybear.ddtassistant.advice.ResultCodeConstant;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Component;

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

    public static Boolean sendEmail(EmailConfig email, String title, String body) {
        return sendEmail(email, title, body, null);
    }

    public static Boolean sendEmail(EmailConfig email, String title, String body, Long id) {
        valid(email);
        try {
            if (Boolean.TRUE.equals(email.getEnableRemoteSender())) {
                sendRemoteEmail(email, title, body, id);
            } else {
                log.info("发送邮件......");
                MailUtil.sendMail(email.getEmailFrom(), email.getEmailPassword(), email.getEmailToList(), title, body);
            }
        } catch (MessagingException e) {
            log.warn("邮件发送失败：{}", e.getMessage());
        }
        return true;
    }

    public static void sendRemoteEmail(EmailConfig email, String title, String body, Long id) {
        EmailConfigDto emailConfigDto = EmailConfigDto.toEmailConfigDto(email);
        if (id == null) {
            id = System.currentTimeMillis();
        }
        emailConfigDto.setId(id);
        emailConfigDto.setTitle(title);
        emailConfigDto.setBody(body);

        String remoteSenderAddr = email.getRemoteSenderAddr();
        if (StringUtils.isBlank(remoteSenderAddr)) {
            log.info("远程地址没有输入，发送邮件失败！");
            return;
        }
        if (!remoteSenderAddr.startsWith("http")) {
            remoteSenderAddr = "http://" + remoteSenderAddr;
        }

        String ss = "/email/sendEmailByRemote";
        if (!remoteSenderAddr.endsWith(ss)) {
            remoteSenderAddr = remoteSenderAddr + ss;
        }

        HttpHelper httpHelper = HttpHelper.makeDefaultTimeoutHttpHelper(remoteSenderAddr, MethodEnum.METHOD_POST);
        String jsonString = Util.parseObjectToJsonString(emailConfigDto);
        httpHelper.setPostBody(jsonString, ContentType.APPLICATION_JSON);
        HttpResponseHelper responseHelper = httpHelper.request();
        String responseBody = responseHelper.getResponseBody();
        if (StringUtils.isBlank(responseBody)) {
            log.info("远程邮件发送失败！远程机器没有响应");
            return;
        }
        ResultCode resultCode = Util.parseJsonToObject(responseBody, ResultCode.class);
        if (resultCode == null || resultCode.getCode() == null) {
            log.info("远程邮件发送失败！远程机器没有响应");
            return;
        }
        if (resultCode.getCode().equals(ResultCodeConstant.CodeEnum.SUCCESS.getCode())) {
            log.info("远程邮件发送成功！返回为：{}", resultCode.getResult());
            return;
        }
        log.info("远程邮件发送成功！但结果出现问题，返回为：{}", resultCode.getMessage());
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

    public Boolean sendTestEmail() {
        EmailConfig email = settingConfig.getEmail();
        if (!email.valid()) {
            throw new FrontException("邮箱设置错误！");
        }
        String subject = "这个是测试邮件的主题-" + RANDOM.nextInt(1000);
        String body = "这里测试邮件内容，时间：" + Util.getTimeString(Util.TIME_ALL_FORMAT_EASY) + "\n主机名：%s\n这个是由 ddt-assistant 发出的测试邮件。";
        sendEmail(subject, String.format(body, email.getHostName()));
        return true;
    }

    public Boolean sendEmailByRemote(EmailConfigDto emailConfigDto) {
        log.info("接收到远程邮件发送请求...");
        EmailConfig emailConfig = settingConfig.getEmail();
        EmailConfigDto merge = EmailConfigDto.merge(emailConfigDto, emailConfig);
        log.info("远程主机名：{}", emailConfigDto.getHostName());

        Integer times = EMAIL_RECEIVER_CACHER.get(merge.getId());
        if (times == null) {
            times = 0;
        }
        EMAIL_RECEIVER_CACHER.put(merge.getId(), ++times);
        if (times >= 5) {
            log.warn("邮件发送出现内部地址循环，已停止!");
            return false;
        }

        sendEmail(merge.toEmailConfig(), merge.getTitle(), merge.getBody(), merge.getId());
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

    public Boolean sendLowBalanceNotify(double balance) {
        EmailConfig email = settingConfig.getEmail();
        return sendLowBalanceNotify(email, balance);
    }
}
