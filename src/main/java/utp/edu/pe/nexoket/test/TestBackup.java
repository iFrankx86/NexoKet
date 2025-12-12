package utp.edu.pe.nexoket.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utp.edu.pe.nexoket.util.BackupService;

/**
 * Clase de prueba para ejecutar backup manual del sistema NexoKet.
 * 
 * @author NexoKet Team
 * @version 1.0
 */
public class TestBackup {
    private static final Logger logger = LoggerFactory.getLogger(TestBackup.class);
    
    public static void main(String[] args) {
        logger.info("═══════════════════════════════════════════════════");
        logger.info("   Test de Backup - Sistema NexoKet");
        logger.info("   Versión 1.0.0 - UTP 2025");
        logger.info("═══════════════════════════════════════════════════");
        
        try {
            // Obtener instancia del servicio de backup
            BackupService backupService = BackupService.getInstance();
            
            logger.info("🔧 Iniciando test de backup manual...");
            logger.info("📁 Directorio de backups: {}", backupService.getBackupPath());
            
            // Ejecutar backup manual
            boolean resultado = backupService.realizarBackup();
            
            if (resultado) {
                logger.info("✅ TEST EXITOSO: Backup completado correctamente");
                logger.info("📋 Puedes verificar los archivos en: {}", backupService.getBackupPath());
            } else {
                logger.error("❌ TEST FALLIDO: Error al realizar backup");
            }
            
        } catch (Exception e) {
            logger.error("❌ ERROR CRÍTICO en test de backup", e);
        }
        
        logger.info("═══════════════════════════════════════════════════");
        logger.info("Test de backup finalizado");
        logger.info("═══════════════════════════════════════════════════");
    }
}