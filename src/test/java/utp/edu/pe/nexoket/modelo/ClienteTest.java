package utp.edu.pe.nexoket.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests unitarios para la clase Cliente
 * Valida la creación y gestión de clientes
 * 
 * @author NexoKet Team
 */
@DisplayName("Tests para Cliente")
class ClienteTest {
    
    private Cliente cliente;
    
    @BeforeEach
    void setUp() {
        cliente = new Cliente("Juan", "Pérez", "12345678", "987654321", false);
    }
    
    @Test
    @DisplayName("Debe crear cliente con todos los datos")
    void testCrearClienteCompleto() {
        assertNotNull(cliente);
        assertEquals("Juan", cliente.getNombre());
        assertEquals("Pérez", cliente.getApellido());
        assertEquals("12345678", cliente.getDni());
        assertEquals("987654321", cliente.getTelefono());
        assertFalse(cliente.isDescuento());
    }
    
    @Test
    @DisplayName("Debe crear cliente sin teléfono")
    void testCrearClienteSinTelefono() {
        Cliente clienteSinTel = new Cliente("María", "García", "87654321", true);
        
        assertNotNull(clienteSinTel);
        assertEquals("María", clienteSinTel.getNombre());
        assertEquals("García", clienteSinTel.getApellido());
        assertEquals("87654321", clienteSinTel.getDni());
        assertEquals("", clienteSinTel.getTelefono());
        assertTrue(clienteSinTel.isDescuento());
    }
    
    @Test
    @DisplayName("Debe crear cliente vacío con constructor por defecto")
    void testCrearClienteVacio() {
        Cliente clienteVacio = new Cliente();
        
        assertNotNull(clienteVacio);
        assertEquals("", clienteVacio.getTelefono());
    }
    
    @Test
    @DisplayName("Debe actualizar nombre del cliente")
    void testActualizarNombre() {
        cliente.setNombre("Pedro");
        assertEquals("Pedro", cliente.getNombre());
    }
    
    @Test
    @DisplayName("Debe actualizar apellido del cliente")
    void testActualizarApellido() {
        cliente.setApellido("López");
        assertEquals("López", cliente.getApellido());
    }
    
    @Test
    @DisplayName("Debe actualizar DNI del cliente")
    void testActualizarDni() {
        cliente.setDni("87654321");
        assertEquals("87654321", cliente.getDni());
    }
    
    @Test
    @DisplayName("Debe actualizar teléfono del cliente")
    void testActualizarTelefono() {
        cliente.setTelefono("999888777");
        assertEquals("999888777", cliente.getTelefono());
    }
    
    @Test
    @DisplayName("Debe activar/desactivar descuento")
    void testActivarDesactivarDescuento() {
        cliente.setDescuento(true);
        assertTrue(cliente.isDescuento());
        
        cliente.setDescuento(false);
        assertFalse(cliente.isDescuento());
    }
}
