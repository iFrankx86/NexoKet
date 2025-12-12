
package utp.edu.pe.nexoket.dao;
import org.bson.Document;

import com.mongodb.client.MongoCollection;

import utp.edu.pe.nexoket.db.MongoDBConnection;
import utp.edu.pe.nexoket.modelo.Cliente;

/**
 * Data Access Object (DAO) para la gestión de clientes en la base de datos MongoDB.
 * Proporciona operaciones CRUD (Create, Read, Update, Delete) para los clientes.
 * 
 * @author NexoKet Team
 * @version 1.0
 */
public class ClienteDAO {
    private final MongoCollection<Document> collection;

    /**
     * Constructor que inicializa la conexión con la colección de clientes en MongoDB.
     */
    public ClienteDAO() {
        // Obtener la colección "Cliente" desde la base de datos
        collection = MongoDBConnection.getInstance().getDatabase().getCollection("Cliente");
    }

    /**
     * Registra un nuevo cliente en la base de datos.
     * 
     * @param cliente El objeto cliente a registrar con todos sus datos
     */
    public void registrarCliente(Cliente cliente) {
        Document doc = new Document("nombre", cliente.getNombre())
                .append("apellido", cliente.getApellido())
                .append("dni", cliente.getDni())
                .append("telefono", cliente.getTelefono())
                .append("descuento", cliente.isDescuento());
        collection.insertOne(doc);
    }

    /**
     * Consulta un cliente por su DNI en la base de datos.
     * 
     * @param dni El DNI del cliente a buscar
     * @return El cliente encontrado o null si no existe
     */
    public Cliente consultarCliente(String dni) {
        Document query = new Document("dni", dni);
        Document resultado = collection.find(query).first();

        if (resultado != null) {
            // Manejar valores null en todos los campos
            // Intentar con "nombres" (plural) primero, luego "nombre" (singular)
            String nombre = resultado.getString("nombres");
            if (nombre == null || nombre.isEmpty()) {
                nombre = resultado.getString("nombre");
            }
            if (nombre == null) nombre = "";
            
            // Intentar con "apellidos" (plural) primero, luego "apellido" (singular)
            String apellido = resultado.getString("apellidos");
            if (apellido == null || apellido.isEmpty()) {
                apellido = resultado.getString("apellido");
            }
            if (apellido == null) apellido = "";
            
            String telefono = resultado.getString("telefono");
            if (telefono == null) telefono = "";
            
            // Manejar descuento null (por defecto false)
            Boolean descuentoObj = resultado.getBoolean("descuento");
            boolean descuento = (descuentoObj != null) ? descuentoObj : false;
            
            return new Cliente(
                nombre,
                apellido,
                resultado.getString("dni"),
                telefono,
                descuento
            );
        }
        return null; // Si no se encuentra el cliente
    }
    /**
     * Actualiza los datos de un cliente existente en la base de datos.
     * 
     * @param dni El DNI del cliente a actualizar
     * @param clienteActualizado El objeto cliente con los nuevos datos
     */
    public void actualizarCliente(String dni, Cliente clienteActualizado) {
        Document query = new Document("dni", dni);
        Document nuevosDatos = new Document("nombre", clienteActualizado.getNombre())
                .append("apellido", clienteActualizado.getApellido())
                .append("dni", clienteActualizado.getDni())
                .append("telefono", clienteActualizado.getTelefono())
                .append("descuento", clienteActualizado.isDescuento());
        collection.updateOne(query, new Document("$set", nuevosDatos));
    }
    /**
     * Elimina un cliente de la base de datos por su DNI.
     * 
     * @param dni El DNI del cliente a eliminar
     */
    public void eliminarCliente(String dni) {
        Document query = new Document("dni", dni);
        collection.deleteOne(query);
    }
}