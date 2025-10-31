package utp.edu.pe.nexoket.test;

import utp.edu.pe.nexoket.modelo.ProductoAbarrote;
import utp.edu.pe.nexoket.modelo.ProductoBase;
import utp.edu.pe.nexoket.modelo.ProductoBebida;
import utp.edu.pe.nexoket.modelo.ProductoLacteo;
import utp.edu.pe.nexoket.modelo.ProductoSnack;

public class EjemploProductosReales {
    
    public void crearProductosRealesPeruanos() {
        
        // === CREAR LÁCTEO ===
        ProductoLacteo leche = new ProductoLacteo("LAC001", "Leche UHT Entera", "Gloria");
        leche.setPrecioCompra(3.50);  // Automáticamente calcula precio de venta
        leche.setUnidadMedida("Litro");
        leche.setStock(50);
        leche.setStockMinimo(10);
        
        // === CREAR BEBIDA ===
        ProductoBebida cocaCola = new ProductoBebida("BEB001", "Coca Cola Personal", "Coca Cola");
        cocaCola.setPrecioCompra(1.80);  // Automáticamente calcula precio de venta
        cocaCola.setUnidadMedida("Botella 500ml");
        cocaCola.setStock(48);
        cocaCola.setStockMinimo(12);
        
        // === CREAR SNACK ===
        ProductoSnack papitas = new ProductoSnack("SNK001", "Papitas Clásicas", "Lay's");
        papitas.setPrecioCompra(1.10);  // Automáticamente calcula precio de venta
        papitas.setUnidadMedida("Bolsa 45g");
        papitas.setStock(60);
        papitas.setStockMinimo(15);
        
        // === CREAR ABARROTE ===
        ProductoAbarrote arroz = new ProductoAbarrote("ABA001", "Arroz Superior", "Costeño");
        arroz.setPrecioCompra(3.80);  // Automáticamente calcula precio de venta
        arroz.setUnidadMedida("Kilogramo");
        arroz.setStock(25);
        arroz.setStockMinimo(5);
        
        // === MOSTRAR RESULTADOS ===
        System.out.println("=== PRODUCTOS CREADOS CON PRECIOS AUTOMÁTICOS ===");
        mostrarResumenProducto(leche);
        mostrarResumenProducto(cocaCola);
        mostrarResumenProducto(papitas);
        mostrarResumenProducto(arroz);
    }
    
    private void mostrarResumenProducto(ProductoBase producto) {
        System.out.println("\n" + producto.getCategoria().toUpperCase() + ": " + producto.getNombre());
        System.out.println("  💰 Compré a: S/. " + String.format("%.2f", producto.getPrecioCompra()));
        System.out.println("  🏷️  Vendo a: S/. " + String.format("%.2f", producto.getPrecioVenta()));
        System.out.println("  💵 Cliente paga: S/. " + String.format("%.2f", producto.getPrecioFinalCliente()));
        System.out.println("  📈 Ganancia: " + String.format("%.1f", producto.getPorcentajeGanancia()) + "% (S/. " + 
                          String.format("%.2f", producto.getGananciaPorUnidad()) + " por unidad)");
        System.out.println("  🧾 IGV: " + (producto.necesitaIGV() ? "Sí paga" : "No paga"));
    }
}