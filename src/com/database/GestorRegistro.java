package com.database;

import com.implementation.PilaAcciones;
import com.implementation.Tarea;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.activation.DataHandler;

public class GestorRegistro {
    private PilaAcciones pilaAcciones;
    private static final String DB_URL = "jdbc:sqlite:taskgestor.db";
    private Connection conn;
    private String codigoConfirmacion;
    private Random random;
    
    // Variables for email verification
    private static String codigoVerificacionActual;
    private static String correoVerificacionActual;
    private static long tiempoGeneracionCodigo;

    public GestorRegistro() {
        this.pilaAcciones = new PilaAcciones();
        this.random = new Random();
        inicializarBaseDatos();
    }

    private void inicializarBaseDatos() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            String script = "";
            try (java.util.Scanner scanner = new java.util.Scanner(getClass().getResourceAsStream("/com/database/schema.sql"))) {
                scanner.useDelimiter("\\A"); // Lee todo el archivo
                if (scanner.hasNext()) {
                    script = scanner.next();
                }
            }
            
            String[] statements = script.split(";");
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    conn.createStatement().execute(trimmed);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    public boolean registrarUsuario(String nombre, String apellido, String correo, String contrasena) {
        try {
            if (existeCorreo(correo)) {
                return false;
            }

            String query = "INSERT INTO Usuarios (nombre, correo, contrasena) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, correo);
                pstmt.setString(3, contrasena);
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean existeCorreo(String correo) {
        String query = "SELECT COUNT(*) FROM Usuarios WHERE correo = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error al verificar correo: " + e.getMessage());
            return true;
        }
    }

    public boolean enviarCodigoVerificacion(String correo) {
        try {
            // Generar código aleatorio de 6 dígitos
            int codigo = random.nextInt(900000) + 100000;
            String codigoStr = String.valueOf(codigo);

            // Configurar propiedades
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.debug", "true");

            // Crear sesión de correo
            final String remitente = "task.gestor@gmail.com";
            final String contrasena = "bvna lsth kgsi ikvp";
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(remitente, contrasena);
                }
            });

            // Crear mensaje
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(remitente));
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
                codigoStr
            );

            mensaje.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(mensaje);

            // Guardar el código y correo actuales
            codigoVerificacionActual = codigoStr;
            correoVerificacionActual = correo;
            tiempoGeneracionCodigo = System.currentTimeMillis();
            
            System.out.println("Correo enviado exitosamente a: " + correo);
            return true;
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
            return false;
        }
    }

    public boolean verificarCodigo(String correo, String codigo) {
        if (codigoVerificacionActual == null || correoVerificacionActual == null) {
            return false;
        }
        
        // Verificar si el código ha expirado (10 minutos)
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - tiempoGeneracionCodigo > 600000) { // 10 minutos en milisegundos
            codigoVerificacionActual = null;
            correoVerificacionActual = null;
            return false;
        }

        // Verificar si el código y correo coinciden con los almacenados
        boolean esValido = codigo.equals(codigoVerificacionActual) && correo.equals(correoVerificacionActual);
        if (esValido) {
            codigoVerificacionActual = null;
            correoVerificacionActual = null;
        }
        return esValido;
    }

    public List<Tarea> buscarTareasPorUsuario(int idUsuario) {
        List<Tarea> tareas = new ArrayList<>();
        try {
            String query = "SELECT idTarea, nombre, descripcion, completada, fechaEntrega FROM Tareas WHERE idUsuario = ? ORDER BY fechaEntrega";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idUsuario);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Tarea tarea = new Tarea(rs.getInt("idTarea"), idUsuario, rs.getString("nombre"), rs.getString("descripcion"));
                    tarea.setCompletada(rs.getBoolean("completada"));
                    tarea.setFechaEntrega(rs.getString("fechaEntrega"));
                    tareas.add(tarea);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar tareas: " + e.getMessage());
        }
        return tareas;
    }

    public int agregarTarea(int idUsuario, String titulo, String descripcion, String fechaEntrega) {
        return agregarTarea(idUsuario, titulo, descripcion, fechaEntrega, true);
    }

    private int agregarTarea(int idUsuario, String titulo, String descripcion, String fechaEntrega, boolean registrarAccion) {
        try {
            String query = "INSERT INTO Tareas (idUsuario, nombre, descripcion, completada, fechaEntrega) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, idUsuario);
                pstmt.setString(2, titulo);
                pstmt.setString(3, descripcion);
                pstmt.setBoolean(4, false);
                pstmt.setString(5, fechaEntrega);
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int idTarea = rs.getInt(1);
                    if (registrarAccion) {
                        pilaAcciones.agregarAccion(() -> eliminarTarea(idTarea, false));
                    }
                    return idTarea;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar tarea: " + e.getMessage());
        }
        return -1;
    }

    public void eliminarTarea(int idTarea) {
        eliminarTarea(idTarea, true);
    }

    private void eliminarTarea(int idTarea, boolean registrarAccion) {
        try {
            String query = "SELECT idUsuario, nombre, descripcion, fechaEntrega FROM Tareas WHERE idTarea = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idTarea);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int idUsuario = rs.getInt("idUsuario");
                    String nombre = rs.getString("nombre");
                    String descripcion = rs.getString("descripcion");
                    String fechaEntrega = rs.getString("fechaEntrega");
                    if (registrarAccion) {
                        pilaAcciones.agregarAccion(() -> {
                            agregarTarea(idUsuario, nombre, descripcion, fechaEntrega, false);
                        });
                    }
                }
            }
            
            query = "DELETE FROM Tareas WHERE idTarea = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idTarea);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar tarea: " + e.getMessage());
        }
    }

    public Tarea obtenerTareaPorId(int idTarea) {
        Tarea tarea = null;
        try {
            String query = "SELECT idUsuario, nombre, descripcion, completada, fechaEntrega FROM Tareas WHERE idTarea = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idTarea);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int idUsuario = rs.getInt("idUsuario");
                    String nombre = rs.getString("nombre");
                    String descripcion = rs.getString("descripcion");
                    tarea = new Tarea(idTarea, idUsuario, nombre, descripcion);
                    tarea.setCompletada(rs.getBoolean("completada"));
                    tarea.setFechaEntrega(rs.getString("fechaEntrega"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tarea por ID: " + e.getMessage());
        }
        return tarea;
    }

    public void actualizarFechaEntrega(int idTarea, String fechaEntrega) {
        try {
            String query = "UPDATE Tareas SET fechaEntrega = ? WHERE idTarea = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fechaEntrega);
                pstmt.setInt(2, idTarea);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar fecha de entrega: " + e.getMessage());
        }
    }

    public boolean deshacerUltimaAccion(JFrame parent) {
        try {
            return pilaAcciones.deshacer(parent);
        } catch (Exception e) {
            System.err.println("Error al deshacer acción: " + e.getMessage());
            return false;
        }
    }

    public int validarCredenciales(String correo, String contrasena) {
        try {
            String query = "SELECT idUsuario FROM Usuarios WHERE correo = ? AND contrasena = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, correo);
                pstmt.setString(2, contrasena);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("idUsuario");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
        }
        return -1;
    }

    public String obtenerNombreUsuario(int idUsuario) {
        try {
            String query = "SELECT nombre FROM Usuarios WHERE idUsuario = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idUsuario);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de usuario: " + e.getMessage());
        }
        return "Usuario";
    }

    public void cerrarConexion() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
