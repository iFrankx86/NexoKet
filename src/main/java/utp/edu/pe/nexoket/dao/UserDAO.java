package utp.edu.pe.nexoket.dao;
import utp.edu.pe.nexoket.modelo.User;
import utp.edu.pe.nexoket.db.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class UserDAO {
    private final MongoCollection<Document> collection;

    public UserDAO() {
        collection = MongoDBConnection.getInstance().getDatabase().getCollection("User");
    }

    public void registrarUser(User user) {
        Document doc = new Document("username", user.getUsername())
                .append("contraseña", user.getContraseña())
                .append("nombre", user.getNombre())
                .append("correo", user.getCorreo())
                .append("apellido", user.getApellido())
                .append("dni", user.getDni())
                .append("descuento", user.isDescuento());
        collection.insertOne(doc);
    }

    public User consultarUser(String username) {
        Document query = new Document("username", username);
        Document resultado = collection.find(query).first();

        if (resultado != null) {
            return new User(
                resultado.getString("username"),
                resultado.getString("contraseña"),
                resultado.getString("correo"),
                resultado.getString("nombre"),
                resultado.getString("apellido"),
                resultado.getString("dni"),
                resultado.getBoolean("descuento")
            );
        }
        return null;
    }

    public void actualizarUser(String username, User userActualizado) {
        Document query = new Document("username", username);
        Document nuevosDatos = new Document("username", userActualizado.getUsername())
                .append("contraseña", userActualizado.getContraseña())
                .append("nombre", userActualizado.getNombre())
                .append("correo", userActualizado.getCorreo())
                .append("apellido", userActualizado.getApellido())
                .append("dni", userActualizado.getDni())
                .append("descuento", userActualizado.isDescuento());
        collection.updateOne(query, new Document("$set", nuevosDatos));
    }

    public void eliminarUser(String username) {
        Document query = new Document("username", username);
        collection.deleteOne(query);
    }

    public boolean autenticarUser(String username, String contraseña) {
        Document query = new Document("username", username)
                .append("contraseña", contraseña);
        return collection.find(query).first() != null;
    }
}