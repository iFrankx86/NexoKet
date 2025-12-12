package utp.edu.pe.nexoket.modelo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests unitarios para la clase Proveedor
 * Valida la gestión de proveedores y sus productos
 * 
 * @author NexoKet Team
 */
@DisplayName("Tests para Proveedor")
class ProveedorTest {
    
    private Proveedor proveedor;
    
    @BeforeEach
    void setUp() {
        proveedor = new Proveedor("20123456789", "Distribuidora ABC S.A.C.");
    }
    
    @Test
    @DisplayName("Debe crear proveedor con RUC y razón social")
    void testCrearProveedor() {
        assertNotNull(proveedor);
        assertEquals("20123456789", proveedor.getRuc());
        assertEquals("Distribuidora ABC S.A.C.", proveedor.getRazonSocial());
        assertTrue(proveedor.isActivo());
        assertNotNull(proveedor.getProductosQueSuply());
    }
    
    @Test
    @DisplayName("Debe validar RUC de 11 dígitos correctamente")
    void testValidarRucCorrecto() {
        assertTrue(proveedor.validarRuc(), "RUC válido debe pasar la validación");
    }
    
    @Test
    @DisplayName("Debe rechazar RUC con menos de 11 dígitos")
    void testValidarRucIncorrecto() {
        proveedor.setRuc("12345");
        assertFalse(proveedor.validarRuc(), "RUC inválido debe fallar la validación");
    }
    
    @Test
    @DisplayName("Debe rechazar RUC con caracteres no numéricos")
    void testValidarRucConLetras() {
        proveedor.setRuc("2012345678A");
        assertFalse(proveedor.validarRuc());
    }
    
    @Test
    @DisplayName("Debe agregar productos al proveedor")
    void testAgregarProducto() {
        proveedor.agregarProducto("PROD001");
        proveedor.agregarProducto("PROD002");
        
        List<String> productos = proveedor.getProductosQueSuply();
        assertEquals(2, productos.size());
        assertTrue(productos.contains("PROD001"));
        assertTrue(productos.contains("PROD002"));
    }
    
    @Test
    @DisplayName("Debe eliminar productos del proveedor")
    void testEliminarProducto() {
        proveedor.agregarProducto("PROD001");
        proveedor.agregarProducto("PROD002");
        proveedor.eliminarProducto("PROD001");
        
        List<String> productos = proveedor.getProductosQueSuply();
        assertEquals(1, productos.size());
        assertFalse(productos.contains("PROD001"));
        assertTrue(productos.contains("PROD002"));
    }
    
    @Test
    @DisplayName("Debe validar email correcto")
    void testValidarEmailCorrecto() {
        proveedor.setEmail("contacto@distribuidora.com");
        assertTrue(proveedor.validarEmail());
    }
    
    @Test
    @DisplayName("Debe rechazar email sin @")
    void testValidarEmailIncorrecto() {
        proveedor.setEmail("contactodistribuidora.com");
        assertFalse(proveedor.validarEmail());
    }
    
    @Test
    @DisplayName("Debe actualizar datos del proveedor")
    void testActualizarDatos() {
        proveedor.setTelefono("987654321");
        proveedor.setEmail("ventas@empresa.com");
        proveedor.setDireccion("Av. Principal 123");
        
        assertEquals("987654321", proveedor.getTelefono());
        assertEquals("ventas@empresa.com", proveedor.getEmail());
        assertEquals("Av. Principal 123", proveedor.getDireccion());
    }
    
    @Test
    @DisplayName("Debe desactivar proveedor")
    void testDesactivarProveedor() {
        proveedor.setActivo(false);
        assertFalse(proveedor.isActivo());
    }
}
