package utp.edu.pe.nexoket.test;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import utp.edu.pe.nexoket.db.MongoDBConnection;

/**
 * Migración para agregar campo teléfono a usuarios existentes
 */
public class MigracionTelefono {
    
    public static void main(String[] args) {
        System.out.println("=== MIGRACIÓN DE TELÉFONOS ===");
        
        try {
            MongoDBConnection conexion = MongoDBConnection.getInstance();
            MongoCollection<Document> coleccionUsuarios = conexion.getDatabase().getCollection("Usuarios");
            
            System.out.println("Buscando usuarios sin teléfono...");
            
            int usuariosActualizados = 0;
            try (MongoCursor<Document> cursor = coleccionUsuarios.find(Filters.exists("telefono", false)).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String username = doc.getString("username");
                    
                    System.out.println("Agregando teléfono vacío a usuario: " + username);
                    
                    coleccionUsuarios.updateOne(
                        Filters.eq("username", username),
                        Updates.set("telefono", "")
                    );
                    
                    usuariosActualizados++;
                }
            }
            
            System.out.println("✓ " + usuariosActualizados + " usuarios actualizados con campo teléfono");
            
            // Verificar usuarios actualizados
            System.out.println("\nVerificando usuarios después de la migración:");
            try (MongoCursor<Document> cursor = coleccionUsuarios.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String username = doc.getString("username");
                    String telefono = doc.getString("telefono");
                    
                    System.out.println("Usuario: " + username + " - Teléfono: " + 
                                     (telefono != null ? ("'" + telefono + "'") : "NULL"));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error en la migración: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== FIN DE LA MIGRACIÓN ===");
    }
}