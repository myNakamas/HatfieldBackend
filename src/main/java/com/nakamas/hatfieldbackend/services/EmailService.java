package com.nakamas.hatfieldbackend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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

        jMailSender.send(message);
    }
//    public void sendEmail(String msgBody, String subject, List<String> recipients) {
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        try {
//            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//            mimeMessageHelper.setFrom(sender);
//            mimeMessageHelper.setText(msgBody, true);
//            FileSystemResource file = new FileSystemResource(new File("C:/Users/Marti/IdeaProjects/DBHumanRes/src/main/resources/images/CompanyLogo.png"));
//            mimeMessageHelper.addInline("company_logo", file);
//            mimeMessageHelper.setSubject(subject);
//            for (String recipient : recipients) {
//                mimeMessageHelper.addTo(recipient);
//            }
//            javaMailSender.send(mimeMessage);
//            log.info("Email '" + subject + "' was sent to: " + recipients);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Could not send email.");
//        }
//    }

}
