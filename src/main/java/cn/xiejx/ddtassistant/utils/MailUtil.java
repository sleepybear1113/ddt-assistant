package cn.xiejx.ddtassistant.utils;

import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/06/22 01:33
 */
public class MailUtil {
    public static void sendMail(String from, String password, String[] to, String subject, String body) throws MessagingException {
        if (StringUtils.isBlank(from)) {
            return;
        }
        String[] split = from.split("@");
        if (split.length <= 1) {
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp." + split[1]);
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setSubject(subject);
        msg.setText(body);
        msg.setSentDate(new Date());

        InternetAddress[] address = new InternetAddress[to.length];
        for (int i = 0; i < to.length; i++) {
            address[i] = new InternetAddress(to[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, address);

        Transport.send(msg);
    }
}
