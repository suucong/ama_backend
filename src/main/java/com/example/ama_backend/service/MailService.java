package com.example.ama_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailSender;

@Service
@AllArgsConstructor
public class MailService {
    @Autowired
    private MailSender mailSender;
    private static final String FROM_ADDRESS = "amaspacealert@gmail.com";

    public void mailSend(String toAddress, String fromAddress,
                         String subject, String msgBody) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(fromAddress);
        smm.setTo(toAddress);
        smm.setSubject(subject);
        smm.setText(msgBody);

        mailSender.send(smm);
    }
}
