package com.eventmgmt.util;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Service for sending emails.
 * This is a simple implementation that should be configured properly
 * with actual SMTP settings in a production environment.
 */
public class EmailService {
    
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    
    private static final String SMTP_HOST = EnvLoader.get("EMAIL_HOST");
    private static final String SMTP_PORT = EnvLoader.get("EMAIL_PORT");
    private static final String USERNAME = EnvLoader.get("EMAIL_USERNAME");
    private static final String PASSWORD = EnvLoader.get("EMAIL_PASSWORD"); 
    private static final String FROM_ADDRESS = EnvLoader.get("FROM_ADDRESS");
    private static final String FROM_NAME = EnvLoader.get("FROM_NAME");
    private static final boolean ENABLE_TLS = "true".equalsIgnoreCase(EnvLoader.get("EMAIL_TLS"));
    
    /**
     * Sends an email.
     * 
     * @param toAddress Recipient email address
     * @param subject Email subject
     * @param body Email body content
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendEmail(String toAddress, String subject, String body) {
        // Create mail session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        if (ENABLE_TLS) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        
        // Create authenticator with credentials
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        };
        
        Session session = Session.getInstance(props, auth);
        
        try {
            // Create message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_ADDRESS, FROM_NAME));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
            message.setSubject(subject);
            message.setText(body);
            
            // Send message
            Transport.send(message);
            LOGGER.info("Email sent successfully to: " + toAddress);
            return true;
            
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email", e);
            return false;
        }
    }
    
    /**
     * Sends an HTML email.
     * 
     * @param toAddress Recipient email address
     * @param subject Email subject
     * @param htmlBody HTML content for email body
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendHtmlEmail(String toAddress, String subject, String htmlBody) {
        // Create mail session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        if (ENABLE_TLS) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        
        // Create authenticator with credentials
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        };
        
        Session session = Session.getInstance(props, auth);
        
        try {
            // Create message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_ADDRESS, FROM_NAME));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");
            
            // Send message
            Transport.send(message);
            LOGGER.info("HTML email sent successfully to: " + toAddress);
            return true;
            
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send HTML email", e);
            return false;
        }
    }
}