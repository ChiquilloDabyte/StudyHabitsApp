# 🗄️ Database Architecture & Optimization Guide

> **Priority**: HIGH (Second phase after compilation fix)  
> **Estimated Time**: 8 hours  
> **Prerequisites**: Build system working

## 🎯 OBJECTIVES

Transform the current basic SQLite implementation into a production-ready database layer with:
- Robust connection pooling
- Prepared statement caching  
- Transaction management
- Performance monitoring
- Graceful error handling

## 🔍 CURRENT ISSUES ANALYSIS

### Critical Problems
1. **Connection Leaks**: Direct JDBC usage without proper cleanup
2. **No Pooling**: Each operation creates new connections
3. **SQL Injection Risk**: String concatenation in queries
4. **Poor Performance**: No statement preparation or caching
5. **No Transactions**: Atomic operations not guaranteed

### Performance Impact
- Database operations taking 37ms+ per query
- Memory leaks from unclosed connections  
- Blocked UI thread during database operations

## 🏗️ ARCHITECTURE REDESIGN

### 1. Connection Pool Implementation

**File**: `src/com/database/OptimizedConnectionPool.java`

```java
package com.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;

/**
 * Production-ready connection pool for SQLite with HikariCP
 * Replaces the basic DatabaseConnectionPool with enterprise-grade pooling
 */
public class OptimizedConnectionPool {
    private static final String DB_PATH = "taskgestor.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_PATH;
    
    // Pool configuration
    private static final int MIN_CONNECTIONS = 3;
    private static final int MAX_CONNECTIONS = 10;
    private static final long CONNECTION_TIMEOUT = 30000; // 30 seconds
    private static final long IDLE_TIMEOUT = 300000; // 5 minutes
    private static final long MAX_LIFETIME = 1800000; // 30 minutes
    
    private final HikariDataSource dataSource;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private static OptimizedConnectionPool instance;
    
    private OptimizedConnectionPool() {
        HikariConfig config = new HikariConfig();
        
        // Basic configuration
        config.setJdbcUrl(JDBC_URL);
        config.setMaximumPoolSize(MAX_CONNECTIONS);
        config.setMinimumIdle(MIN_CONNECTIONS);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setIdleTimeout(IDLE_TIMEOUT);
        config.setMaxLifetime(MAX_LIFETIME);
        
        // SQLite optimizations
        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("synchronous", "NORMAL");
        config.addDataSourceProperty("cache_size", "10000");
        config.addDataSourceProperty("temp_store", "MEMORY");
        config.addDataSourceProperty("mmap_size", "268435456"); // 256MB
        
        // Connection testing
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(3000);
        
        // Pool monitoring
        config.setRegisterMbeans(true);
        config.setPoolName("StudyHabitsPool");
        
        this.dataSource = new HikariDataSource(config);
        
        // Shutdown hook for graceful cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }
    
    public static synchronized OptimizedConnectionPool getInstance() {
        if (instance == null) {
            instance = new OptimizedConnectionPool();
        }
        return instance;
    }
    
    /**
     * MANDATORY: Use this method for all database operations
     * Automatically tracks connection usage for monitoring
     */
    public Connection getConnection() throws SQLException {
        if (dataSource.isClosed()) {
            throw new SQLException("Connection pool has been shut down");
        }
        
        Connection conn = dataSource.getConnection();
        activeConnections.incrementAndGet();
        
        // Wrap connection to track when it's returned to pool
        return new ConnectionWrapper(conn, () -> activeConnections.decrementAndGet());
    }
    
    /**
     * Get pool statistics for monitoring
     */
    public PoolStatistics getStatistics() {
        return new PoolStatistics(
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            activeConnections.get()
        );
    }
    
    /**
     * Graceful shutdown - MUST be called on application exit
     */
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
    
    // Statistics holder
    public static class PoolStatistics {
        public final int activeConnections;
        public final int idleConnections;
        public final int totalConnections;
        public final int currentlyInUse;
        
        public PoolStatistics(int active, int idle, int total, int inUse) {
            this.activeConnections = active;
            this.idleConnections = idle;
            this.totalConnections = total;
            this.currentlyInUse = inUse;
        }
    }
}
```

### 2. Repository Pattern Implementation

**File**: `src/com/database/TaskRepository.java`

```java
package com.database;

import com.implementation.Tarea;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * MANDATORY: All database operations must go through repository pattern
 * Provides clean abstraction and consistent error handling
 */
public class TaskRepository {
    private final OptimizedConnectionPool connectionPool;
    private final PreparedStatementCache statementCache;
    private final PerformanceMonitor performanceMonitor;
    
    // Pre-compiled SQL statements for performance
    private static final String SELECT_TASKS_BY_USER = 
        "SELECT idTarea, titulo, descripcion, fechaCreacion, fechaEntrega, completada, prioridad " +
        "FROM Tareas WHERE idUsuario = ? ORDER BY fechaEntrega ASC LIMIT ? OFFSET ?";
    
    private static final String INSERT_TASK = 
        "INSERT INTO Tareas (idUsuario, titulo, descripcion, fechaCreacion, fechaEntrega, completada, prioridad) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_TASK_STATUS = 
        "UPDATE Tareas SET completada = ?, fechaModificacion = CURRENT_TIMESTAMP WHERE idTarea = ?";
    
    private static final String DELETE_TASK = 
        "DELETE FROM Tareas WHERE idTarea = ? AND idUsuario = ?";
    
    private static final String COUNT_TASKS_BY_USER = 
        "SELECT COUNT(*) FROM Tareas WHERE idUsuario = ?";
    
    public TaskRepository() {
        this.connectionPool = OptimizedConnectionPool.getInstance();
        this.statementCache = new PreparedStatementCache();
        this.performanceMonitor = new PerformanceMonitor();
    }
    
    /**
     * MANDATORY: Use pagination for large datasets
     * Returns tasks for a user with pagination support
     */
    public CompletableFuture<List<Tarea>> findTasksByUserId(int userId, int limit, int offset) {
        return CompletableFuture.supplyAsync(() -> {
            return performanceMonitor.monitorDatabaseOperation("findTasksByUserId", () -> {
                List<Tarea> tasks = new ArrayList<>();
                
                try (Connection conn = connectionPool.getConnection();
                     PreparedStatement stmt = statementCache.getStatement(conn, SELECT_TASKS_BY_USER)) {
                    
                    stmt.setInt(1, userId);
                    stmt.setInt(2, limit);
                    stmt.setInt(3, offset);
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            tasks.add(mapResultSetToTask(rs));
                        }
                    }
                    
                } catch (SQLException e) {
                    throw new DatabaseException("Failed to fetch tasks for user: " + userId, e);
                }
                
                return tasks;
            });
        });
    }
    
    /**
     * MANDATORY: Use transactions for data consistency
     * Creates a new task within a transaction
     */
    public CompletableFuture<Tarea> createTask(Tarea task) {
        return CompletableFuture.supplyAsync(() -> {
            return performanceMonitor.monitorDatabaseOperation("createTask", () -> {
                try (Connection conn = connectionPool.getConnection()) {
                    conn.setAutoCommit(false); // Start transaction
                    
                    try (PreparedStatement stmt = statementCache.getStatement(conn, INSERT_TASK)) {
                        stmt.setInt(1, task.getIdUsuario());
                        stmt.setString(2, task.getTitulo());
                        stmt.setString(3, task.getDescripcion());
                        stmt.setTimestamp(4, Timestamp.valueOf(task.getFechaCreacion()));
                        stmt.setTimestamp(5, Timestamp.valueOf(task.getFechaEntrega()));
                        stmt.setBoolean(6, task.isCompletada());
                        stmt.setInt(7, task.getPrioridad());
                        
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected == 0) {
                            throw new DatabaseException("Failed to create task - no rows affected");
                        }
                        
                        // Get the generated ID
                        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                task.setIdTarea(generatedKeys.getInt(1));
                            }
                        }
                        
                        conn.commit(); // Commit transaction
                        return task;
                        
                    } catch (SQLException e) {
                        conn.rollback(); // Rollback on error
                        throw e;
                    }
                    
                } catch (SQLException e) {
                    throw new DatabaseException("Failed to create task: " + task.getTitulo(), e);
                }
            });
        });
    }
    
    /**
     * MANDATORY: Batch operations for better performance
     * Updates multiple task statuses in a single transaction
     */
    public CompletableFuture<Integer> updateTaskStatuses(List<Integer> taskIds, boolean completed) {
        return CompletableFuture.supplyAsync(() -> {
            return performanceMonitor.monitorDatabaseOperation("updateTaskStatuses", () -> {
                try (Connection conn = connectionPool.getConnection()) {
                    conn.setAutoCommit(false);
                    
                    try (PreparedStatement stmt = statementCache.getStatement(conn, UPDATE_TASK_STATUS)) {
                        for (Integer taskId : taskIds) {
                            stmt.setBoolean(1, completed);
                            stmt.setInt(2, taskId);
                            stmt.addBatch();
                        }
                        
                        int[] results = stmt.executeBatch();
                        conn.commit();
                        
                        return Arrays.stream(results).sum();
                        
                    } catch (SQLException e) {
                        conn.rollback();
                        throw e;
                    }
                    
                } catch (SQLException e) {
                    throw new DatabaseException("Failed to update task statuses", e);
                }
            });
        });
    }
    
    private Tarea mapResultSetToTask(ResultSet rs) throws SQLException {
        Tarea task = new Tarea();
        task.setIdTarea(rs.getInt("idTarea"));
        task.setTitulo(rs.getString("titulo"));
        task.setDescripcion(rs.getString("descripcion"));
        task.setFechaCreacion(rs.getTimestamp("fechaCreacion").toLocalDateTime());
        task.setFechaEntrega(rs.getTimestamp("fechaEntrega").toLocalDateTime());
        task.setCompletada(rs.getBoolean("completada"));
        task.setPrioridad(rs.getInt("prioridad"));
        return task;
    }
}
```

### 3. Enhanced GestorRegistro Update

**Replace the existing validarCredenciales method in GestorRegistro.java:**

```java
/**
 * MANDATORY: Secure credential validation with rate limiting
 * Uses connection pool and proper error handling
 */
public CompletableFuture<Integer> validarCredenciales(String correo, String contrasena) {
    return CompletableFuture.supplyAsync(() -> {
        // Rate limiting - prevent brute force attacks
        if (!rateLimiter.tryAcquire(1, 1, TimeUnit.SECONDS)) {
            throw new SecurityException("Too many login attempts. Please wait.");
        }
        
        return performanceMonitor.monitorDatabaseOperation("validarCredenciales", () -> {
            try (Connection conn = connectionPool.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT idUsuario, contrasena FROM Usuarios WHERE correo = ?")) {
                
                stmt.setString(1, correo);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String hashedPassword = rs.getString("contrasena");
                        
                        // Use BCrypt for password verification
                        if (BCrypt.checkpw(contrasena, hashedPassword)) {
                            return rs.getInt("idUsuario");
                        }
                    }
                    
                    // Constant-time response to prevent timing attacks
                    BCrypt.checkpw("dummy", "$2a$10$dummy.hash.to.prevent.timing.attacks");
                    return -1;
                }
                
            } catch (SQLException e) {
                throw new DatabaseException("Authentication failed", e);
            }
        });
    });
}
```

### 4. Database Initialization with Indexes

**File**: `src/com/database/DatabaseInitializer.java`

```java
package com.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MANDATORY: Proper database initialization with indexes
 * Must be called on application startup
 */
public class DatabaseInitializer {
    private final OptimizedConnectionPool connectionPool;
    
    public DatabaseInitializer() {
        this.connectionPool = OptimizedConnectionPool.getInstance();
    }
    
    /**
     * Initialize database with proper indexes for performance
     */
    public void initializeDatabase() throws SQLException {
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Enable WAL mode for better concurrent access
            stmt.execute("PRAGMA journal_mode=WAL");
            stmt.execute("PRAGMA synchronous=NORMAL");
            stmt.execute("PRAGMA cache_size=10000");
            stmt.execute("PRAGMA temp_store=MEMORY");
            
            // Create indexes if they don't exist
            createIndexes(stmt);
            
            // Analyze tables for query planner optimization
            stmt.execute("ANALYZE");
        }
    }
    
    private void createIndexes(Statement stmt) throws SQLException {
        String[] indexes = {
            "CREATE INDEX IF NOT EXISTS idx_tareas_usuario ON Tareas(idUsuario)",
            "CREATE INDEX IF NOT EXISTS idx_tareas_fecha_entrega ON Tareas(fechaEntrega)",
            "CREATE INDEX IF NOT EXISTS idx_tareas_completada ON Tareas(completada)",
            "CREATE INDEX IF NOT EXISTS idx_tareas_prioridad ON Tareas(prioridad)",
            "CREATE INDEX IF NOT EXISTS idx_usuarios_correo ON Usuarios(correo)",
            "CREATE INDEX IF NOT EXISTS idx_tareas_usuario_fecha ON Tareas(idUsuario, fechaEntrega)",
            "CREATE INDEX IF NOT EXISTS idx_tareas_usuario_completada ON Tareas(idUsuario, completada)"
        };
        
        for (String indexSql : indexes) {
            stmt.execute(indexSql);
        }
    }
}
```

## 🚀 IMPLEMENTATION CHECKLIST

### Phase 1: Core Infrastructure
- [ ] Replace `DatabaseConnectionPool` with `OptimizedConnectionPool`
- [ ] Create `TaskRepository` class
- [ ] Implement `PreparedStatementCache`
- [ ] Add `DatabaseInitializer`

### Phase 2: Query Optimization  
- [ ] Add database indexes
- [ ] Convert all queries to use prepared statements
- [ ] Implement batch operations for bulk updates
- [ ] Add pagination support

### Phase 3: Error Handling & Monitoring
- [ ] Create custom `DatabaseException` class
- [ ] Implement `PerformanceMonitor`
- [ ] Add connection pool statistics
- [ ] Set up logging for slow queries

### Phase 4: Integration & Testing
- [ ] Update all services to use new repository pattern
- [ ] Test connection pool under load
- [ ] Verify transaction rollback works correctly
- [ ] Monitor query performance improvements

## 🎯 PERFORMANCE TARGETS

- **Query Response Time**: < 10ms for single operations
- **Bulk Operations**: < 50ms for 100 records
- **Connection Pool**: < 1ms to get connection
- **Memory Usage**: < 50MB for connection pool
- **Startup Time**: < 500ms for database initialization

## 🔍 MONITORING & VALIDATION

Add this code to verify improvements:

```java
// In your main application class
public void validateDatabasePerformance() {
    PerformanceMonitor monitor = new PerformanceMonitor();
    
    // Test connection pool performance
    long start = System.currentTimeMillis();
    try (Connection conn = connectionPool.getConnection()) {
        // Simple query
    }
    long connectionTime = System.currentTimeMillis() - start;
    
    if (connectionTime > 1) {
        logger.warn("Slow connection acquisition: {}ms", connectionTime);
    }
    
    // Display pool statistics
    PoolStatistics stats = connectionPool.getStatistics();
    logger.info("Pool stats - Active: {}, Idle: {}, Total: {}", 
        stats.activeConnections, stats.idleConnections, stats.totalConnections);
}
```

## 🚫 ANTI-PATTERNS TO AVOID

```java
// ❌ NEVER: Direct JDBC without pool
Connection conn = DriverManager.getConnection("jdbc:sqlite:db.db");

// ❌ NEVER: String concatenation in SQL  
String sql = "SELECT * FROM users WHERE name = '" + userName + "'";

// ❌ NEVER: Unclosed resources
ResultSet rs = stmt.executeQuery(); // No try-with-resources

// ❌ NEVER: Blocking UI thread with database calls
SwingUtilities.invokeLater(() -> {
    List<Task> tasks = repository.getAllTasks(); // Blocks EDT!
});
```

---

*Complete this guide before proceeding to UI improvements. Database performance is critical for application responsiveness.*
