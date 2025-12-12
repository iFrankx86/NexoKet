package utp.edu.pe.nexoket.modelo;

import java.util.Date;

public class Producto {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String marca;
    private String categoria;              // String simple
    private String subcategoria;
    private String unidadMedida;           // String simple
    private int cantidadPorUnidad;
    private double precio;
    private int stock;
    private int stockMinimo;
    private String proveedor;
    private Date fechaVencimiento;
    private String ubicacion;
    private boolean activo;
    private boolean aplicaIGV;
    private Date fechaCreacion;
    private Date fechaActualizacion;

    // Constructor vacío
    public Producto() {
        this.activo = true;
        this.aplicaIGV = true;
        this.fechaCreacion = new Date();
        this.fechaActualizacion = new Date();
        this.cantidadPorUnidad = 1;
    }

    // Constructor con parámetros principales
    public Producto(String codigo, String nombre, double precio, int stock, String categoria) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    // Constructor completo
    public Producto(String codigo, String nombre, String marca, String categoria, 
                   String subcategoria, String unidadMedida, int cantidadPorUnidad,
                   double precio, int stock, int stockMinimo) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.unidadMedida = unidadMedida;
        this.cantidadPorUnidad = cantidadPorUnidad;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    // Constructor completo con todos los campos
    public Producto(String codigo, String nombre, String descripcion, String marca, String categoria, 
                   String subcategoria, String unidadMedida, int cantidadPorUnidad,
                   double precio, int stock, int stockMinimo, String proveedor, 
                   Date fechaVencimiento, String ubicacion) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.unidadMedida = unidadMedida;
        this.cantidadPorUnidad = cantidadPorUnidad;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.proveedor = proveedor;
        this.fechaVencimiento = fechaVencimiento;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
        this.fechaActualizacion = new Date();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.fechaActualizacion = new Date();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
        this.fechaActualizacion = new Date();
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
        this.fechaActualizacion = new Date();
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
        this.fechaActualizacion = new Date();
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
        this.fechaActualizacion = new Date();
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
        this.fechaActualizacion = new Date();
    }

    public int getCantidadPorUnidad() {
        return cantidadPorUnidad;
    }

    public void setCantidadPorUnidad(int cantidadPorUnidad) {
        this.cantidadPorUnidad = cantidadPorUnidad;
        this.fechaActualizacion = new Date();
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
        this.fechaActualizacion = new Date();
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
        this.fechaActualizacion = new Date();
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
        this.fechaActualizacion = new Date();
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
        this.fechaActualizacion = new Date();
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
        this.fechaActualizacion = new Date();
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
        this.fechaActualizacion = new Date();
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
        this.fechaActualizacion = new Date();
    }

    public boolean isAplicaIGV() {
        return aplicaIGV;
    }

    public void setAplicaIGV(boolean aplicaIGV) {
        this.aplicaIGV = aplicaIGV;
        this.fechaActualizacion = new Date();
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    // Métodos de negocio
    public boolean tieneStockSuficiente(int cantidad) {
        return this.stock >= cantidad;
    }

    public boolean requiereReposicion() {
        return this.stock <= this.stockMinimo;
    }

    public void reducirStock(int cantidad) {
        if (tieneStockSuficiente(cantidad)) {
            this.stock -= cantidad;
            this.fechaActualizacion = new Date();
        } else {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + this.stock);
        }
    }

    public void aumentarStock(int cantidad) {
        this.stock += cantidad;
        this.fechaActualizacion = new Date();
    }

    public double calcularValorInventario() {
        return this.stock * this.precio;
    }

    public boolean estaProximoAVencer(int diasAnticipacion) {
        if (fechaVencimiento == null) return false;
        
        Date fechaLimite = new Date();
        fechaLimite.setTime(fechaLimite.getTime() + (diasAnticipacion * 24 * 60 * 60 * 1000));
        
        return fechaVencimiento.before(fechaLimite);
    }

    // Métodos utilitarios
    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombre);
        if (marca != null && !marca.isEmpty()) {
            sb.append(" - ").append(marca);
        }
        if (cantidadPorUnidad > 1) {
            sb.append(" (").append(cantidadPorUnidad);
            if (unidadMedida != null) {
                sb.append(" ").append(unidadMedida);
            }
            sb.append(")");
        }
        return sb.toString();
    }

    public String getCategoriaCompleta() {
        StringBuilder sb = new StringBuilder();
        if (categoria != null) {
            sb.append(categoria);
        }
        if (subcategoria != null && !subcategoria.isEmpty()) {
            sb.append(" > ").append(subcategoria);
        }
        return sb.toString();
    }

    public int getStockRealUnidades() {
        return stock * cantidadPorUnidad;
    }

    // toString, equals, hashCode
    @Override
    public String toString() {
        return "Producto{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", unidadMedida='" + unidadMedida + '\'' +
                ", activo=" + activo +
                ", aplicaIGV=" + aplicaIGV +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Producto producto = (Producto) obj;
        return codigo != null ? codigo.equals(producto.codigo) : producto.codigo == null;
    }

    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }
}