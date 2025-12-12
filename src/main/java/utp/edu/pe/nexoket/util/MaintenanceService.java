package utp.edu.pe.nexoket.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoDatabase;

import utp.edu.pe.nexoket.config.ConfigManager;
import utp.edu.pe.nexoket.db.MongoDBConnection;

/**
 * Servicio de mantenimiento automático del sistema
 * Ejecuta tareas periódicas: limpieza de logs, verificación de salud, etc.
 * 
 * @author NexoKet Team
 */
public class MaintenanceService {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);
    private static MaintenanceService instance;
    private ScheduledExecutorService scheduler;
    
    private MaintenanceService() {}
    
    public static MaintenanceService getInstance() {
        if (instance == null) {
            synchronized (MaintenanceService.class) {
                if (instance == null) {
                    instance = new MaintenanceService();
                }
            }
        }
        return instance;
    }
    
    /**
     * Inicia tareas de mantenimiento programadas
     */
    public void iniciarMantenimientoAutomatico() {
        scheduler = Executors.newScheduledThreadPool(1);
        
        // Limpieza de logs cada día a las 3 AM
        long initialDelay = calcularDelayHasta3AM();
        scheduler.scheduleAtFixedRate(
            this::limpiarLogsAntiguos,
            initialDelay,
            24,
            TimeUnit.HOURS
        );
        
        // Verificación de salud del sistema cada hora
        scheduler.scheduleAtFixedRate(
            this::verificarSaludSistema,
            5, // Primer check en 5 minutos
            60, // Luego cada hora
            TimeUnit.MINUTES
        );
        
        logger.info("✓ Servicio de mantenimiento iniciado");
        logger.info("  - Limpieza de logs: cada 24h a las 3:00 AM");
        logger.info("  - Verificación de salud: cada hora");
    }
    
    /**
     * Limpia logs mayores a N días según configuración
     */
    public void limpiarLogsAntiguos() {
        logger.info("🧹 Iniciando limpieza de logs antiguos...");
        
        ConfigManager config = ConfigManager.getInstance();
        String logPath = config.getProperty("log.path");
        int maxDias = config.getIntProperty("log.max.history", 30);
        
        Path logsDir = Paths.get(logPath != null ? logPath : "./logs");
        
        if (!Files.exists(logsDir)) {
            logger.warn("Directorio de logs no existe: {}", logsDir);
            return;
        }
        
        long tiempoLimite = System.currentTimeMillis() - (maxDias * 24L * 60 * 60 * 1000);
        
        try {
            long eliminados = Files.walk(logsDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".log"))
                .filter(path -> path.toFile().lastModified() < tiempoLimite)
                .peek(path -> logger.debug("Eliminando log antiguo: {}", path.getFileName()))
                .map(Path::toFile)
                .filter(File::delete)
                .count();
            
            if (eliminados > 0) {
                logger.info("✓ Limpieza completada: {} archivos eliminados", eliminados);
            } else {
                logger.debug("No hay logs antiguos para eliminar (>{} días)", maxDias);
            }
            
        } catch (IOException e) {
            logger.error("✗ Error al limpiar logs", e);
        }
    }
    
    /**
     * Verifica el estado de salud del sistema
     */
    public void verificarSaludSistema() {
        logger.debug("🏥 Verificando salud del sistema...");
        
        boolean hayProblemas = false;
        
        // 1. Verificar conexión a BD
        try {
            boolean dbOk = verificarConexionBD();
            if (!dbOk) {
                logger.error("⚠️ ALERTA: Base de datos no responde");
                hayProblemas = true;
            }
        } catch (Exception e) {
            logger.error("⚠️ ALERTA: Error verificando BD", e);
            hayProblemas = true;
        }
        
        // 2. Verificar espacio en disco
        File raiz = new File(".");
        long espacioLibre = raiz.getUsableSpace();
        long espacioTotal = raiz.getTotalSpace();
        double porcentajeLibre = (espacioLibre * 100.0) / espacioTotal;
        
        if (porcentajeLibre < 10) {
            logger.warn("⚠️ ALERTA: Espacio en disco bajo ({:.2f}% libre)", porcentajeLibre);
            hayProblemas = true;
        } else if (porcentajeLibre < 20) {
            logger.info("ℹ️ Espacio en disco: {:.2f}% libre", porcentajeLibre);
        }
        
        // 3. Verificar memoria
        Runtime runtime = Runtime.getRuntime();
        long memoriaUsada = runtime.totalMemory() - runtime.freeMemory();
        long memoriaMax = runtime.maxMemory();
        double porcentajeMemoria = (memoriaUsada * 100.0) / memoriaMax;
        
        if (porcentajeMemoria > 90) {
            logger.warn("⚠️ ALERTA: Uso de memoria crítico ({:.2f}%)", porcentajeMemoria);
            hayProblemas = true;
        } else if (porcentajeMemoria > 75) {
            logger.info("ℹ️ Uso de memoria alto ({:.2f}%)", porcentajeMemoria);
        }
        
        // 4. Verificar cantidad de hilos
        int threadCount = Thread.activeCount();
        if (threadCount > 50) {
            logger.warn("⚠️ ALERTA: Número alto de hilos activos: {}", threadCount);
            hayProblemas = true;
        }
        
        if (!hayProblemas) {
            logger.debug("✓ Sistema saludable (Disco: {:.1f}% libre, Memoria: {:.1f}% usada, Hilos: {})", 
                        porcentajeLibre, porcentajeMemoria, threadCount);
        }
    }
    
    private boolean verificarConexionBD() {
        try {
            MongoDatabase db = MongoDBConnection.getInstance().getDatabase();
            // Intentar listar colecciones (operación rápida)
            db.listCollectionNames().first();
            return true;
        } catch (Exception e) {
            logger.error("Error verificando conexión a BD", e);
            return false;
        }
    }
    
    private long calcularDelayHasta3AM() {
        Calendar ahora = Calendar.getInstance();
        Calendar manana3AM = (Calendar) ahora.clone();
        
        // Si ya pasaron las 3 AM, programar para mañana
        if (ahora.get(Calendar.HOUR_OF_DAY) >= 3) {
            manana3AM.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        manana3AM.set(Calendar.HOUR_OF_DAY, 3);
        manana3AM.set(Calendar.MINUTE, 0);
        manana3AM.set(Calendar.SECOND, 0);
        manana3AM.set(Calendar.MILLISECOND, 0);
        
        long diff = manana3AM.getTimeInMillis() - ahora.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toHours(diff);
    }
    
    /**
     * Detiene el servicio de mantenimiento
     */
    public void detener() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                logger.info("✓ Servicio de mantenimiento detenido");
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Ejecuta todas las tareas de mantenimiento manualmente
     */
    public void ejecutarMantenimientoCompleto() {
        logger.info("🔧 Ejecutando mantenimiento completo...");
        limpiarLogsAntiguos();
        verificarSaludSistema();
        logger.info("✓ Mantenimiento completo finalizado");
    }
}
