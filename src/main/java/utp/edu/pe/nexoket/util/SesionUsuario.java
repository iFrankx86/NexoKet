package utp.edu.pe.nexoket.util;

import utp.edu.pe.nexoket.modelo.User;

/**
 * Clase Singleton para mantener la sesión del usuario logueado
 * @author User
 */
public class SesionUsuario {
    private static SesionUsuario instancia;
    private User usuarioActual;
    
    private SesionUsuario() {
    }
    
    public static SesionUsuario getInstance() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }
    
    public void iniciarSesion(User usuario) {
        this.usuarioActual = usuario;
        System.out.println("✅ Sesión iniciada: " + usuario.getUsername());
    }
    
    public void cerrarSesion() {
        System.out.println("👋 Sesión cerrada: " + (usuarioActual != null ? usuarioActual.getUsername() : ""));
        this.usuarioActual = null;
    }
    
    public User getUsuarioActual() {
        return usuarioActual;
    }
    
    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
    
    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual.getUsername() : "Desconocido";
    }
    
    public String getNombreCompleto() {
        if (usuarioActual != null) {
            return usuarioActual.getNombre() + " " + usuarioActual.getApellido();
        }
        return "Usuario Desconocido";
    }
}
