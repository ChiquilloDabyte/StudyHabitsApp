package com.database;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class GestorEmail {
    private static final String FROM_EMAIL = "task.gestor@gmail.com";
    private static final String APP_PASSWORD = "bvna lsth kgsi ikvp";
    private String codigoConfirmacion;

    public GestorEmail() {
        // Constructor
    }

    public String getCodigoConfirmacion() {
        return codigoConfirmacion;
    }

    private String generarCodigo() {
        return String.format("%06d", new java.util.Random().nextInt(1000000));
    }

    public boolean enviarCodigoConfirmacion(String correo) {
        codigoConfirmacion = generarCodigo();
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(FROM_EMAIL));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
            mensaje.setSubject("Código de Verificación - Study Habits App");
            
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
                codigoConfirmacion
            );

            mensaje.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(mensaje);
            System.out.println("Correo enviado exitosamente a: " + correo);
            return true;
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean validarCodigo(String codigoIngresado) {
        return codigoIngresado != null && codigoIngresado.equals(codigoConfirmacion);
    }
}
