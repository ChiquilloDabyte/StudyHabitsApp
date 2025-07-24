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
    private DatabaseConnectionPool connectionPool;
    private PreparedStatementCache statementCache;
    private Connection cachedConnection;
    private String codigoConfirmacion;
    private Random random;
    
    // Variables for email verification
    private static String codigoVerificacionActual;
    private static String correoVerificacionActual;
    private static long tiempoGeneracionCodigo;

    public GestorRegistro() {
        this.pilaAcciones = new PilaAcciones();
        this.random = new Random();
        this.connectionPool = DatabaseConnectionPool.getInstance();
        inicializarBaseDatos();
    }

    private void inicializarBaseDatos() {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            this.cachedConnection = conn;
            this.statementCache = new PreparedStatementCache(conn);
            
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
            
            System.out.println("Database initialized with connection pool. Available connections: " 
                + connectionPool.getAvailableConnections());
        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
            // Try fallback initialization
            try {
                Thread.sleep(1000); // Wait a bit
                conn = connectionPool.getConnection();
                this.statementCache = new PreparedStatementCache(conn);
                System.out.println("Fallback database initialization successful");
            } catch (Exception fallbackException) {
                System.err.println("Fallback initialization also failed: " + fallbackException.getMessage());
                // Create minimal fallback to prevent null pointer exceptions
                this.statementCache = null;
            } finally {
                if (conn != null) {
                    connectionPool.releaseConnection(conn);
                }
            }
        }
    }

    public boolean registrarUsuario(String nombre, String apellido, String correo, String contrasena) {
        long startTime = System.nanoTime();
        try {
            if (existeCorreo(correo)) {
                return false;
            }

            if (statementCache == null) {
                System.err.println("Statement cache not initialized, using direct connection fallback");
                return registrarUsuarioFallback(nombre, apellido, correo, contrasena);
            }

            PreparedStatement pstmt = statementCache.getStatement(PreparedStatementCache.INSERT_USER);
            pstmt.setString(1, nombre);
            pstmt.setString(2, correo);
            pstmt.setString(3, contrasena);
            pstmt.executeUpdate();
            
            long endTime = System.nanoTime();
            System.out.printf("User registration took: %.2f ms%n", (endTime - startTime) / 1_000_000.0);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    private boolean registrarUsuarioFallback(String nombre, String apellido, String correo, String contrasena) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            String query = "INSERT INTO Usuarios (nombre, correo, contrasena) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, nombre);
                pstmt.setString(2, correo);
                pstmt.setString(3, contrasena);
                pstmt.executeUpdate();
                System.out.println("User registered successfully using fallback method");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error in fallback user registration: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    public boolean existeCorreo(String correo) {
        try {
            if (statementCache == null) {
                System.err.println("Statement cache not initialized, using fallback for email check");
                return existeCorreoFallback(correo);
            }
            PreparedStatement pstmt = statementCache.getStatement(PreparedStatementCache.CHECK_EMAIL_EXISTS);
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar correo: " + e.getMessage());
            return true;
        }
    }

    private boolean existeCorreoFallback(String correo) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            String query = "SELECT COUNT(*) FROM Usuarios WHERE correo = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, correo);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in fallback email check: " + e.getMessage());
            return true; // Assume email exists to prevent duplicate registrations
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return false;
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
        long startTime = System.nanoTime();
        List<Tarea> tareas = new ArrayList<>();
        
        try {
            PreparedStatement pstmt = statementCache.getStatement(PreparedStatementCache.SELECT_TASKS_BY_USER);
            pstmt.setInt(1, idUsuario);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Tarea tarea = new Tarea(
                    rs.getInt("idTarea"), 
                    idUsuario, 
                    rs.getString("nombre"), 
                    rs.getString("descripcion")
                );
                tarea.setCompletada(rs.getBoolean("completada"));
                tarea.setFechaEntrega(rs.getString("fechaEntrega"));
                tareas.add(tarea);
            }
            
            long endTime = System.nanoTime();
            double timeMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("Loaded %d tasks in %.2f ms (%.2f ms per 100 tasks)%n", 
                tareas.size(), timeMs, (timeMs / Math.max(1, tareas.size())) * 100);
                
        } catch (SQLException e) {
            System.err.println("Error al buscar tareas: " + e.getMessage());
        }
        return tareas;
    }

    public int agregarTarea(int idUsuario, String titulo, String descripcion, String fechaEntrega) {
        return agregarTarea(idUsuario, titulo, descripcion, fechaEntrega, true);
    }

    private int agregarTarea(int idUsuario, String titulo, String descripcion, String fechaEntrega, boolean registrarAccion) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
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
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return -1;
    }

    public void eliminarTarea(int idTarea) {
        eliminarTarea(idTarea, true);
    }

    private void eliminarTarea(int idTarea, boolean registrarAccion) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
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
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
    }

    public Tarea obtenerTareaPorId(int idTarea) {
        Tarea tarea = null;
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
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
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return tarea;
    }

    public void actualizarFechaEntrega(int idTarea, String fechaEntrega) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            String query = "UPDATE Tareas SET fechaEntrega = ? WHERE idTarea = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, fechaEntrega);
                pstmt.setInt(2, idTarea);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error al actualizar fecha de entrega: " + e.getMessage());
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
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
            if (statementCache == null) {
                System.err.println("Statement cache not initialized, using fallback for credential validation");
                return validarCredencialesFallback(correo, contrasena);
            }
            PreparedStatement pstmt = statementCache.getStatement(PreparedStatementCache.VALIDATE_CREDENTIALS);
            pstmt.setString(1, correo);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idUsuario");
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
        }
        return -1;
    }

    private int validarCredencialesFallback(String correo, String contrasena) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
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
            System.err.println("Error in fallback credential validation: " + e.getMessage());
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return -1;
    }

    public String obtenerNombreUsuario(int idUsuario) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
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
        } finally {
            if (conn != null) {
                connectionPool.releaseConnection(conn);
            }
        }
        return "Usuario";
    }

    public void cerrarConexion() {
        if (statementCache != null) {
            statementCache.closeAll();
        }
        if (cachedConnection != null) {
            connectionPool.releaseConnection(cachedConnection);
        }
    }
    
    // Method to get connection pool statistics for monitoring
    public String getConnectionPoolStats() {
        return String.format("Connection Pool - Available: %d, Total: %d, Cache Size: %d", 
            connectionPool.getAvailableConnections(),
            connectionPool.getTotalConnections(),
            statementCache != null ? statementCache.getCacheSize() : 0);
    }
    
    // Shutdown hook for proper cleanup
    public static void shutdown() {
        DatabaseConnectionPool.getInstance().shutdown();
    }
}
