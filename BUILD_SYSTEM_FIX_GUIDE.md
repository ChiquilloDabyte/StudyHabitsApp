# 🔧 Build System & Compilation Fix Guide

> **Priority**: CRITICAL (Must be completed first)  
> **Estimated Time**: 2 hours  
> **Dependencies**: None

## 🚨 IMMEDIATE ACTION REQUIRED

The application currently **cannot compile** due to Java version compatibility issues. This must be fixed before any other improvements can be implemented.

### Problem Analysis
- NetBeans project configured for Java 21
- Local compiler rejecting target releases (21, 17, 11)
- Likely PATH/JAVA_HOME configuration issue

### SOLUTION 1: Quick Fix - Use Java 8 Compatibility

**Step 1**: Update project properties
```bash
# Edit: nbproject/project.properties
javac.source=1.8
javac.target=1.8
```

**Step 2**: Test compilation
```bash
cd "c:\Users\Office1\Downloads\college\Software\estructuras de datos\Study-Habit\StudyHabitsApp"
ant clean compile
```

**Step 3**: If still failing, use direct javac
```bash
javac -cp "lib/*" -d build/classes src/com/**/*.java
```

### SOLUTION 2: Environment Fix (Recommended)

**Step 1**: Verify Java installation
```bash
java -version
javac -version
echo %JAVA_HOME%
echo %PATH%
```

**Step 2**: Set correct JAVA_HOME
```bash
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.2
set PATH=%JAVA_HOME%\bin;%PATH%
```

**Step 3**: Rebuild project
```bash
ant clean compile
```

### SOLUTION 3: Manual Build Script (Fallback)

Create `build_manual.bat`:
```batch
@echo off
echo Manual build script for StudyHabits App

rem Clean previous build
if exist build\classes rmdir /s /q build\classes
mkdir build\classes

rem Compile with explicit classpath
javac -cp "lib\sqlite-jdbc-3.50.2.0.jar;lib\jbcrypt-0.4.jar;lib\absolutelayout.jar;lib\json-simple-1.1.1.jar;lib\javax.mail-1.6.2.jar;lib\javax.activation-api-1.2.0.jar;lib\jcalendar-1.4.jar" -d build\classes src\com\login\*.java src\com\database\*.java src\com\components\*.java src\com\windowP\*.java src\com\implementation\*.java src\com\estructuras\*.java src\com\utils\*.java src\com\integration\*.java src\com\debug\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
    pause
    exit /b 1
)
```

### Verification Steps

After successful compilation:

1. **Check class files exist**:
   ```bash
   dir build\classes\com\login
   dir build\classes\com\database
   ```

2. **Test application startup**:
   ```bash
   java -cp "build\classes;lib\*" com.login.login
   ```

3. **Verify no runtime errors**:
   - Application window should appear
   - No console errors should be displayed
   - Database connection should work

### AI Code Quality Enforcement

Once compilation is fixed, implement these **mandatory** coding standards:

#### Database Operations
```java
// ✅ REQUIRED: Always use try-with-resources
try (Connection conn = pool.getConnection();
     PreparedStatement stmt = conn.prepareStatement(sql)) {
    // Implementation
} catch (SQLException e) {
    logger.error("Database error", e);
    throw new DatabaseException("Operation failed", e);
}

// ❌ FORBIDDEN: Manual resource management
Connection conn = pool.getConnection(); // Resource leak risk
```

#### Exception Handling
```java
// ✅ REQUIRED: Specific exception handling
catch (SQLException e) {
    logger.error("Database operation failed for user: " + userId, e);
    return Optional.empty();
}

// ❌ FORBIDDEN: Generic exception swallowing
catch (Exception e) {
    // Do nothing - This hides real problems
}
```

#### Input Validation
```java
// ✅ REQUIRED: Validate all inputs
public boolean login(String email, String password) {
    if (email == null || email.trim().isEmpty()) {
        throw new IllegalArgumentException("Email cannot be empty");
    }
    if (!isValidEmail(email)) {
        throw new IllegalArgumentException("Invalid email format");
    }
    // Continue with validation...
}

// ❌ FORBIDDEN: Direct database queries with user input
String sql = "SELECT * FROM users WHERE email = '" + email + "'"; // SQL Injection!
```

### Next Steps

Once compilation is working:
1. ✅ Proceed to **DATABASE_OPTIMIZATION_GUIDE.md**
2. ✅ Then implement **UI_UX_IMPROVEMENT_GUIDE.md**  
3. ✅ Finally apply **SECURITY_PERFORMANCE_GUIDE.md**

### Troubleshooting

**If compilation still fails:**
1. Check all JAR files exist in `lib/` directory
2. Verify no missing import statements in source files
3. Check for circular dependencies between packages
4. Try compiling individual packages separately

**Common Issues:**
- Missing `absolutelayout.jar` → Download from NetBeans
- SQLite driver not found → Verify `sqlite-jdbc-3.50.2.0.jar` exists
- BCrypt errors → Confirm `jbcrypt-0.4.jar` is in classpath

**Emergency Fallback:**
If all else fails, use the existing `run_login.bat` which includes fallback compilation recovery.

---

*This file MUST be completed before proceeding with other improvements. The application is non-functional until compilation issues are resolved.*
