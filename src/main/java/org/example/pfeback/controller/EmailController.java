package org.example.pfeback.controller;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.pfeback.repository.UserRepository;
import org.example.pfeback.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class EmailController {

    private final UserRepository userRepo;

    private final EmailService emailService;
    private final JavaMailSender javaMailSender;


    public EmailController(UserRepository userRepo, EmailService emailService, JavaMailSender javaMailSender) {

        this.userRepo = userRepo;
        this.emailService=emailService;
        this.javaMailSender=javaMailSender;
    }


    @PostMapping("/send")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        emailService.sendEmail(to, subject, text);
        return "Email sent successfully";
    }

    @PostMapping("/sendWithAttach")
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam("attachment") MultipartFile attachment) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body);

            helper.addAttachment(attachment.getOriginalFilename(), () -> {
                try {
                    return attachment.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            javaMailSender.send(message);

            return ResponseEntity.ok("Email sent successfully!");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email: " + e.getMessage());
        }
    }

}
