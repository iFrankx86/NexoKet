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
    
    public DetalleVenta() {
    }
    
    public DetalleVenta(String codigoProducto, String nombreProducto, double precioUnitario, int cantidad) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public DetalleVenta(Producto producto, int cantidad) {
        this.codigoProducto = producto.getCodigo();
        this.nombreProducto = producto.getNombre();
        this.categoria = producto.getCategoria();
        this.precioUnitario = producto.getPrecio();
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    // Getters y Setters
    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

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
