package utp.edu.pe.nexoket.util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitor de rendimiento y salud del sistema para aplicaciones en producción.
 * Implementa monitoreo automático periódico con alertas inteligentes.
 * 
 * @author NexoKet Team
 */
public class MonitorRendimiento {
    private static final Logger logger = LoggerFactory.getLogger(MonitorRendimiento.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("performance");
    private static MonitorRendimiento instancia;
    
    private final MemoryMXBean memoryBean;
    private final ThreadMXBean threadBean;
    private final RuntimeMXBean runtimeBean;
    private final OperatingSystemMXBean osBean;
    private final ScheduledExecutorService scheduler;
    
    // Umbrales de alerta
    private static final double UMBRAL_MEMORIA_CRITICO = 90.0;
    private static final double UMBRAL_MEMORIA_ALTO = 75.0;
    private static final int UMBRAL_HILOS_ALTO = 50;
    
    // Estado del sistema
    private EstadoSistema estadoActual = EstadoSistema.NORMAL;
    private LocalDateTime ultimaAlerta;
    
    /**
     * Estados posibles del sistema
     */
    public enum EstadoSistema {
        NORMAL, ADVERTENCIA, CRITICO
    }
    
    private MonitorRendimiento() {
        memoryBean = ManagementFactory.getMemoryMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        osBean = ManagementFactory.getOperatingSystemMXBean();
        
        // Programar monitoreo automático cada 5 minutos
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
            this::monitoreoAutomatico,
            1, 5, TimeUnit.MINUTES
        );
        
        logger.info("Monitor de rendimiento inicializado - Monitoreo cada 5 minutos");
    }
    
    /**
     * Obtiene la instancia única del monitor (Singleton thread-safe)
     */
    public static MonitorRendimiento getInstancia() {
        if (instancia == null) {
            synchronized (MonitorRendimiento.class) {
                if (instancia == null) {
                    instancia = new MonitorRendimiento();
                }
            }
        }
        return instancia;
    }
    
    /**
     * Obtiene las métricas actuales del sistema
     * @return Mapa con todas las métricas recolectadas
     */
    public Map<String, Object> obtenerMetricas() {
        Map<String, Object> metricas = new HashMap<>();
        
        // Memoria Heap
        MemoryUsage heapMemory = memoryBean.getHeapMemoryUsage();
        long memoriaUsada = heapMemory.getUsed();
        long memoriaMaxima = heapMemory.getMax();
        double porcentaje = (memoriaUsada * 100.0) / memoriaMaxima;
        
        metricas.put("memoria_usada_mb", memoriaUsada / (1024 * 1024));
        metricas.put("memoria_maxima_mb", memoriaMaxima / (1024 * 1024));
        metricas.put("memoria_porcentaje", porcentaje);
        metricas.put("memoria_libre_mb", (memoriaMaxima - memoriaUsada) / (1024 * 1024));
        
        // Hilos
        metricas.put("hilos_activos", threadBean.getThreadCount());
        metricas.put("hilos_daemon", threadBean.getDaemonThreadCount());
        metricas.put("hilos_pico", threadBean.getPeakThreadCount());
        metricas.put("hilos_iniciados_total", threadBean.getTotalStartedThreadCount());
        
        // Sistema
        metricas.put("tiempo_ejecucion_ms", runtimeBean.getUptime());
        metricas.put("procesadores", osBean.getAvailableProcessors());
        metricas.put("carga_sistema", osBean.getSystemLoadAverage());
        
        // Memoria no-heap (metadatos, clases cargadas, etc.)
        MemoryUsage nonHeapMemory = memoryBean.getNonHeapMemoryUsage();
        metricas.put("nonheap_usada_mb", nonHeapMemory.getUsed() / (1024 * 1024));
        
        return metricas;
    }
    
    /**
     * Monitoreo automático periódico ejecutado por el scheduler
     */
    private void monitoreoAutomatico() {
        try {
            Map<String, Object> metricas = obtenerMetricas();
            EstadoSistema estadoAnterior = estadoActual;
            estadoActual = evaluarEstadoSistema(metricas);
            
            // Log de rendimiento en archivo separado
            performanceLogger.info("METRICAS|{}|Memoria:{}%|Hilos:{}|Estado:{}",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                String.format("%.2f", metricas.get("memoria_porcentaje")),
                metricas.get("hilos_activos"),
                estadoActual);
            
            // Alertas solo si cambia el estado
            if (estadoActual != estadoAnterior) {
                logearCambioEstado(estadoAnterior, estadoActual, metricas);
            }
            
            // Alerta crítica con acciones automáticas
            if (estadoActual == EstadoSistema.CRITICO) {
                alertaCritica(metricas);
            }
            
        } catch (Exception e) {
            logger.error("Error en monitoreo automático", e);
        }
    }
    
    /**
     * Evalúa el estado del sistema basado en métricas
     */
    private EstadoSistema evaluarEstadoSistema(Map<String, Object> metricas) {
        double memoriaPorc = (double) metricas.get("memoria_porcentaje");
        int hilos = (int) metricas.get("hilos_activos");
        
        if (memoriaPorc >= UMBRAL_MEMORIA_CRITICO || hilos >= UMBRAL_HILOS_ALTO) {
            return EstadoSistema.CRITICO;
        } else if (memoriaPorc >= UMBRAL_MEMORIA_ALTO) {
            return EstadoSistema.ADVERTENCIA;
        }
        
        return EstadoSistema.NORMAL;
    }
    
    /**
     * Registra cambio de estado del sistema
     */
    private void logearCambioEstado(EstadoSistema anterior, EstadoSistema actual, Map<String, Object> metricas) {
        logger.warn("═══════════════════════════════════════════════════");
        logger.warn("CAMBIO DE ESTADO DEL SISTEMA: {} → {}", anterior, actual);
        logger.warn("Memoria: {}% | Hilos: {}",
            String.format("%.2f", metricas.get("memoria_porcentaje")),
            metricas.get("hilos_activos"));
        logger.warn("═══════════════════════════════════════════════════");
    }
    
    /**
     * Genera alerta crítica y toma acciones automáticas
     */
    private void alertaCritica(Map<String, Object> metricas) {
        // Evitar spam de alertas (máximo una cada 5 minutos)
        if (ultimaAlerta != null && 
            LocalDateTime.now().minusMinutes(5).isBefore(ultimaAlerta)) {
            return;
        }
        
        ultimaAlerta = LocalDateTime.now();
        
        logger.error("╔════════════════════════════════════════════════════╗");
        logger.error("║          ⚠️  ALERTA CRÍTICA DEL SISTEMA  ⚠️         ║");
        logger.error("╠════════════════════════════════════════════════════╣");
        logger.error("║ Memoria: {}% de {} MB",
            String.format("%.2f", metricas.get("memoria_porcentaje")),
            metricas.get("memoria_maxima_mb"));
        logger.error("║ Hilos activos: {}", metricas.get("hilos_activos"));
        logger.error("║ Tiempo ejecución: {} min",
            (long)metricas.get("tiempo_ejecucion_ms") / 60000);
        logger.error("╚════════════════════════════════════════════════════╝");
        
        // Intentar liberar memoria
        System.gc();
        logger.info("Garbage Collection forzado para liberar memoria");
    }
    
    /**
     * Registra métricas completas en el log
     */
    public void logearMetricas() {
        Map<String, Object> metricas = obtenerMetricas();
        
        logger.info("┌─────────────── MÉTRICAS DEL SISTEMA ───────────────┐");
        logger.info("│ MEMORIA:");
        logger.info("│   Usada: {} MB / {} MB ({}%)",
            metricas.get("memoria_usada_mb"),
            metricas.get("memoria_maxima_mb"),
            String.format("%.2f", metricas.get("memoria_porcentaje")));
        logger.info("│   Libre: {} MB",
            metricas.get("memoria_libre_mb"));
        logger.info("│");
        logger.info("│ HILOS:");
        logger.info("│   Activos: {} | Daemon: {} | Pico: {}",
            metricas.get("hilos_activos"),
            metricas.get("hilos_daemon"),
            metricas.get("hilos_pico"));
        logger.info("│");
        logger.info("│ SISTEMA:");
        logger.info("│   Procesadores: {}", metricas.get("procesadores"));
        logger.info("│   Carga: {}", metricas.get("carga_sistema"));
        logger.info("│   Tiempo ejecución: {} min",
            (long)metricas.get("tiempo_ejecucion_ms") / 60000);
        logger.info("│   Estado: {}", estadoActual);
        logger.info("└────────────────────────────────────────────────────┘");
    }
    
    /**
     * Verifica la salud del sistema
     * @return true si el sistema está saludable, false si está en estado crítico
     */
    public boolean verificarSaludSistema() {
        Map<String, Object> metricas = obtenerMetricas();
        EstadoSistema estado = evaluarEstadoSistema(metricas);
        
        if (estado == EstadoSistema.CRITICO) {
            logger.error("❌ Sistema en estado CRÍTICO");
            return false;
        } else if (estado == EstadoSistema.ADVERTENCIA) {
            logger.warn("⚠️  Sistema en estado de ADVERTENCIA");
            return true;
        } else {
            logger.info("✓ Sistema saludable");
            return true;
        }
    }
    
    /**
     * Obtiene el estado actual del sistema
     */
    public EstadoSistema getEstadoActual() {
        return estadoActual;
    }
    
    /**
     * Detiene el monitoreo automático (llamar al cerrar la aplicación)
     */
    public void detener() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                logger.info("Monitor de rendimiento detenido correctamente");
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
                logger.warn("Monitor de rendimiento forzado a detenerse");
            }
        }
    }
}
