package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
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
            log.error("Email could not be sent to '%s'. Printing stack trace:".formatted(user.getEmail()));
            e.printStackTrace();
        } catch (MailAuthenticationException e){
            log.error("Email sending is turned on, but gmail credentials are incorrect! To fix this issue change the credentials to the correct ones. Gmail returned `{}`", e.getMessage());
        }
    }

    private void send(String shopMail, String shopPass, MimeMessage message) {
        JavaMailSenderImpl jMailSender = (JavaMailSenderImpl)javaMailSender;

        jMailSender.setUsername(shopMail);
        jMailSender.setPassword(shopPass);
        jMailSender.send(message);
    }

    public boolean isEmailEnabled(User user) {
        return user.getEmailPermission() && user.getShop().getSettings().isEmailEnabled();
    }
}
