# 🔐 Security & Performance Optimization Guide

> **Priority**: CRITICAL (Final phase - Production readiness)  
> **Estimated Time**: 10 hours  
> **Prerequisites**: All previous guides completed

## 🎯 OBJECTIVES

Transform the application into a production-ready, secure system:
- Implement enterprise-grade security measures
- Add comprehensive performance monitoring
- Establish robust error handling and logging
- Implement data encryption and secure authentication
- Add automated performance testing

## 🚨 CRITICAL SECURITY VULNERABILITIES

### Current High-Risk Issues
1. **Plain Text Passwords**: Stored without hashing
2. **SQL Injection**: String concatenation in queries
3. **No Rate Limiting**: Brute force attacks possible
4. **Unencrypted Data**: Sensitive information in plain text
5. **No Input Validation**: User input processed directly
6. **Missing HTTPS**: Network communication unencrypted

### Security Impact Assessment
- **Risk Level**: CRITICAL - Application unsuitable for production
- **Data Exposure**: User credentials, personal information
- **Attack Vectors**: Database injection, credential theft, session hijacking

## 🛡️ ENTERPRISE SECURITY IMPLEMENTATION

### 1. Secure Authentication System

**File**: `src/com/security/SecureAuthenticationService.java`

```java
package com.security;

import com.google.common.util.concurrent.RateLimiter;
import org.mindrot.jbcrypt.BCrypt;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * MANDATORY: Enterprise-grade authentication with security best practices
 * Implements OWASP recommendations for secure authentication
 */
public class SecureAuthenticationService {
    private static final Logger logger = Logger.getLogger(SecureAuthenticationService.class.getName());
    
    // Security configuration
    private static final int BCRYPT_ROUNDS = 12; // High security
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    private static final int SESSION_TIMEOUT_MINUTES = 30;
    
    // Rate limiting - prevent brute force attacks
    private final RateLimiter globalRateLimiter = RateLimiter.create(10.0); // 10 attempts/second globally
    private final ConcurrentHashMap<String, UserAttemptTracker> attemptTrackers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    
    // Secure random for tokens
    private final SecureRandom secureRandom = new SecureRandom();
    
    // Encryption for sensitive data
    private final EncryptionService encryptionService;
    
    public SecureAuthenticationService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
        
        // Cleanup expired sessions every 5 minutes
        startSessionCleanupTask();
    }
    
    /**
     * MANDATORY: Secure password hashing
     * Uses BCrypt with high work factor
     */
    public String hashPassword(String plainPassword) {
        validatePasswordStrength(plainPassword);
        
        // Generate salt and hash with high work factor
        String salt = BCrypt.gensalt(BCRYPT_ROUNDS, secureRandom);
        String hashedPassword = BCrypt.hashpw(plainPassword, salt);
        
        logger.info("Password hashed successfully with BCrypt rounds: " + BCRYPT_ROUNDS);
        return hashedPassword;
    }
    
    /**
     * MANDATORY: Secure authentication with rate limiting and account lockout
     */
    public AuthenticationResult authenticate(String email, String password) {
        // Input validation
        if (!isValidInput(email, password)) {
            logger.warning("Invalid input provided for authentication");
            return AuthenticationResult.invalidInput();
        }
        
        // Global rate limiting
        if (!globalRateLimiter.tryAcquire()) {
            logger.warning("Global rate limit exceeded");
            return AuthenticationResult.rateLimited();
        }
        
        // Per-user rate limiting and account lockout
        UserAttemptTracker tracker = attemptTrackers.computeIfAbsent(email, 
            k -> new UserAttemptTracker());
        
        if (tracker.isLockedOut()) {
            logger.warning("Account locked out: " + email);
            return AuthenticationResult.accountLocked();
        }
        
        if (!tracker.canAttempt()) {
            logger.warning("Too many attempts for user: " + email);
            return AuthenticationResult.rateLimited();
        }
        
        try {
            // Fetch user from database
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                // Constant-time response to prevent timing attacks
                BCrypt.checkpw("dummy_password", "$2a$12$dummy.hash.to.prevent.timing");
                tracker.recordFailedAttempt();
                return AuthenticationResult.failed();
            }
            
            User user = userOpt.get();
            
            // Verify password
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                // Successful authentication
                tracker.recordSuccessfulAttempt();
                
                // Create secure session
                UserSession session = createSession(user);
                activeSessions.put(session.getSessionId(), session);
                
                logger.info("Successful authentication for user: " + email);
                return AuthenticationResult.success(user, session);
            } else {
                // Failed authentication
                tracker.recordFailedAttempt();
                logger.warning("Failed authentication attempt for user: " + email);
                return AuthenticationResult.failed();
            }
            
        } catch (Exception e) {
            logger.severe("Authentication error: " + e.getMessage());
            return AuthenticationResult.error();
        }
    }
    
    /**
     * MANDATORY: Strong password validation
     * Implements industry-standard password requirements
     */
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new WeakPasswordException("Password must be at least 8 characters long");
        }
        
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new WeakPasswordException(
                "Password must contain uppercase, lowercase, digit, and special character");
        }
        
        // Check against common passwords
        if (isCommonPassword(password)) {
            throw new WeakPasswordException("Password is too common");
        }
    }
    
    /**
     * MANDATORY: Secure session management
     */
    private UserSession createSession(User user) {
        String sessionId = generateSecureToken();
        Instant expiryTime = Instant.now().plus(SESSION_TIMEOUT_MINUTES, ChronoUnit.MINUTES);
        
        return new UserSession(sessionId, user.getId(), expiryTime);
    }
    
    /**
     * MANDATORY: Cryptographically secure token generation
     */
    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(tokenBytes);
        
        // Convert to base64 for storage
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    /**
     * MANDATORY: Session validation
     */
    public boolean isValidSession(String sessionId) {
        UserSession session = activeSessions.get(sessionId);
        if (session == null) {
            return false;
        }
        
        if (session.isExpired()) {
            activeSessions.remove(sessionId);
            return false;
        }
        
        // Extend session on activity
        session.updateLastAccess();
        return true;
    }
    
    /**
     * MANDATORY: Secure logout
     */
    public void logout(String sessionId) {
        UserSession session = activeSessions.remove(sessionId);
        if (session != null) {
            logger.info("User logged out: " + session.getUserId());
        }
    }
    
    // Helper classes
    private static class UserAttemptTracker {
        private int failedAttempts = 0;
        private Instant lastAttempt = Instant.now();
        private Instant lockoutUntil = null;
        private final RateLimiter userRateLimiter = RateLimiter.create(1.0); // 1 attempt/second per user
        
        boolean canAttempt() {
            return userRateLimiter.tryAcquire();
        }
        
        boolean isLockedOut() {
            if (lockoutUntil != null && Instant.now().isBefore(lockoutUntil)) {
                return true;
            }
            if (lockoutUntil != null && Instant.now().isAfter(lockoutUntil)) {
                // Lockout expired, reset
                failedAttempts = 0;
                lockoutUntil = null;
            }
            return false;
        }
        
        void recordFailedAttempt() {
            failedAttempts++;
            lastAttempt = Instant.now();
            
            if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                lockoutUntil = Instant.now().plus(LOCKOUT_DURATION_MINUTES, ChronoUnit.MINUTES);
            }
        }
        
        void recordSuccessfulAttempt() {
            failedAttempts = 0;
            lockoutUntil = null;
            lastAttempt = Instant.now();
        }
    }
    
    private static class UserSession {
        private final String sessionId;
        private final int userId;
        private final Instant createdAt;
        private Instant expiryTime;
        private Instant lastAccess;
        
        public UserSession(String sessionId, int userId, Instant expiryTime) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.createdAt = Instant.now();
            this.expiryTime = expiryTime;
            this.lastAccess = Instant.now();
        }
        
        boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }
        
        void updateLastAccess() {
            lastAccess = Instant.now();
            // Extend session
            expiryTime = Instant.now().plus(SESSION_TIMEOUT_MINUTES, ChronoUnit.MINUTES);
        }
        
        // Getters
        public String getSessionId() { return sessionId; }
        public int getUserId() { return userId; }
    }
}
```

### 2. Data Encryption Service

**File**: `src/com/security/EncryptionService.java`

```java
package com.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * MANDATORY: AES-256-GCM encryption for sensitive data
 * Provides authenticated encryption with associated data (AEAD)
 */
public class EncryptionService {
    private static final Logger logger = Logger.getLogger(EncryptionService.class.getName());
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 16; // 128 bits
    
    private final SecretKey secretKey;
    private final SecureRandom secureRandom;
    
    /**
     * Initialize with a secure key from configuration
     */
    public EncryptionService(String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            this.secureRandom = new SecureRandom();
            
            logger.info("Encryption service initialized with AES-256-GCM");
        } catch (Exception e) {
            throw new SecurityException("Failed to initialize encryption service", e);
        }
    }
    
    /**
     * Generate a new 256-bit AES key
     */
    public static String generateNewKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256); // AES-256
            SecretKey key = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            throw new SecurityException("Failed to generate encryption key", e);
        }
    }
    
    /**
     * MANDATORY: Encrypt sensitive data with authenticated encryption
     */
    public String encrypt(String plaintext) {
        try {
            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            // Initialize cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            
            // Encrypt data
            byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV and encrypted data
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);
            
            // Return base64 encoded result
            return Base64.getEncoder().encodeToString(byteBuffer.array());
            
        } catch (Exception e) {
            logger.severe("Encryption failed: " + e.getMessage());
            throw new SecurityException("Encryption failed", e);
        }
    }
    
    /**
     * MANDATORY: Decrypt data with authentication verification
     */
    public String decrypt(String encryptedData) {
        try {
            // Decode from base64
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            
            // Extract IV and encrypted data
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            
            byte[] encrypted = new byte[byteBuffer.remaining()];
            byteBuffer.get(encrypted);
            
            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            
            // Decrypt and verify authentication tag
            byte[] decryptedData = cipher.doFinal(encrypted);
            
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.severe("Decryption failed: " + e.getMessage());
            throw new SecurityException("Decryption failed - data may be corrupted", e);
        }
    }
    
    /**
     * MANDATORY: Secure key derivation for passwords
     */
    public String deriveKey(String password, String salt) {
        // Use PBKDF2 for key derivation
        try {
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                password.toCharArray(), 
                salt.getBytes(StandardCharsets.UTF_8), 
                100000, // 100,000 iterations
                256);   // 256-bit key
            
            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] derivedKey = factory.generateSecret(spec).getEncoded();
            
            return Base64.getEncoder().encodeToString(derivedKey);
            
        } catch (Exception e) {
            throw new SecurityException("Key derivation failed", e);
        }
    }
}
```

### 3. Comprehensive Performance Monitor

**File**: `src/com/monitoring/PerformanceMonitor.java`

```java
package com.monitoring;

import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * MANDATORY: Production-grade performance monitoring
 * Tracks all critical application metrics
 */
public class PerformanceMonitor {
    private static final Logger logger = Logger.getLogger(PerformanceMonitor.class.getName());
    
    // Singleton instance
    private static volatile PerformanceMonitor instance;
    
    // Metrics storage
    private final ConcurrentHashMap<String, OperationMetrics> operationMetrics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final AtomicReference<SystemMetrics> currentSystemMetrics = new AtomicReference<>();
    
    // Background monitoring
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    // Performance thresholds (configurable)
    private static final long SLOW_DATABASE_OPERATION_MS = 100;
    private static final long SLOW_UI_OPERATION_MS = 16; // 60 FPS target
    private static final long MEMORY_WARNING_THRESHOLD = 100 * 1024 * 1024; // 100MB
    
    private PerformanceMonitor() {
        startSystemMonitoring();
    }
    
    public static PerformanceMonitor getInstance() {
        if (instance == null) {
            synchronized (PerformanceMonitor.class) {
                if (instance == null) {
                    instance = new PerformanceMonitor();
                }
            }
        }
        return instance;
    }
    
    /**
     * MANDATORY: Monitor database operations
     */
    public <T> T monitorDatabaseOperation(String operationName, DatabaseOperation<T> operation) {
        long startTime = System.nanoTime();
        Instant startInstant = Instant.now();
        
        try {
            T result = operation.execute();
            
            long durationNanos = System.nanoTime() - startTime;
            long durationMillis = durationNanos / 1_000_000L;
            
            // Record metrics
            recordOperationMetrics("db." + operationName, durationNanos, true);
            
            // Log slow operations
            if (durationMillis > SLOW_DATABASE_OPERATION_MS) {
                logger.warning(String.format("Slow database operation: %s took %dms", 
                    operationName, durationMillis));
            }
            
            return result;
            
        } catch (Exception e) {
            long durationNanos = System.nanoTime() - startTime;
            recordOperationMetrics("db." + operationName, durationNanos, false);
            
            logger.severe(String.format("Database operation failed: %s after %dms - %s", 
                operationName, durationNanos / 1_000_000L, e.getMessage()));
            
            throw new RuntimeException("Database operation failed: " + operationName, e);
        }
    }
    
    /**
     * MANDATORY: Monitor UI operations for 60 FPS compliance
     */
    public void monitorUIOperation(String operationName, Runnable operation) {
        long startTime = System.nanoTime();
        
        try {
            operation.run();
            
            long durationNanos = System.nanoTime() - startTime;
            long durationMillis = durationNanos / 1_000_000L;
            
            // Record metrics
            recordOperationMetrics("ui." + operationName, durationNanos, true);
            
            // Check 60 FPS compliance
            if (durationMillis > SLOW_UI_OPERATION_MS) {
                logger.warning(String.format("Slow UI operation: %s took %dms (target: <%dms)", 
                    operationName, durationMillis, SLOW_UI_OPERATION_MS));
                
                // Increment slow UI counter
                incrementCounter("ui.slow_operations");
            }
            
        } catch (Exception e) {
            long durationNanos = System.nanoTime() - startTime;
            recordOperationMetrics("ui." + operationName, durationNanos, false);
            
            logger.severe(String.format("UI operation failed: %s - %s", operationName, e.getMessage()));
            incrementCounter("ui.failed_operations");
            
            throw new RuntimeException("UI operation failed: " + operationName, e);
        }
    }
    
    /**
     * MANDATORY: Monitor memory usage and garbage collection
     */
    private void startSystemMonitoring() {
        // System metrics collection every 30 seconds
        scheduler.scheduleAtFixedRate(() -> {
            try {
                collectSystemMetrics();
            } catch (Exception e) {
                logger.severe("Failed to collect system metrics: " + e.getMessage());
            }
        }, 0, 30, TimeUnit.SECONDS);
        
        // Performance report every 5 minutes
        scheduler.scheduleAtFixedRate(() -> {
            try {
                logPerformanceReport();
            } catch (Exception e) {
                logger.severe("Failed to generate performance report: " + e.getMessage());
            }
        }, 5, 5, TimeUnit.MINUTES);
    }
    
    private void collectSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();
        
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        SystemMetrics metrics = new SystemMetrics(
            usedMemory,
            totalMemory,
            maxMemory,
            (double) usedMemory / maxMemory * 100,
            runtime.availableProcessors(),
            Thread.activeCount()
        );
        
        currentSystemMetrics.set(metrics);
        
        // Memory warning
        if (usedMemory > MEMORY_WARNING_THRESHOLD) {
            logger.warning(String.format("High memory usage: %d MB (%.1f%% of max)", 
                usedMemory / (1024 * 1024), metrics.memoryUsagePercent));
        }
        
        // Suggest garbage collection if memory usage is high
        if (metrics.memoryUsagePercent > 80.0) {
            logger.info("Suggesting garbage collection due to high memory usage");
            System.gc();
        }
    }
    
    private void recordOperationMetrics(String operationName, long durationNanos, boolean success) {
        operationMetrics.computeIfAbsent(operationName, k -> new OperationMetrics())
            .recordExecution(durationNanos, success);
    }
    
    public void incrementCounter(String counterName) {
        counters.computeIfAbsent(counterName, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * Generate comprehensive performance report
     */
    private void logPerformanceReport() {
        StringBuilder report = new StringBuilder("\n=== PERFORMANCE REPORT ===\n");
        
        // System metrics
        SystemMetrics sysMetrics = currentSystemMetrics.get();
        if (sysMetrics != null) {
            report.append(String.format("Memory: %d MB used (%.1f%% of %d MB max)\n", 
                sysMetrics.usedMemory / (1024 * 1024),
                sysMetrics.memoryUsagePercent,
                sysMetrics.maxMemory / (1024 * 1024)));
            report.append(String.format("Threads: %d active\n", sysMetrics.activeThreads));
        }
        
        // Operation metrics
        report.append("\nOperation Performance:\n");
        operationMetrics.entrySet().stream()
            .sorted(Map.Entry.<String, OperationMetrics>comparingByKey())
            .forEach(entry -> {
                String operation = entry.getKey();
                OperationMetrics metrics = entry.getValue();
                report.append(String.format("  %s: %d calls, avg=%.2fms, max=%.2fms, success=%.1f%%\n",
                    operation,
                    metrics.getCallCount(),
                    metrics.getAverageTimeMs(),
                    metrics.getMaxTimeMs(),
                    metrics.getSuccessRate() * 100));
            });
        
        // Counters
        if (!counters.isEmpty()) {
            report.append("\nCounters:\n");
            counters.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> report.append(String.format("  %s: %d\n", 
                    entry.getKey(), entry.getValue().get())));
        }
        
        report.append("========================\n");
        logger.info(report.toString());
    }
    
    /**
     * Get current performance statistics
     */
    public PerformanceStats getPerformanceStats() {
        return new PerformanceStats(
            currentSystemMetrics.get(),
            new ConcurrentHashMap<>(operationMetrics),
            new ConcurrentHashMap<>(counters)
        );
    }
    
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // Helper classes
    @FunctionalInterface
    public interface DatabaseOperation<T> {
        T execute() throws Exception;
    }
    
    private static class OperationMetrics {
        private final AtomicLong totalCalls = new AtomicLong(0);
        private final AtomicLong successfulCalls = new AtomicLong(0);
        private final AtomicLong totalTimeNanos = new AtomicLong(0);
        private final AtomicLong maxTimeNanos = new AtomicLong(0);
        
        void recordExecution(long durationNanos, boolean success) {
            totalCalls.incrementAndGet();
            if (success) {
                successfulCalls.incrementAndGet();
            }
            totalTimeNanos.addAndGet(durationNanos);
            
            // Update max time
            long currentMax = maxTimeNanos.get();
            while (durationNanos > currentMax && 
                   !maxTimeNanos.compareAndSet(currentMax, durationNanos)) {
                currentMax = maxTimeNanos.get();
            }
        }
        
        long getCallCount() { return totalCalls.get(); }
        double getAverageTimeMs() { 
            long calls = totalCalls.get();
            return calls == 0 ? 0.0 : (totalTimeNanos.get() / (double) calls) / 1_000_000.0;
        }
        double getMaxTimeMs() { return maxTimeNanos.get() / 1_000_000.0; }
        double getSuccessRate() {
            long calls = totalCalls.get();
            return calls == 0 ? 0.0 : successfulCalls.get() / (double) calls;
        }
    }
    
    public static class SystemMetrics {
        public final long usedMemory;
        public final long totalMemory;
        public final long maxMemory;
        public final double memoryUsagePercent;
        public final int availableProcessors;
        public final int activeThreads;
        
        public SystemMetrics(long usedMemory, long totalMemory, long maxMemory, 
                           double memoryUsagePercent, int availableProcessors, int activeThreads) {
            this.usedMemory = usedMemory;
            this.totalMemory = totalMemory;
            this.maxMemory = maxMemory;
            this.memoryUsagePercent = memoryUsagePercent;
            this.availableProcessors = availableProcessors;
            this.activeThreads = activeThreads;
        }
    }
    
    public static class PerformanceStats {
        public final SystemMetrics systemMetrics;
        public final Map<String, OperationMetrics> operationMetrics;
        public final Map<String, AtomicLong> counters;
        
        public PerformanceStats(SystemMetrics systemMetrics, 
                              Map<String, OperationMetrics> operationMetrics,
                              Map<String, AtomicLong> counters) {
            this.systemMetrics = systemMetrics;
            this.operationMetrics = operationMetrics;
            this.counters = counters;
        }
    }
}
```

### 4. Application Security Configuration

**File**: `src/com/security/SecurityConfiguration.java`

```java
package com.security;

import java.security.Security;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * MANDATORY: Application security configuration
 * Must be initialized on application startup
 */
public class SecurityConfiguration {
    private static final Logger logger = Logger.getLogger(SecurityConfiguration.class.getName());
    
    private static final String CONFIG_FILE = "security.properties";
    private static final String DEFAULT_ENCRYPTION_KEY = generateDefaultKey();
    
    private static SecurityConfiguration instance;
    private final Properties securityProperties;
    private final EncryptionService encryptionService;
    
    private SecurityConfiguration() {
        this.securityProperties = loadSecurityProperties();
        this.encryptionService = new EncryptionService(getEncryptionKey());
        
        configureJVMSecurity();
        logger.info("Security configuration initialized");
    }
    
    public static synchronized SecurityConfiguration getInstance() {
        if (instance == null) {
            instance = new SecurityConfiguration();
        }
        return instance;
    }
    
    private Properties loadSecurityProperties() {
        Properties props = new Properties();
        
        try {
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                props.load(Files.newInputStream(Paths.get(CONFIG_FILE)));
                logger.info("Loaded security configuration from: " + CONFIG_FILE);
            } else {
                // Create default configuration
                createDefaultSecurityProperties(props);
                logger.info("Created default security configuration");
            }
        } catch (IOException e) {
            logger.severe("Failed to load security configuration: " + e.getMessage());
            createDefaultSecurityProperties(props);
        }
        
        return props;
    }
    
    private void createDefaultSecurityProperties(Properties props) {
        props.setProperty("encryption.key", DEFAULT_ENCRYPTION_KEY);
        props.setProperty("session.timeout.minutes", "30");
        props.setProperty("max.login.attempts", "5");
        props.setProperty("lockout.duration.minutes", "15");
        props.setProperty("password.min.length", "8");
        props.setProperty("enable.https", "true");
        props.setProperty("log.security.events", "true");
        
        // Save default configuration
        try {
            props.store(Files.newOutputStream(Paths.get(CONFIG_FILE)), 
                "Default Security Configuration - DO NOT COMMIT TO VERSION CONTROL");
        } catch (IOException e) {
            logger.warning("Could not save default security configuration: " + e.getMessage());
        }
    }
    
    private void configureJVMSecurity() {
        // Disable weak cryptographic algorithms
        Security.setProperty("jdk.tls.disabledAlgorithms", 
            "SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224");
        
        // Enable strong random number generation
        System.setProperty("java.security.egd", "file:/dev/./urandom");
        
        // Configure SSL/TLS
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
        
        logger.info("JVM security configuration applied");
    }
    
    private String getEncryptionKey() {
        String key = securityProperties.getProperty("encryption.key");
        if (key == null || key.equals(DEFAULT_ENCRYPTION_KEY)) {
            logger.warning("Using default encryption key - CHANGE FOR PRODUCTION!");
        }
        return key;
    }
    
    private static String generateDefaultKey() {
        return EncryptionService.generateNewKey();
    }
    
    public EncryptionService getEncryptionService() {
        return encryptionService;
    }
    
    public int getSessionTimeoutMinutes() {
        return Integer.parseInt(securityProperties.getProperty("session.timeout.minutes", "30"));
    }
    
    public int getMaxLoginAttempts() {
        return Integer.parseInt(securityProperties.getProperty("max.login.attempts", "5"));
    }
    
    public boolean isHttpsEnabled() {
        return Boolean.parseBoolean(securityProperties.getProperty("enable.https", "true"));
    }
}
```

## 🚀 IMPLEMENTATION CHECKLIST

### Phase 1: Security Foundation (4 hours)
- [ ] Implement `SecureAuthenticationService`
- [ ] Create `EncryptionService` with AES-256-GCM
- [ ] Add `SecurityConfiguration` class
- [ ] Update password storage to use BCrypt

### Phase 2: Performance Monitoring (3 hours)
- [ ] Implement `PerformanceMonitor` singleton
- [ ] Add database operation monitoring
- [ ] Add UI operation monitoring  
- [ ] Set up automated performance reporting

### Phase 3: Input Validation & Security (2 hours)
- [ ] Add comprehensive input validation
- [ ] Implement SQL injection prevention
- [ ] Add rate limiting for all endpoints
- [ ] Set up security logging

### Phase 4: Integration & Testing (1 hour)
- [ ] Integrate security services with existing code
- [ ] Test authentication flows
- [ ] Validate encryption/decryption
- [ ] Performance test under load

## 🎯 SECURITY COMPLIANCE TARGETS

### Authentication Security
- [ ] BCrypt with 12+ rounds for password hashing
- [ ] Account lockout after 5 failed attempts
- [ ] Rate limiting: 10 global attempts/second, 1 per user/second
- [ ] Session timeout after 30 minutes of inactivity
- [ ] Cryptographically secure session tokens

### Data Protection
- [ ] AES-256-GCM encryption for sensitive data
- [ ] No hardcoded secrets or credentials
- [ ] Secure key management and rotation
- [ ] Input validation for all user inputs
- [ ] SQL injection prevention with prepared statements

### Performance Requirements
- [ ] Authentication: < 100ms per request
- [ ] Encryption/Decryption: < 10ms per operation  
- [ ] Session validation: < 5ms per check
- [ ] Memory usage: < 50MB for security services
- [ ] Zero memory leaks in security components

## 🔍 SECURITY TESTING CHECKLIST

### Penetration Testing
```bash
# Test SQL injection
sqlmap -u "app://login" --data="email=test&password=test"

# Test brute force protection
hydra -l admin -P passwords.txt app://login

# Test session management
burpsuite # Manual session testing

# Test encryption strength
openssl # Verify cipher strength
```

### Performance Testing
```java
// Load test authentication
public void loadTestAuthentication() {
    int concurrentUsers = 100;
    int requestsPerUser = 10;
    
    ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
    CountDownLatch latch = new CountDownLatch(concurrentUsers * requestsPerUser);
    
    for (int i = 0; i < concurrentUsers * requestsPerUser; i++) {
        executor.submit(() -> {
            try {
                // Test authentication
                authService.authenticate("test@example.com", "password123");
            } finally {
                latch.countDown();
            }
        });
    }
    
    latch.await(60, TimeUnit.SECONDS);
    executor.shutdown();
}
```

## 🚫 SECURITY ANTI-PATTERNS TO AVOID

```java
// ❌ NEVER: Plain text passwords
user.setPassword(plainTextPassword);

// ❌ NEVER: Hardcoded secrets
String apiKey = "sk-1234567890abcdef";

// ❌ NEVER: SQL injection vulnerable code
String sql = "SELECT * FROM users WHERE name = '" + userName + "'";

// ❌ NEVER: Weak encryption
Cipher cipher = Cipher.getInstance("DES"); // Weak algorithm

// ❌ NEVER: Insufficient error handling
try {
    authenticate(user);
} catch (Exception e) {
    // Ignore - reveals information to attackers
}

// ❌ NEVER: No rate limiting
while (true) {
    attempt_login(); // Allows brute force
}
```

## 📊 MONITORING & ALERTING

Set up alerts for:
- Failed authentication attempts > 10/minute
- Memory usage > 80%
- Database operations > 100ms
- UI operations > 16ms
- Encryption failures
- Security configuration changes

---

*This completes the security hardening required for production deployment. All security measures are mandatory for protecting user data and system integrity.*
