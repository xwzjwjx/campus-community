package com.wjx.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author wjx
 * @description 邮件发送工具类
 */
@Component
public class MailClient {
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件
     * @param to 收件人
     * @param subject 主题
     * @param context 内容
     */
    public void sendMail(String to,String subject,String context){
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            //发送者
            helper.setFrom(from);
            //接收者
            helper.setTo(to);
            //邮件主题
            helper.setSubject(subject);
            //邮件内容,true表示接受HTML格式
            helper.setText(context,true);
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败："+e.getMessage());
        }

    }
}
