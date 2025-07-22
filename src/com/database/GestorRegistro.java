package com.database;

import com.implementation.PilaAcciones;
import com.implementation.Tarea;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class GestorRegistro {
    private PilaAcciones pilaAcciones;
    private static final String DB_URL = "jdbc:sqlite:taskgestor.db";

    public GestorRegistro() {
        this.pilaAcciones = new PilaAcciones();
        inicializarBaseDatos();
    }

    private void inicializarBaseDatos() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Primero, eliminar las tablas si existen
            try {
                conn.createStatement().execute("DROP TABLE IF EXISTS Tareas");
                conn.createStatement().execute("DROP TABLE IF EXISTS Usuarios");
            } catch (SQLException e) {
                System.err.println("Error al eliminar tablas existentes: " + e.getMessage());
            }

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
            e.printStackTrace();
        }
    }

    public int validarCredenciales(String correo, String contrasena) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT idUsuario FROM Usuarios WHERE correo = ? AND contrasena = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, correo);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idUsuario");
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public String obtenerNombreUsuario(int idUsuario) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT nombre FROM Usuarios WHERE idUsuario = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener nombre de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return "Usuario";
    }

    public List<Tarea> buscarTareasPorUsuario(int idUsuario) {
        List<Tarea> tareas = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT idTarea, nombre, descripcion, completada, fechaEntrega FROM Tareas WHERE idUsuario = ? ORDER BY fechaEntrega";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Tarea tarea = new Tarea(rs.getInt("idTarea"), idUsuario, rs.getString("nombre"), rs.getString("descripcion"));
                tarea.setCompletada(rs.getBoolean("completada"));
                tarea.setFechaEntrega(rs.getString("fechaEntrega"));
                tareas.add(tarea);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error al buscar tareas: " + e.getMessage());
        }
        return tareas;
    }


    public int agregarTarea(int idUsuario, String titulo, String descripcion, String fechaEntrega) {
        return agregarTarea(idUsuario, titulo, descripcion, fechaEntrega, true);
    }

    private int agregarTarea(int idUsuario, String titulo, String descripcion, String fechaEntrega, boolean registrarAccion) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "INSERT INTO Tareas (idUsuario, nombre, descripcion, completada, fechaEntrega) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, idUsuario);
            pstmt.setString(2, titulo);
            pstmt.setString(3, descripcion);
            pstmt.setBoolean(4, false); // Inicialmente no completada
            pstmt.setString(5, fechaEntrega); // Nueva fecha de entrega
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int idTarea = rs.getInt(1);
                if (registrarAccion) {
                    pilaAcciones.agregarAccion(() -> eliminarTarea(idTarea, false));
                }
                return idTarea;
            }
        } catch (SQLException e) {
            System.err.println("Error al agregar tarea: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // Subtask functionality has been removed

    public boolean deshacerUltimaAccion(JFrame parent) {
        try {
            return pilaAcciones.deshacer(parent);
        } catch (Exception e) {
            System.err.println("Error al deshacer acción: " + e.getMessage());
            return false;
        }
    }

    public boolean enviarCodigoConfirmacion(String correo) {
        // Implementación simulada
        return true;
    }

    public boolean validarCodigo(String codigo) {
        // Implementación simulada
        return true;
    }

    public int registrarUsuario(String nombre, String correo, String contrasena) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "INSERT INTO Usuarios (nombre, correo, contrasena) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nombre);
            pstmt.setString(2, correo);
            pstmt.setString(3, contrasena);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void eliminarTarea(int idTarea) {
        eliminarTarea(idTarea, true);
    }

    private void eliminarTarea(int idTarea, boolean registrarAccion) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT idUsuario, nombre, descripcion, fechaEntrega FROM Tareas WHERE idTarea = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idTarea);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int idUsuario = rs.getInt("idUsuario");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                // También necesitamos obtener la fecha de entrega
                String fechaEntrega = rs.getString("fechaEntrega");
                if (registrarAccion) {
                    pilaAcciones.agregarAccion(() -> {
                        agregarTarea(idUsuario, nombre, descripcion, fechaEntrega, false);
                    });
                }
            }
            query = "DELETE FROM Tareas WHERE idTarea = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, idTarea);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar tarea: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Tarea obtenerTareaPorId(int idTarea) {
        Tarea tarea = null;
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "SELECT idUsuario, nombre, descripcion, completada, fechaEntrega FROM Tareas WHERE idTarea = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
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
        } catch (SQLException e) {
            System.err.println("Error al obtener tarea por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return tarea;
    }
    
    public void actualizarFechaEntrega(int idTarea, String fechaEntrega) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String query = "UPDATE Tareas SET fechaEntrega = ? WHERE idTarea = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, fechaEntrega);
            pstmt.setInt(2, idTarea);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar fecha de entrega: " + e.getMessage());
        }
    }
}