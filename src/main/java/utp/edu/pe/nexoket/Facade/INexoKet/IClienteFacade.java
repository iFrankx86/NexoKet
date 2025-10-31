
package utp.edu.pe.nexoket.Facade.INexoKet;
import utp.edu.pe.nexoket.modelo.Cliente;
import utp.edu.pe.nexoket.modelo.User;

public interface IClienteFacade {
    // Operaciones de Cliente
    boolean registrarCliente(String nombre, String apellido, String dni, boolean descuento);
    Cliente buscarCliente(String dni);
    boolean actualizarCliente(String dni, String nombre, String apellido, boolean descuento);
    boolean eliminarCliente(String dni);
    
    // Operaciones de Usuario
    boolean registrarUsuario(String username, String contraseña, String nombre, String apellido, String dni, boolean descuento);
    User buscarUsuario(String username);
    boolean autenticarUsuario(String username, String contraseña);
    boolean actualizarUsuario(String username, String contraseña, String nombre, String apellido, String dni, boolean descuento);
    boolean eliminarUsuario(String username);
    
    // Operación combinada
    boolean registrarClienteYUsuario(String username, String contraseña, String nombre, String apellido, String dni, boolean descuento);
}
