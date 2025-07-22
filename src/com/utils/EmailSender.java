package com.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    private static final String FROM_EMAIL = "task.gestor@gmail.com"; // Reemplaza con tu correo Gmail
    private static final String APP_PASSWORD = "bvna lsth kgsi ikvp"; // Reemplaza con la contraseña de aplicación que generaste

    public static void sendVerificationCode(String toEmail, String code) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Código de Verificación - Study Habits App");
        
        String htmlContent = String.format(
            "<div style='font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;'>" +
            "<h2 style='color: #006666;'>Verificación de Correo Electrónico</h2>" +
            "<p>Gracias por registrarte en Study Habits App. Tu código de verificación es:</p>" +
            "<div style='background-color: #f0f0f0; padding: 15px; text-align: center; font-size: 24px; " +
            "letter-spacing: 5px; margin: 20px 0; font-weight: bold; color: #006666;'>" +
            "%s" +
            "</div>" +
            "<p>Este código expirará en 10 minutos.</p>" +
            "<p>Si no solicitaste este código, puedes ignorar este correo.</p>" +
            "</div>",
            code
        );

        message.setContent(htmlContent, "text/html; charset=utf-8");
        Transport.send(message);
    }
}
