package com.example.ama_backend.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@EnableAsync
public class MailService {
    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "dev.choiey@gmail.com";

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    @Async
    public void mailSend(String toAddress, String subject, String msgBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_ADDRESS);
        message.setTo(toAddress);
        message.setSubject(subject);
        message.setText(msgBody);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage());
        }
    }
}
