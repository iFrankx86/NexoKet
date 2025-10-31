package utp.edu.pe.nexoket.modelo;

import java.util.Date;

public abstract class ProductoBase {
    // === INFORMACIÓN BÁSICA ===
    protected String codigo;
    protected String nombre;
    protected String marca;
    protected String descripcion;
    protected String unidadMedida;
    protected int stock;
    protected int stockMinimo;
    protected boolean activo;
    protected Date fechaCreacion;
    protected Date fechaActualizacion;
    
    // === PRECIOS SIMPLES ===
    protected double precioCompra;    // Lo que me cuesta
    protected double precioVenta;     // Lo que cobro al cliente
    
    // Constructor básico
    public ProductoBase(String codigo, String nombre, String marca) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.activo = true;
        this.fechaCreacion = new Date();
        this.fechaActualizacion = new Date();
    }
    
    // === MÉTODOS ABSTRACTOS (cada categoría los implementa) ===
    public abstract String getCategoria();               // "Lácteos", "Bebidas", etc.
    public abstract double getMargenGananciaSugerido(); // Cuánto % quiero ganar
    public abstract boolean necesitaIGV();              // Si pago impuestos
    
    // === MÉTODOS DE PRECIOS (lo más importante) ===
    
    // Calcular precio de venta automáticamente
    public void calcularPrecioVentaAutomatico() {
        double margen = getMargenGananciaSugerido(); // Ej: 0.25 = 25%
        this.precioVenta = this.precioCompra * (1 + margen);
        
        // Redondear a precios bonitos
        this.precioVenta = redondearPrecio(this.precioVenta);
        this.fechaActualizacion = new Date();
    }
    
    // Precio final que paga el cliente (con impuestos si aplica)
    public double getPrecioFinalCliente() {
        if (necesitaIGV()) {
            return precioVenta * 1.18; // +18% IGV en Perú
        }
        return precioVenta;
    }
    
    // Cuánto gano por producto
    public double getGananciaPorUnidad() {
        return precioVenta - precioCompra;
    }
    
    // Porcentaje de ganancia real
    public double getPorcentajeGanancia() {
        if (precioCompra == 0) return 0;
        return ((precioVenta - precioCompra) / precioCompra) * 100;
    }
    
    // === MÉTODOS AUXILIARES ===
    
    // Redondear a precios psicológicos (4.20, 4.50, 4.90)
    private double redondearPrecio(double precio) {
        double parteEntera = Math.floor(precio);
        double parteDecimal = precio - parteEntera;
        
        if (parteDecimal <= 0.20) {
            return parteEntera + 0.20; // Ej: 4.20
        } else if (parteDecimal <= 0.50) {
            return parteEntera + 0.50; // Ej: 4.50
        } else if (parteDecimal <= 0.90) {
            return parteEntera + 0.90; // Ej: 4.90
        } else {
            return parteEntera + 1.00; // Ej: 5.00
        }
    }
    
    // === GETTERS Y SETTERS BÁSICOS ===
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { 
        this.codigo = codigo; 
        this.fechaActualizacion = new Date();
    }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
        this.fechaActualizacion = new Date();
    }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { 
        this.marca = marca; 
        this.fechaActualizacion = new Date();
    }
    
    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { 
        this.precioCompra = precioCompra;
        // Cuando cambio el precio de compra, recalculo el de venta
        calcularPrecioVentaAutomatico();
    }
    
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { 
        this.precioVenta = precioVenta; 
        this.fechaActualizacion = new Date();
    }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { 
        this.stock = stock; 
        this.fechaActualizacion = new Date();
    }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { 
        this.activo = activo; 
        this.fechaActualizacion = new Date();
    }
    
    // Resto de getters y setters...
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public Date getFechaActualizacion() { return fechaActualizacion; }
    
    @Override
    public String toString() {
        return String.format("%s - %s (S/. %.2f)", codigo, nombre, getPrecioFinalCliente());
    }
}