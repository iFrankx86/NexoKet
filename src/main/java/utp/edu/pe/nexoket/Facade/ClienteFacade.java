package utp.edu.pe.nexoket.facade;
import utp.edu.pe.nexoket.Facade.INexoKet.IClienteFacade;
import utp.edu.pe.nexoket.dao.ClienteDAO;
import utp.edu.pe.nexoket.dao.UserDAO;
import utp.edu.pe.nexoket.modelo.Cliente;
import utp.edu.pe.nexoket.modelo.User;

public class ClienteFacade implements IClienteFacade {
    private final ClienteDAO clienteDAO;
    private final UserDAO userDAO;

    public ClienteFacade() {
        this.clienteDAO = new ClienteDAO();
        this.userDAO = new UserDAO();
    }

    // Operaciones de Cliente (implementación de la interfaz)
    @Override
    public boolean registrarCliente(String nombre, String apellido, String dni, boolean descuento) {
        try {
            Cliente cliente = new Cliente(nombre, apellido, dni, descuento);
            clienteDAO.registrarCliente(cliente);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Cliente buscarCliente(String dni) {
        try {
            return clienteDAO.consultarCliente(dni);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean actualizarCliente(String dni, String nombre, String apellido, boolean descuento) {
        try {
            Cliente clienteActualizado = new Cliente(nombre, apellido, dni, descuento);
            clienteDAO.actualizarCliente(dni, clienteActualizado);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean eliminarCliente(String dni) {
        try {
            clienteDAO.eliminarCliente(dni);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Operaciones de Usuario (implementación de la interfaz)
    @Override
    public boolean registrarUsuario(String username, String contraseña, String nombre, String apellido, String dni, boolean descuento) {
        try {
            // Como la interfaz no incluye correo, usar string vacío por defecto
            User user = new User(username, "", "", nombre, apellido, dni, "", descuento);
            userDAO.registrarUser(user, contraseña);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public User buscarUsuario(String username) {
        try {
            return userDAO.consultarUser(username);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean autenticarUsuario(String username, String contraseña) {
        try {
            return userDAO.autenticarUser(username, contraseña);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean actualizarUsuario(String username, String contraseña, String nombre, String apellido, String dni, boolean descuento) {
        try {
            User userActualizado = new User(username, contraseña, "", nombre, apellido, dni, "", descuento);
            userDAO.actualizarUser(username, userActualizado);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean eliminarUsuario(String username) {
        try {
            userDAO.desactivarUser(username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean registrarClienteYUsuario(String username, String contraseña, String nombre, String apellido, String dni, boolean descuento) {
        try {
            // Registrar como cliente
            Cliente cliente = new Cliente(nombre, apellido, dni, descuento);
            clienteDAO.registrarCliente(cliente);
            
            // Registrar como usuario
            User user = new User(username, "", "", nombre, apellido, dni, "", descuento);
            userDAO.registrarUser(user, contraseña);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Métodos adicionales para manejar correo (no están en la interfaz pero los necesitas)
    public boolean registrarUsuarioConCorreo(String username, String contraseña, String correo, String nombre, String apellido, String dni, boolean descuento) {
        try {
            User user = new User(username, "", correo, nombre, apellido, dni, "", descuento);
            userDAO.registrarUser(user, contraseña);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean actualizarUsuarioConCorreo(String username, String contraseña, String correo, String nombre, String apellido, String dni, boolean descuento) {
        try {
            User userActualizado = new User(username, contraseña, correo, nombre, apellido, dni, "", descuento);
            userDAO.actualizarUser(username, userActualizado);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean registrarClienteYUsuarioConCorreo(String username, String contraseña, String correo, String nombre, String apellido, String dni, boolean descuento) {
        try {
            // Registrar como cliente
            Cliente cliente = new Cliente(nombre, apellido, dni, descuento);
            clienteDAO.registrarCliente(cliente);
            
            // Registrar como usuario con correo
            User user = new User(username, "", correo, nombre, apellido, dni, "", descuento);
            userDAO.registrarUser(user, contraseña);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean registrarClienteYUsuarioConCorreoYTelefono(String username, String contraseña, String correo, String nombre, String apellido, String dni, String telefono, boolean descuento) {
        try {
            // Registrar como cliente con teléfono
            Cliente cliente = new Cliente(nombre, apellido, dni, telefono, descuento);
            clienteDAO.registrarCliente(cliente);
            
            // Registrar como usuario con correo Y teléfono (User hereda teléfono de Cliente)
            User user = new User(username, "", correo, nombre, apellido, dni, telefono, descuento);
            userDAO.registrarUser(user, contraseña);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}