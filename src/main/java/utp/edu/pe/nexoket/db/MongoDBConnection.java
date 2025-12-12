
package utp.edu.pe.nexoket.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import utp.edu.pe.nexoket.config.ConfigManager;

/**
 * Gestor de conexión a MongoDB usando patrón Singleton
 * Configuración segura usando variables de entorno
 * 
 * @author NexoKet Team
 */
public class MongoDBConnection {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBConnection.class);
    private static MongoDBConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;
    
    private MongoDBConnection() {
        try {
            ConfigManager config = ConfigManager.getInstance();
            String uri = config.getMongoUri();
            String dbName = config.getMongoDatabase();
            
            logger.info("Conectando a MongoDB...");
            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase(dbName);
            
            // Verificar conexión
            database.listCollectionNames().first();
            
            logger.info("✓ Conexión a MongoDB establecida: {}", dbName);
        } catch (Exception e) {
            logger.error("✗ Error al conectar con MongoDB", e);
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
    }
    
    public static MongoDBConnection getInstance() {
        if (instance == null) {
            synchronized (MongoDBConnection.class) {
                if (instance == null) {
                    instance = new MongoDBConnection();
                }
            }
        }
        return instance;
    }
    
    public MongoDatabase getDatabase() {
        return database;
    }
    
    /**
     * Cierra la conexión a MongoDB
     * Debe llamarse al cerrar la aplicación
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            logger.info("✓ Conexión a MongoDB cerrada");
        }
    }
}
