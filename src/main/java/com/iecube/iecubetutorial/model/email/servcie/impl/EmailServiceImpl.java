package com.iecube.iecubetutorial.model.email.servcie.impl;

import com.iecube.iecubetutorial.model.email.exception.SendEmailException;
import com.iecube.iecubetutorial.model.email.servcie.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Async("emailExecutor") // 指定使用emailExecutor线程池
    public CompletableFuture<Void> sendSimpleMessage(String to, String subject, String text) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("已经向{}发送邮件:{}",to,subject);
            return CompletableFuture.completedFuture(null);
        }catch (Exception e){
            log.error("发送给{}的邮件失败:{}",to,e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Async("emailExecutor") // 指定使用emailExecutor线程池
    public CompletableFuture<Void> sendHtmlMessage(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("已经向{}发送邮件:{}",to,subject);
            return CompletableFuture.completedFuture(null);
        } catch (MessagingException e) {
            log.error("发送给{}的邮件失败:{}",to,e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
}
