package ar.edu.itba.paw.services;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import ar.edu.itba.paw.interfaces.services.EmailService;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    private String emailFromString = "testMail@paw.com";

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setFrom(emailFromString);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    @Async
    public void sendSimpleMessageTemplate(String to, String subject, Map<String, Object> templateModel, String templateName) throws MessagingException{
        Context context = new Context();
        context.setVariables(templateModel);
        String htmlBody = templateEngine.process(templateName, context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.setFrom(emailFromString);

        emailSender.send(message);
    }

    @Override
    public void sendTestEmail() {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", "testName");
        try {
            sendSimpleMessageTemplate("testMail@gmail.com", "Test", templateModel, "testTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }

}
