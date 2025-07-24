package com.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Cache for prepared statements to improve database performance
 * Reduces statement compilation overhead for frequently used queries
 */
public class PreparedStatementCache {
    private final Map<String, PreparedStatement> statementCache = new ConcurrentHashMap<>();
    private final Connection connection;
    
    // Common query constants for caching
    public static final String SELECT_TASKS_BY_USER = "SELECT idTarea, nombre, descripcion, completada, fechaEntrega FROM Tareas WHERE idUsuario = ? ORDER BY fechaEntrega";
    public static final String INSERT_TASK = "INSERT INTO Tareas (idUsuario, nombre, descripcion, completada, fechaEntrega) VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE_TASK_COMPLETION = "UPDATE Tareas SET completada = ? WHERE idTarea = ?";
    public static final String UPDATE_TASK_DATE = "UPDATE Tareas SET fechaEntrega = ? WHERE idTarea = ?";
    public static final String DELETE_TASK = "DELETE FROM Tareas WHERE idTarea = ?";
    public static final String SELECT_TASK_BY_ID = "SELECT idUsuario, nombre, descripcion, fechaEntrega FROM Tareas WHERE idTarea = ?";
    public static final String VALIDATE_CREDENTIALS = "SELECT idUsuario FROM Usuarios WHERE correo = ? AND contrasena = ?";
    public static final String CHECK_EMAIL_EXISTS = "SELECT 1 FROM Usuarios WHERE correo = ?";
    public static final String INSERT_USER = "INSERT INTO Usuarios (nombre, correo, contrasena) VALUES (?, ?, ?)";
    
    public PreparedStatementCache(Connection connection) {
        this.connection = connection;
        initializeCommonStatements();
    }
    
    private void initializeCommonStatements() {
        try {
            // Pre-compile frequently used statements
            prepareStatement(SELECT_TASKS_BY_USER);
            prepareStatement(INSERT_TASK);
            prepareStatement(UPDATE_TASK_COMPLETION);
            prepareStatement(UPDATE_TASK_DATE);
            prepareStatement(DELETE_TASK);
            prepareStatement(SELECT_TASK_BY_ID);
            prepareStatement(VALIDATE_CREDENTIALS);
            prepareStatement(CHECK_EMAIL_EXISTS);
            prepareStatement(INSERT_USER);
        } catch (SQLException e) {
            System.err.println("Error initializing prepared statements: " + e.getMessage());
        }
    }
    
    public PreparedStatement getStatement(String query) throws SQLException {
        PreparedStatement stmt = statementCache.get(query);
        if (stmt == null || stmt.isClosed()) {
            stmt = prepareStatement(query);
        }
        return stmt;
    }
    
    private PreparedStatement prepareStatement(String query) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        statementCache.put(query, stmt);
        return stmt;
    }
    
    public void clearParameters(String query) throws SQLException {
        PreparedStatement stmt = statementCache.get(query);
        if (stmt != null && !stmt.isClosed()) {
            stmt.clearParameters();
        }
    }
    
    public void closeAll() {
        for (PreparedStatement stmt : statementCache.values()) {
            try {
                if (stmt != null && !stmt.isClosed()) {
                    stmt.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing prepared statement: " + e.getMessage());
            }
        }
        statementCache.clear();
    }
    
    public int getCacheSize() {
        return statementCache.size();
    }
}
