package utp.edu.pe.nexoket.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoDatabase;

import utp.edu.pe.nexoket.config.ConfigManager;
import utp.edu.pe.nexoket.db.MongoDBConnection;

/**
 * Servicio de backup automático de la base de datos MongoDB
 * Realiza respaldos periódicos y limpieza de backups antiguos
 * 
 * @author NexoKet Team
 */
public class BackupService {
    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);
    private static BackupService instance;
    private ScheduledExecutorService scheduler;
    private Path backupPath;
    
    private BackupService() {
        ConfigManager config = ConfigManager.getInstance();
        String path = config.getProperty("backup.path");
        backupPath = Paths.get(path != null ? path : "./backups");
        
        try {
            Files.createDirectories(backupPath);
            logger.info("✓ Directorio de backups creado: {}", backupPath);
        } catch (IOException e) {
            logger.error("✗ Error creando directorio de backups", e);
        }
    }
    
    /**
     * Obtiene la instancia única del BackupService (Singleton).
     * 
     * @return La instancia única de BackupService
     */
    public static BackupService getInstance() {
        if (instance == null) {
            synchronized (BackupService.class) {
                if (instance == null) {
                    instance = new BackupService();
                }
            }
        }
        return instance;
    }
    
    /**
     * Inicia backup automático según configuración
     */
    public void iniciarBackupAutomatico() {
        ConfigManager config = ConfigManager.getInstance();
        boolean enabled = config.getBooleanProperty("backup.enabled", true);
        
        if (!enabled) {
            logger.info("⏸️ Backup automático deshabilitado en configuración");
            return;
        }
        
        int intervalHoras = config.getIntProperty("backup.interval.hours", 24);
        
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
            this::realizarBackup,
            1, // Primer backup después de 1 hora
            intervalHoras,
            TimeUnit.HOURS
        );
        
        logger.info("✓ Backup automático iniciado (cada {} horas)", intervalHoras);
    }
    
    /**
     * Realiza backup manual de todas las colecciones
     * @return true si fue exitoso
     */
    public boolean realizarBackup() {
        logger.info("🔄 Iniciando backup de base de datos...");
        long startTime = System.currentTimeMillis();
        
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFolder = "backup_" + timestamp;
            Path backupDir = backupPath.resolve(backupFolder);
            Files.createDirectories(backupDir);
            
            MongoDatabase db = MongoDBConnection.getInstance().getDatabase();
            String[] colecciones = {"Clientes", "Productos", "Ventas", "Proveedores", "Usuarios", "RegistroVenta"};
            
            int totalDocumentos = 0;
            for (String coleccion : colecciones) {
                int docs = backupColeccion(db, coleccion, backupDir);
                totalDocumentos += docs;
            }
            
            // Crear archivo de metadata
            crearMetadata(backupDir, colecciones.length, totalDocumentos);
            
            // Limpiar backups antiguos
            limpiarBackupsAntiguos();
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("✓ Backup completado exitosamente: {} ({} colecciones, {} documentos, {} ms)", 
                       backupFolder, colecciones.length, totalDocumentos, duration);
            return true;
            
        } catch (Exception e) {
            logger.error("✗ Error al realizar backup", e);
            return false;
        }
    }
    
    private int backupColeccion(MongoDatabase db, String nombreColeccion, Path backupDir) {
        try {
            File file = backupDir.resolve(nombreColeccion + ".json").toFile();
            
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("[\n");
                
                var collection = db.getCollection(nombreColeccion);
                var documentos = collection.find().iterator();
                
                boolean primero = true;
                int count = 0;
                while (documentos.hasNext()) {
                    if (!primero) {
                        writer.write(",\n");
                    }
                    Document doc = documentos.next();
                    writer.write("  " + doc.toJson());
                    primero = false;
                    count++;
                }
                
                writer.write("\n]");
                
                logger.debug("✓ Backup de colección '{}': {} documentos", nombreColeccion, count);
                return count;
            }
            
        } catch (Exception e) {
            logger.error("✗ Error al hacer backup de colección {}", nombreColeccion, e);
            return 0;
        }
    }
    
    private void crearMetadata(Path backupDir, int numColecciones, int totalDocumentos) {
        try {
            File metadataFile = backupDir.resolve("_metadata.txt").toFile();
            try (FileWriter writer = new FileWriter(metadataFile)) {
                writer.write("NexoKet Database Backup\n");
                writer.write("======================\n\n");
                writer.write("Fecha: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                writer.write("Colecciones: " + numColecciones + "\n");
                writer.write("Total documentos: " + totalDocumentos + "\n");
                writer.write("Base de datos: " + ConfigManager.getInstance().getMongoDatabase() + "\n");
            }
        } catch (IOException e) {
            logger.warn("No se pudo crear archivo de metadata", e);
        }
    }
    
    private void limpiarBackupsAntiguos() {
        ConfigManager config = ConfigManager.getInstance();
        int diasRetencion = config.getIntProperty("backup.retention.days", 7);
        long tiempoLimite = System.currentTimeMillis() - (diasRetencion * 24L * 60 * 60 * 1000);
        
        try {
            Files.list(backupPath)
                .filter(Files::isDirectory)
                .filter(path -> path.toFile().lastModified() < tiempoLimite)
                .forEach(path -> {
                    try {
                        Files.walk(path)
                            .sorted((a, b) -> b.compareTo(a)) // Orden inverso para borrar archivos antes que carpetas
                            .forEach(p -> {
                                try {
                                    Files.delete(p);
                                } catch (IOException e) {
                                    logger.warn("No se pudo eliminar: {}", p);
                                }
                            });
                        logger.info("✓ Backup antiguo eliminado: {}", path.getFileName());
                    } catch (IOException e) {
                        logger.warn("Error eliminando backup antiguo: {}", path);
                    }
                });
        } catch (IOException e) {
            logger.error("Error limpiando backups antiguos", e);
        }
    }
    
    /**
     * Detiene el servicio de backup automático
     */
    public void detener() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                logger.info("✓ Servicio de backup detenido");
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Obtiene la ruta del directorio de backups.
     * 
     * @return La ruta del directorio donde se almacenan los backups
     */
    public Path getBackupPath() {
        return backupPath;
    }
}
