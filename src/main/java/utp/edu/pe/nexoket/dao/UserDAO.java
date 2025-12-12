package utp.edu.pe.nexoket.dao;

import java.util.Date;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import utp.edu.pe.nexoket.db.MongoDBConnection;
import utp.edu.pe.nexoket.modelo.User;
import utp.edu.pe.nexoket.security.PasswordService;

/**
 * DAO para gestión de usuarios con seguridad mejorada
 * Implementa encriptación de contraseñas con BCrypt
 * 
 * @author NexoKet Team
 */
public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private final MongoCollection<Document> collection;

    public UserDAO() {
        collection = MongoDBConnection.getInstance()
                .getDatabase()
                .getCollection("Usuarios");
    }

    /**
     * Registra un nuevo usuario con contraseña encriptada
     * @param user Usuario a registrar
     * @param plainPassword Contraseña en texto plano
     * @return true si se registró correctamente, false en caso contrario
     */
    public boolean registrarUser(User user, String plainPassword) {
        try {
            // Validar fortaleza de contraseña
            String validationError = PasswordService.validatePassword(plainPassword);
            if (validationError != null) {
                logger.warn("Intento de registro con contraseña débil: {}", user.getUsername());
                throw new IllegalArgumentException(validationError);
            }
            
            // Verificar si usuario ya existe
            if (existeUsuario(user.getUsername())) {
                logger.warn("Intento de registro de usuario duplicado: {}", user.getUsername());
                return false;
            }
            
            // Encriptar contraseña
            String hashedPassword = PasswordService.hashPassword(plainPassword);
            
            // Guardar en BD (incluir teléfono heredado de Cliente)
            Document doc = new Document()
                .append("username", user.getUsername())
                .append("password", hashedPassword)
                .append("correo", user.getCorreo())
                .append("nombre", user.getNombre())
                .append("apellido", user.getApellido())
                .append("dni", user.getDni())
                .append("telefono", user.getTelefono()) // Heredado de Cliente
                .append("descuento", user.isDescuento())
                .append("activo", true)
                .append("fechaCreacion", new Date())
                .append("ultimoAcceso", null);
            
            collection.insertOne(doc);
            logger.info("✓ Usuario registrado exitosamente: {}", user.getUsername());
            return true;
            
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al registrar usuario: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("✗ Error al registrar usuario: {}", user.getUsername(), e);
            return false;
        }
    }
    
    /**
     * Valida credenciales de login (MÉTODO SEGURO)
     * @param username Nombre de usuario
     * @param plainPassword Contraseña en texto plano
     * @return Usuario si las credenciales son válidas, null en caso contrario
     */
    public User validarLogin(String username, String plainPassword) {
        try {
            Document doc = collection.find(Filters.eq("username", username)).first();
            
            if (doc == null) {
                logger.warn("⚠️ Intento de login con usuario inexistente: {}", username);
                return null;
            }
            
            // Verificar si usuario está activo
            Boolean activo = doc.getBoolean("activo");
            if (activo != null && !activo) {
                logger.warn("⚠️ Intento de login con usuario inactivo: {}", username);
                return null;
            }
            
            String hashedPassword = doc.getString("password");
            
            if (PasswordService.verifyPassword(plainPassword, hashedPassword)) {
                // Actualizar último acceso
                actualizarUltimoAcceso(username);
                
                logger.info("✓ Login exitoso: {}", username);
                return documentToUser(doc);
            } else {
                logger.warn("⚠️ Intento de login con contraseña incorrecta: {}", username);
                return null;
            }
            
        } catch (Exception e) {
            logger.error("✗ Error en validación de login: {}", username, e);
            return null;
        }
    }

    /**
     * Consulta un usuario por su username (SIN RETORNAR CONTRASEÑA)
     */
    public User consultarUser(String username) {
        try {
            Document query = new Document("username", username);
            Document resultado = collection.find(query).first();

            if (resultado != null) {
                return documentToUser(resultado);
            }
            return null;
        } catch (Exception e) {
            logger.error("Error consultando usuario: {}", username, e);
            return null;
        }
    }

    /**
     * Actualiza los datos de un usuario
     * @param username Username actual
     * @param userActualizado Datos actualizados
     * @return true si se actualizó correctamente
     */
    public boolean actualizarUser(String username, User userActualizado) {
        try {
            Document query = new Document("username", username);
            Document nuevosDatos = new Document()
                    .append("nombre", userActualizado.getNombre())
                    .append("correo", userActualizado.getCorreo())
                    .append("apellido", userActualizado.getApellido())
                    .append("dni", userActualizado.getDni())
                    .append("telefono", userActualizado.getTelefono()) // Incluir teléfono
                    .append("descuento", userActualizado.isDescuento())
                    .append("fechaActualizacion", new Date());
            
            collection.updateOne(query, new Document("$set", nuevosDatos));
            logger.info("✓ Usuario actualizado: {}", username);
            return true;
        } catch (Exception e) {
            logger.error("Error actualizando usuario: {}", username, e);
            return false;
        }
    }
    
    /**
     * Cambia la contraseña de un usuario
     * @param username Username del usuario
     * @param oldPassword Contraseña actual
     * @param newPassword Nueva contraseña
     * @return true si se cambió correctamente
     */
    public boolean cambiarPassword(String username, String oldPassword, String newPassword) {
        try {
            // Validar credenciales actuales
            User user = validarLogin(username, oldPassword);
            if (user == null) {
                logger.warn("Intento de cambio de contraseña con credenciales inválidas: {}", username);
                return false;
            }
            
            // Validar nueva contraseña
            String validationError = PasswordService.validatePassword(newPassword);
            if (validationError != null) {
                logger.warn("Nueva contraseña no cumple requisitos: {}", username);
                throw new IllegalArgumentException(validationError);
            }
            
            // Encriptar nueva contraseña
            String hashedPassword = PasswordService.hashPassword(newPassword);
            
            // Actualizar en BD
            Document query = new Document("username", username);
            Document update = new Document("$set", 
                new Document("password", hashedPassword)
                    .append("fechaActualizacion", new Date())
            );
            
            collection.updateOne(query, update);
            logger.info("✓ Contraseña actualizada: {}", username);
            return true;
            
        } catch (Exception e) {
            logger.error("Error cambiando contraseña: {}", username, e);
            return false;
        }
    }

    /**
     * Desactiva un usuario (no lo elimina físicamente)
     */
    public boolean desactivarUser(String username) {
        try {
            Document query = new Document("username", username);
            Document update = new Document("$set", new Document("activo", false));
            collection.updateOne(query, update);
            logger.info("✓ Usuario desactivado: {}", username);
            return true;
        } catch (Exception e) {
            logger.error("Error desactivando usuario: {}", username, e);
            return false;
        }
    }

    /**
     * MÉTODO DEPRECADO - Usar validarLogin() en su lugar
     * @deprecated Método inseguro, usa validarLogin()
     */
    @Deprecated
    public boolean autenticarUser(String username, String contraseña) {
        logger.warn("⚠️ Método autenticarUser() está deprecado. Usar validarLogin()");
        User user = validarLogin(username, contraseña);
        return user != null;
    }
    
    private boolean existeUsuario(String username) {
        return collection.countDocuments(Filters.eq("username", username)) > 0;
    }
    
    private void actualizarUltimoAcceso(String username) {
        try {
            Document query = new Document("username", username);
            Document update = new Document("$set", new Document("ultimoAcceso", new Date()));
            collection.updateOne(query, update);
        } catch (Exception e) {
            logger.warn("Error actualizando último acceso: {}", username);
        }
    }
    
    private User documentToUser(Document doc) {
        return new User(
            doc.getString("username"),
            "", // No retornar contraseña por seguridad
            doc.getString("correo"),
            doc.getString("nombre"),
            doc.getString("apellido"),
            doc.getString("dni"),
            doc.getString("telefono") != null ? doc.getString("telefono") : "", // Teléfono
            doc.getBoolean("descuento", false)
        );
    }
}