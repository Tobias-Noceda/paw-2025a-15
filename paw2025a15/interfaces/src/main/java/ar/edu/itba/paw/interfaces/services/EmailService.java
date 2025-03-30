package ar.edu.itba.paw.interfaces.services;

import java.util.Map;

import javax.mail.MessagingException;

public interface EmailService {
    public void sendSimpleMessage(String to, String subject, String text);

    public void sendSimpleMessageTemplate(String to, String subject, Map<String, Object> templateModel, String templateName) throws MessagingException;

    public void sendTestEmail();
}
