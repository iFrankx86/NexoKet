package utp.edu.pe.nexoket.modelo;

/**
 * Modelo para representar el detalle de una venta (cada producto vendido)
 * @author User
 */
public class DetalleVenta {
    private String codigoProducto;
    private String nombreProducto;
    private String categoria;
    private double precioUnitario;
    private int cantidad;
    private double subtotal;
    
    /**
     * Constructor vacío por defecto.
     */
    public DetalleVenta() {
    }
    
    /**
     * Constructor con parámetros específicos del producto.
     * 
     * @param codigoProducto Código del producto vendido
     * @param nombreProducto Nombre del producto vendido
     * @param precioUnitario Precio unitario del producto
     * @param cantidad Cantidad vendida del producto
     */
    public DetalleVenta(String codigoProducto, String nombreProducto, double precioUnitario, int cantidad) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    /**
     * Constructor que crea un detalle de venta a partir de un producto.
     * 
     * @param producto El producto del cual se creará el detalle de venta
     * @param cantidad La cantidad vendida del producto
     */
    public DetalleVenta(Producto producto, int cantidad) {
        this.codigoProducto = producto.getCodigo();
        this.nombreProducto = producto.getNombre();
        this.categoria = producto.getCategoria();
        this.precioUnitario = producto.getPrecio();
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    // Getters y Setters
    
    /**
     * Obtiene el código del producto.
     * 
     * @return El código del producto
     */
    public String getCodigoProducto() {
        return codigoProducto;
    }

    /**
     * Establece el código del producto.
     * 
     * @param codigoProducto El nuevo código del producto
     */
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    /**
     * Obtiene el nombre del producto.
     * 
     * @return El nombre del producto
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * Establece el nombre del producto.
     * 
     * @param nombreProducto El nuevo nombre del producto
     */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /**
     * Obtiene la categoría del producto.
     * 
     * @return La categoría del producto
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría del producto.
     * 
     * @param categoria La nueva categoría del producto
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el precio unitario del producto.
     * 
     * @return El precio unitario del producto
     */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Establece el precio unitario del producto y recalcula el subtotal.
     * 
     * @param precioUnitario El nuevo precio unitario del producto
     */
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    /**
     * Obtiene la cantidad vendida del producto.
     * 
     * @return La cantidad vendida del producto
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad vendida del producto y recalcula el subtotal.
     * 
     * @param cantidad La nueva cantidad vendida del producto
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    /**
     * Obtiene el subtotal de la venta (precio unitario * cantidad).
     * 
     * @return El subtotal de la venta
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Establece el subtotal de la venta.
     * 
     * @param subtotal El nuevo subtotal de la venta
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
    
    /**
     * Calcula el subtotal (precio * cantidad)
     */
    public void calcularSubtotal() {
        this.subtotal = this.precioUnitario * this.cantidad;
    }
    
    @Override
    public String toString() {
        return "DetalleVenta{" +
                "codigo='" + codigoProducto + '\'' +
                ", producto='" + nombreProducto + '\'' +
                ", precio=" + precioUnitario +
                ", cantidad=" + cantidad +
                ", subtotal=" + subtotal +
                '}';
    }
}
