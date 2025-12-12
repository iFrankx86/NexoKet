package utp.edu.pe.nexoket.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import utp.edu.pe.nexoket.db.MongoDBConnection;
import utp.edu.pe.nexoket.modelo.Proveedor;

/**
 * DAO para gestionar proveedores en MongoDB
 * @author User
 */
public class ProveedorDAO {
    private final MongoCollection<Document> collection;
    
    public ProveedorDAO() {
        MongoDatabase database = MongoDBConnection.getInstance().getDatabase();
        this.collection = database.getCollection("proveedores");
    }
    
    /**
     * Inserta un nuevo proveedor
     */
    public boolean insertarProveedor(Proveedor proveedor) {
        try {
            // Verificar que no exista otro proveedor con el mismo RUC
            if (buscarPorRuc(proveedor.getRuc()) != null) {
                System.err.println("❌ Error: Ya existe un proveedor con RUC " + proveedor.getRuc());
                return false;
            }
            
            Document doc = proveedorToDocument(proveedor);
            collection.insertOne(doc);
            System.out.println("✅ Proveedor insertado: " + proveedor.getRazonSocial());
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al insertar proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Busca un proveedor por RUC
     */
    public Proveedor buscarPorRuc(String ruc) {
        try {
            Document doc = collection.find(Filters.eq("ruc", ruc)).first();
            return doc != null ? documentToProveedor(doc) : null;
        } catch (Exception e) {
            System.err.println("❌ Error al buscar proveedor: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene todos los proveedores activos
     */
    public List<Proveedor> obtenerTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        try {
            for (Document doc : collection.find(Filters.eq("activo", true))) {
                proveedores.add(documentToProveedor(doc));
            }
            System.out.println("✅ Se encontraron " + proveedores.size() + " proveedores");
        } catch (Exception e) {
            System.err.println("❌ Error al obtener proveedores: " + e.getMessage());
        }
        return proveedores;
    }
    
    /**
     * Actualiza un proveedor existente
     */
    public boolean actualizarProveedor(String ruc, Proveedor proveedor) {
        try {
            Document updates = new Document()
                .append("razonSocial", proveedor.getRazonSocial())
                .append("telefono", proveedor.getTelefono())
                .append("email", proveedor.getEmail())
                .append("direccion", proveedor.getDireccion())
                .append("productosQueSuply", proveedor.getProductosQueSuply())
                .append("activo", proveedor.isActivo());
            
            collection.updateOne(
                Filters.eq("ruc", ruc),
                new Document("$set", updates)
            );
            
            System.out.println("✅ Proveedor actualizado: " + proveedor.getRazonSocial());
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Elimina un proveedor (eliminación lógica)
     */
    public boolean eliminarProveedor(String ruc) {
        try {
            collection.updateOne(
                Filters.eq("ruc", ruc),
                Updates.set("activo", false)
            );
            System.out.println("✅ Proveedor eliminado (lógico): " + ruc);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar proveedor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca proveedores por RUC o nombre (para filtro)
     */
    public List<Proveedor> buscarPorRucONombre(String filtro) {
        List<Proveedor> proveedores = new ArrayList<>();
        try {
            // Buscar por RUC o razón social (case insensitive)
            var query = Filters.and(
                Filters.eq("activo", true),
                Filters.or(
                    Filters.regex("ruc", filtro, "i"),
                    Filters.regex("razonSocial", filtro, "i")
                )
            );
            
            for (Document doc : collection.find(query)) {
                proveedores.add(documentToProveedor(doc));
            }
        } catch (Exception e) {
            System.err.println("❌ Error al buscar proveedores: " + e.getMessage());
        }
        return proveedores;
    }
    
    /**
     * Convierte un Proveedor a Document de MongoDB
     */
    private Document proveedorToDocument(Proveedor proveedor) {
        return new Document()
            .append("ruc", proveedor.getRuc())
            .append("razonSocial", proveedor.getRazonSocial())
            .append("telefono", proveedor.getTelefono())
            .append("email", proveedor.getEmail())
            .append("direccion", proveedor.getDireccion())
            .append("productosQueSuply", proveedor.getProductosQueSuply())
            .append("activo", proveedor.isActivo());
    }
    
    /**
     * Convierte un Document de MongoDB a Proveedor
     */
    private Proveedor documentToProveedor(Document doc) {
        Proveedor proveedor = new Proveedor();
        proveedor.setRuc(doc.getString("ruc"));
        proveedor.setRazonSocial(doc.getString("razonSocial"));
        proveedor.setTelefono(doc.getString("telefono"));
        proveedor.setEmail(doc.getString("email"));
        proveedor.setDireccion(doc.getString("direccion"));
        
        List<String> productos = doc.getList("productosQueSuply", String.class, new ArrayList<>());
        proveedor.setProductosQueSuply(productos);
        
        proveedor.setActivo(doc.getBoolean("activo", true));
        
        return proveedor;
    }
}
