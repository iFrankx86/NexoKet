package utp.edu.pe.nexoket.facade;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import utp.edu.pe.nexoket.Facade.INexoKet.IProductoFacade;
import utp.edu.pe.nexoket.dao.ProductoDAO;
import utp.edu.pe.nexoket.modelo.Producto;

public class ProductoFacade implements IProductoFacade {
    private final ProductoDAO productoDAO;

    public ProductoFacade() {
        this.productoDAO = new ProductoDAO();
    }

    @Override
    public boolean registrarProducto(String codigo, String nombre, String marca, String categoria, 
                                   String subcategoria, String unidadMedida, int cantidadPorUnidad,
                                   String descripcion, double precio, int stock, int stockMinimo, 
                                   String proveedor, Date fechaVencimiento, String ubicacion) {
        try {
            // Verificar si ya existe un producto con ese código
            Producto existente = productoDAO.consultarProducto(codigo);
            if (existente != null) {
                return false; // Ya existe
            }
            
            // Validaciones básicas
            if (codigo == null || codigo.trim().isEmpty() || 
                nombre == null || nombre.trim().isEmpty() || 
                precio < 0 || stock < 0 || stockMinimo < 0 || cantidadPorUnidad < 1) {
                return false;
            }
            
            Producto producto = new Producto(codigo, nombre, descripcion, marca, categoria, 
                                           subcategoria, unidadMedida, cantidadPorUnidad, 
                                           precio, stock, stockMinimo, proveedor, 
                                           fechaVencimiento, ubicacion);
            
            productoDAO.registrarProducto(producto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Producto buscarProducto(String codigo) {
        try {
            return productoDAO.consultarProducto(codigo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Producto buscarProductoPorNombre(String nombre) {
        try {
            return productoDAO.consultarProductoPorNombre(nombre);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean actualizarProducto(String codigo, String nombre, String marca, String categoria, 
                                    String subcategoria, String unidadMedida, int cantidadPorUnidad,
                                    String descripcion, double precio, int stock, int stockMinimo, 
                                    String proveedor, Date fechaVencimiento, String ubicacion) {
        try {
            // Validaciones básicas
            if (codigo == null || codigo.trim().isEmpty() || 
                nombre == null || nombre.trim().isEmpty() || 
                precio < 0 || stock < 0 || stockMinimo < 0 || cantidadPorUnidad < 1) {
                return false;
            }
            
            Producto producto = new Producto(codigo, nombre, descripcion, marca, categoria, 
                                           subcategoria, unidadMedida, cantidadPorUnidad, 
                                           precio, stock, stockMinimo, proveedor, 
                                           fechaVencimiento, ubicacion);
            
            productoDAO.actualizarProducto(codigo, producto);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminarProducto(String codigo) {
        try {
            productoDAO.eliminarProducto(codigo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Producto> obtenerTodosLosProductos() {
        try {
            return productoDAO.obtenerTodosLosProductos();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<Producto> obtenerProductosActivos() {
        try {
            return productoDAO.obtenerProductosActivos();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        try {
            return productoDAO.obtenerProductosPorCategoria(categoria);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<Producto> obtenerProductosPorCategoriaYSubcategoria(String categoria, String subcategoria) {
        try {
            return productoDAO.obtenerProductosPorCategoriaYSubcategoria(categoria, subcategoria);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<Producto> obtenerProductosPorMarca(String marca) {
        try {
            return productoDAO.obtenerProductosPorMarca(marca);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<Producto> buscarProductos(String texto) {
        try {
            if (texto == null || texto.trim().isEmpty()) {
                return obtenerProductosActivos();
            }
            return productoDAO.buscarProductos(texto.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean actualizarStock(String codigo, int nuevoStock) {
        try {
            if (nuevoStock < 0) return false;
            productoDAO.actualizarStock(codigo, nuevoStock);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean reducirStock(String codigo, int cantidad) {
        try {
            return productoDAO.reducirStock(codigo, cantidad);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean aumentarStock(String codigo, int cantidad) {
        try {
            if (cantidad <= 0) return false;
            productoDAO.aumentarStock(codigo, cantidad);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Producto> obtenerProductosStockBajo() {
        try {
            return productoDAO.obtenerProductosStockBajo();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean tieneStockSuficiente(String codigo, int cantidad) {
        try {
            Producto producto = productoDAO.consultarProducto(codigo);
            return producto != null && producto.tieneStockSuficiente(cantidad);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public double calcularValorInventarioTotal() {
        try {
            List<Producto> productos = productoDAO.obtenerProductosActivos();
            return productos.stream()
                    .mapToDouble(Producto::calcularValorInventario)
                    .sum();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public double calcularValorInventarioPorCategoria(String categoria) {
        try {
            List<Producto> productos = productoDAO.obtenerProductosPorCategoria(categoria);
            return productos.stream()
                    .mapToDouble(Producto::calcularValorInventario)
                    .sum();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public List<Producto> obtenerProductosProximosAVencer(int dias) {
        try {
            List<Producto> productos = productoDAO.obtenerProductosActivos();
            return productos.stream()
                    .filter(p -> p.estaProximoAVencer(dias))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Object[][] obtenerDatosTablaInventario() {
        try {
            List<Producto> productos = obtenerProductosActivos();
            Object[][] datos = new Object[productos.size()][9];
            
            for (int i = 0; i < productos.size(); i++) {
                Producto p = productos.get(i);
                datos[i][0] = p.getCodigo();
                datos[i][1] = p.getNombreCompleto();
                datos[i][2] = p.getMarca() != null ? p.getMarca() : "";
                datos[i][3] = p.getCategoriaCompleta();
                datos[i][4] = p.getStock() + " " + (p.getUnidadMedida() != null ? p.getUnidadMedida() : "");
                datos[i][5] = p.getStockRealUnidades() + " unidades";
                datos[i][6] = "S/. " + String.format("%.2f", p.getPrecio());
                datos[i][7] = "S/. " + String.format("%.2f", p.calcularValorInventario());
                datos[i][8] = p.requiereReposicion() ? "⚠️ Reponer" : "✅ OK";
            }
            
            return datos;
        } catch (Exception e) {
            e.printStackTrace();
            return new Object[0][9];
        }
    }

    @Override
    public String[] getColumnasTablaInventario() {
        return new String[]{"Código", "Producto", "Marca", "Categoría", "Stock", "Unidades Totales", "Precio Unit.", "Valor Total", "Estado"};
    }

    @Override
    public List<String> obtenerCategorias() {
        try {
            return productoDAO.obtenerCategorias();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<String> obtenerMarcas() {
        try {
            return productoDAO.obtenerMarcas();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<String> obtenerSubcategoriasPorCategoria(String categoria) {
        try {
            return productoDAO.obtenerSubcategoriasPorCategoria(categoria);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<String> obtenerUnidadesMedida() {
        try {
            return productoDAO.obtenerUnidadesMedida();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // Métodos adicionales útiles
    public String generarCodigoProducto() {
        try {
            List<Producto> productos = productoDAO.obtenerTodosLosProductos();
            int ultimoNumero = productos.size();
            return String.format("PROD%04d", ultimoNumero + 1);
        } catch (Exception e) {
            return "PROD0001";
        }
    }

    /**
     * Genera un código automático basado en la categoría seleccionada
     * @param categoria La categoría del producto
     * @return El código generado (ej: SNK001, BEB001, ABA001)
     */
    public String generarCodigoPorCategoria(String categoria) {
        try {
            // Obtener el prefijo según la categoría
            String prefijo = obtenerPrefijoPorCategoria(categoria);
            
            // Obtener productos de la misma categoría
            List<Producto> productosCategoria = productoDAO.obtenerProductosPorCategoria(categoria);
            
            // Filtrar solo los que tengan el prefijo correcto
            long cantidadConPrefijo = productosCategoria.stream()
                .filter(p -> p.getCodigo() != null && p.getCodigo().startsWith(prefijo))
                .count();
            
            // Generar el siguiente número
            int siguienteNumero = (int) cantidadConPrefijo + 1;
            
            return String.format("%s%03d", prefijo, siguienteNumero);
        } catch (Exception e) {
            e.printStackTrace();
            return "GEN001"; // Código genérico en caso de error
        }
    }
    
    /**
     * Obtiene el prefijo correspondiente a cada categoría
     * @param categoria La categoría del producto
     * @return El prefijo de 3 letras
     */
    private String obtenerPrefijoPorCategoria(String categoria) {
        if (categoria == null) return "GEN";
        
        switch (categoria.toLowerCase()) {
            case "snacks":
                return "SNK";
            case "bebidas":
                return "BEB";
            case "abarrotes":
                return "ABA";
            case "lacteos":
                return "LAC";
            case "limpieza":
                return "LIM";
            case "higiene":
                return "HIG";
            default:
                return "GEN"; // Genérico para categorías no definidas
        }
    }

    public boolean validarProducto(Producto producto) {
        return producto != null 
            && producto.getCodigo() != null && !producto.getCodigo().trim().isEmpty()
            && producto.getNombre() != null && !producto.getNombre().trim().isEmpty()
            && producto.getPrecio() > 0
            && producto.getStock() >= 0
            && producto.getStockMinimo() >= 0
            && producto.getCantidadPorUnidad() > 0;
    }
}