
package utp.edu.pe.nexoket.Facade.INexoKet;
import utp.edu.pe.nexoket.modelo.Cliente;
import utp.edu.pe.nexoket.modelo.User;

/**
 * Interfaz que define las operaciones del facade para la gestión de clientes y usuarios.
 * Proporciona métodos para realizar operaciones CRUD sobre clientes y usuarios,
 * así como operaciones de autenticación.
 * 
 * @author NexoKet Team
 * @version 1.0
 */
public interface IClienteFacade {
    // Operaciones de Cliente
    
    /**
     * Registra un nuevo cliente en el sistema.
     * 
     * @param nombre Nombre del cliente
     * @param apellido Apellido del cliente
     * @param dni DNI del cliente
     * @param descuento Indica si el cliente tiene derecho a descuentos
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean registrarCliente(String nombre, String apellido, String dni, boolean descuento);
    
    /**
     * Busca un cliente por su DNI.
     * 
     * @param dni DNI del cliente a buscar
     * @return El cliente encontrado o null si no existe
     */
    Cliente buscarCliente(String dni);
    
    /**
     * Actualiza los datos de un cliente existente.
     * 
     * @param dni DNI del cliente a actualizar
     * @param nombre Nuevo nombre del cliente
     * @param apellido Nuevo apellido del cliente
     * @param descuento Nuevo estado de descuento
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean actualizarCliente(String dni, String nombre, String apellido, boolean descuento);
    
    /**
     * Elimina un cliente del sistema.
     * 
     * @param dni DNI del cliente a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean eliminarCliente(String dni);
    
    // Operaciones de Usuario
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param username Nombre de usuario
     * @param contraseña Contraseña del usuario
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param dni DNI del usuario
     * @param descuento Indica si el usuario tiene derecho a descuentos
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean registrarUsuario(String username, String contraseña, String nombre, String apellido, String dni, boolean descuento);
    
    /**
     * Busca un usuario por su nombre de usuario.
     * 
     * @param username Nombre de usuario a buscar
     * @return El usuario encontrado o null si no existe
     */
    User buscarUsuario(String username);
    
    /**
     * Autentica un usuario en el sistema.
     * 
     * @param username Nombre de usuario
     * @param contraseña Contraseña del usuario
     * @return true si la autenticación fue exitosa, false en caso contrario
     */
    boolean autenticarUsuario(String username, String contraseña);
    
    /**
     * Actualiza los datos de un usuario existente.
     * 
     * @param username Nombre de usuario a actualizar
     * @param contraseña Nueva contraseña del usuario
     * @param nombre Nuevo nombre del usuario
     * @param apellido Nuevo apellido del usuario
     * @param dni Nuevo DNI del usuario
     * @param descuento Nuevo estado de descuento
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean actualizarUsuario(String username, String contraseña, String nombre, String apellido, String dni, boolean descuento);
    
    /**
     * Elimina un usuario del sistema.
     * 
     * @param username Nombre de usuario a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean eliminarUsuario(String username);
    
    // Operación combinada
    
    /**
     * Registra simultáneamente un cliente y su usuario asociado.
     * 
     * @param username Nombre de usuario
     * @param contraseña Contraseña del usuario
     * @param nombre Nombre del cliente/usuario
     * @param apellido Apellido del cliente/usuario
     * @param dni DNI del cliente/usuario
     * @param descuento Indica si tiene derecho a descuentos
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean registrarClienteYUsuario(String username, String contraseña, String nombre, String apellido, String dni, boolean descuento);
}
