package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String shopMail, String shopPass, String mailAddress, String title, String mailMessage){

        JavaMailSenderImpl jMailSender = (JavaMailSenderImpl)javaMailSender;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(shopMail);
        jMailSender.setUsername(shopMail);
        jMailSender.setPassword(shopPass);
        message.setTo(mailAddress);
        message.setSubject(title);
        message.setText(mailMessage);
    }

    public void sendMail(User user, String msgBody, String title) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(user.getShop().getSettings().getGmail());
            mimeMessageHelper.setText(msgBody, true);
            mimeMessageHelper.setSubject(title);
            mimeMessageHelper.setTo(user.getEmail());
            send(user.getShop().getSettings().getGmail(),user.getShop().getSettings().getGmailPassword(), mimeMessage);
            log.info("Email '" + title + "' was sent to: " + user.getEmail());
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not send email.");
        }
    }

    private void send(String shopMail, String shopPass, MimeMessage message) {
        JavaMailSenderImpl jMailSender = (JavaMailSenderImpl)javaMailSender;

        jMailSender.setUsername(shopMail);
        jMailSender.setPassword(shopPass);
        jMailSender.send(message);
    }

}
