
package utp.edu.pe.nexoket.modelo;


public class Cliente {
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private boolean descuento;

    // Constructor completo
    public Cliente(String nombre, String apellido, String dni, String telefono, boolean descuento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.descuento = descuento;
    }
    
    // Constructor sin teléfono (compatibilidad)
    public Cliente(String nombre, String apellido, String dni, boolean descuento) {
        this(nombre, apellido, dni, "", descuento);
    }

    // Constructor vacío
    public Cliente() {
        this.telefono = "";
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { 
        return apellido; }
    
    public void setApellido(String apellido) { 
        this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public boolean isDescuento() { return descuento; }
    public void setDescuento(boolean descuento) { this.descuento = descuento; }
}