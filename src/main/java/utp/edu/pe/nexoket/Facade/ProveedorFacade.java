package utp.edu.pe.nexoket.Facade;

import java.util.List;

import utp.edu.pe.nexoket.dao.ProveedorDAO;
import utp.edu.pe.nexoket.modelo.Proveedor;

/**
 * Facade para gestionar la lógica de negocio de Proveedores
 * @author User
 */
public class ProveedorFacade {
    private final ProveedorDAO proveedorDAO;
    
    public ProveedorFacade() {
        this.proveedorDAO = new ProveedorDAO();
    }
    
    /**
     * Registra un nuevo proveedor con validaciones
     */
    public boolean registrarProveedor(Proveedor proveedor) {
        try {
            // Validar RUC
            if (!proveedor.validarRuc()) {
                System.err.println("❌ Error: RUC inválido (debe tener 11 dígitos)");
                return false;
            }
            
            // Validar razón social
            if (proveedor.getRazonSocial() == null || proveedor.getRazonSocial().trim().isEmpty()) {
                System.err.println("❌ Error: Razón social es obligatoria");
                return false;
            }
            
            // Validar email si existe
            if (!proveedor.validarEmail()) {
                System.err.println("❌ Error: Email inválido");
                return false;
            }
            
            return proveedorDAO.insertarProveedor(proveedor);
            
        } catch (Exception e) {
            System.err.println("❌ Error al registrar proveedor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza un proveedor existente
     */
    public boolean actualizarProveedor(String ruc, Proveedor proveedor) {
        try {
            // Validar que existe
            Proveedor existente = proveedorDAO.buscarPorRuc(ruc);
            if (existente == null) {
                System.err.println("❌ Error: Proveedor no encontrado");
                return false;
            }
            
            // Validar email si existe
            if (!proveedor.validarEmail()) {
                System.err.println("❌ Error: Email inválido");
                return false;
            }
            
            return proveedorDAO.actualizarProveedor(ruc, proveedor);
            
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un proveedor (lógicamente)
     */
    public boolean eliminarProveedor(String ruc) {
        return proveedorDAO.eliminarProveedor(ruc);
    }
    
    /**
     * Busca un proveedor por RUC
     */
    public Proveedor buscarPorRuc(String ruc) {
        return proveedorDAO.buscarPorRuc(ruc);
    }
    
    /**
     * Obtiene todos los proveedores
     */
    public List<Proveedor> obtenerTodos() {
        return proveedorDAO.obtenerTodos();
    }
    
    /**
     * Busca proveedores por RUC o nombre
     */
    public List<Proveedor> buscarPorRucONombre(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return obtenerTodos();
        }
        return proveedorDAO.buscarPorRucONombre(filtro.trim());
    }
}
