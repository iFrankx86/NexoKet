package utp.edu.pe.nexoket.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Gestor de configuración centralizado del sistema
 * Lee configuraciones desde application.properties y variables de entorno
 * 
 * @author NexoKet Team
 */
public class ConfigManager {
    private static ConfigManager instance;
    private Properties properties;
    
    private ConfigManager() {
        properties = new Properties();
        loadProperties();
    }
    
    /**
     * Obtiene la instancia única del ConfigManager (Singleton).
     * 
     * @return La instancia única de ConfigManager
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
    
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                System.out.println("✓ Configuración cargada exitosamente");
            } else {
                System.err.println("⚠️ Archivo application.properties no encontrado");
            }
        } catch (IOException e) {
            System.err.println("✗ Error cargando configuración: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la URI de MongoDB (prioriza variable de entorno).
     * 
     * @return La URI de conexión a MongoDB
     */
    public String getMongoUri() {
        String uri = System.getenv("MONGODB_URI");
        if (uri != null && !uri.isEmpty()) {
            return uri;
        }
        return expandProperty(properties.getProperty("mongodb.uri"));
    }
    
    /**
     * Obtiene el nombre de la base de datos MongoDB.
     * 
     * @return El nombre de la base de datos MongoDB
     */
    public String getMongoDatabase() {
        String db = System.getenv("MONGODB_DATABASE");
        if (db != null && !db.isEmpty()) {
            return db;
        }
        return expandProperty(properties.getProperty("mongodb.database"));
    }
    
    /**
     * Obtiene una propiedad como String.
     * 
     * @param key La clave de la propiedad a obtener
     * @return El valor de la propiedad o null si no existe
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Obtiene una propiedad como entero.
     * 
     * @param key La clave de la propiedad a obtener
     * @param defaultValue El valor por defecto si la propiedad no existe o no es válida
     * @return El valor entero de la propiedad o el valor por defecto
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Obtiene una propiedad como boolean.
     * 
     * @param key La clave de la propiedad a obtener
     * @param defaultValue El valor por defecto si la propiedad no existe
     * @return El valor booleano de la propiedad o el valor por defecto
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    /**
     * Expande variables de entorno en formato ${VAR:default}
     */
    private String expandProperty(String value) {
        if (value == null) return null;
        
        // Buscar patrón ${VAR:default}
        int start = value.indexOf("${");
        if (start == -1) return value;
        
        int end = value.indexOf("}", start);
        if (end == -1) return value;
        
        String varPart = value.substring(start + 2, end);
        String[] parts = varPart.split(":", 2);
        String varName = parts[0];
        String defaultValue = parts.length > 1 ? parts[1] : "";
        
        String envValue = System.getenv(varName);
        String replacement = envValue != null ? envValue : defaultValue;
        
        return value.substring(0, start) + replacement + value.substring(end + 1);
    }
}
