package com.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple connection pool for SQLite to improve performance
 * Reduces connection creation overhead and manages resource cleanup
 */
public class DatabaseConnectionPool {
    private static final String DB_URL = "jdbc:sqlite:taskgestor.db";
    private static final int MIN_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 10;
    
    private final BlockingQueue<Connection> availableConnections = new LinkedBlockingQueue<>();
    private final AtomicInteger currentPoolSize = new AtomicInteger(0);
    private volatile boolean isShutdown = false;
    
    private static DatabaseConnectionPool instance;
    
    private DatabaseConnectionPool() {
        initializePool();
    }
    
    public static synchronized DatabaseConnectionPool getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionPool();
        }
        return instance;
    }
    
    private void initializePool() {
        try {
            // Enable SQLite optimizations
            System.setProperty("sqlite.journal_mode", "WAL");
            System.setProperty("sqlite.synchronous", "NORMAL");
            
            for (int i = 0; i < MIN_POOL_SIZE; i++) {
                Connection conn = createConnection();
                if (conn != null) {
                    availableConnections.offer(conn);
                    currentPoolSize.incrementAndGet();
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing connection pool: " + e.getMessage());
        }
    }
    
    private Connection createConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        // SQLite performance optimizations
        conn.createStatement().execute("PRAGMA journal_mode=WAL");
        conn.createStatement().execute("PRAGMA synchronous=NORMAL");
        conn.createStatement().execute("PRAGMA cache_size=10000");
        conn.createStatement().execute("PRAGMA temp_store=MEMORY");
        return conn;
    }
    
    public Connection getConnection() throws SQLException {
        if (isShutdown) {
            throw new SQLException("Connection pool is shutdown");
        }
        
        Connection conn = availableConnections.poll();
        if (conn == null || conn.isClosed()) {
            if (currentPoolSize.get() < MAX_POOL_SIZE) {
                conn = createConnection();
                currentPoolSize.incrementAndGet();
            } else {
                // Wait for available connection
                try {
                    conn = availableConnections.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("Interrupted while waiting for connection");
                }
            }
        }
        
        return conn;
    }
    
    public void releaseConnection(Connection conn) {
        if (conn != null && !isShutdown) {
            try {
                if (!conn.isClosed()) {
                    availableConnections.offer(conn);
                } else {
                    currentPoolSize.decrementAndGet();
                }
            } catch (SQLException e) {
                System.err.println("Error checking connection status: " + e.getMessage());
                currentPoolSize.decrementAndGet();
            }
        }
    }
    
    public void shutdown() {
        isShutdown = true;
        
        // Close all connections
        Connection conn;
        while ((conn = availableConnections.poll()) != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        
        currentPoolSize.set(0);
        instance = null;
    }
    
    public int getAvailableConnections() {
        return availableConnections.size();
    }
    
    public int getTotalConnections() {
        return currentPoolSize.get();
    }
}
