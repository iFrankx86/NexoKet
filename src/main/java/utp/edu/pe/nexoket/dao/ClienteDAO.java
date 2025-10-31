
package utp.edu.pe.nexoket.dao;
import org.bson.Document;

import com.mongodb.client.MongoCollection;

import utp.edu.pe.nexoket.db.MongoDBConnection;
import utp.edu.pe.nexoket.modelo.Cliente;

public class ClienteDAO {
    private final MongoCollection<Document> collection;

    public ClienteDAO() {
        // Obtener la colección "Cliente" desde la base de datos
        collection = MongoDBConnection.getInstance().getDatabase().getCollection("Cliente");
    }

    // Método para registrar un cliente
    public void registrarCliente(Cliente cliente) {
        Document doc = new Document("nombre", cliente.getNombre())
                .append("apellido", cliente.getApellido())
                .append("dni", cliente.getDni())
                .append("telefono", cliente.getTelefono())
                .append("descuento", cliente.isDescuento());
        collection.insertOne(doc);
    }

    // Método para consultar un cliente por DNI
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
    // Método para actualizar un cliente
    public void actualizarCliente(String dni, Cliente clienteActualizado) {
        Document query = new Document("dni", dni);
        Document nuevosDatos = new Document("nombre", clienteActualizado.getNombre())
                .append("apellido", clienteActualizado.getApellido())
                .append("dni", clienteActualizado.getDni())
                .append("telefono", clienteActualizado.getTelefono())
                .append("descuento", clienteActualizado.isDescuento());
        collection.updateOne(query, new Document("$set", nuevosDatos));
    }
    // Método para eliminar un cliente
    public void eliminarCliente(String dni) {
        Document query = new Document("dni", dni);
        collection.deleteOne(query);
    }
}