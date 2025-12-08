package utp.edu.pe.nexoket.test;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utp.edu.pe.nexoket.util.MonitorRendimiento;

/**
 * Clase de prueba para verificar el funcionamiento del sistema de monitoreo.
 * Ejecutar este main para validar logs, métricas y alertas.
 * 
 * @author NexoKet Team
 */
public class TestMonitoreo {
    private static final Logger logger = LoggerFactory.getLogger(TestMonitoreo.class);
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("INICIANDO TEST DE MONITOREO");
        System.out.println("========================================\n");
        
        logger.info("═══════════════════════════════════════");
        logger.info("  TEST DE SISTEMA DE MONITOREO");
        logger.info("═══════════════════════════════════════");
        
        MonitorRendimiento monitor = MonitorRendimiento.getInstancia();
        
        // Test 1: Métricas iniciales
        System.out.println("\n>>> TEST 1: Métricas del sistema");
        logger.info("\n>>> TEST 1: Métricas iniciales del sistema");
        monitor.logearMetricas();
        
        // Test 2: Verificar salud
        System.out.println("\n>>> TEST 2: Verificación de salud");
        logger.info("\n>>> TEST 2: Verificación de salud del sistema");
        boolean saludable = monitor.verificarSaludSistema();
        System.out.println("Sistema saludable: " + saludable);
        logger.info("Resultado de salud: {}", saludable ? "✓ SALUDABLE" : "✗ NO SALUDABLE");
        
        // Test 3: Simular carga y monitorear
        System.out.println("\n>>> TEST 3: Simulando carga del sistema...");
        logger.info("\n>>> TEST 3: Simulando carga del sistema");
        simularCarga();
        monitor.logearMetricas();
        
        // Test 4: Mostrar métricas detalladas
        System.out.println("\n>>> TEST 4: Métricas detalladas");
        logger.info("\n>>> TEST 4: Métricas detalladas");
        mostrarMetricasDetalladas(monitor);
        
        // Test 5: Monitoreo continuo (10 segundos)
        System.out.println("\n>>> TEST 5: Monitoreo continuo (10 segundos)");
        logger.info("\n>>> TEST 5: Iniciando monitoreo continuo por 10 segundos");
        monitorearContinuo(10, monitor);
        
        System.out.println("\n========================================");
        System.out.println("TEST DE MONITOREO COMPLETADO");
        System.out.println("========================================");
        System.out.println("\n📄 Revisa los archivos de logs:");
        System.out.println("   - logs/nexoket.log (logs generales)");
        System.out.println("   - logs/error.log (solo errores)");
        System.out.println("   - logs/performance.log (métricas de rendimiento)");
        
        logger.info("═══════════════════════════════════════");
        logger.info("  TEST COMPLETADO EXITOSAMENTE");
        logger.info("═══════════════════════════════════════\n");
        
        // Detener el monitor
        monitor.detener();
        
        // Esperar un momento antes de cerrar
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\n✓ Proceso finalizado. Revisa los logs para más detalles.");
    }
    
    /**
     * Simula carga en el sistema
     */
    private static void simularCarga() {
        logger.info("Iniciando simulación de carga...");
        
        // Simular uso de memoria
        java.util.List<byte[]> memoria = new java.util.ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memoria.add(new byte[1024 * 1024]); // 1 MB
            logger.debug("Asignado {} MB de memoria", i + 1);
            System.out.println("  - Asignado " + (i + 1) + " MB de memoria");
        }
        
        // Simular procesamiento
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Simulación interrumpida");
        }
        
        logger.info("✓ Simulación de carga completada");
        System.out.println("  ✓ Simulación completada");
    }
    
    /**
     * Muestra métricas detalladas en consola y logs
     */
    private static void mostrarMetricasDetalladas(MonitorRendimiento monitor) {
        Map<String, Object> metricas = monitor.obtenerMetricas();
        
        System.out.println("\n┌─────────── MÉTRICAS DETALLADAS ───────────┐");
        System.out.println("│ MEMORIA:");
        System.out.printf("│   Usada: %d MB%n", metricas.get("memoria_usada_mb"));
        System.out.printf("│   Máxima: %d MB%n", metricas.get("memoria_maxima_mb"));
        System.out.printf("│   Libre: %d MB%n", metricas.get("memoria_libre_mb"));
        System.out.printf("│   Porcentaje: %.2f%%%n", metricas.get("memoria_porcentaje"));
        System.out.println("│");
        System.out.println("│ HILOS:");
        System.out.printf("│   Activos: %d%n", metricas.get("hilos_activos"));
        System.out.printf("│   Daemon: %d%n", metricas.get("hilos_daemon"));
        System.out.printf("│   Pico: %d%n", metricas.get("hilos_pico"));
        System.out.println("│");
        System.out.println("│ SISTEMA:");
        System.out.printf("│   Procesadores: %d%n", metricas.get("procesadores"));
        System.out.printf("│   Carga: %.2f%n", metricas.get("carga_sistema"));
        System.out.printf("│   Estado: %s%n", monitor.getEstadoActual());
        System.out.println("└───────────────────────────────────────────┘");
        
        logger.info("Métricas detalladas mostradas en consola");
    }
    
    /**
     * Realiza monitoreo continuo por un número de segundos
     */
    private static void monitorearContinuo(int segundos, MonitorRendimiento monitor) {
        for (int i = 0; i < segundos; i++) {
            System.out.println("\n--- Segundo " + (i + 1) + " ---");
            logger.info("═══ Monitoreo continuo - Segundo {} ═══", i + 1);
            
            Map<String, Object> metricas = monitor.obtenerMetricas();
            
            System.out.printf("Memoria: %.2f%% | Hilos: %d | Estado: %s%n",
                metricas.get("memoria_porcentaje"),
                metricas.get("hilos_activos"),
                monitor.getEstadoActual());
            
            logger.info("Memoria: {}% | Hilos: {} | Estado: {}",
                String.format("%.2f", metricas.get("memoria_porcentaje")),
                metricas.get("hilos_activos"),
                monitor.getEstadoActual());
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Monitoreo continuo interrumpido");
                break;
            }
        }
        
        logger.info("✓ Monitoreo continuo completado");
        System.out.println("\n✓ Monitoreo continuo completado");
    }
}
