package utp.edu.pe.nexoket.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para representar un Proveedor
 * @author User
 */
public class Proveedor {
    private String ruc; // 11 dígitos
    private String razonSocial;
    private String telefono;
    private String email;
    private String direccion;
    private List<String> productosQueSuply; // Array de códigos de productos
    private boolean activo;
    
    public Proveedor() {
        this.productosQueSuply = new ArrayList<>();
        this.activo = true;
    }
    
    public Proveedor(String ruc, String razonSocial) {
        this();
        this.ruc = ruc;
        this.razonSocial = razonSocial;
    }

    // Getters y Setters
    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<String> getProductosQueSuply() {
        return productosQueSuply;
    }

    public void setProductosQueSuply(List<String> productosQueSuply) {
        this.productosQueSuply = productosQueSuply;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    /**
     * Valida que el RUC tenga 11 dígitos
     */
    public boolean validarRuc() {
        return ruc != null && ruc.matches("\\d{11}");
    }
    
    /**
     * Valida que el email tenga formato correcto
     */
    public boolean validarEmail() {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email es opcional
        }
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Agrega un producto al array de productos que suministra
     */
    public void agregarProducto(String codigoProducto) {
        if (!productosQueSuply.contains(codigoProducto)) {
            productosQueSuply.add(codigoProducto);
        }
    }
    
    /**
     * Elimina un producto del array
     */
    public void eliminarProducto(String codigoProducto) {
        productosQueSuply.remove(codigoProducto);
    }
    
    @Override
    public String toString() {
        return "Proveedor{" +
                "ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", telefono='" + telefono + '\'' +
                ", activo=" + activo +
                '}';
    }
}
