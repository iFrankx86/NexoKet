/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package utp.edu.pe.nexoket.jform;

import utp.edu.pe.nexoket.dao.VentaDAO;
import utp.edu.pe.nexoket.dao.ProductoDAO;
import utp.edu.pe.nexoket.dao.ClienteDAO;
import utp.edu.pe.nexoket.modelo.Venta;
import utp.edu.pe.nexoket.modelo.Producto;
import utp.edu.pe.nexoket.modelo.DetalleVenta;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileWriter;
import java.io.BufferedWriter;

/**
 *
 * @author User
 */
public class ItmReportes extends javax.swing.JInternalFrame {

    // Variables de instancia
    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;
    private ClienteDAO clienteDAO;
    private DefaultTableModel modeloTabla;
    private DecimalFormat formatoMoneda;
    private SimpleDateFormat formatoFecha;
    
    /**
     * Creates new form ItmReportes
     */
    public ItmReportes() {
        initComponents();
        inicializarComponentes();
    }
    
    /**
     * Inicializa los componentes personalizados
     */
    private void inicializarComponentes() {
        ventaDAO = new VentaDAO();
        productoDAO = new ProductoDAO();
        clienteDAO = new ClienteDAO();
        
        formatoMoneda = new DecimalFormat("#,##0.00");
        formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        
        configurarTabla();
        configurarSpinners();
        agregarListenerComboBox();
        configurarFiltrosIniciales();
    }
    
    /**
     * Configura el modelo inicial de la tabla
     */
    private void configurarTabla() {
        String[] columnas = {"Col 1", "Col 2", "Col 3", "Col 4", "Col 5"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblResultados.setModel(modeloTabla);
    }
    
    /**
     * Configura los valores de los spinners
     */
    private void configurarSpinners() {
        // Spinner de Top Productos (5-50)
        spnTopProductos.setModel(new javax.swing.SpinnerNumberModel(10, 5, 50, 1));
        
        // Spinner de Stock Mínimo (1-100)
        spnStockMinimo.setModel(new javax.swing.SpinnerNumberModel(20, 1, 100, 1));
    }
    
    /**
     * Agrega listener al ComboBox para cambiar filtros dinámicamente
     */
    private void agregarListenerComboBox() {
        cmbTipoReporte.addActionListener(e -> configurarFiltrosSegunTipo());
    }
    
    /**
     * Configura la visibilidad inicial de los filtros
     */
    private void configurarFiltrosIniciales() {
        configurarFiltrosSegunTipo();
    }
    
    /**
     * Configura la visibilidad de los filtros según el tipo de reporte
     */
    private void configurarFiltrosSegunTipo() {
        String tipoReporte = (String) cmbTipoReporte.getSelectedItem();
        
        // Por defecto, ocultar todos
        dtchFechaInicio.setVisible(false);
        dtchFechaFin.setVisible(false);
        spnTopProductos.setVisible(false);
        spnStockMinimo.setVisible(false);
        jLabel3.setVisible(false);
        jLabel4.setVisible(false);
        jLabel8.setVisible(false);
        jLabel9.setVisible(false);
        
        switch (tipoReporte) {
            case "Ventas por Fecha ":
                dtchFechaInicio.setVisible(true);
                dtchFechaFin.setVisible(true);
                jLabel3.setVisible(true);
                jLabel4.setVisible(true);
                break;
                
            case "Productos Más Vendidos":
                dtchFechaInicio.setVisible(true);
                dtchFechaFin.setVisible(true);
                spnTopProductos.setVisible(true);
                jLabel3.setVisible(true);
                jLabel4.setVisible(true);
                jLabel8.setVisible(true);
                break;
                
            case "Stock Bajo":
                spnStockMinimo.setVisible(true);
                jLabel9.setVisible(true);
                break;
                
            case "Ventas por Cliente":
                dtchFechaInicio.setVisible(true);
                dtchFechaFin.setVisible(true);
                jLabel3.setVisible(true);
                jLabel4.setVisible(true);
                break;
        }
        
        limpiarResultados();
    }
    
    /**
     * Limpia los resultados de la tabla y el resumen
     */
    private void limpiarResultados() {
        modeloTabla.setRowCount(0);
        lblTotalVentas.setText("Total de Ventas: ");
        lblTicketPromedio.setText("Ticket Promedio:");
        lblCantidadVentas.setText("Cantidad:");
    }
    
    /**
     * Cambia las columnas de la tabla según el tipo de reporte
     */
    private void cambiarColumnasTabla(String tipoReporte) {
        switch (tipoReporte) {
            case "Ventas por Fecha ":
                modeloTabla.setColumnIdentifiers(new String[]{
                    "Fecha", "Cantidad de Ventas", "Total Vendido", "Ticket Promedio"
                });
                break;
                
            case "Productos Más Vendidos":
                modeloTabla.setColumnIdentifiers(new String[]{
                    "Top", "Nombre Producto", "Cantidad Vendida", "Ingresos", "% del Total"
                });
                break;
                
            case "Stock Bajo":
                modeloTabla.setColumnIdentifiers(new String[]{
                    "Código", "Nombre Producto", "Stock Actual", "Stock Mínimo", "Estado"
                });
                break;
                
            case "Ventas por Cliente":
                modeloTabla.setColumnIdentifiers(new String[]{
                    "DNI/RUC", "Nombre Cliente", "Nº Compras", "Total Comprado", "Ticket Promedio"
                });
                break;
        }
        modeloTabla.setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cmbTipoReporte = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnGenerarReporte = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblResultados = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblTotalVentas = new javax.swing.JLabel();
        lblTicketPromedio = new javax.swing.JLabel();
        lblCantidadVentas = new javax.swing.JLabel();
        btnExportarPDF = new javax.swing.JButton();
        btnExportarExcel = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        dtchFechaInicio = new com.toedter.calendar.JDateChooser();
        dtchFechaFin = new com.toedter.calendar.JDateChooser();
        spnTopProductos = new javax.swing.JSpinner();
        spnStockMinimo = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Agente de Consulta");

        jLabel1.setText("Tipo de Reporte:");

        cmbTipoReporte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ventas por Fecha ", "Productos Más Vendidos", "Stock Bajo", "Ventas por Cliente" }));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel2.setText("Filtros");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Fecha de Inicio:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Fecha Fin:");

        btnGenerarReporte.setText("Generar Reporte");
        btnGenerarReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarReporteActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel5.setText("Resultados");

        tblResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblResultados);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel6.setText("REPORTE DE VENTAS");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel7.setText("Resumen");

        lblTotalVentas.setText("Total de Ventas: ");

        lblTicketPromedio.setText("Ticket Promedio:");

        lblCantidadVentas.setText("Cantidad:");

        btnExportarPDF.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnExportarPDF.setText("Exportar PDF");

        btnExportarExcel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnExportarExcel.setText("Exportar EXCEL");
        btnExportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarExcelActionPerformed(evt);
            }
        });

        btnLimpiar.setText("Limpiar");

        jLabel8.setText("TOP");

        jLabel9.setText("Stock Minimo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(cmbTipoReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(254, 254, 254))
            .addGroup(layout.createSequentialGroup()
                .addGap(118, 118, 118)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(spnTopProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dtchFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dtchFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(spnStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGenerarReporte))
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(28, 28, 28)
                                            .addComponent(jLabel7))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(56, 56, 56)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lblTicketPromedio)
                                                .addComponent(lblTotalVentas))))
                                    .addGap(162, 162, 162)
                                    .addComponent(lblCantidadVentas)
                                    .addGap(220, 220, 220))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(136, 136, 136)
                                    .addComponent(btnExportarPDF)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnExportarExcel)
                                    .addGap(77, 77, 77)))
                            .addComponent(btnLimpiar))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 654, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(128, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel6)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbTipoReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3))
                    .addComponent(dtchFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dtchFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerarReporte))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnTopProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spnStockMinimo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(32, 32, 32)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalVentas)
                    .addComponent(lblCantidadVentas))
                .addGap(28, 28, 28)
                .addComponent(lblTicketPromedio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExportarPDF)
                    .addComponent(btnExportarExcel)
                    .addComponent(btnLimpiar))
                .addGap(35, 35, 35))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarReporteActionPerformed
        String tipoReporte = (String) cmbTipoReporte.getSelectedItem();
        
        try {
            switch (tipoReporte) {
                case "Ventas por Fecha ":
                    generarReporteVentasPorFecha();
                    break;
                    
                case "Productos Más Vendidos":
                    generarReporteProductosMasVendidos();
                    break;
                    
                case "Stock Bajo":
                    generarReporteStockBajo();
                    break;
                    
                case "Ventas por Cliente":
                    generarReporteVentasPorCliente();
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al generar el reporte: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnGenerarReporteActionPerformed

    private void btnExportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarExcelActionPerformed
        exportarAExcel();
    }//GEN-LAST:event_btnExportarExcelActionPerformed
    
    private void btnExportarPDFActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this,
            "Funcionalidad de exportar a PDF próximamente",
            "Información",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {
        // Limpiar tabla y resumen
        limpiarResultados();
        
        // Limpiar fechas
        dtchFechaInicio.setDate(null);
        dtchFechaFin.setDate(null);
        
        // Resetear spinners
        spnTopProductos.setValue(10);
        spnStockMinimo.setValue(20);
        
        JOptionPane.showMessageDialog(this,
            "Resultados limpiados",
            "Información",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Genera el reporte de ventas por fecha
     */
    private void generarReporteVentasPorFecha() {
        Date fechaInicio = dtchFechaInicio.getDate();
        Date fechaFin = dtchFechaFin.getDate();
        
        if (fechaInicio == null || fechaFin == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar ambas fechas",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (fechaInicio.after(fechaFin)) {
            JOptionPane.showMessageDialog(this,
                "La fecha de inicio debe ser menor o igual a la fecha fin",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        cambiarColumnasTabla("Ventas por Fecha ");
        
        // Obtener ventas del rango de fechas
        List<Venta> todasLasVentas = ventaDAO.obtenerTodas();
        
        // Filtrar por rango de fechas y estado completada
        LocalDate ldInicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ldFin = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        Map<String, List<Venta>> ventasPorFecha = new TreeMap<>();
        
        for (Venta venta : todasLasVentas) {
            if (!"Completada".equals(venta.getEstado())) continue;
            
            LocalDate fechaVenta = venta.getFechaEmision().toLocalDate();
            
            if ((fechaVenta.isEqual(ldInicio) || fechaVenta.isAfter(ldInicio)) &&
                (fechaVenta.isEqual(ldFin) || fechaVenta.isBefore(ldFin))) {
                
                String fechaKey = formatoFecha.format(Date.from(fechaVenta.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                ventasPorFecha.computeIfAbsent(fechaKey, k -> new ArrayList<>()).add(venta);
            }
        }
        
        // Llenar tabla
        double totalGeneral = 0;
        int cantidadTotal = 0;
        
        for (Map.Entry<String, List<Venta>> entry : ventasPorFecha.entrySet()) {
            String fecha = entry.getKey();
            List<Venta> ventas = entry.getValue();
            
            int cantidad = ventas.size();
            double total = ventas.stream().mapToDouble(Venta::getTotal).sum();
            double promedio = total / cantidad;
            
            modeloTabla.addRow(new Object[]{
                fecha,
                cantidad,
                "S/ " + formatoMoneda.format(total),
                "S/ " + formatoMoneda.format(promedio)
            });
            
            totalGeneral += total;
            cantidadTotal += cantidad;
        }
        
        // Actualizar resumen
        lblTotalVentas.setText("Total de Ventas: S/ " + formatoMoneda.format(totalGeneral));
        lblCantidadVentas.setText("Cantidad: " + cantidadTotal + " ventas");
        lblTicketPromedio.setText("Ticket Promedio: S/ " + 
            formatoMoneda.format(cantidadTotal > 0 ? totalGeneral / cantidadTotal : 0));
        
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron ventas en el rango de fechas seleccionado",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Genera el reporte de productos más vendidos
     */
    private void generarReporteProductosMasVendidos() {
        Date fechaInicio = dtchFechaInicio.getDate();
        Date fechaFin = dtchFechaFin.getDate();
        
        if (fechaInicio == null || fechaFin == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar ambas fechas",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int topN = (Integer) spnTopProductos.getValue();
        
        cambiarColumnasTabla("Productos Más Vendidos");
        
        // Obtener ventas del rango
        List<Venta> todasLasVentas = ventaDAO.obtenerTodas();
        
        LocalDate ldInicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ldFin = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Agrupar productos
        Map<String, ProductoVendido> productosMap = new HashMap<>();
        
        for (Venta venta : todasLasVentas) {
            if (!"Completada".equals(venta.getEstado())) continue;
            
            LocalDate fechaVenta = venta.getFechaEmision().toLocalDate();
            
            if ((fechaVenta.isEqual(ldInicio) || fechaVenta.isAfter(ldInicio)) &&
                (fechaVenta.isEqual(ldFin) || fechaVenta.isBefore(ldFin))) {
                
                for (DetalleVenta detalle : venta.getDetalles()) {
                    String codigo = detalle.getCodigoProducto();
                    ProductoVendido pv = productosMap.getOrDefault(codigo, 
                        new ProductoVendido(detalle.getNombreProducto()));
                    pv.cantidad += detalle.getCantidad();
                    pv.ingresos += detalle.getSubtotal();
                    productosMap.put(codigo, pv);
                }
            }
        }
        
        // Ordenar y obtener top N
        List<Map.Entry<String, ProductoVendido>> topProductos = productosMap.entrySet().stream()
            .sorted((e1, e2) -> Integer.compare(e2.getValue().cantidad, e1.getValue().cantidad))
            .limit(topN)
            .collect(Collectors.toList());
        
        double totalIngresos = productosMap.values().stream()
            .mapToDouble(pv -> pv.ingresos).sum();
        
        // Llenar tabla
        int posicion = 1;
        for (Map.Entry<String, ProductoVendido> entry : topProductos) {
            ProductoVendido pv = entry.getValue();
            double porcentaje = (pv.ingresos / totalIngresos) * 100;
            
            modeloTabla.addRow(new Object[]{
                posicion++,
                pv.nombre,
                pv.cantidad,
                "S/ " + formatoMoneda.format(pv.ingresos),
                formatoMoneda.format(porcentaje) + "%"
            });
        }
        
        // Actualizar resumen
        int cantidadTotal = productosMap.values().stream().mapToInt(pv -> pv.cantidad).sum();
        lblTotalVentas.setText("Total Ingresos: S/ " + formatoMoneda.format(totalIngresos));
        lblCantidadVentas.setText("Cantidad: " + cantidadTotal + " unidades");
        lblTicketPromedio.setText("Productos Diferentes: " + productosMap.size());
        
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron productos vendidos en el rango seleccionado",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Genera el reporte de stock bajo
     */
    private void generarReporteStockBajo() {
        int stockMinimo = (Integer) spnStockMinimo.getValue();
        
        cambiarColumnasTabla("Stock Bajo");
        
        List<Producto> todosLosProductos = productoDAO.obtenerTodosLosProductos();
        
        int criticos = 0;
        int bajos = 0;
        
        for (Producto producto : todosLosProductos) {
            if (!producto.isActivo()) continue;
            
            if (producto.getStock() < stockMinimo) {
                String estado;
                if (producto.getStock() <= 5) {
                    estado = "🔴 URGENTE";
                    criticos++;
                } else if (producto.getStock() <= 10) {
                    estado = "⚠️ CRÍTICO";
                    criticos++;
                } else {
                    estado = "⚠️ BAJO";
                    bajos++;
                }
                
                modeloTabla.addRow(new Object[]{
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getStock(),
                    stockMinimo,
                    estado
                });
            }
        }
        
        // Actualizar resumen
        lblTotalVentas.setText("Productos con Stock Bajo: " + modeloTabla.getRowCount());
        lblCantidadVentas.setText("Críticos/Urgentes: " + criticos);
        lblTicketPromedio.setText("Nivel Bajo: " + bajos);
        
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "¡Excelente! No hay productos con stock bajo",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Genera el reporte de ventas por cliente
     */
    private void generarReporteVentasPorCliente() {
        Date fechaInicio = dtchFechaInicio.getDate();
        Date fechaFin = dtchFechaFin.getDate();
        
        if (fechaInicio == null || fechaFin == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar ambas fechas",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        cambiarColumnasTabla("Ventas por Cliente");
        
        List<Venta> todasLasVentas = ventaDAO.obtenerTodas();
        
        LocalDate ldInicio = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ldFin = fechaFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        // Agrupar por cliente
        Map<String, ClienteCompras> clientesMap = new HashMap<>();
        
        for (Venta venta : todasLasVentas) {
            if (!"Completada".equals(venta.getEstado())) continue;
            
            LocalDate fechaVenta = venta.getFechaEmision().toLocalDate();
            
            if ((fechaVenta.isEqual(ldInicio) || fechaVenta.isAfter(ldInicio)) &&
                (fechaVenta.isEqual(ldFin) || fechaVenta.isBefore(ldFin))) {
                
                String dni = venta.getDniCliente() != null ? venta.getDniCliente() : "00000000";
                String nombre = venta.getNombreCliente() != null ? venta.getNombreCliente() : "Cliente General";
                
                ClienteCompras cc = clientesMap.getOrDefault(dni, new ClienteCompras(nombre));
                cc.numeroCompras++;
                cc.totalComprado += venta.getTotal();
                clientesMap.put(dni, cc);
            }
        }
        
        // Ordenar por total comprado
        List<Map.Entry<String, ClienteCompras>> clientesOrdenados = clientesMap.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue().totalComprado, e1.getValue().totalComprado))
            .collect(Collectors.toList());
        
        double totalGeneral = 0;
        int comprasTotal = 0;
        
        // Llenar tabla
        for (Map.Entry<String, ClienteCompras> entry : clientesOrdenados) {
            String dni = entry.getKey();
            ClienteCompras cc = entry.getValue();
            double ticketPromedio = cc.totalComprado / cc.numeroCompras;
            
            modeloTabla.addRow(new Object[]{
                dni,
                cc.nombre,
                cc.numeroCompras,
                "S/ " + formatoMoneda.format(cc.totalComprado),
                "S/ " + formatoMoneda.format(ticketPromedio)
            });
            
            totalGeneral += cc.totalComprado;
            comprasTotal += cc.numeroCompras;
        }
        
        // Actualizar resumen
        lblTotalVentas.setText("Total Vendido: S/ " + formatoMoneda.format(totalGeneral));
        lblCantidadVentas.setText("Cantidad: " + comprasTotal + " compras");
        lblTicketPromedio.setText("Clientes: " + clientesMap.size());
        
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron ventas de clientes en el rango seleccionado",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Exporta los resultados a CSV (compatible con Excel)
     */
    private void exportarAExcel() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay datos para exportar",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte en CSV (Excel)");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivo CSV (*.csv)", "csv"));
        
        String tipoReporte = (String) cmbTipoReporte.getSelectedItem();
        String nombreArchivo = "Reporte_" + tipoReporte.replace(" ", "_") + "_" + 
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv";
        fileChooser.setSelectedFile(new java.io.File(nombreArchivo));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(fileChooser.getSelectedFile()))) {
                
                // Escribir encabezados
                for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
                    writer.write(modeloTabla.getColumnName(i));
                    if (i < modeloTabla.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
                
                // Escribir datos
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    for (int j = 0; j < modeloTabla.getColumnCount(); j++) {
                        Object value = modeloTabla.getValueAt(i, j);
                        if (value != null) {
                            String valueStr = value.toString().replace(",", ";");
                            writer.write(valueStr);
                        }
                        if (j < modeloTabla.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();
                }
                
                JOptionPane.showMessageDialog(this,
                    "Reporte exportado exitosamente a CSV\nPuede abrirlo con Excel",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    // Clases auxiliares
    private static class ProductoVendido {
        String nombre;
        int cantidad = 0;
        double ingresos = 0;
        
        ProductoVendido(String nombre) {
            this.nombre = nombre;
        }
    }
    
    private static class ClienteCompras {
        String nombre;
        int numeroCompras = 0;
        double totalComprado = 0;
        
        ClienteCompras(String nombre) {
            this.nombre = nombre;
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportarExcel;
    private javax.swing.JButton btnExportarPDF;
    private javax.swing.JButton btnGenerarReporte;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbTipoReporte;
    private com.toedter.calendar.JDateChooser dtchFechaFin;
    private com.toedter.calendar.JDateChooser dtchFechaInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCantidadVentas;
    private javax.swing.JLabel lblTicketPromedio;
    private javax.swing.JLabel lblTotalVentas;
    private javax.swing.JSpinner spnStockMinimo;
    private javax.swing.JSpinner spnTopProductos;
    private javax.swing.JTable tblResultados;
    // End of variables declaration//GEN-END:variables
}
