package com.database;

import com.implementation.Tarea;
import java.util.List;

/**
 * Simple performance test for database optimizations
 */
public class DatabasePerformanceTest {
    
    public static void main(String[] args) {
        System.out.println("=== Database Performance Test ===");
        
        GestorRegistro gestor = new GestorRegistro();
        
        // Test 1: User registration performance
        testUserRegistration(gestor);
        
        // Test 2: Task loading performance
        testTaskLoading(gestor);
        
        // Test 3: Connection pool stats
        testConnectionPoolStats(gestor);
        
        // Cleanup
        gestor.cerrarConexion();
        GestorRegistro.shutdown();
        
        System.out.println("=== Test Complete ===");
    }
    
    private static void testUserRegistration(GestorRegistro gestor) {
        System.out.println("\n--- User Registration Test ---");
        long startTime = System.nanoTime();
        
        // Try to register a test user (may fail if already exists)
        boolean success = gestor.registrarUsuario("TestUser", "Test", "test@example.com", "password123");
        
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        
        System.out.printf("User registration result: %s (%.2f ms)%n", success, timeMs);
    }
    
    private static void testTaskLoading(GestorRegistro gestor) {
        System.out.println("\n--- Task Loading Test ---");
        
        // Test with different user IDs to see performance
        int[] testUserIds = {1, 2, 999}; // 999 likely has no tasks
        
        for (int userId : testUserIds) {
            long startTime = System.nanoTime();
            List<Tarea> tareas = gestor.buscarTareasPorUsuario(userId);
            long endTime = System.nanoTime();
            
            double timeMs = (endTime - startTime) / 1_000_000.0;
            System.out.printf("User %d: Loaded %d tasks in %.2f ms%n", 
                userId, tareas.size(), timeMs);
        }
    }
    
    private static void testConnectionPoolStats(GestorRegistro gestor) {
        System.out.println("\n--- Connection Pool Stats ---");
        System.out.println(gestor.getConnectionPoolStats());
    }
}
