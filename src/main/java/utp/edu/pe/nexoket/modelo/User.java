
package utp.edu.pe.nexoket.modelo;

public class User extends Cliente{
    private String username;
    private String contraseña;
    private String correo;
    public User(String nombre, String apellido, String dni, boolean descuento) {
        super(nombre, apellido, dni, descuento);
    }
    //constructor
    public User(String username, String contraseña, String correo, String nombre, String apellido, String dni, boolean descuento) {
        super(nombre, apellido, dni, descuento);
        this.username = username;
        this.contraseña = contraseña;
        this.correo = correo;
    }
    //getter and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    
}
