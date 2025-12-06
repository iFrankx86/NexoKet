/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package utp.edu.pe.nexoket.jform;

import utp.edu.pe.nexoket.Facade.VentaFacade;
import utp.edu.pe.nexoket.modelo.Venta;
import utp.edu.pe.nexoket.modelo.DetalleVenta;
import utp.edu.pe.nexoket.util.GeneradorBoletaPDF;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.text.DecimalFormat;

/**
 *
 * @author User
 */
public class ItmHistorialVentas extends javax.swing.JInternalFrame {

    private VentaFacade ventaFacade;
    private DefaultTableModel modeloTabla;
    private DecimalFormat formatoMoneda;
    private List<Venta> ventasActuales;
    
    /**
     * Creates new form ItmHistorialVentas
     */
    public ItmHistorialVentas() {
        initComponents();
        inicializarComponentes();
        cargarVentas();
    }
    
    /**
     * Inicializa componentes personalizados
     */
    private void inicializarComponentes() {
        ventaFacade = new VentaFacade();
        formatoMoneda = new DecimalFormat("#,##0.00");
        
        // Configurar tabla
        configurarTabla();
        
        // Configurar ComboBox de estados
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Todos");
        jComboBox1.addItem("Completada");
        jComboBox1.addItem("Cancelada");
        
        // Configurar ComboBox de vendedores (por ahora solo "Todos")
        jComboBox2.removeAllItems();
        jComboBox2.addItem("Todos");
        
        // Configurar campo de fecha
        txtFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        // Agregar listeners para filtros
        jComboBox1.addActionListener(e -> aplicarFiltros());
        jComboBox2.addActionListener(e -> aplicarFiltros());
        txtFecha.addActionListener(e -> aplicarFiltros());
    }
    
    /**
     * Configura la tabla de ventas
     */
    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Nro Venta", "Fecha", "Cliente", "Vendedor", "Total", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };
        
        jTable1.setModel(modeloTabla);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        
        // Configurar anchos de columnas
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(100); // Nro Venta
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(150); // Fecha
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(200); // Cliente
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(150); // Vendedor
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(100); // Total
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado
    }
    
    /**
     * Carga todas las ventas
     */
    private void cargarVentas() {
        try {
            ventasActuales = ventaFacade.obtenerTodasLasVentas();
            actualizarTabla(ventasActuales);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar ventas: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza la tabla con la lista de ventas
     */
    private void actualizarTabla(List<Venta> ventas) {
        modeloTabla.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Venta venta : ventas) {
            String cliente = venta.getNombreCliente() != null ? venta.getNombreCliente() : "Cliente General";
            
            modeloTabla.addRow(new Object[]{
                venta.getNumeroVenta(),
                venta.getFechaEmision().format(formatter),
                cliente,
                venta.getEmisorBoleta(),
                "S/ " + formatoMoneda.format(venta.getTotal()),
                venta.getEstado()
            });
        }
    }
    
    /**
     * Aplica los filtros seleccionados
     */
    private void aplicarFiltros() {
        if (ventasActuales == null) return;
        
        List<Venta> ventasFiltradas = ventasActuales;
        
        // Filtrar por estado
        String estadoSeleccionado = (String) jComboBox1.getSelectedItem();
        if (!"Todos".equals(estadoSeleccionado)) {
            ventasFiltradas = ventasFiltradas.stream()
                .filter(v -> estadoSeleccionado.equals(v.getEstado()))
                .collect(Collectors.toList());
        }
        
        actualizarTabla(ventasFiltradas);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnVerDetalle = new javax.swing.JButton();
        btnReImprimirBoleta = new javax.swing.JButton();
        btnAnularVenta = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtFecha = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setClosable(true);
        setTitle("Historial de Ventas");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Nro Venta", "Fecha", "Cliente", "Vendedor", "Total", "Estado"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        btnVerDetalle.setText("Ver Detalle");
        btnVerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetalleActionPerformed(evt);
            }
        });

        btnReImprimirBoleta.setText("Re-Imprimir Boleta");
        btnReImprimirBoleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReImprimirBoletaActionPerformed(evt);
            }
        });

        btnAnularVenta.setText("Anular Venta");
        btnAnularVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnularVentaActionPerformed(evt);
            }
        });

        jLabel1.setText("Filtro");

        jLabel2.setText("Vendedor");

        jLabel3.setText("Cliente");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel4.setText("FORMULARIO DE HISTORIAL");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 899, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnVerDetalle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReImprimirBoleta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnAnularVenta)))))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVerDetalle)
                    .addComponent(btnReImprimirBoleta)
                    .addComponent(btnAnularVenta)
                    .addComponent(jLabel1)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetalleActionPerformed
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una venta de la tabla",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String numeroVenta = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Venta venta = ventaFacade.buscarVenta(numeroVenta);
        
        if (venta == null) {
            JOptionPane.showMessageDialog(this,
                "No se encontró la venta seleccionada",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        mostrarDetalleVenta(venta);
    }//GEN-LAST:event_btnVerDetalleActionPerformed
    
    /**
     * Muestra un diálogo con el detalle de la venta
     */
    private void mostrarDetalleVenta(Venta venta) {
        JDialog dialogo = new JDialog();
        dialogo.setTitle("Detalle de Venta - " + venta.getNumeroVenta());
        dialogo.setModal(true);
        dialogo.setSize(700, 500);
        dialogo.setLocationRelativeTo(this);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Información general
        JPanel panelInfo = new JPanel(new GridLayout(6, 2, 10, 5));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información General"));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        panelInfo.add(new JLabel("Número de Venta:"));
        panelInfo.add(new JLabel(venta.getNumeroVenta()));
        
        panelInfo.add(new JLabel("Fecha:"));
        panelInfo.add(new JLabel(venta.getFechaEmision().format(formatter)));
        
        panelInfo.add(new JLabel("Cliente:"));
        panelInfo.add(new JLabel(venta.getNombreCliente() != null ? venta.getNombreCliente() : "Cliente General"));
        
        panelInfo.add(new JLabel("Vendedor:"));
        panelInfo.add(new JLabel(venta.getEmisorBoleta()));
        
        panelInfo.add(new JLabel("Tipo de Pago:"));
        panelInfo.add(new JLabel(venta.getTipoPago()));
        
        panelInfo.add(new JLabel("Estado:"));
        JLabel lblEstado = new JLabel(venta.getEstado());
        lblEstado.setForeground("Completada".equals(venta.getEstado()) ? Color.GREEN.darker() : Color.RED.darker());
        lblEstado.setFont(lblEstado.getFont().deriveFont(Font.BOLD));
        panelInfo.add(lblEstado);
        
        // Tabla de productos
        DefaultTableModel modeloProductos = new DefaultTableModel(
            new String[]{"Código", "Producto", "Cantidad", "P. Unitario", "Subtotal"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (DetalleVenta detalle : venta.getDetalles()) {
            modeloProductos.addRow(new Object[]{
                detalle.getCodigoProducto(),
                detalle.getNombreProducto(),
                detalle.getCantidad(),
                "S/ " + formatoMoneda.format(detalle.getPrecioUnitario()),
                "S/ " + formatoMoneda.format(detalle.getSubtotal())
            });
        }
        
        JTable tablaProductos = new JTable(modeloProductos);
        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        scrollProductos.setBorder(BorderFactory.createTitledBorder("Productos"));
        
        // Panel de totales
        JPanel panelTotales = new JPanel(new GridLayout(5, 2, 10, 5));
        panelTotales.setBorder(BorderFactory.createTitledBorder("Totales"));
        
        panelTotales.add(new JLabel("Subtotal:"));
        panelTotales.add(new JLabel("S/ " + formatoMoneda.format(venta.getSubtotal())));
        
        panelTotales.add(new JLabel("IGV (18%):"));
        panelTotales.add(new JLabel("S/ " + formatoMoneda.format(venta.getIgv())));
        
        JLabel lblTotal = new JLabel("TOTAL:");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        panelTotales.add(lblTotal);
        
        JLabel lblMontoTotal = new JLabel("S/ " + formatoMoneda.format(venta.getTotal()));
        lblMontoTotal.setFont(lblMontoTotal.getFont().deriveFont(Font.BOLD, 14f));
        lblMontoTotal.setForeground(new Color(46, 125, 50));
        panelTotales.add(lblMontoTotal);
        
        panelTotales.add(new JLabel("Efectivo Recibido:"));
        panelTotales.add(new JLabel("S/ " + formatoMoneda.format(venta.getEfectivoRecibido())));
        
        panelTotales.add(new JLabel("Vuelto:"));
        panelTotales.add(new JLabel("S/ " + formatoMoneda.format(venta.getVuelto())));
        
        // Agregar componentes al panel principal
        panelPrincipal.add(panelInfo, BorderLayout.NORTH);
        panelPrincipal.add(scrollProductos, BorderLayout.CENTER);
        panelPrincipal.add(panelTotales, BorderLayout.SOUTH);
        
        dialogo.add(panelPrincipal);
        dialogo.setVisible(true);
    }
    
    /**
     * Re-imprime la boleta de una venta seleccionada
     */
    private void btnReImprimirBoletaActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una venta de la tabla",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String numeroVenta = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Venta venta = ventaFacade.buscarVenta(numeroVenta);
        
        if (venta == null) {
            JOptionPane.showMessageDialog(this,
                "No se encontró la venta seleccionada",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Seleccionar ubicación para guardar el PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Boleta PDF");
        fileChooser.setSelectedFile(new java.io.File("Boleta_" + venta.getNumeroVenta() + "_REIMPRESION.pdf"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF", "pdf"));
        
        int resultado = fileChooser.showSaveDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
            
            if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }
            
            boolean exito = GeneradorBoletaPDF.generarBoletaPDF(venta, rutaArchivo);
            
            if (exito) {
                int opcion = JOptionPane.showOptionDialog(this,
                    "Boleta re-impresa exitosamente en:\\n" + rutaArchivo + "\\n\\n¿Desea abrir el PDF?",
                    "PDF Generado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"Abrir PDF", "Cerrar"},
                    "Abrir PDF");
                
                if (opcion == 0) {
                    try {
                        if (java.awt.Desktop.isDesktopSupported()) {
                            java.awt.Desktop.getDesktop().open(new java.io.File(rutaArchivo));
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this,
                            "Error al abrir el PDF: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al generar el PDF.\\nVerifique la librería iText.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Anula una venta seleccionada
     */
    private void btnAnularVentaActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una venta de la tabla",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String numeroVenta = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 5);
        
        // Validar que la venta no esté ya cancelada
        if ("Cancelada".equals(estadoActual)) {
            JOptionPane.showMessageDialog(this,
                "Esta venta ya está cancelada",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirmar anulación
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea anular la venta " + numeroVenta + "?\\n" +
            "Esta acción devolverá el stock de los productos.\\n\\n" +
            "⚠️ Esta acción no se puede deshacer.",
            "Confirmar Anulación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Anular venta
        boolean exito = ventaFacade.cancelarVenta(numeroVenta);
        
        if (exito) {
            JOptionPane.showMessageDialog(this,
                "Venta anulada exitosamente.\\nEl stock ha sido devuelto.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Recargar ventas
            cargarVentas();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al anular la venta.\\nIntente nuevamente.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAnularVenta;
    private javax.swing.JButton btnReImprimirBoleta;
    private javax.swing.JButton btnVerDetalle;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtFecha;
    // End of variables declaration//GEN-END:variables
}
