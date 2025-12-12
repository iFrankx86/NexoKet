package utp.edu.pe.nexoket.Facade.INexoKet;
import java.util.Date;
import java.util.List;

import utp.edu.pe.nexoket.modelo.Producto;

public interface IProductoFacade {
    // Operaciones CRUD básicas
    boolean registrarProducto(String codigo, String nombre, String marca, String categoria, 
                             String subcategoria, String unidadMedida, int cantidadPorUnidad,
                             boolean aplicaIGV, String descripcion, double precio, int stock, int stockMinimo, 
                             String proveedor, Date fechaVencimiento, String ubicacion, boolean activo);
    
    Producto buscarProducto(String codigo);
    Producto buscarProductoPorNombre(String nombre);
    
    boolean actualizarProducto(String codigo, String nombre, String marca, String categoria, 
                              String subcategoria, String unidadMedida, int cantidadPorUnidad,
                              boolean aplicaIGV, String descripcion, double precio, int stock, int stockMinimo, 
                              String proveedor, Date fechaVencimiento, String ubicacion, boolean activo);
    
    boolean eliminarProducto(String codigo);
    
    // Operaciones de consulta
    List<Producto> obtenerTodosLosProductos();
    List<Producto> obtenerProductosActivos();
    List<Producto> obtenerProductosPorCategoria(String categoria);
    List<Producto> obtenerProductosPorCategoriaYSubcategoria(String categoria, String subcategoria);
    List<Producto> obtenerProductosPorMarca(String marca);
    List<Producto> buscarProductos(String texto);
    
    // Operaciones de inventario
    boolean actualizarStock(String codigo, int nuevoStock);
    boolean reducirStock(String codigo, int cantidad);
    boolean aumentarStock(String codigo, int cantidad);
    List<Producto> obtenerProductosStockBajo();
    
    // Operaciones de negocio
    boolean tieneStockSuficiente(String codigo, int cantidad);
    double calcularValorInventarioTotal();
    double calcularValorInventarioPorCategoria(String categoria);
    List<Producto> obtenerProductosProximosAVencer(int dias);
    
    // Operaciones para reportes
    Object[][] obtenerDatosTablaInventario();
    String[] getColumnasTablaInventario();
    List<String> obtenerCategorias();
    List<String> obtenerMarcas();
    List<String> obtenerSubcategoriasPorCategoria(String categoria);
    List<String> obtenerUnidadesMedida();
}