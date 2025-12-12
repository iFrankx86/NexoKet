/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package utp.edu.pe.nexoket.jform;

import java.util.List;
import java.util.ArrayList;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import utp.edu.pe.nexoket.facade.ProductoFacade;
import utp.edu.pe.nexoket.Facade.ProveedorFacade;
import utp.edu.pe.nexoket.modelo.Producto;
import utp.edu.pe.nexoket.modelo.Proveedor;
import java.io.IOException;

/**
 *
 * @author User
 */
public class ItmProductos extends javax.swing.JInternalFrame {

    private final ProductoFacade productoFacade;
    private final ProveedorFacade proveedorFacade;
    private DefaultTableModel modeloTabla;
    private boolean modoEdicion = false; // Indica si estamos en modo edición
    private String codigoProductoEnEdicion = null; // Código del producto que se está editando
    
    // Variables para actualización automática
    private Timer autoRefreshTimer;
    private boolean autoRefreshEnabled = false;
    private static final int REFRESH_INTERVAL = 30000; // 30 segundos
    private TableRowSorter<DefaultTableModel> sorter;
    private String ultimaActualizacion = "";
    
    // Para almacenar precio de compra temporalmente
    private double precioCompraEnEdicion = 0.0;

    /**
     * Creates new form ItmProductos
     */
    public ItmProductos() {
        initComponents();
        this.productoFacade = new ProductoFacade();
        this.proveedorFacade = new ProveedorFacade();
        configurarTabla();
        cargarDatosTabla();
        cargarProveedores();
        configurarFiltros();
        configurarListeners();
        configurarAutoRefresh();
        configurarBusqueda();
        configurarDobleClick();
        // Activar auto-refresh por defecto sin invocar métodos sobreescribibles
        this.autoRefreshEnabled = true;
        if (autoRefreshTimer != null) {
            autoRefreshTimer.start();
        }
        // Generar código inicial basado en la categoría seleccionada por defecto
        try {
            generarCodigoAutomatico();
        } catch (Exception e) {
            System.out.println("Error al generar código inicial: " + e.getMessage());
            // Código por defecto si hay error
            txtCodigo.setText("ABA001");
        }
    }
    
    /**
     * Configura los listeners necesarios
     */
    private void configurarListeners() {
        // Listener para selección de filas en la tabla
        tblProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProductos.getSelectedRow() >= 0) {
                // La fila está seleccionada, el usuario puede hacer clic en Actualizar
                // No cargamos automáticamente, esperamos el clic en el botón
            }
        });
        
        // Listener para el botón de búsqueda con filtros
        btnBuscarFiltro.addActionListener(e -> aplicarFiltros());
        
        // Listener para el botón de exportar Excel
        btnEXCEL.addActionListener(e -> exportarExcel());
        
        // Listener para el botón de ver detalle
        btnVerDetalle.addActionListener(e -> verDetalleProducto());
        
        // Listener para el botón de refrescar
        btnRefrescar.addActionListener(e -> refrescarTablaManual());
        
        // Listener para el botón de agregar
        btnAgregar.addActionListener(e -> agregarProducto());
        
        // Listener para el botón de escanear
        btnEscanear.addActionListener(e -> btnEscanearActionPerformed(null));
        
        // Listener para cambio de categoría
        cmbCategoria.addActionListener(e -> generarCodigoAutomatico());
    }
    
    /**
     * Configura el doble click en la tabla para editar rápidamente
     */
    private void configurarDobleClick() {
        tblProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int filaVista = tblProductos.getSelectedRow();
                    if (filaVista < 0) {
                        return;
                    }

                    // Convertir índice de vista a modelo para evitar desfasajes con filtros/sorter
                    int filaModelo = tblProductos.convertRowIndexToModel(filaVista);

                    cargarDatosProductoEnCampos();
                    modoEdicion = true;
                    codigoProductoEnEdicion = (String) modeloTabla.getValueAt(filaModelo, 0);

                    // Sincronizar estado de botones como en la primera pulsación de Actualizar
                    btnActualizar.setText("Guardar Cambios");
                    btnAgregar.setEnabled(false);
                    btnEliminar.setEnabled(false);
                    btnRefrescar.setEnabled(false);
                    btnVerDetalle.setEnabled(false);
                    btnLimpiar.setEnabled(true);
                }
            }
        });
    }
    
    /**
     * Configura la actualización automática de la tabla
     */
    private void configurarAutoRefresh() {
        autoRefreshTimer = new Timer(REFRESH_INTERVAL, e -> {
            if (autoRefreshEnabled && !modoEdicion) {
                refrescarTablaSilencioso();
            }
        });
    }
    
    /**
     * Activa o desactiva la actualización automática
     */
    public void setAutoRefreshEnabled(boolean enabled) {
        this.autoRefreshEnabled = enabled;
        if (enabled) {
            autoRefreshTimer.start();
            System.out.println("Auto-refresh activado (cada " + (REFRESH_INTERVAL/1000) + " segundos)");
        } else {
            autoRefreshTimer.stop();
            System.out.println("Auto-refresh desactivado");
        }
    }
    
    /**
     * Configura el sistema de búsqueda en la tabla
     */
    private void configurarBusqueda() {
        sorter = new TableRowSorter<>(modeloTabla);
        tblProductos.setRowSorter(sorter);
    }
    
    /**
     * Filtra la tabla por texto de búsqueda
     */
    public void filtrarTabla(String textoBusqueda) {
        if (textoBusqueda.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + textoBusqueda));
        }
    }
    
    /**
     * Refresca la tabla sin mostrar mensajes (para auto-refresh)
     */
    private void refrescarTablaSilencioso() {
        try {
            // Deshabilitar sorter temporalmente para evitar parpadeos
            tblProductos.setRowSorter(null);
            
            int filaSeleccionada = tblProductos.getSelectedRow();
            String codigoSeleccionado = null;
            if (filaSeleccionada >= 0) {
                codigoSeleccionado = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
            }
            
            modeloTabla.setRowCount(0);
            List<Producto> productos = productoFacade.obtenerTodosLosProductos();
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("========== REFRESCO DE TABLA - INICIO ==========");
            for (Producto producto : productos) {
                Object[] fila = new Object[11];
                fila[0] = producto.getCodigo() != null ? producto.getCodigo() : "Sin código";
                fila[1] = producto.getNombre() != null ? producto.getNombre() : "Sin nombre";
                fila[2] = producto.getMarca() != null ? producto.getMarca() : "Sin marca";
                fila[3] = producto.getCategoria() != null ? producto.getCategoria() : "Sin categoría";
                fila[4] = producto.getStock();
                fila[5] = String.format("S/. %.2f", producto.getPrecio());
                fila[6] = producto.isAplicaIGV() ? "Habilitado" : "Deshabilitado";
                fila[7] = producto.isActivo() ? "Activo" : "Inactivo";
                fila[8] = producto.getUbicacion() != null ? producto.getUbicacion() : "Sin ubicación";
                fila[9] = producto.getProveedor() != null && !producto.getProveedor().isEmpty() ? producto.getProveedor() : "Sin proveedor";
                fila[10] = producto.getFechaVencimiento() != null ? sdf.format(producto.getFechaVencimiento()) : "Sin fecha";
                
                // Logging detallado para debugging
                System.out.println("TABLA: Producto " + producto.getCodigo() + " - isAplicaIGV()=" + producto.isAplicaIGV() + " - Columna[6]=" + fila[6]);
                System.out.println("TABLA: Producto " + producto.getCodigo() + " - isActivo()=" + producto.isActivo() + " - Columna[7]=" + fila[7]);
                System.out.println("✓ TABLA: Producto " + producto.getCodigo() + " - Proveedor=" + fila[9] + " - Fecha=" + fila[10]);
                
                modeloTabla.addRow(fila);
            }
            System.out.println("========== REFRESCO DE TABLA - FIN ==========");
            
            // Restaurar sorter
            tblProductos.setRowSorter(sorter);
            
            // Restaurar selección si es posible
            if (codigoSeleccionado != null) {
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (codigoSeleccionado.equals(modeloTabla.getValueAt(i, 0))) {
                        tblProductos.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
            
            // Actualizar timestamp
            SimpleDateFormat sdfTimestamp = new SimpleDateFormat("HH:mm:ss");
            ultimaActualizacion = sdfTimestamp.format(new Date());
            System.out.println("Tabla actualizada automáticamente a las " + ultimaActualizacion);
            
        } catch (Exception e) {
            System.err.println("Error en actualización automática: " + e.getMessage());
        } finally {
            // Asegurar que el sorter se restaure incluso si hay error
            if (tblProductos.getRowSorter() == null) {
                tblProductos.setRowSorter(sorter);
            }
        }
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
        tblProductos = new javax.swing.JTable();
        btnActualizar = new javax.swing.JButton();
        btnAgregar = new javax.swing.JButton();
        txtNombre = new javax.swing.JLabel();
        txtMarca = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtMarcaProducto = new javax.swing.JTextField();
        txtDescripcion = new javax.swing.JTextField();
        txtMedidaUnidad = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPrecioCompra = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPrecioVenta = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cmbIGV = new javax.swing.JComboBox<>();
        cmbCategoria = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        txtStock = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtLimiteStock = new javax.swing.JTextField();
        btnEliminar = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtUbicacion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cmbEstadoDisponibilidad = new javax.swing.JComboBox<>();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        btnLimpiar = new javax.swing.JButton();
        btnEscanear = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtRegistroEscaner = new javax.swing.JTextField();
        btnRefrescar = new javax.swing.JButton();
        btnVerDetalle = new javax.swing.JButton();
        btnEXCEL = new javax.swing.JButton();
        FechaFiltro = new com.toedter.calendar.JDateChooser();
        cmbFiltroTipo = new javax.swing.JComboBox<>();
        txtFiltro = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        btnBuscarFiltro = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        fechaVencimiento = new com.toedter.calendar.JDateChooser();
        cmbProovedor = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Registrar Producto");

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblProductos);

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnAgregar.setText("Agregar");

        txtNombre.setText("Nombre:");

        txtMarca.setText("Marca:");

        jLabel3.setText("Categoria:");

        jLabel4.setText("Descripcion:");

        jLabel1.setText("Unidad/Medida:");

        jLabel2.setText("Precio de Compra:");

        txtPrecioCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioCompraActionPerformed(evt);
            }
        });

        jLabel5.setText("Precio de Venta:");

        jLabel6.setText("Aplicar IGV:");

        cmbIGV.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Habilitado", "Deshabilitado" }));

        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Abarrotes", "Bebidas", "Snacks" }));

        jLabel7.setText("Stock:");

        jLabel8.setText("Limte de Stock:");

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        jLabel9.setText("Ubicacion:");

        txtUbicacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUbicacionActionPerformed(evt);
            }
        });

        jLabel10.setText("Estado:");

        cmbEstadoDisponibilidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Inactivo" }));
        cmbEstadoDisponibilidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbEstadoDisponibilidadActionPerformed(evt);
            }
        });

        lblCodigo.setText("Codigo:");

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        btnEscanear.setText("Escaner");

        jLabel11.setText("R-Codigo");

        btnRefrescar.setText("Refrescar");

        btnVerDetalle.setText("Ver Detalle");

        btnEXCEL.setText("Exportar EXCEL");

        jLabel12.setText("Filtro:");

        btnBuscarFiltro.setText("Buscar");

        jLabel13.setText("Fecha de Vencimiento:");

        jLabel14.setText("Proveedor:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNombre)
                                            .addComponent(txtMarca)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4))
                                        .addGap(39, 39, 39))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(btnRefrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addGap(18, 18, 18)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMedidaUnidad)
                                    .addComponent(txtDescripcion)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txtMarcaProducto)
                                    .addComponent(txtName)
                                    .addComponent(fechaVencimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(114, 114, 114)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel14))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbProovedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtStock, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtPrecioCompra)
                                    .addComponent(txtPrecioVenta)
                                    .addComponent(cmbIGV, javax.swing.GroupLayout.Alignment.TRAILING, 0, 140, Short.MAX_VALUE)
                                    .addComponent(txtLimiteStock, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(127, 127, 127)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel10)
                                            .addComponent(lblCodigo)
                                            .addComponent(jLabel11))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmbEstadoDisponibilidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtCodigo)
                                            .addComponent(txtUbicacion)
                                            .addComponent(btnEscanear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addComponent(txtRegistroEscaner, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(36, 36, 36))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbFiltroTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FechaFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBuscarFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(211, 211, 211)
                                .addComponent(btnVerDetalle)
                                .addGap(18, 18, 18)
                                .addComponent(btnEXCEL)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnActualizar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(16, 16, 16))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1067, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNombre)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtMarca)
                            .addComponent(txtMarcaProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(txtPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(cmbIGV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtMedidaUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(txtLimiteStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fechaVencimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cmbProovedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel14))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(txtUbicacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel10)
                                .addComponent(cmbEstadoDisponibilidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblCodigo)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(86, 86, 86))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnEscanear)
                                .addComponent(jLabel11))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtRegistroEscaner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRefrescar)
                            .addComponent(btnActualizar)
                            .addComponent(btnLimpiar)
                            .addComponent(btnAgregar)
                            .addComponent(btnEliminar)
                            .addComponent(btnVerDetalle)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEXCEL)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(FechaFiltro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbFiltroTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnBuscarFiltro))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        eliminarProducto();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtPrecioCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioCompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioCompraActionPerformed

    private void txtUbicacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUbicacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUbicacionActionPerformed

    private void cmbEstadoDisponibilidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoDisponibilidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbEstadoDisponibilidadActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarProducto();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {
        refrescarTablaManual();
    }
    
    private void btnEscanearActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this, 
            "Funcionalidad de escáner en desarrollo.\nPróximamente disponible.", 
            "Escáner", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void btnVerDetalleActionPerformed(java.awt.event.ActionEvent evt) {
        verDetalleProducto();
    }
    
    /**
     * Muestra un diálogo con todos los detalles del producto seleccionado
     */
    private void verDetalleProducto() {
        try {
            // Verificar que haya un producto seleccionado
            int filaSeleccionada = tblProductos.getSelectedRow();
            if (filaSeleccionada < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor, seleccione un producto de la tabla", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Convertir índice de vista a modelo
            int filaModelo = tblProductos.convertRowIndexToModel(filaSeleccionada);
            
            // Obtener el código del producto seleccionado
            String codigo = (String) modeloTabla.getValueAt(filaModelo, 0);
            
            // Buscar el producto completo
            Producto producto = productoFacade.buscarProducto(codigo);
            
            if (producto == null) {
                JOptionPane.showMessageDialog(this, 
                    "No se pudo encontrar el producto", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Construir mensaje con todos los detalles
            StringBuilder detalles = new StringBuilder();
            detalles.append("═══════════════════════════════════════\n");
            detalles.append("     DETALLE COMPLETO DEL PRODUCTO\n");
            detalles.append("═══════════════════════════════════════\n\n");
            
            detalles.append("📦 INFORMACIÓN BÁSICA\n");
            detalles.append("───────────────────────────────────────\n");
            detalles.append(String.format("• Código: %s\n", producto.getCodigo()));
            detalles.append(String.format("• Nombre: %s\n", producto.getNombre()));
            detalles.append(String.format("• Marca: %s\n", 
                producto.getMarca() != null ? producto.getMarca() : "Sin marca"));
            detalles.append(String.format("• Categoría: %s\n", 
                producto.getCategoria() != null ? producto.getCategoria() : "Sin categoría"));
            detalles.append(String.format("• Descripción: %s\n\n", 
                producto.getDescripcion() != null ? producto.getDescripcion() : "Sin descripción"));
            
            detalles.append("💰 INFORMACIÓN DE PRECIOS\n");
            detalles.append("───────────────────────────────────────\n");
            double precioCompraEstimado = producto.getPrecio() * 0.7;
            detalles.append(String.format("• Precio de Compra (est.): S/. %.2f\n", precioCompraEstimado));
            detalles.append(String.format("• Precio de Venta: S/. %.2f\n", producto.getPrecio()));
            detalles.append(String.format("• IGV: %s\n", producto.isAplicaIGV() ? "Habilitado" : "Deshabilitado"));
            double margen = ((producto.getPrecio() - precioCompraEstimado) / precioCompraEstimado) * 100;
            detalles.append(String.format("• Margen de Ganancia: %.1f%%\n\n", margen));
            
            detalles.append("📊 INVENTARIO\n");
            detalles.append("───────────────────────────────────────\n");
            detalles.append(String.format("• Stock Actual: %d unidades\n", producto.getStock()));
            detalles.append(String.format("• Stock Mínimo: %d unidades\n", producto.getStockMinimo()));
            detalles.append(String.format("• Unidad de Medida: %s\n", 
                producto.getUnidadMedida() != null ? producto.getUnidadMedida() : "N/A"));
            
            // Alerta de stock bajo
            if (producto.getStock() <= producto.getStockMinimo()) {
                detalles.append("⚠️  ¡ALERTA! Stock por debajo del mínimo\n");
            } else {
                detalles.append("✓ Stock en nivel óptimo\n");
            }
            detalles.append("\n");
            
            detalles.append("📍 UBICACIÓN Y ESTADO\n");
            detalles.append("───────────────────────────────────────\n");
            detalles.append(String.format("• Ubicación: %s\n", 
                producto.getUbicacion() != null ? producto.getUbicacion() : "Sin ubicación"));
            detalles.append(String.format("• Estado: %s\n\n", 
                producto.isActivo() ? "✓ Activo" : "✗ Inactivo"));
            
            detalles.append("🏢 INFORMACIÓN DE PROVEEDOR\n");
            detalles.append("───────────────────────────────────────\n");
            String proveedorInfo = producto.getProveedor() != null && !producto.getProveedor().isEmpty() 
                ? producto.getProveedor() : "Sin proveedor";
            detalles.append(String.format("• Proveedor: %s\n", proveedorInfo));
            
            // Si no hay proveedor, mostrar advertencia
            if (producto.getProveedor() == null || producto.getProveedor().isEmpty()) {
                detalles.append("  ⚠️  ADVERTENCIA: Producto sin proveedor asignado\n");
            } else {
                detalles.append("  ✓ Proveedor registrado correctamente\n");
            }
            detalles.append("\n");
            
            detalles.append("📅 INFORMACIÓN DE VENCIMIENTO\n");
            detalles.append("───────────────────────────────────────\n");
            SimpleDateFormat sdfDetalle = new SimpleDateFormat("dd/MM/yyyy");
            if (producto.getFechaVencimiento() != null) {
                String fechaFormateada = sdfDetalle.format(producto.getFechaVencimiento());
                detalles.append(String.format("• Fecha de Vencimiento: %s\n", fechaFormateada));
                
                // Calcular días hasta vencimiento
                Date fechaActual = new Date();
                long diferenciaMilisegundos = producto.getFechaVencimiento().getTime() - fechaActual.getTime();
                long diasHastaVencimiento = diferenciaMilisegundos / (1000 * 60 * 60 * 24);
                
                if (diasHastaVencimiento < 0) {
                    detalles.append(String.format("  🔴 PRODUCTO VENCIDO hace %d días\n", Math.abs(diasHastaVencimiento)));
                    detalles.append("  ⚠️  ACCIÓN REQUERIDA: Retirar del inventario\n");
                } else if (diasHastaVencimiento == 0) {
                    detalles.append("  🔴 PRODUCTO VENCE HOY\n");
                    detalles.append("  ⚠️  ACCIÓN URGENTE: Verificar estado del producto\n");
                } else if (diasHastaVencimiento <= 7) {
                    detalles.append(String.format("  🔴 CRÍTICO: Vence en %d días\n", diasHastaVencimiento));
                    detalles.append("  ⚠️  Producto próximo a vencer - Considerar promoción o descuento\n");
                } else if (diasHastaVencimiento <= 30) {
                    detalles.append(String.format("  🟡 ADVERTENCIA: Vence en %d días\n", diasHastaVencimiento));
                    detalles.append("  ⚠️  Producto próximo a vencer - Monitorear rotación\n");
                } else if (diasHastaVencimiento <= 90) {
                    detalles.append(String.format("  🟢 Vence en %d días (≈%d meses)\n", diasHastaVencimiento, diasHastaVencimiento/30));
                    detalles.append("  ✓ Tiempo de vencimiento aceptable\n");
                } else {
                    detalles.append(String.format("  🟢 Vence en %d días (≈%d meses)\n", diasHastaVencimiento, diasHastaVencimiento/30));
                    detalles.append("  ✓ Producto con fecha de vencimiento adecuada\n");
                }
            } else {
                detalles.append("• Fecha de Vencimiento: Sin fecha registrada\n");
                detalles.append("  ℹ️  Sin información de vencimiento\n");
            }
            detalles.append("\n");
            
            detalles.append("💵 VALOR TOTAL EN INVENTARIO\n");
            detalles.append("───────────────────────────────────────\n");
            double valorTotalCompra = precioCompraEstimado * producto.getStock();
            double valorTotalVenta = producto.getPrecio() * producto.getStock();
            detalles.append(String.format("• Valor de Compra: S/. %.2f\n", valorTotalCompra));
            detalles.append(String.format("• Valor de Venta: S/. %.2f\n", valorTotalVenta));
            detalles.append(String.format("• Ganancia Potencial: S/. %.2f\n", 
                valorTotalVenta - valorTotalCompra));
            
            detalles.append("\n═══════════════════════════════════════\n");
            
            // Mostrar en un JTextArea dentro de un JScrollPane
            javax.swing.JTextArea textArea = new javax.swing.JTextArea(detalles.toString());
            textArea.setEditable(false);
            textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
            textArea.setCaretPosition(0);
            
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(600, 750));
            
            JOptionPane.showMessageDialog(this, 
                scrollPane, 
                "📋 Detalle del Producto: " + producto.getCodigo(), 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al obtener detalles: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {
        // Generar código automáticamente basado en la categoría seleccionada
        generarCodigoAutomatico();
    }
    
    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {
        agregarProducto();
    }
    
    /**
     * Actualiza un producto existente en la base de datos
     * Primera pulsación: Carga datos del producto seleccionado en los campos
     * Segunda pulsación: Guarda los cambios si hubo modificaciones, o limpia campos si no hubo cambios
     */
    private void actualizarProducto() {
        try {
            int filaVista = tblProductos.getSelectedRow();
            if (filaVista < 0) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, seleccione un producto de la tabla",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Convertir índice de vista a modelo para que no falle con el sorter activo
            int filaModelo = tblProductos.convertRowIndexToModel(filaVista);
            String codigoSeleccionado = (String) modeloTabla.getValueAt(filaModelo, 0);

            // PRIMERA PULSACIÓN: activar modo edición y cargar datos
            if (!modoEdicion) {
                Producto producto = productoFacade.buscarProducto(codigoSeleccionado);
                if (producto == null) {
                    JOptionPane.showMessageDialog(this,
                            "No se encontró el producto seleccionado",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                System.out.println("====== MODO EDICIÓN ACTIVADO ======");
                System.out.println("Código: " + producto.getCodigo());

                txtCodigo.setText(producto.getCodigo());
                txtName.setText(producto.getNombre());
                txtMarcaProducto.setText(producto.getMarca() != null ? producto.getMarca() : "");
                txtUbicacion.setText(producto.getUbicacion() != null ? producto.getUbicacion() : "");
                txtDescripcion.setText(producto.getDescripcion() != null ? producto.getDescripcion() : "");
                txtMedidaUnidad.setText(producto.getUnidadMedida() != null ? producto.getUnidadMedida() : "");

                // Calcular precio de compra (aprox. 70% del precio de venta)
                precioCompraEnEdicion = producto.getPrecio() * 0.7;
                txtPrecioCompra.setText(String.format("%.2f", precioCompraEnEdicion));

                txtPrecioVenta.setText(String.valueOf(producto.getPrecio()));
                txtStock.setText(String.valueOf(producto.getStock()));
                txtLimiteStock.setText(String.valueOf(producto.getStockMinimo()));

                String categoria = producto.getCategoria();
                if (categoria != null) {
                    cmbCategoria.setSelectedItem(categoria);
                }

                cmbEstadoDisponibilidad.setSelectedItem(producto.isActivo() ? "Activo" : "Inactivo");
                cmbIGV.setSelectedItem(producto.isAplicaIGV() ? "Habilitado" : "Deshabilitado");
                
                // ⭐ CARGAR PROVEEDOR CON VALIDACIÓN MEJORADA
                String proveedorDB = producto.getProveedor();
                System.out.println("Proveedor desde DB: '" + proveedorDB + "'");
                
                if (proveedorDB != null && !proveedorDB.trim().isEmpty() && !proveedorDB.equals("Sin proveedor")) {
                    // Buscar el proveedor en el combobox
                    boolean encontrado = false;
                    for (int i = 0; i < cmbProovedor.getItemCount(); i++) {
                        String item = cmbProovedor.getItemAt(i);
                        if (item != null && item.equals(proveedorDB)) {
                            cmbProovedor.setSelectedIndex(i);
                            encontrado = true;
                            System.out.println("✓ Proveedor seleccionado: " + item);
                            break;
                        }
                    }
                    if (!encontrado) {
                        System.out.println("⚠ Proveedor '" + proveedorDB + "' no encontrado en combobox");
                        cmbProovedor.setSelectedIndex(0);
                    }
                } else {
                    System.out.println("Sin proveedor válido, seleccionando índice 0");
                    cmbProovedor.setSelectedIndex(0);
                }
                
                // ⭐ CARGAR FECHA DE VENCIMIENTO CON LOGGING
                Date fechaDB = producto.getFechaVencimiento();
                System.out.println("Fecha Vencimiento desde DB: " + fechaDB);
                fechaVencimiento.setDate(fechaDB);
                
                if (fechaDB != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    System.out.println("✓ Fecha configurada: " + sdf.format(fechaDB));
                } else {
                    System.out.println("Sin fecha de vencimiento");
                }

                System.out.println("====================================");

                modoEdicion = true;
                codigoProductoEnEdicion = codigoSeleccionado;

                btnActualizar.setText("Guardar Cambios");
                btnAgregar.setEnabled(false);
                btnEliminar.setEnabled(false);
                btnRefrescar.setEnabled(false);
                btnVerDetalle.setEnabled(false);
                btnLimpiar.setEnabled(true);

                JOptionPane.showMessageDialog(this,
                        "✏️ Modo Edición Activado\n\nRealice los cambios y presione 'Guardar Cambios'\nO presione 'Limpiar' para cancelar.",
                        "Modo Edición",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // SEGUNDA PULSACIÓN: validar y guardar
            if (codigoProductoEnEdicion == null) {
                resetearModoEdicion();
                JOptionPane.showMessageDialog(this,
                        "No hay producto en edición. Seleccione uno nuevamente.",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Siempre usar el código almacenado al entrar en modo edición
            String codigo = codigoProductoEnEdicion;
            Producto productoOriginal = productoFacade.buscarProducto(codigo);
            if (productoOriginal == null) {
                JOptionPane.showMessageDialog(this,
                        "Error: No se pudo encontrar el producto original",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                resetearModoEdicion();
                return;
            }

            // Validaciones de campos obligatorios
            String nombre = txtName.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El nombre es obligatorio",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double precioVenta;
            try {
                precioVenta = Double.parseDouble(txtPrecioVenta.getText().trim());
                if (precioVenta <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "El precio de venta debe ser mayor a 0",
                            "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "El precio de venta debe ser un número válido",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int stock;
            try {
                stock = Integer.parseInt(txtStock.getText().trim());
                if (stock < 0) {
                    JOptionPane.showMessageDialog(this,
                            "El stock no puede ser negativo",
                            "Validación",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "El stock debe ser un número entero válido",
                        "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int stockMinimo = 5;
            try {
                String stockMinimoStr = txtLimiteStock.getText().trim();
                if (!stockMinimoStr.isEmpty()) {
                    stockMinimo = Integer.parseInt(stockMinimoStr);
                }
            } catch (NumberFormatException e) {
                stockMinimo = 5;
            }

            String marca = txtMarcaProducto.getText().trim();
            String categoria = (String) cmbCategoria.getSelectedItem();
            
            // ⭐ OBTENER PROVEEDOR DEL COMBOBOX CON VALIDACIÓN Y LOGGING
            String proveedor = "";
            if (cmbProovedor.getSelectedItem() != null) {
                String provSeleccionado = cmbProovedor.getSelectedItem().toString().trim();
                // Solo asignar si no es el valor por defecto
                if (!provSeleccionado.equals("-- Seleccione Proveedor --") && 
                    !provSeleccionado.isEmpty() && 
                    !provSeleccionado.equals("Sin proveedor")) {
                    proveedor = provSeleccionado;
                    System.out.println("✓ Proveedor a guardar: '" + proveedor + "'");
                } else {
                    System.out.println("Sin proveedor válido seleccionado");
                }
            }
            
            // ⭐ OBTENER FECHA DE VENCIMIENTO CON LOGGING
            Date fechaVenc = fechaVencimiento.getDate();
            if (fechaVenc != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println("✓ Fecha a guardar: " + sdf.format(fechaVenc));
            } else {
                System.out.println("Sin fecha de vencimiento a guardar");
            }
            
            String descripcion = txtDescripcion.getText().trim();
            String unidadMedida = txtMedidaUnidad.getText().trim();
            String ubicacion = txtUbicacion.getText().trim();

            double precioCompra = precioCompraEnEdicion;
            try {
                String precioCompraStr = txtPrecioCompra.getText().trim();
                if (!precioCompraStr.isEmpty()) {
                    precioCompra = Double.parseDouble(precioCompraStr);
                }
            } catch (NumberFormatException e) {
                precioCompra = precioCompraEnEdicion;
            }

            String estadoSeleccionado = (String) cmbEstadoDisponibilidad.getSelectedItem();
            String igvSeleccionado = (String) cmbIGV.getSelectedItem();
            if (estadoSeleccionado == null || igvSeleccionado == null) {
                JOptionPane.showMessageDialog(this,
                        "Error: Estado o IGV no seleccionado correctamente",
                        "Error de Validación",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean activo = estadoSeleccionado.trim().equals("Activo");
            boolean aplicaIGV = igvSeleccionado.trim().equals("Habilitado");

            // Detectar cambios de forma robusta
            boolean huboCambios = detectarCambios(productoOriginal);
            if (!huboCambios) {
                JOptionPane.showMessageDialog(this,
                        "No se detectaron cambios. No se guardará nada.",
                        "Sin cambios",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                resetearModoEdicion();
                return;
            }

            // Confirmar antes de persistir
            String mensajeConfirmacion = String.format(
                    "¿Actualizar producto %s?\n\n" +
                            "Nombre: %s\n" +
                            "Precio Venta: S/. %.2f\n" +
                            "Stock: %d\n" +
                            "Estado: %s\n" +
                            "IGV: %s",
                    codigo, nombre, precioVenta, stock,
                    activo ? "Activo" : "Inactivo",
                    aplicaIGV ? "Habilitado" : "Deshabilitado");

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    mensajeConfirmacion,
                    "Confirmar Actualización",
                    JOptionPane.YES_NO_OPTION);
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            System.out.println("======= ACTUALIZANDO PRODUCTO =======");
            System.out.println("Código: " + codigo);
            System.out.println("Nombre: " + nombre);
            System.out.println("Proveedor a guardar: '" + proveedor + "'");
            System.out.println("Fecha Vencimiento a guardar: " + fechaVenc);
            System.out.println("IGV Aplicado: " + aplicaIGV + " (ComboBox: " + igvSeleccionado + ")");
            System.out.println("Estado Activo: " + activo + " (ComboBox: " + estadoSeleccionado + ")");
            System.out.println("=====================================");

                boolean exito = productoFacade.actualizarProducto(
                    codigo, nombre, marca, categoria,
                    "", // subcategoría
                    unidadMedida,
                    1, // cantidadPorUnidad
                    aplicaIGV,
                    descripcion,
                    precioVenta,
                    stock,
                    stockMinimo,
                    proveedor,
                    fechaVenc,
                    ubicacion,
                    activo
                );

            if (exito) {
                System.out.println("✓ Actualización exitosa en facade");
                System.out.println("Proveedor guardado: '" + proveedor + "'");
                System.out.println("Fecha guardada: " + fechaVenc);

                // Reset UI
                limpiarCampos();
                resetearModoEdicion();

                try {
                    Thread.sleep(300); // dar tiempo a MongoDB
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                refrescarTablaConVerificacion(codigo);

                JOptionPane.showMessageDialog(this,
                        "✓ Producto actualizado correctamente",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.err.println("✗ Error al actualizar en facade");
                JOptionPane.showMessageDialog(this,
                        "Error al actualizar el producto",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            resetearModoEdicion();
        }
    }
    
    /**
     * Detecta si hubo cambios entre el producto original y los campos actuales
     */
    private boolean detectarCambios(Producto productoOriginal) {
        try {
            System.out.println("====== DETECTANDO CAMBIOS ======");
            
            // Comparar cada campo
            if (!productoOriginal.getNombre().equals(txtName.getText().trim())) {
                System.out.println("CAMBIO detectado: Nombre");
                return true;
            }
            
            String marcaOriginal = productoOriginal.getMarca() != null ? productoOriginal.getMarca() : "";
            if (!marcaOriginal.equals(txtMarcaProducto.getText().trim())) {
                System.out.println("CAMBIO detectado: Marca");
                return true;
            }
            
            String ubicacionOriginal = productoOriginal.getUbicacion() != null ? productoOriginal.getUbicacion() : "";
            if (!ubicacionOriginal.equals(txtUbicacion.getText().trim())) {
                System.out.println("CAMBIO detectado: Ubicación");
                return true;
            }
            
            String descripcionOriginal = productoOriginal.getDescripcion() != null ? productoOriginal.getDescripcion() : "";
            if (!descripcionOriginal.equals(txtDescripcion.getText().trim())) {
                System.out.println("CAMBIO detectado: Descripción");
                return true;
            }
            
            String unidadMedidaOriginal = productoOriginal.getUnidadMedida() != null ? productoOriginal.getUnidadMedida() : "";
            if (!unidadMedidaOriginal.equals(txtMedidaUnidad.getText().trim())) {
                System.out.println("CAMBIO detectado: Unidad/Medida");
                return true;
            }
            
            double precioVenta = Double.parseDouble(txtPrecioVenta.getText().trim());
            if (Math.abs(productoOriginal.getPrecio() - precioVenta) > 0.01) {
                System.out.println("CAMBIO detectado: Precio");
                return true;
            }
            
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (productoOriginal.getStock() != stock) {
                System.out.println("CAMBIO detectado: Stock");
                return true;
            }
            
            int stockMinimo = Integer.parseInt(txtLimiteStock.getText().trim());
            if (productoOriginal.getStockMinimo() != stockMinimo) {
                System.out.println("CAMBIO detectado: Stock Mínimo");
                return true;
            }
            
            String categoriaOriginal = productoOriginal.getCategoria() != null ? productoOriginal.getCategoria() : "";
            String categoriaActual = (String) cmbCategoria.getSelectedItem();
            if (!categoriaOriginal.equals(categoriaActual)) {
                System.out.println("CAMBIO detectado: Categoría");
                return true;
            }
            
            // ⭐ VALIDACIÓN ROBUSTA PARA ESTADO
            boolean estadoOriginal = productoOriginal.isActivo();
            String estadoSeleccionado = (String) cmbEstadoDisponibilidad.getSelectedItem();
            if (estadoSeleccionado == null) {
                System.err.println("ERROR: Estado ComboBox es null");
                return true; // Asumir cambio si hay error
            }
            boolean estadoActual = estadoSeleccionado.trim().equals("Activo");
            if (estadoOriginal != estadoActual) {
                System.out.println("CAMBIO detectado: Estado (Original=" + estadoOriginal + ", Actual=" + estadoActual + ")");
                return true;
            }

            // ⭐ VALIDACIÓN ROBUSTA PARA IGV
            boolean igvOriginal = productoOriginal.isAplicaIGV();
            String igvSeleccionado = (String) cmbIGV.getSelectedItem();
            if (igvSeleccionado == null) {
                System.err.println("ERROR: IGV ComboBox es null");
                return true; // Asumir cambio si hay error
            }
            boolean igvActual = igvSeleccionado.trim().equals("Habilitado");
            if (igvOriginal != igvActual) {
                System.out.println("CAMBIO detectado: IGV (Original=" + igvOriginal + ", Actual=" + igvActual + ")");
                return true;
            }
            
            // Verificar precio de compra
            try {
                double precioCompraActual = Double.parseDouble(txtPrecioCompra.getText().trim());
                if (Math.abs(precioCompraEnEdicion - precioCompraActual) > 0.01) {
                    System.out.println("CAMBIO detectado: Precio Compra");
                    return true;
                }
            } catch (NumberFormatException e) {
                // Ignorar si no es válido
            }
            
            // Verificar proveedor
            String proveedorOriginal = productoOriginal.getProveedor() != null ? productoOriginal.getProveedor() : "";
            String proveedorActual = "";
            if (cmbProovedor.getSelectedItem() != null) {
                String provSeleccionado = cmbProovedor.getSelectedItem().toString().trim();
                // Ignorar el valor por defecto del combobox
                if (!provSeleccionado.equals("-- Seleccione Proveedor --") && !provSeleccionado.isEmpty()) {
                    proveedorActual = provSeleccionado;
                }
            }
            if (!proveedorOriginal.equals(proveedorActual)) {
                System.out.println("CAMBIO detectado: Proveedor (Original=\"" + proveedorOriginal + "\", Actual=\"" + proveedorActual + "\")");
                return true;
            }
            
            // Verificar fecha de vencimiento
            Date fechaOriginal = productoOriginal.getFechaVencimiento();
            Date fechaActual = fechaVencimiento.getDate();
            
            if (fechaOriginal == null && fechaActual != null) {
                System.out.println("CAMBIO detectado: Fecha de vencimiento (Original=null, Actual=" + fechaActual + ")");
                return true;
            }
            if (fechaOriginal != null && fechaActual == null) {
                System.out.println("CAMBIO detectado: Fecha de vencimiento (Original=" + fechaOriginal + ", Actual=null)");
                return true;
            }
            if (fechaOriginal != null && fechaActual != null) {
                // Comparar solo las fechas sin hora
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fechaOriginalStr = sdf.format(fechaOriginal);
                String fechaActualStr = sdf.format(fechaActual);
                if (!fechaOriginalStr.equals(fechaActualStr)) {
                    System.out.println("CAMBIO detectado: Fecha de vencimiento (Original=" + fechaOriginalStr + ", Actual=" + fechaActualStr + ")");
                    return true;
                }
            }
            
            System.out.println("NO se detectaron cambios");
            return false; // No hubo cambios
            
        } catch (Exception e) {
            System.err.println("ERROR en detectarCambios: " + e.getMessage());
            e.printStackTrace();
            // Si hay error al comparar, asumimos que hubo cambios
            return true;
        }
    }
    
    /**
     * Resetea el modo edición
     */
    private void resetearModoEdicion() {
        modoEdicion = false;
        codigoProductoEnEdicion = null;
        precioCompraEnEdicion = 0.0;
        btnActualizar.setText("Actualizar");
        
        // Reactivar botones
        btnAgregar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnRefrescar.setEnabled(true);
        btnVerDetalle.setEnabled(true);
        btnLimpiar.setEnabled(true);
    }
    
    /**
     * Agrega un nuevo producto a la base de datos
     */
    private void agregarProducto() {
        try {
            // Validar que los campos obligatorios estén llenos
            if (txtCodigo.getText().trim().isEmpty() || 
                txtName.getText().trim().isEmpty() ||
                txtPrecioVenta.getText().trim().isEmpty() ||
                txtStock.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(this, 
                    "Por favor, rellene todos los campos obligatorios:\n" +
                    "- Código\n" +
                    "- Nombre\n" +
                    "- Precio de Venta\n" +
                    "- Stock", 
                    "Rellenar Campos", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validar precio de venta
            double precioVenta = 0;
            try {
                precioVenta = Double.parseDouble(txtPrecioVenta.getText().trim());
                if (precioVenta <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "El precio de venta debe ser mayor a 0", 
                        "Validación", 
                        JOptionPane.WARNING_MESSAGE);
                    txtPrecioVenta.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "El precio de venta debe ser un número válido", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                txtPrecioVenta.requestFocus();
                return;
            }
            
            // Validar stock
            int stock = 0;
            try {
                stock = Integer.parseInt(txtStock.getText().trim());
                if (stock < 0) {
                    JOptionPane.showMessageDialog(this, 
                        "El stock no puede ser negativo", 
                        "Validación", 
                        JOptionPane.WARNING_MESSAGE);
                    txtStock.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "El stock debe ser un número entero válido", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                txtStock.requestFocus();
                return;
            }
            
            // Obtener datos de los campos
            String codigo = txtCodigo.getText().trim();
            String nombre = txtName.getText().trim();
            String marca = txtMarcaProducto.getText().trim();
            String categoria = (String) cmbCategoria.getSelectedItem();
            String descripcion = txtDescripcion.getText().trim();
            String unidadMedida = txtMedidaUnidad.getText().trim();
            boolean aplicaIGV = cmbIGV.getSelectedItem().equals("Habilitado");
            boolean activo = cmbEstadoDisponibilidad.getSelectedItem().equals("Activo");
            
            // ⭐ OBTENER PROVEEDOR DEL COMBOBOX CON VALIDACIÓN Y LOGGING
            String proveedor = "";
            if (cmbProovedor.getSelectedItem() != null) {
                String provSeleccionado = cmbProovedor.getSelectedItem().toString().trim();
                // Solo asignar si no es el valor por defecto
                if (!provSeleccionado.equals("-- Seleccione Proveedor --") && 
                    !provSeleccionado.isEmpty() && 
                    !provSeleccionado.equals("Sin proveedor")) {
                    proveedor = provSeleccionado;
                    System.out.println("✓ Proveedor a registrar: '" + proveedor + "'");
                } else {
                    System.out.println("Sin proveedor válido seleccionado");
                }
            }
            
            // ⭐ OBTENER FECHA DE VENCIMIENTO CON LOGGING
            Date fechaVenc = fechaVencimiento.getDate();
            if (fechaVenc != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println("✓ Fecha a registrar: " + sdf.format(fechaVenc));
            } else {
                System.out.println("Sin fecha de vencimiento a registrar");
            }
            
            // Precio de compra (opcional)
            double precioCompra = 0;
            try {
                String precioCompraStr = txtPrecioCompra.getText().trim();
                if (!precioCompraStr.isEmpty()) {
                    precioCompra = Double.parseDouble(precioCompraStr);
                }
            } catch (NumberFormatException e) {
                precioCompra = 0;
            }
            
            // Stock mínimo (opcional)
            int stockMinimo = 0;
            try {
                String stockMinimoStr = txtLimiteStock.getText().trim();
                if (!stockMinimoStr.isEmpty()) {
                    stockMinimo = Integer.parseInt(stockMinimoStr);
                }
            } catch (NumberFormatException e) {
                stockMinimo = 5; // Valor por defecto
            }
            
            String ubicacion = txtUbicacion.getText().trim();
            
            System.out.println("======= REGISTRANDO PRODUCTO =======");
            System.out.println("Código: " + codigo);
            System.out.println("Nombre: " + nombre);
            System.out.println("Proveedor a registrar: '" + proveedor + "'");
            System.out.println("Fecha Vencimiento a registrar: " + fechaVenc);
            System.out.println("IGV: " + aplicaIGV);
            System.out.println("Estado: " + activo);
            System.out.println("====================================");
            
            // Llamar al facade para registrar el producto
            boolean exito = productoFacade.registrarProducto(
                codigo, nombre, marca, categoria,
                "", // subcategoría (vacía por ahora)
                unidadMedida,
                1, // cantidadPorUnidad por defecto
                aplicaIGV,
                descripcion,
                precioVenta,
                stock,
                stockMinimo,
                proveedor,
                fechaVenc,
                ubicacion,
                activo
            );
            
            if (exito) {
                System.out.println("✓ Producto registrado exitosamente");
                System.out.println("Proveedor guardado: '" + proveedor + "'");
                System.out.println("Fecha guardada: " + fechaVenc);
                // Limpiar campos
                limpiarCampos();
                
                // Refrescar tabla silenciosamente
                refrescarTablaSilencioso();
                
                // Generar nuevo código
                generarCodigoAutomatico();
                
                // Mensaje breve de éxito
                JOptionPane.showMessageDialog(this, 
                    "✓ Producto agregado", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al agregar el producto. El código ya existe o hay datos inválidos.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        // Si está en modo edición, preguntar si desea cancelar
        if (modoEdicion) {
            int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Cancelar la edición y limpiar los campos?", 
                "Confirmar Cancelación", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        txtName.setText(""); // Nombre
        txtMarcaProducto.setText(""); // Marca
        txtUbicacion.setText(""); // Ubicación
        txtDescripcion.setText(""); // Descripción
        txtMedidaUnidad.setText(""); // Unidad/Medida
        txtPrecioCompra.setText(""); // Precio Compra
        txtPrecioVenta.setText(""); // Precio Venta
        txtStock.setText(""); // Stock
        txtLimiteStock.setText(""); // Stock Mínimo
        cmbCategoria.setSelectedIndex(0);
        cmbEstadoDisponibilidad.setSelectedIndex(0);
        cmbIGV.setSelectedIndex(0);
        cmbProovedor.setSelectedIndex(0); // Limpiar proveedor
        fechaVencimiento.setDate(null); // Limpiar fecha de vencimiento
        tblProductos.clearSelection();
        generarCodigoAutomatico();
        txtName.requestFocus(); // Poner el foco en el campo nombre
        resetearModoEdicion(); // Resetear el modo edición
    }
    
    /**
     * Carga los datos del producto seleccionado en los campos
     */
    private void cargarDatosProductoEnCampos() {
        int filaSeleccionada = tblProductos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            // Convertir índice de vista a modelo
            int filaModelo = tblProductos.convertRowIndexToModel(filaSeleccionada);
            
            String codigo = (String) modeloTabla.getValueAt(filaModelo, 0);
            Producto producto = productoFacade.buscarProducto(codigo);
            
            if (producto != null) {
                System.out.println("====== CARGANDO PRODUCTO EN CAMPOS ======");
                System.out.println("Código: " + producto.getCodigo());
                
                txtCodigo.setText(producto.getCodigo());
                txtName.setText(producto.getNombre());
                txtMarcaProducto.setText(producto.getMarca() != null ? producto.getMarca() : "");
                txtUbicacion.setText(producto.getUbicacion() != null ? producto.getUbicacion() : "");
                txtDescripcion.setText(producto.getDescripcion() != null ? producto.getDescripcion() : "");
                txtMedidaUnidad.setText(producto.getUnidadMedida() != null ? producto.getUnidadMedida() : "");
                txtPrecioCompra.setText(""); // Precio compra no disponible en modelo simple
                txtPrecioVenta.setText(String.valueOf(producto.getPrecio()));
                txtStock.setText(String.valueOf(producto.getStock()));
                txtLimiteStock.setText(String.valueOf(producto.getStockMinimo()));
                
                // Seleccionar categoría
                String categoria = producto.getCategoria();
                if (categoria != null) {
                    cmbCategoria.setSelectedItem(categoria);
                }
                
                // Seleccionar estado
                cmbEstadoDisponibilidad.setSelectedItem(producto.isActivo() ? "Activo" : "Inactivo");

                // Seleccionar IGV
                cmbIGV.setSelectedItem(producto.isAplicaIGV() ? "Habilitado" : "Deshabilitado");
                
                // ⭐ CARGAR PROVEEDOR CON LOGGING
                String proveedorDB = producto.getProveedor();
                System.out.println("Proveedor desde DB: '" + proveedorDB + "'");
                
                if (proveedorDB != null && !proveedorDB.trim().isEmpty() && !proveedorDB.equals("Sin proveedor")) {
                    // Buscar el proveedor en el combobox
                    boolean encontrado = false;
                    for (int i = 0; i < cmbProovedor.getItemCount(); i++) {
                        String item = cmbProovedor.getItemAt(i);
                        if (item != null && item.equals(proveedorDB)) {
                            cmbProovedor.setSelectedIndex(i);
                            encontrado = true;
                            System.out.println("✓ Proveedor seleccionado en combobox: " + item);
                            break;
                        }
                    }
                    if (!encontrado) {
                        System.out.println("⚠ Proveedor '" + proveedorDB + "' no encontrado en combobox");
                        cmbProovedor.setSelectedIndex(0);
                    }
                } else {
                    System.out.println("Sin proveedor válido, seleccionando índice 0");
                    cmbProovedor.setSelectedIndex(0);
                }
                
                // ⭐ CARGAR FECHA DE VENCIMIENTO CON LOGGING
                Date fechaDB = producto.getFechaVencimiento();
                System.out.println("Fecha Vencimiento desde DB: " + fechaDB);
                fechaVencimiento.setDate(fechaDB);
                
                if (fechaDB != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    System.out.println("✓ Fecha configurada: " + sdf.format(fechaDB));
                } else {
                    System.out.println("Sin fecha de vencimiento");
                }
                
                System.out.println("========================================");
            }
        }
    }
    
    /**
     * Elimina un producto de la base de datos (eliminación lógica)
     */
    private void eliminarProducto() {
        try {
            // Verificar que haya un producto seleccionado en la tabla
            int filaSeleccionada = tblProductos.getSelectedRow();
            if (filaSeleccionada < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor, seleccione un producto de la tabla para eliminar", 
                    "Validación", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Convertir índice de vista a modelo
            int filaModelo = tblProductos.convertRowIndexToModel(filaSeleccionada);
            
            // Obtener el código del producto seleccionado
            String codigo = (String) modeloTabla.getValueAt(filaModelo, 0);
            String nombre = (String) modeloTabla.getValueAt(filaModelo, 1);
            
            // Confirmar eliminación
            int confirmacion = JOptionPane.showConfirmDialog(this, 
                "⚠️ ADVERTENCIA: ELIMINACIÓN PERMANENTE\n\n" +
                "¿Está seguro de ELIMINAR el producto?\n\n" +
                "Código: " + codigo + "\n" +
                "Nombre: " + nombre + "\n\n" +
                "❌ Esta acción NO se puede deshacer.\n" +
                "❌ El producto será eliminado permanentemente de la base de datos.", 
                "⚠️ Confirmar Eliminación Permanente", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Llamar al facade para eliminar el producto (eliminación lógica)
            boolean exito = productoFacade.eliminarProducto(codigo);
            
            if (exito) {
                // Limpiar campos
                limpiarCampos();
                
                // Refrescar tabla silenciosamente
                refrescarTablaSilencioso();
                
                // Mensaje breve de éxito
                JOptionPane.showMessageDialog(this, 
                    "✓ Producto eliminado permanentemente\n" +
                    "El producto " + codigo + " ha sido eliminado de la base de datos.", 
                    "Eliminación Exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar el producto", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Prueba rápida de conectividad con MongoDB
     */
    private void probarConectividadRapida() {
        try {
            System.out.println("=== PRUEBA DE CONECTIVIDAD ===");
            
            // Probar obtener todos los productos con información detallada
            List<Producto> productos = productoFacade.obtenerTodosLosProductos();
            System.out.println("Productos obtenidos del facade: " + productos.size());
            
            // Mostrar información en consola
            for (Producto p : productos) {
                System.out.println("Producto encontrado: " + p.getCodigo() + " - " + p.getNombre());
            }
            
        } catch (Exception e) {
            System.out.println("Error en prueba de conectividad: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método de prueba para verificar la conexión y datos
     */
    private void probarConexionDB() {
        try {
            // Probar conexión y obtener datos
            List<Producto> todosLosProductos = productoFacade.obtenerTodosLosProductos();
            List<Producto> productosActivos = productoFacade.obtenerProductosActivos();
            
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("=== DIAGNÓSTICO DE BASE DE DATOS ===\n");
            mensaje.append("Total de productos en BD: ").append(todosLosProductos.size()).append("\n");
            mensaje.append("Productos activos: ").append(productosActivos.size()).append("\n\n");
            
            if (!todosLosProductos.isEmpty()) {
                mensaje.append("=== PRIMEROS 3 PRODUCTOS ===\n");
                for (int i = 0; i < Math.min(3, todosLosProductos.size()); i++) {
                    Producto p = todosLosProductos.get(i);
                    mensaje.append("- Código: ").append(p.getCodigo())
                           .append(", Nombre: ").append(p.getNombre())
                           .append(", Activo: ").append(p.isActivo()).append("\n");
                }
            } else {
                mensaje.append("¡No se encontraron productos en la base de datos!\n");
                mensaje.append("Verifique:\n");
                mensaje.append("1. Conexión a MongoDB\n");
                mensaje.append("2. Nombre de la base de datos\n");
                mensaje.append("3. Nombre de la colección (Producto vs Productos)\n");
            }
            
            JOptionPane.showMessageDialog(this, 
                mensaje.toString(), 
                "Diagnóstico de BD", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error en diagnóstico: " + e.getMessage(), 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Configura el modelo de la tabla con las columnas necesarias
     */
    private void configurarTabla() {
        // Definir columnas de la tabla
        String[] columnas = {"Código", "Nombre", "Marca", "Categoría", "Stock", "Precio", "IGV", "Estado", "Ubicación", "Proveedor", "Fecha Venc."};
        
        // Crear modelo de tabla no editable
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que la tabla no sea editable
            }
        };
        
        // Asignar el modelo a la tabla
        tblProductos.setModel(modeloTabla);
        
        // Configurar ancho de columnas
        tblProductos.getColumnModel().getColumn(0).setPreferredWidth(80);  // Código
        tblProductos.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tblProductos.getColumnModel().getColumn(2).setPreferredWidth(120); // Marca
        tblProductos.getColumnModel().getColumn(3).setPreferredWidth(120); // Categoría
        tblProductos.getColumnModel().getColumn(4).setPreferredWidth(80);  // Stock
        tblProductos.getColumnModel().getColumn(5).setPreferredWidth(100); // Precio
        tblProductos.getColumnModel().getColumn(6).setPreferredWidth(80);  // IGV
        tblProductos.getColumnModel().getColumn(7).setPreferredWidth(80);  // Estado
        tblProductos.getColumnModel().getColumn(8).setPreferredWidth(120); // Ubicación
        tblProductos.getColumnModel().getColumn(9).setPreferredWidth(150); // Proveedor
        tblProductos.getColumnModel().getColumn(10).setPreferredWidth(100); // Fecha Venc.
        
        // Habilitar selección de filas
        tblProductos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }
    
    /**
     * Carga los datos de productos desde la base de datos a la tabla
     */
    private void cargarDatosTabla() {
        try {
            // Limpiar tabla
            modeloTabla.setRowCount(0);
            
            // Primero intentar obtener todos los productos
            List<Producto> productos = productoFacade.obtenerTodosLosProductos();
            System.out.println("Cargando " + productos.size() + " productos...");
            
            // Si no hay productos, solo log en consola
            if (productos.isEmpty()) {
                System.out.println("ADVERTENCIA: No se encontraron productos en la base de datos");
                return;
            }
            
            // Llenar la tabla con los datos
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Producto producto : productos) {
                Object[] fila = new Object[11];
                fila[0] = producto.getCodigo() != null ? producto.getCodigo() : "Sin código";
                fila[1] = producto.getNombre() != null ? producto.getNombre() : "Sin nombre";
                fila[2] = producto.getMarca() != null ? producto.getMarca() : "Sin marca";
                fila[3] = producto.getCategoria() != null ? producto.getCategoria() : "Sin categoría";
                fila[4] = producto.getStock();
                fila[5] = String.format("S/. %.2f", producto.getPrecio());
                fila[6] = producto.isAplicaIGV() ? "Habilitado" : "Deshabilitado";
                fila[7] = producto.isActivo() ? "Activo" : "Inactivo";
                fila[8] = producto.getUbicacion() != null ? producto.getUbicacion() : "Sin ubicación";
                fila[9] = producto.getProveedor() != null && !producto.getProveedor().isEmpty() ? producto.getProveedor() : "Sin proveedor";
                fila[10] = producto.getFechaVencimiento() != null ? sdf.format(producto.getFechaVencimiento()) : "Sin fecha";
                modeloTabla.addRow(fila);
            }
            
            // Actualizar timestamp
            SimpleDateFormat sdfTimestamp = new SimpleDateFormat("HH:mm:ss");
            ultimaActualizacion = sdfTimestamp.format(new Date());
            
            System.out.println("✓ Tabla cargada exitosamente: " + productos.size() + " productos - " + ultimaActualizacion);
            
        } catch (Exception e) {
            String errorMsg = "Error al cargar los productos: " + e.getMessage();
            System.err.println("ERROR: " + errorMsg);
            
            JOptionPane.showMessageDialog(this, 
                errorMsg, 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Refresca los datos de la tabla
     */
    public void refrescarTabla() {
        cargarDatosTabla();
    }
    
    /**
     * Refresca la tabla y verifica que los cambios se hayan guardado correctamente
     */
    private void refrescarTablaConVerificacion(String codigoProducto) {
        try {
            System.out.println("Refrescando tabla y verificando cambios para: " + codigoProducto);
            
            // Deshabilitar sorter temporalmente
            tblProductos.setRowSorter(null);
            
            modeloTabla.setRowCount(0);
            List<Producto> productos = productoFacade.obtenerTodosLosProductos();
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Producto productoActualizado = null;
            int filaParaSeleccionar = -1;
            for (Producto producto : productos) {
                Object[] fila = new Object[11];
                fila[0] = producto.getCodigo() != null ? producto.getCodigo() : "Sin código";
                fila[1] = producto.getNombre() != null ? producto.getNombre() : "Sin nombre";
                fila[2] = producto.getMarca() != null ? producto.getMarca() : "Sin marca";
                fila[3] = producto.getCategoria() != null ? producto.getCategoria() : "Sin categoría";
                fila[4] = producto.getStock();
                fila[5] = String.format("S/. %.2f", producto.getPrecio());
                fila[6] = producto.isAplicaIGV() ? "Habilitado" : "Deshabilitado";
                fila[7] = producto.isActivo() ? "Activo" : "Inactivo";
                fila[8] = producto.getUbicacion() != null ? producto.getUbicacion() : "Sin ubicación";
                fila[9] = producto.getProveedor() != null && !producto.getProveedor().isEmpty() ? producto.getProveedor() : "Sin proveedor";
                fila[10] = producto.getFechaVencimiento() != null ? sdf.format(producto.getFechaVencimiento()) : "Sin fecha";
                modeloTabla.addRow(fila);
                
                if (producto.getCodigo().equals(codigoProducto)) {
                    productoActualizado = producto;
                    filaParaSeleccionar = modeloTabla.getRowCount() - 1;
                }
            }
            
            // Restaurar sorter
            tblProductos.setRowSorter(sorter);

            // Reseleccionar el producto actualizado si existe
            if (filaParaSeleccionar >= 0) {
                tblProductos.setRowSelectionInterval(filaParaSeleccionar, filaParaSeleccionar);
            }
            
            // Verificar y mostrar en log el producto actualizado
            if (productoActualizado != null) {
                System.out.println("======= VERIFICACIÓN POST-ACTUALIZACIÓN =======");
                System.out.println("Código: " + productoActualizado.getCodigo());
                System.out.println("IGV en BD: " + productoActualizado.isAplicaIGV());
                System.out.println("Estado en BD: " + productoActualizado.isActivo());
                System.out.println("⭐ Proveedor en BD: '" + productoActualizado.getProveedor() + "'");
                System.out.println("⭐ Fecha Venc. en BD: " + productoActualizado.getFechaVencimiento());
                System.out.println("Tabla muestra IGV: " + (productoActualizado.isAplicaIGV() ? "Habilitado" : "Deshabilitado"));
                System.out.println("Tabla muestra Estado: " + (productoActualizado.isActivo() ? "Activo" : "Inactivo"));
                SimpleDateFormat sdfVerif = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println("✓ Tabla muestra Proveedor: " + (productoActualizado.getProveedor() != null && !productoActualizado.getProveedor().isEmpty() ? productoActualizado.getProveedor() : "Sin proveedor"));
                System.out.println("✓ Tabla muestra Fecha: " + (productoActualizado.getFechaVencimiento() != null ? sdfVerif.format(productoActualizado.getFechaVencimiento()) : "Sin fecha"));
                System.out.println("=============================================");
            } else {
                System.err.println("⚠ ADVERTENCIA: No se encontró el producto actualizado en la tabla");
            }
            
            // Actualizar timestamp
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
            ultimaActualizacion = sdfTime.format(new Date());
            
        } catch (Exception e) {
            System.err.println("Error al refrescar tabla con verificación: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Asegurar que el sorter se restaure
            if (tblProductos.getRowSorter() == null) {
                tblProductos.setRowSorter(sorter);
            }
        }
    }
    
    /**
     * Refresca la tabla manualmente con confirmación visual
     */
    private void refrescarTablaManual() {
        try {
            // Deshabilitar sorter temporalmente para evitar parpadeo
            tblProductos.setRowSorter(null);
            
            // Guardar selección actual
            int filaSeleccionada = tblProductos.getSelectedRow();
            String codigoSeleccionado = null;
            if (filaSeleccionada >= 0) {
                codigoSeleccionado = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
            }
            
            // Limpiar y recargar
            modeloTabla.setRowCount(0);
            List<Producto> productos = productoFacade.obtenerTodosLosProductos();
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Producto producto : productos) {
                Object[] fila = new Object[11];
                fila[0] = producto.getCodigo() != null ? producto.getCodigo() : "Sin código";
                fila[1] = producto.getNombre() != null ? producto.getNombre() : "Sin nombre";
                fila[2] = producto.getMarca() != null ? producto.getMarca() : "Sin marca";
                fila[3] = producto.getCategoria() != null ? producto.getCategoria() : "Sin categoría";
                fila[4] = producto.getStock();
                fila[5] = String.format("S/. %.2f", producto.getPrecio());
                fila[6] = producto.isAplicaIGV() ? "Habilitado" : "Deshabilitado";
                fila[7] = producto.isActivo() ? "Activo" : "Inactivo";
                fila[8] = producto.getUbicacion() != null ? producto.getUbicacion() : "Sin ubicación";
                fila[9] = producto.getProveedor() != null && !producto.getProveedor().isEmpty() ? producto.getProveedor() : "Sin proveedor";
                fila[10] = producto.getFechaVencimiento() != null ? sdf.format(producto.getFechaVencimiento()) : "Sin fecha";
                modeloTabla.addRow(fila);
            }
            
            // Restaurar sorter
            tblProductos.setRowSorter(sorter);
            
            // Restaurar selección
            if (codigoSeleccionado != null) {
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (codigoSeleccionado.equals(modeloTabla.getValueAt(i, 0))) {
                        tblProductos.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }
            
            // Actualizar timestamp
            SimpleDateFormat sdfTimestamp2 = new SimpleDateFormat("HH:mm:ss");
            ultimaActualizacion = sdfTimestamp2.format(new Date());
            
            // Mostrar confirmación breve
            btnRefrescar.setBackground(new Color(144, 238, 144)); // Verde claro
            Timer colorTimer = new Timer(1000, evt -> {
                btnRefrescar.setBackground(null);
            });
            colorTimer.setRepeats(false);
            colorTimer.start();
            
            System.out.println("✓ Tabla actualizada manualmente: " + productos.size() + " productos - " + ultimaActualizacion);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al refrescar: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            // Asegurar que el sorter se restaure incluso si hay error
            if (tblProductos.getRowSorter() == null) {
                tblProductos.setRowSorter(sorter);
            }
        }
    }
    
    /**
     * Carga los proveedores en el combobox
     */
    private void cargarProveedores() {
        try {
            cmbProovedor.removeAllItems();
            cmbProovedor.addItem("-- Seleccione Proveedor --");
            
            List<Proveedor> proveedores = proveedorFacade.obtenerTodos();
            for (Proveedor proveedor : proveedores) {
                cmbProovedor.addItem(proveedor.getRazonSocial());
            }
        } catch (Exception e) {
            System.err.println("Error al cargar proveedores: " + e.getMessage());
            cmbProovedor.addItem("-- Sin proveedores --");
        }
    }
    
    /**
     * Configura los filtros en el combobox
     */
    private void configurarFiltros() {
        cmbFiltroTipo.removeAllItems();
        cmbFiltroTipo.addItem("Categoría");
        cmbFiltroTipo.addItem("Código");
        cmbFiltroTipo.addItem("Nombre");
        cmbFiltroTipo.addItem("Marca");
        cmbFiltroTipo.addItem("Fecha Vencimiento");
        cmbFiltroTipo.addItem("Stock Bajo");
        cmbFiltroTipo.addItem("Estado");
        cmbFiltroTipo.setSelectedIndex(0);
    }
    
    /**
     * Aplica los filtros a la tabla según los criterios seleccionados
     */
    private void aplicarFiltros() {
        try {
            String tipoFiltro = (String) cmbFiltroTipo.getSelectedItem();
            String valorFiltro = txtFiltro.getText().trim();
            Date fechaFiltro = FechaFiltro.getDate();
            
            if (tipoFiltro == null) {
                return;
            }
            
            List<RowFilter<Object, Object>> filtros = new ArrayList<>();
            
            switch (tipoFiltro) {
                case "Categoría":
                    if (!valorFiltro.isEmpty()) {
                        filtros.add(RowFilter.regexFilter("(?i)" + valorFiltro, 3)); // Columna 3: Categoría
                    }
                    break;
                    
                case "Código":
                    if (!valorFiltro.isEmpty()) {
                        filtros.add(RowFilter.regexFilter("(?i)" + valorFiltro, 0)); // Columna 0: Código
                    }
                    break;
                    
                case "Nombre":
                    if (!valorFiltro.isEmpty()) {
                        filtros.add(RowFilter.regexFilter("(?i)" + valorFiltro, 1)); // Columna 1: Nombre
                    }
                    break;
                    
                case "Marca":
                    if (!valorFiltro.isEmpty()) {
                        filtros.add(RowFilter.regexFilter("(?i)" + valorFiltro, 2)); // Columna 2: Marca
                    }
                    break;
                    
                case "Fecha Vencimiento":
                    if (fechaFiltro != null) {
                        // Filtrar por productos con fecha de vencimiento cercana
                        JOptionPane.showMessageDialog(this,
                            "Filtro de fecha de vencimiento requiere consulta a la base de datos.\\nEn desarrollo.",
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                    
                case "Stock Bajo":
                    // Filtrar productos con stock menor a stockMinimo
                    filtros.add(new RowFilter<Object, Object>() {
                        @Override
                        public boolean include(Entry<? extends Object, ? extends Object> entry) {
                            try {
                                int stock = Integer.parseInt(entry.getValue(4).toString());
                                return stock < 10; // Consideramos stock bajo si es menor a 10
                            } catch (Exception e) {
                                return false;
                            }
                        }
                    });
                    break;
                    
                case "Estado":
                    if (!valorFiltro.isEmpty()) {
                        filtros.add(RowFilter.regexFilter("(?i)" + valorFiltro, 7)); // Columna 7: Estado
                    }
                    break;
            }
            
            if (!filtros.isEmpty()) {
                sorter.setRowFilter(RowFilter.andFilter(filtros));
                JOptionPane.showMessageDialog(this,
                    "Filtro aplicado: " + tblProductos.getRowCount() + " productos encontrados",
                    "Filtro Aplicado",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                sorter.setRowFilter(null); // Limpiar filtro si no hay criterios
                JOptionPane.showMessageDialog(this,
                    "No se especificaron criterios de filtro.\\nMostrando todos los productos.",
                    "Sin Filtro",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al aplicar filtros: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Exporta la tabla completa a un archivo Excel (CSV)
     */
    private void exportarExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exportar a Excel");
            fileChooser.setSelectedFile(new File("productos_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File archivoExportar = fileChooser.getSelectedFile();
                
                // Asegurar extensión .csv
                if (!archivoExportar.getName().toLowerCase().endsWith(".csv")) {
                    archivoExportar = new File(archivoExportar.getAbsolutePath() + ".csv");
                }
                
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoExportar))) {
                    // Escribir encabezados
                    writer.write("Código,Nombre,Marca,Categoría,Stock,Precio,IGV,Estado,Ubicación,Proveedor,Fecha Vencimiento");
                    writer.newLine();
                    
                    // Escribir datos de todas las filas visibles
                    for (int i = 0; i < tblProductos.getRowCount(); i++) {
                        StringBuilder fila = new StringBuilder();
                        for (int j = 0; j < tblProductos.getColumnCount(); j++) {
                            int modelRow = tblProductos.convertRowIndexToModel(i);
                            Object valor = modeloTabla.getValueAt(modelRow, j);
                            String valorStr = valor != null ? valor.toString() : "";
                            
                            // Escapar comas y comillas para formato CSV
                            if (valorStr.contains(",") || valorStr.contains("\"")) {
                                valorStr = "\"" + valorStr.replace("\"", "\"\"") + "\"";
                            }
                            
                            fila.append(valorStr);
                            if (j < tblProductos.getColumnCount() - 1) {
                                fila.append(",");
                            }
                        }
                        writer.write(fila.toString());
                        writer.newLine();
                    }
                    
                    JOptionPane.showMessageDialog(this,
                        "✓ Exportación exitosa\\n\\n" +
                        "Archivo: " + archivoExportar.getName() + "\\n" +
                        "Productos exportados: " + tblProductos.getRowCount(),
                        "Exportación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error al escribir el archivo: " + ex.getMessage(),
                        "Error de Escritura",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al exportar: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene el producto seleccionado en la tabla
     */
    public Producto obtenerProductoSeleccionado() {
        int filaSeleccionada = tblProductos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            // Convertir índice de vista a modelo
            int filaModelo = tblProductos.convertRowIndexToModel(filaSeleccionada);
            String codigo = (String) modeloTabla.getValueAt(filaModelo, 0);
            return productoFacade.buscarProducto(codigo);
        }
        return null;
    }
    
    /**
     * Genera un código automático basado en la categoría seleccionada
     */
    private void generarCodigoAutomatico() {
        try {
            String categoriaSeleccionada = (String) cmbCategoria.getSelectedItem();
            if (categoriaSeleccionada != null && !categoriaSeleccionada.isEmpty()) {
                String codigoGenerado = productoFacade.generarCodigoPorCategoria(categoriaSeleccionada);
                txtCodigo.setText(codigoGenerado);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al generar código: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser FechaFiltro;
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBuscarFiltro;
    private javax.swing.JButton btnEXCEL;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEscanear;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JButton btnVerDetalle;
    private javax.swing.JComboBox<String> cmbCategoria;
    private javax.swing.JComboBox<String> cmbEstadoDisponibilidad;
    private javax.swing.JComboBox<String> cmbFiltroTipo;
    private javax.swing.JComboBox<String> cmbIGV;
    private javax.swing.JComboBox<String> cmbProovedor;
    private com.toedter.calendar.JDateChooser fechaVencimiento;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtFiltro;
    private javax.swing.JTextField txtLimiteStock;
    private javax.swing.JLabel txtMarca;
    private javax.swing.JTextField txtMarcaProducto;
    private javax.swing.JTextField txtMedidaUnidad;
    private javax.swing.JTextField txtName;
    private javax.swing.JLabel txtNombre;
    private javax.swing.JTextField txtPrecioCompra;
    private javax.swing.JTextField txtPrecioVenta;
    private javax.swing.JTextField txtRegistroEscaner;
    private javax.swing.JTextField txtStock;
    private javax.swing.JTextField txtUbicacion;
    // End of variables declaration//GEN-END:variables
}
