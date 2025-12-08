/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package utp.edu.pe.nexoket.jform;

import utp.edu.pe.nexoket.Facade.VentaFacade;
import utp.edu.pe.nexoket.modelo.Venta;
import utp.edu.pe.nexoket.modelo.DetalleVenta;
import utp.edu.pe.nexoket.util.GeneradorBoletaPDF;
import utp.edu.pe.nexoket.util.MonitorRendimiento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.text.DecimalFormat;

/**
 *
 * @author User
 */
public class ItmHistorialVentas extends javax.swing.JInternalFrame {

    private static final Logger logger = LoggerFactory.getLogger(ItmHistorialVentas.class);
    private MonitorRendimiento monitor;
    
    private VentaFacade ventaFacade;
    private DefaultTableModel modeloTabla;
    private DecimalFormat formatoMoneda;
    private List<Venta> ventasActuales;
    
    // Componentes de monitoreo visual
    private JLabel lblIndicadorSistema;
    private Timer timerActualizacion;
    
    /**
     * Creates new form ItmHistorialVentas
     */
    public ItmHistorialVentas() {
        logger.info("═══════════════════════════════════════════════════");
        logger.info("Iniciando ItmHistorialVentas");
        logger.info("═══════════════════════════════════════════════════");
        long tiempoInicio = System.currentTimeMillis();
        
        try {
            initComponents();
            inicializarComponentes();
            agregarIndicadorSistema();
            cargarVentas();
            
            monitor = MonitorRendimiento.getInstancia();
            monitor.verificarSaludSistema();
            
            long tiempoFin = System.currentTimeMillis();
            logger.info("✓ ItmHistorialVentas cargado exitosamente en {} ms", (tiempoFin - tiempoInicio));
            monitor.logearMetricas();
            logger.info("═══════════════════════════════════════════════════\n");
            
        } catch (Exception e) {
            logger.error("✗ Error crítico al inicializar ItmHistorialVentas", e);
            throw e;
        }
    }
    
    /**
     * Inicializa componentes personalizados
     */
    private void inicializarComponentes() {
        ventaFacade = new VentaFacade();
        formatoMoneda = new DecimalFormat("#,##0.00");
        
        // Configurar tabla
        configurarTabla();
        
        // Configurar ComboBox de estados (Vendedor label pero filtra por estado)
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Todos");
        jComboBox1.addItem("Completada");
        jComboBox1.addItem("Cancelada");
        
        // Configurar ComboBox de clientes
        jComboBox2.removeAllItems();
        jComboBox2.addItem("Todos");
        
        // Configurar DateChooser de fecha (sin fecha por defecto para mostrar todos)
        dtchFechaFiltro.setDate(null);
        dtchFechaFiltro.setDateFormatString("dd/MM/yyyy");
        
        // Agregar listeners para filtros
        jComboBox1.addActionListener(e -> aplicarFiltros());
        jComboBox2.addActionListener(e -> aplicarFiltros());
        
        // Listener para el DateChooser
        dtchFechaFiltro.addPropertyChangeListener("date", evt -> aplicarFiltros());
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
        
        long tiempoInicio = System.currentTimeMillis();
        logger.debug("Aplicando filtros - Estado: {}, Fecha: {}", 
            jComboBox1.getSelectedItem(), dtchFechaFiltro.getDate());
        
        List<Venta> ventasFiltradas = ventasActuales;
        
        // Filtrar por estado
        String estadoSeleccionado = (String) jComboBox1.getSelectedItem();
        if (estadoSeleccionado != null && !"Todos".equals(estadoSeleccionado)) {
            ventasFiltradas = ventasFiltradas.stream()
                .filter(v -> estadoSeleccionado.equals(v.getEstado()))
                .collect(Collectors.toList());
        }
        
        // Filtrar por fecha
        Date fechaSeleccionada = dtchFechaFiltro.getDate();
        if (fechaSeleccionada != null) {
            LocalDate fechaFiltro = fechaSeleccionada.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            
            ventasFiltradas = ventasFiltradas.stream()
                .filter(v -> {
                    LocalDate fechaVenta = v.getFechaEmision().toLocalDate();
                    return fechaVenta.isEqual(fechaFiltro);
                })
                .collect(Collectors.toList());
        }
        
        actualizarTabla(ventasFiltradas);
        
        long duracion = System.currentTimeMillis() - tiempoInicio;
        logger.info("✓ Filtros aplicados: {} resultados en {} ms", ventasFiltradas.size(), duracion);
    }
    
    /**
     * Agrega un indicador discreto del estado del sistema
     */
    private void agregarIndicadorSistema() {
        logger.debug("Inicializando indicador de sistema");
        
        // SOLUCIÓN: Agregar al título existente (jLabel4)
        try {
            // Modificar el texto del título para incluir el indicador
            String tituloOriginal = jLabel4.getText();
            
            // Crear indicador simple en el título
            lblIndicadorSistema = new JLabel(" ● ");
            lblIndicadorSistema.setFont(new Font("Arial", Font.BOLD, 18));
            lblIndicadorSistema.setForeground(new Color(40, 167, 69)); // Verde
            lblIndicadorSistema.setToolTipText("Sistema: Normal - Click para ver detalles");
            lblIndicadorSistema.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Evento click
            lblIndicadorSistema.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    mostrarDetallesMonitoreo();
                }
            });
            
            // Crear panel horizontal para título + indicador
            JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            panelTitulo.setOpaque(false);
            panelTitulo.add(jLabel4);
            panelTitulo.add(lblIndicadorSistema);
            
            // Reemplazar jLabel4 con el panel
            Container parent = jLabel4.getParent();
            if (parent != null) {
                // Obtener constraints del layout
                parent.remove(jLabel4);
                parent.add(panelTitulo);
                parent.revalidate();
                parent.repaint();
                logger.info("✓ Indicador agregado al título");
            }
            
        } catch (Exception e) {
            logger.warn("No se pudo agregar indicador al título, usando alternativa");
            
            // ALTERNATIVA: Agregar como ventana flotante
            lblIndicadorSistema = new JLabel(" ● SISTEMA: Normal ");
            lblIndicadorSistema.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblIndicadorSistema.setForeground(new Color(40, 167, 69));
            lblIndicadorSistema.setOpaque(true);
            lblIndicadorSistema.setBackground(new Color(240, 240, 240));
            lblIndicadorSistema.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
            ));
            lblIndicadorSistema.setToolTipText("Click para ver detalles del sistema");
            lblIndicadorSistema.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            lblIndicadorSistema.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    mostrarDetallesMonitoreo();
                }
            });
            
            // Posicionar en esquina superior derecha
            lblIndicadorSistema.setBounds(
                getWidth() - 180, 
                10, 
                170, 
                25
            );
            
            // Agregar al layered pane (siempre visible)
            getLayeredPane().add(lblIndicadorSistema, JLayeredPane.PALETTE_LAYER);
            logger.info("✓ Indicador agregado como componente flotante");
        }
        
        // Timer para actualizar cada 10 segundos
        final JLabel indicador = lblIndicadorSistema; // Referencia final
        timerActualizacion = new Timer(10000, e -> {
            if (monitor == null) return;
            
            MonitorRendimiento.EstadoSistema estado = monitor.getEstadoActual();
            String textoEstado = "";
            Color color;
            
            switch (estado) {
                case NORMAL:
                    textoEstado = " ● SISTEMA: Normal ";
                    color = new Color(40, 167, 69); // Verde
                    indicador.setToolTipText("Sistema: Normal - Click para ver detalles");
                    break;
                case ADVERTENCIA:
                    textoEstado = " ● SISTEMA: Advertencia ";
                    color = new Color(255, 193, 7); // Amarillo
                    indicador.setToolTipText("Sistema: Advertencia - Click para ver detalles");
                    break;
                case CRITICO:
                    textoEstado = " ● SISTEMA: CRÍTICO ";
                    color = new Color(220, 53, 69); // Rojo
                    indicador.setToolTipText("Sistema: CRÍTICO - Click para ver detalles");
                    // Parpadear en crítico
                    indicador.setVisible(!indicador.isVisible());
                    break;
                default:
                    textoEstado = " ● SISTEMA: Normal ";
                    color = new Color(40, 167, 69);
            }
            
            indicador.setText(textoEstado);
            indicador.setForeground(color);
        });
        timerActualizacion.start();
        
        logger.info("✓ Sistema de monitoreo visual inicializado");
    }
    
    /**
     * Actualiza el indicador visual del sistema
     */
    private void actualizarIndicador() {
        if (monitor == null) return;
        
        MonitorRendimiento.EstadoSistema estado = monitor.getEstadoActual();
        
        switch (estado) {
            case NORMAL:
                lblIndicadorSistema.setForeground(new Color(40, 167, 69)); // Verde
                lblIndicadorSistema.setToolTipText("Sistema: Normal");
                lblIndicadorSistema.setVisible(true);
                break;
            case ADVERTENCIA:
                lblIndicadorSistema.setForeground(new Color(255, 193, 7)); // Amarillo
                lblIndicadorSistema.setToolTipText("Sistema: Advertencia - Click para detalles");
                lblIndicadorSistema.setVisible(true);
                break;
            case CRITICO:
                lblIndicadorSistema.setForeground(new Color(220, 53, 69)); // Rojo
                lblIndicadorSistema.setToolTipText("Sistema: Crítico - Click para detalles");
                // Parpadear si es crítico
                lblIndicadorSistema.setVisible(!lblIndicadorSistema.isVisible());
                break;
        }
    }
    
    /**
     * Muestra detalles de monitoreo cuando el usuario hace click
     */
    private void mostrarDetallesMonitoreo() {
        logger.info("Usuario consultó estado del sistema");
        
        if (monitor == null) {
            JOptionPane.showMessageDialog(this,
                "Monitor no disponible",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Map<String, Object> metricas = monitor.obtenerMetricas();
        
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("═══ ESTADO DEL SISTEMA ═══\n\n");
        mensaje.append(String.format("🖥️  Estado: %s\n\n", monitor.getEstadoActual()));
        mensaje.append(String.format("💾 Memoria: %.1f%% (%d/%d MB)\n",
            metricas.get("memoria_porcentaje"),
            metricas.get("memoria_usada_mb"),
            metricas.get("memoria_maxima_mb")));
        mensaje.append(String.format("🔀 Hilos activos: %d\n", metricas.get("hilos_activos")));
        mensaje.append(String.format("⚙️  Procesadores: %d\n", metricas.get("procesadores")));
        mensaje.append(String.format("⏱️  Tiempo ejecución: %d min\n", 
            (long)metricas.get("tiempo_ejecucion_ms") / 60000));
        mensaje.append("\n📄 Los logs se guardan en: logs/nexoket.log");
        
        JOptionPane.showMessageDialog(this,
            mensaje.toString(),
            "Monitor del Sistema",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public void dispose() {
        logger.info("Cerrando ItmHistorialVentas");
        
        if (timerActualizacion != null) {
            timerActualizacion.stop();
            logger.debug("Timer de monitoreo detenido");
        }
        
        super.dispose();
        logger.info("ItmHistorialVentas cerrado correctamente\n");
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
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        dtchFechaFiltro = new com.toedter.calendar.JDateChooser();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
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
                                .addGap(18, 18, 18)
                                .addComponent(dtchFechaFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnVerDetalle)
                        .addComponent(btnReImprimirBoleta)
                        .addComponent(btnAnularVenta)
                        .addComponent(jLabel1)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3))
                    .addComponent(dtchFechaFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 447, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetalleActionPerformed
        logger.info("❯ Usuario solicitó ver detalle de venta");
        
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            logger.warn("Intento de ver detalle sin selección de fila");
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una venta de la tabla",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String numeroVenta = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        logger.info("Cargando detalle de venta: {}", numeroVenta);
        
        Venta venta = ventaFacade.buscarVenta(numeroVenta);
        
        if (venta == null) {
            logger.error("✗ Venta no encontrada: {}", numeroVenta);
            JOptionPane.showMessageDialog(this,
                "No se encontró la venta seleccionada",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        logger.info("✓ Mostrando detalle de venta: {}", numeroVenta);
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
        logger.info("❯ Usuario solicitó re-imprimir boleta");
        
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            logger.warn("Intento de re-imprimir boleta sin selección");
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una venta de la tabla",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String numeroVenta = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        logger.info("Buscando venta para re-imprimir: {}", numeroVenta);
        
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
            
            logger.info("Generando PDF en: {}", rutaArchivo);
            boolean exito = GeneradorBoletaPDF.generarBoletaPDF(venta, rutaArchivo);
            
            if (exito) {
                logger.info("✓ Boleta re-impresa exitosamente: {}", rutaArchivo);
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
                logger.error("✗ Error al generar PDF para venta: {}", numeroVenta);
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
        logger.warn("❯ Usuario solicitó anular venta");
        
        int filaSeleccionada = jTable1.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            logger.warn("Intento de anular venta sin selección");
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una venta de la tabla",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String numeroVenta = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modeloTabla.getValueAt(filaSeleccionada, 5);
        
        logger.info("Intentando anular venta: {} (Estado: {})", numeroVenta, estadoActual);
        
        // Validar que la venta no esté ya cancelada
        if ("Cancelada".equals(estadoActual)) {
            logger.warn("Venta ya está cancelada: {}", numeroVenta);
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
        logger.info("Ejecutando anulación de venta: {}", numeroVenta);
        boolean exito = ventaFacade.cancelarVenta(numeroVenta);
        
        if (exito) {
            logger.info("✓ Venta anulada exitosamente: {}", numeroVenta);
            JOptionPane.showMessageDialog(this,
                "Venta anulada exitosamente.\\nEl stock ha sido devuelto.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Recargar ventas
            cargarVentas();
            
            // Log de métricas después de operación importante
            if (monitor != null) {
                monitor.logearMetricas();
            }
        } else {
            logger.error("✗ Error al anular venta: {}", numeroVenta);
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
    private com.toedter.calendar.JDateChooser dtchFechaFiltro;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
