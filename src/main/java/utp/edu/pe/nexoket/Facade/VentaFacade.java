package utp.edu.pe.nexoket.Facade;

import java.time.LocalDateTime;
import java.util.List;

import utp.edu.pe.nexoket.dao.ProductoDAO;
import utp.edu.pe.nexoket.dao.VentaDAO;
import utp.edu.pe.nexoket.modelo.DetalleVenta;
import utp.edu.pe.nexoket.modelo.Producto;
import utp.edu.pe.nexoket.modelo.Venta;

/**
 * Facade para gestionar la lógica de negocio de Ventas
 * @author User
 */
public class VentaFacade {
    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;
    
    public VentaFacade() {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
    }
    
    /**
     * Procesa una venta completa: valida stock, reduce inventario, guarda venta
     */
    public boolean procesarVenta(Venta venta) {
        try {
            // 1. Validar que la venta tenga detalles
            if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
                System.err.println("❌ Error: La venta no tiene productos");
                return false;
            }
            
            // 2. Validar stock de todos los productos
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = productoDAO.buscarPorCodigo(detalle.getCodigoProducto());
                if (producto == null) {
                    System.err.println("❌ Error: Producto no encontrado - " + detalle.getCodigoProducto());
                    return false;
                }
                
                if (!producto.tieneStockSuficiente(detalle.getCantidad())) {
                    System.err.println("❌ Error: Stock insuficiente para " + producto.getNombre() + 
                                     ". Stock actual: " + producto.getStock() + 
                                     ", Solicitado: " + detalle.getCantidad());
                    return false;
                }
            }
            
            // 3. Reducir stock de todos los productos
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = productoDAO.buscarPorCodigo(detalle.getCodigoProducto());
                producto.reducirStock(detalle.getCantidad());
                productoDAO.actualizarProducto(producto.getCodigo(), producto);
                System.out.println("✅ Stock reducido: " + producto.getNombre() + 
                                 " (-" + detalle.getCantidad() + ") = " + producto.getStock());
            }
            
            // 4. Calcular totales
            venta.calcularTotales();
            venta.calcularVuelto();
            
            // 5. Guardar la venta
            boolean resultado = ventaDAO.insertarVenta(venta);
            
            if (resultado) {
                System.out.println("✅ Venta procesada exitosamente: " + venta.getNumeroVenta());
                System.out.println("   Total: S/ " + venta.getTotal());
            }
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("❌ Error al procesar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cancela una venta y devuelve el stock
     */
    public boolean cancelarVenta(String numeroVenta) {
        try {
            Venta venta = ventaDAO.buscarPorNumero(numeroVenta);
            if (venta == null) {
                System.err.println("❌ Error: Venta no encontrada - " + numeroVenta);
                return false;
            }
            
            // Devolver stock
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = productoDAO.buscarPorCodigo(detalle.getCodigoProducto());
                if (producto != null) {
                    producto.aumentarStock(detalle.getCantidad());
                    productoDAO.actualizarProducto(producto.getCodigo(), producto);
                    System.out.println("✅ Stock devuelto: " + producto.getNombre() + 
                                     " (+" + detalle.getCantidad() + ") = " + producto.getStock());
                }
            }
            
            // Marcar venta como cancelada
            venta.setEstado("Cancelada");
            ventaDAO.insertarVenta(venta); // Actualiza el estado
            
            System.out.println("✅ Venta cancelada: " + numeroVenta);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al cancelar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Busca una venta por número
     */
    public Venta buscarVenta(String numeroVenta) {
        return ventaDAO.buscarPorNumero(numeroVenta);
    }
    
    /**
     * Obtiene todas las ventas
     */
    public List<Venta> obtenerTodasLasVentas() {
        return ventaDAO.obtenerTodas();
    }
    
    /**
     * Obtiene ventas por rango de fechas
     */
    public List<Venta> obtenerVentasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaDAO.obtenerPorFecha(fechaInicio, fechaFin);
    }
    
    /**
     * Obtiene ventas de un cliente
     */
    public List<Venta> obtenerVentasDeCliente(String dniCliente) {
        return ventaDAO.obtenerPorCliente(dniCliente);
    }
    
    /**
     * Genera un nuevo número de venta
     */
    public String generarNumeroVenta() {
        return ventaDAO.generarNumeroVenta();
    }
    
    /**
     * Agrega un producto a la venta
     */
    public boolean agregarProductoAVenta(Venta venta, String codigoProducto, int cantidad) {
        try {
            Producto producto = productoDAO.buscarPorCodigo(codigoProducto);
            
            if (producto == null) {
                System.err.println("❌ Error: Producto no encontrado - " + codigoProducto);
                return false;
            }
            
            if (!producto.tieneStockSuficiente(cantidad)) {
                System.err.println("❌ Error: Stock insuficiente para " + producto.getNombre());
                return false;
            }
            
            // Buscar si el producto ya está en el carrito
            DetalleVenta detalleExistente = null;
            for (DetalleVenta d : venta.getDetalles()) {
                if (d.getCodigoProducto().equals(codigoProducto)) {
                    detalleExistente = d;
                    break;
                }
            }
            
            if (detalleExistente != null) {
                // Actualizar cantidad del detalle existente
                int nuevaCantidad = detalleExistente.getCantidad() + cantidad;
                if (!producto.tieneStockSuficiente(nuevaCantidad)) {
                    System.err.println("❌ Error: Stock insuficiente para cantidad total");
                    return false;
                }
                detalleExistente.setCantidad(nuevaCantidad);
                detalleExistente.calcularSubtotal();
            } else {
                // Crear nuevo detalle
                DetalleVenta detalle = new DetalleVenta(producto, cantidad);
                venta.agregarDetalle(detalle);
            }
            
            System.out.println("✅ Producto agregado: " + producto.getNombre() + " x" + cantidad);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error al agregar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Busca un producto por código (para usar en ventas)
     */
    public Producto buscarProducto(String codigo) {
        return productoDAO.buscarPorCodigo(codigo);
    }
    
    /**
     * Calcula el total de ventas en un período
     */
    public double calcularTotalVentasPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Venta> ventas = ventaDAO.obtenerPorFecha(fechaInicio, fechaFin);
        double total = 0;
        for (Venta venta : ventas) {
            if ("Completada".equals(venta.getEstado())) {
                total += venta.getTotal();
            }
        }
        return total;
    }
}
