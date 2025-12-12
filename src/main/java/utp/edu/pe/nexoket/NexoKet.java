/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package utp.edu.pe.nexoket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utp.edu.pe.nexoket.jform.InicioSesion;
import utp.edu.pe.nexoket.util.BackupService;
import utp.edu.pe.nexoket.util.MaintenanceService;
import utp.edu.pe.nexoket.util.MonitorRendimiento;

/**
 * Clase principal de NexoKet
 * Sistema de gestión de bodega con seguridad, monitoreo y backups automáticos
 * 
 * @author NexoKet Team - UTP 2025
 */
public class NexoKet {
    private static final Logger logger = LoggerFactory.getLogger(NexoKet.class);

    public static void main(String[] args) {
        logger.info("═══════════════════════════════════════════════════");
        logger.info("   NexoKet - Sistema de Gestión de Bodega");
        logger.info("   Versión 1.0.0 - UTP 2025");
        logger.info("═══════════════════════════════════════════════════");
        
        try {
            // Inicializar servicios del sistema
            inicializarServicios();
            
            // Abrir ventana de inicio de sesión
            logger.info("Abriendo ventana de inicio de sesión...");
            InicioSesion inicioSesion = new InicioSesion();
            inicioSesion.setVisible(true);
            
            logger.info("✓ Aplicación iniciada correctamente");
            
            // Agregar shutdown hook para cerrar servicios correctamente
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Cerrando aplicación...");
                detenerServicios();
                logger.info("✓ Aplicación cerrada correctamente");
            }));
            
        } catch (Exception e) {
            logger.error("✗ Error crítico al iniciar la aplicación", e);
            System.err.println("Error al iniciar NexoKet. Ver logs para más detalles.");
            System.exit(1);
        }
    }
    
    /**
     * Inicializa todos los servicios del sistema
     */
    private static void inicializarServicios() {
        logger.info("Inicializando servicios del sistema...");
        
        try {
            // 1. Iniciar monitor de rendimiento
            MonitorRendimiento monitor = MonitorRendimiento.getInstancia();
            logger.info("✓ Monitor de rendimiento iniciado");
            
            // 2. Iniciar servicio de backups automáticos
            BackupService backupService = BackupService.getInstance();
            backupService.iniciarBackupAutomatico();
            logger.info("✓ Servicio de backups iniciado");
            
            // 3. Iniciar servicio de mantenimiento
            MaintenanceService maintenanceService = MaintenanceService.getInstance();
            maintenanceService.iniciarMantenimientoAutomatico();
            logger.info("✓ Servicio de mantenimiento iniciado");
            
            logger.info("═══════════════════════════════════════════════════");
            logger.info("Todos los servicios iniciados correctamente");
            logger.info("═══════════════════════════════════════════════════\n");
            
        } catch (Exception e) {
            logger.error("Error al inicializar servicios", e);
            throw new RuntimeException("Fallo en la inicialización de servicios", e);
        }
    }
    
    /**
     * Detiene todos los servicios del sistema de forma ordenada
     */
    private static void detenerServicios() {
        try {
            logger.info("Deteniendo servicios...");
            
            // Detener servicios en orden inverso
            MaintenanceService.getInstance().detener();
            BackupService.getInstance().detener();
            MonitorRendimiento.getInstancia().detener();
            
            // Cerrar conexión a MongoDB
            try {
                utp.edu.pe.nexoket.db.MongoDBConnection.getInstance().close();
            } catch (Exception e) {
                logger.warn("Error al cerrar conexión MongoDB", e);
            }
            
            logger.info("✓ Todos los servicios detenidos");
            
        } catch (Exception e) {
            logger.error("Error al detener servicios", e);
        }
    }
}
