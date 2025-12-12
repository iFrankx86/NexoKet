
package utp.edu.pe.nexoket.modelo;

/**
 * Clase que representa un cliente en el sistema NexoKet.
 * Contiene información personal del cliente y su elegibilidad para descuentos.
 * 
 * @author NexoKet Team
 * @version 1.0
 */
public class Cliente {
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private boolean descuento;

    /**
     * Constructor completo para crear un cliente con todos los datos.
     * 
     * @param nombre Nombre del cliente
     * @param apellido Apellido del cliente
     * @param dni Documento Nacional de Identidad del cliente
     * @param telefono Número de teléfono del cliente
     * @param descuento Indica si el cliente tiene derecho a descuentos
     */
    public Cliente(String nombre, String apellido, String dni, String telefono, boolean descuento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.descuento = descuento;
    }
    
    /**
     * Constructor para crear un cliente sin número de teléfono.
     * Se mantiene por compatibilidad con versiones anteriores.
     * 
     * @param nombre Nombre del cliente
     * @param apellido Apellido del cliente
     * @param dni Documento Nacional de Identidad del cliente
     * @param descuento Indica si el cliente tiene derecho a descuentos
     */
    public Cliente(String nombre, String apellido, String dni, boolean descuento) {
        this(nombre, apellido, dni, "", descuento);
    }

    /**
     * Constructor vacío por defecto.
     * Inicializa el teléfono como cadena vacía.
     */
    public Cliente() {
        this.telefono = "";
    }

    // Getters y Setters
    
    /**
     * Obtiene el nombre del cliente.
     * 
     * @return El nombre del cliente
     */
    public String getNombre() { return nombre; }
    
    /**
     * Establece el nombre del cliente.
     * 
     * @param nombre El nuevo nombre del cliente
     */
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    /**
     * Obtiene el apellido del cliente.
     * 
     * @return El apellido del cliente
     */
    public String getApellido() { 
        return apellido; }
    
    /**
     * Establece el apellido del cliente.
     * 
     * @param apellido El nuevo apellido del cliente
     */
    public void setApellido(String apellido) { 
        this.apellido = apellido; }

    /**
     * Obtiene el DNI del cliente.
     * 
     * @return El DNI del cliente
     */
    public String getDni() { return dni; }
    
    /**
     * Establece el DNI del cliente.
     * 
     * @param dni El nuevo DNI del cliente
     */
    public void setDni(String dni) { this.dni = dni; }
    
    /**
     * Obtiene el teléfono del cliente.
     * 
     * @return El número de teléfono del cliente
     */
    public String getTelefono() { return telefono; }
    
    /**
     * Establece el teléfono del cliente.
     * 
     * @param telefono El nuevo número de teléfono del cliente
     */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /**
     * Verifica si el cliente tiene derecho a descuentos.
     * 
     * @return true si el cliente tiene descuento, false en caso contrario
     */
    public boolean isDescuento() { return descuento; }
    
    /**
     * Establece el estado de descuento del cliente.
     * 
     * @param descuento true si el cliente tiene derecho a descuentos, false en caso contrario
     */
    public void setDescuento(boolean descuento) { this.descuento = descuento; }
}