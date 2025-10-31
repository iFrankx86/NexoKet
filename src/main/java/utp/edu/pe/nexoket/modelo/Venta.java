package utp.edu.pe.nexoket.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para representar una venta
 * @author User
 */
public class Venta {
    private String numeroVenta;
    private LocalDateTime fechaEmision;
    private String dniCliente;
    private String nombreCliente;
    private String telefonoCliente;
    private String emisorBoleta;
    private String tipoPago; // Efectivo, Tarjeta, Yape
    private List<DetalleVenta> detalles;
    private double subtotal;
    private double igv;
    private double total;
    private double efectivoRecibido;
    private double vuelto;
    private String estado; // "Completada", "Cancelada"
    
    public Venta() {
        this.detalles = new ArrayList<>();
        this.fechaEmision = LocalDateTime.now();
        this.estado = "Completada";
    }
    
    public Venta(String numeroVenta, String emisorBoleta) {
        this();
        this.numeroVenta = numeroVenta;
        this.emisorBoleta = emisorBoleta;
    }

    // Getters y Setters
    public String getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getEmisorBoleta() {
        return emisorBoleta;
    }

    public void setEmisorBoleta(String emisorBoleta) {
        this.emisorBoleta = emisorBoleta;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIgv() {
        return igv;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getEfectivoRecibido() {
        return efectivoRecibido;
    }

    public void setEfectivoRecibido(double efectivoRecibido) {
        this.efectivoRecibido = efectivoRecibido;
    }

    public double getVuelto() {
        return vuelto;
    }

    public void setVuelto(double vuelto) {
        this.vuelto = vuelto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    /**
     * Agrega un detalle a la venta
     */
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        calcularTotales();
    }
    
    /**
     * Elimina un detalle de la venta
     */
    public void eliminarDetalle(int index) {
        if (index >= 0 && index < detalles.size()) {
            this.detalles.remove(index);
            calcularTotales();
        }
    }
    
    /**
     * Calcula subtotal, IGV y total
     */
    public void calcularTotales() {
        this.subtotal = 0;
        for (DetalleVenta detalle : detalles) {
            this.subtotal += detalle.getSubtotal();
        }
        this.igv = this.subtotal * 0.18; // 18% IGV
        this.total = this.subtotal + this.igv;
    }
    
    /**
     * Calcula el vuelto
     */
    public void calcularVuelto() {
        this.vuelto = this.efectivoRecibido - this.total;
    }
    
    @Override
    public String toString() {
        return "Venta{" +
                "numeroVenta='" + numeroVenta + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", cliente='" + nombreCliente + '\'' +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                '}';
    }
}
