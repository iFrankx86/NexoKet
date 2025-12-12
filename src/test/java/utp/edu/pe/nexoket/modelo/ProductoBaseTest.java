package utp.edu.pe.nexoket.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests unitarios para ProductoBase y sus clases derivadas
 * Valida cálculos de precios, márgenes e IGV
 *
 * @author NexoKet Team
 */
@DisplayName("Tests para ProductoBase y sus derivados")
class ProductoBaseTest {

    private ProductoLacteo productoLacteo;
    private ProductoBebida productoBebida;
    private ProductoSnack productoSnack;
    private ProductoAbarrote productoAbarrote;

    @BeforeEach
    void setUp() {
        productoLacteo = new ProductoLacteo("LAC001", "Leche Gloria 1L", "Gloria");
        productoBebida = new ProductoBebida("BEB001", "Coca Cola 1.5L", "Coca Cola");        
        productoSnack = new ProductoSnack("SNK001", "Papas Lays 45g", "Lays");
        productoAbarrote = new ProductoAbarrote("ABA001", "Arroz Superior 1kg", "Costeño"); 
    }

    @Test
    @DisplayName("Debe calcular precio de venta automático correctamente para lácteos")    
    void testCalcularPrecioVentaLacteos() {
        productoLacteo.setPrecioCompra(3.50);
        productoLacteo.calcularPrecioVentaAutomatico();

        // Lácteos tienen 22% de margen, sin IGV
        // 3.50 * 1.22 = 4.27 -> redondea a 4.50
        assertEquals(4.50, productoLacteo.getPrecioVenta(), 0.01);
    }

    @Test
    @DisplayName("Debe calcular precio de venta automático para bebidas")
    void testCalcularPrecioVentaBebidas() {
        productoBebida.setPrecioCompra(2.00);
        productoBebida.calcularPrecioVentaAutomatico();

        // Bebidas tienen 33% de margen
        // 2.00 * 1.33 = 2.66 -> redondea a 2.90
        assertEquals(2.90, productoBebida.getPrecioVenta(), 0.01);
    }

    @Test
    @DisplayName("Debe calcular precio final con IGV para bebidas")
    void testPrecioFinalConIGV() {
        productoBebida.setPrecioCompra(2.00);
        productoBebida.calcularPrecioVentaAutomatico();

        double precioFinal = productoBebida.getPrecioFinalCliente();

        // Bebidas tienen 33% margen + 18% IGV
        // 2.00 * 1.33 = 2.66 -> redondea a 2.90
        // 2.90 * 1.18 = 3.422
        assertTrue(precioFinal > 3.40 && precioFinal < 3.50,
                  "Precio final debe estar entre 3.40 y 3.50");
    }

    @Test
    @DisplayName("Lácteos no deben tener IGV en el precio final")
    void testLacteoSinIGV() {
        productoLacteo.setPrecioCompra(3.50);
        productoLacteo.calcularPrecioVentaAutomatico();

        double precioVenta = productoLacteo.getPrecioVenta();
        double precioFinal = productoLacteo.getPrecioFinalCliente();

        // Para lácteos, precio final = precio venta (sin IGV)
        assertEquals(precioVenta, precioFinal, 0.01);
    }

    @Test
    @DisplayName("Debe calcular correctamente el margen de ganancia")
    void testMargenGanancia() {
        productoLacteo.setPrecioCompra(3.50);
        productoLacteo.setPrecioVenta(4.50);

        double ganancia = productoLacteo.getGananciaPorUnidad();
        
        assertEquals(1.00, ganancia, 0.01);
    }

    @Test
    @DisplayName("Porcentaje de ganancia debe calcularse correctamente")
    void testPorcentajeGanancia() {
        productoLacteo.setPrecioCompra(3.50);
        productoLacteo.setPrecioVenta(4.50);

        double porcentaje = productoLacteo.getPorcentajeGanancia();
        
        // (4.50 - 3.50) / 3.50 * 100 = 28.57%
        assertTrue(porcentaje >= 28.0 && porcentaje <= 29.0);
    }

    @Test
    @DisplayName("Categoría de producto debe corresponder a su tipo")
    void testCategoriaProducto() {
        assertEquals("Lácteos", productoLacteo.getCategoria());
        assertEquals("Bebidas", productoBebida.getCategoria());
        assertEquals("Snacks", productoSnack.getCategoria());
        assertEquals("Abarrotes", productoAbarrote.getCategoria());
    }

    @Test
    @DisplayName("Margen de ganancia sugerido debe ser correcto por categoría")
    void testMargenGananciaSugerido() {
        assertEquals(0.22, productoLacteo.getMargenGananciaSugerido(), 0.01);
        assertEquals(0.33, productoBebida.getMargenGananciaSugerido(), 0.01);
        assertEquals(0.40, productoSnack.getMargenGananciaSugerido(), 0.01);
        assertEquals(0.18, productoAbarrote.getMargenGananciaSugerido(), 0.01);
    }

    @Test
    @DisplayName("Aplicación de IGV debe ser correcta por categoría")
    void testNecesitaIGV() {
        assertFalse(productoLacteo.necesitaIGV(), "Lácteos no deben tener IGV");
        assertTrue(productoBebida.necesitaIGV(), "Bebidas deben tener IGV");
        assertTrue(productoSnack.necesitaIGV(), "Snacks deben tener IGV");
        assertFalse(productoAbarrote.necesitaIGV(), "Abarrotes básicos no deben tener IGV");
    }

    @Test
    @DisplayName("Producto debe tener fecha de creación")
    void testFechaCreacion() {
        assertNotNull(productoLacteo.getFechaCreacion());
    }

    @Test
    @DisplayName("Producto debe estar activo por defecto")
    void testProductoActivoPorDefecto() {
        assertTrue(productoLacteo.isActivo());
    }

    @Test
    @DisplayName("Stock no debe ser negativo")
    void testStockNoNegativo() {
        productoLacteo.setStock(10);
        assertEquals(10, productoLacteo.getStock());

        // Intentar establecer stock negativo debería mantener el valor anterior
        productoLacteo.setStock(-5);
        assertEquals(10, productoLacteo.getStock(), "Stock debe mantenerse en 10 despues de intentar valor negativo");
    }

    @Test
    @DisplayName("Precio de compra debe ser mayor a cero")
    void testPrecioCompraPositivo() {
        productoLacteo.setPrecioCompra(5.0);
        assertEquals(5.0, productoLacteo.getPrecioCompra(), 0.01);

        // Intentar establecer precio negativo debería mantener el valor anterior
        productoLacteo.setPrecioCompra(-1.0);
        assertEquals(5.0, productoLacteo.getPrecioCompra(), 0.01, "Precio debe mantenerse en 5.0 despues de intentar valor negativo");
    }

    @Test
    @DisplayName("Código de producto debe ser único y no nulo")
    void testCodigoProducto() {
        assertNotNull(productoLacteo.getCodigo());
        assertEquals("LAC001", productoLacteo.getCodigo());

        assertNotNull(productoBebida.getCodigo());
        assertEquals("BEB001", productoBebida.getCodigo());
    }

    @Test
    @DisplayName("Nombre de producto no debe estar vacío")
    void testNombreProducto() {
        assertNotNull(productoLacteo.getNombre());
        assertFalse(productoLacteo.getNombre().trim().isEmpty());
    }
}
