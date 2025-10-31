
package utp.edu.pe.nexoket.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoDBConnection instance; // Instancia única
    private MongoDatabase database;
    // Constructor privado para evitar instanciación directa
    private MongoDBConnection() {
        // URL de conexión a MongoDB Atlas
        String connectionString = "mongodb+srv://u22311204:frank@cluster0.zknqatn.mongodb.net/";
        
        // Crear el cliente y conectar a la base de datos
        MongoClient mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("NexoKet"); // Nombre de tu base de datos
    }
    // Método estático para obtener la instancia única
    public static MongoDBConnection getInstance() {
        if (instance == null) {
            synchronized (MongoDBConnection.class) { // Bloqueo para evitar problemas en entornos multihilo
                if (instance == null) {
                    instance = new MongoDBConnection();
                }
            }
        }
        return instance;
    }
    // Método para obtener la base de datos
    public MongoDatabase getDatabase() {
        return database;
    }
}
