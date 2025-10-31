/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package utp.edu.pe.nexoket.jform;

import utp.edu.pe.nexoket.Facade.VentaFacade;
import utp.edu.pe.nexoket.dao.ClienteDAO;
import utp.edu.pe.nexoket.modelo.Venta;
import utp.edu.pe.nexoket.modelo.DetalleVenta;
import utp.edu.pe.nexoket.modelo.Producto;
import utp.edu.pe.nexoket.modelo.Cliente;
import utp.edu.pe.nexoket.util.WebcamBarcodeScanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;

/**
 * Formulario para registrar ventas con scanner de códigos de barras
 * @author User
 */
public class ItmRegistrarVenta extends javax.swing.JInternalFrame {

    private VentaFacade ventaFacade;
    private ClienteDAO clienteDAO;
    private Venta ventaActual;
    private DefaultTableModel modeloTabla;
    private DecimalFormat formatoMoneda;
    private WebcamBarcodeScanner scanner;
    private JPanel panelCamara;
    private boolean camaraActiva = false;
    
    /**
     * Creates new form ItmRegistrarVenta
     */
    public ItmRegistrarVenta() {
        initComponents();
        inicializarComponentes();
        inicializarVenta();
    }
    
    /**
     * Inicializa componentes personalizados
     */
    private void inicializarComponentes() {
        // Inicializar facades
        ventaFacade = new VentaFacade();
        clienteDAO = new ClienteDAO();
        formatoMoneda = new DecimalFormat("#,##0.00");
        
        // Configurar tabla del carrito
        configurarTablaCarrito();
        
        // Configurar campos de solo lectura
        txtNumeroBoleta.setEditable(false);
        txtFechaEmision.setEditable(false);
        txtNombreProducto.setEditable(false);
        txtPrecioUnitario.setEditable(false);
        txtStockDisponible.setEditable(false);
        txtSubtotal.setEditable(false);
        txtIGV.setEditable(false);
        txtTotal.setEditable(false);
        txtVuelto.setEditable(false);
        
        // Establecer nombre del vendedor automáticamente desde la sesión
        cargarVendedorActual();
        
        // Configurar spinner de cantidad
        spnCantidad.setModel(new SpinnerNumberModel(1, 1, 9999, 1));
        
        // Listener para calcular vuelto automáticamente
        txtEfectivoRecibido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularVuelto();
            }
        });
        
        // Listener para Enter en código de barras
        txtCodigoEscaneado.addActionListener(evt -> {
            buscarYAgregarProducto(txtCodigoEscaneado.getText().trim());
        });
        
        // Listener para búsqueda automática de cliente al escribir DNI
        txtDniCliente.addActionListener(evt -> {
            buscarCliente();
        });
        
        // Deshabilitar botón de imprimir al inicio
        btnImprimirBoleta.setEnabled(false);
    }
    
    /**
     * Carga el nombre del vendedor actual desde la sesión
     */
    private void cargarVendedorActual() {
        try {
            String nombreVendedor = utp.edu.pe.nexoket.util.SesionUsuario.getInstance().getNombreCompleto();
            txtNombreDelVendedor.setText(nombreVendedor);
            txtNombreDelVendedor.setEditable(false);
            System.out.println("✅ Vendedor cargado: " + nombreVendedor);
        } catch (Exception e) {
            txtNombreDelVendedor.setText("Vendedor Demo");
            System.out.println("⚠️ No hay sesión activa, usando vendedor demo");
        }
    }
    
    /**
     * Configura la tabla del carrito de compras
     */
    private void configurarTablaCarrito() {
        String[] columnas = {"Código", "Producto", "Precio", "Cantidad", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        
        // Aquí deberías tener un JTable en lugar de JTextArea
        // Como el formulario tiene JTextArea, lo convertiré después
        // Por ahora trabajaremos con lo que hay
    }
    
    /**
     * Inicializa una nueva venta
     */
    private void inicializarVenta() {
        ventaActual = new Venta();
        
        // Generar número de venta
        String numeroVenta = ventaFacade.generarNumeroVenta();
        ventaActual.setNumeroVenta(numeroVenta);
        txtNumeroBoleta.setText(numeroVenta);
        
        // Fecha actual
        LocalDateTime ahora = LocalDateTime.now();
        ventaActual.setFechaEmision(ahora);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtFechaEmision.setText(ahora.format(formatter));
        
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Resetear totales
        actualizarTotales();
        
        // Limpiar campos
        limpiarCamposProducto();
        limpiarCamposCliente();
        txtEfectivoRecibido.setText("");
        txtVuelto.setText("");
        
        // Habilitar controles
        btnProcesarVenta.setEnabled(true);
        btnImprimirBoleta.setEnabled(false);
        
        System.out.println("✅ Nueva venta iniciada: " + numeroVenta);
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
        txtNumeroBoleta = new javax.swing.JTextField();
        btnGenerarNumeroDeBoleta = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtFechaEmision = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cmbTipoPago = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtNombreDelVendedor = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCarritoVenta = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtNombreProducto = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtPrecioUnitario = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtStockDisponible = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        spnCantidad = new javax.swing.JSpinner();
        btnEliminarItem = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        txtNombreCliente = new javax.swing.JTextField();
        txtDniCliente = new javax.swing.JTextField();
        txtTelefonoCliente = new javax.swing.JTextField();
        btnBuscarCliente = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        txtCodigoEscaneado = new javax.swing.JTextField();
        txtSubtotal = new javax.swing.JTextField();
        txtIGV = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        txtEfectivoRecibido = new javax.swing.JTextField();
        txtVuelto = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        btnProcesarVenta = new javax.swing.JButton();
        btnCancelarVenta = new javax.swing.JButton();
        btnImprimirBoleta = new javax.swing.JButton();
        btnNuevaVenta = new javax.swing.JButton();

        setClosable(true);
        setTitle("Registro de Venta");

        jLabel1.setText("Numero de Boleta");

        btnGenerarNumeroDeBoleta.setText("Generar");
        btnGenerarNumeroDeBoleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarNumeroDeBoletaActionPerformed(evt);
            }
        });

        jLabel2.setText("Fecha de Emision");

        jLabel3.setText("Tipo de Pago");

        cmbTipoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Tarjeta", "Yape" }));

        jLabel4.setText("Emisor de la Boleta");

        txtNombreDelVendedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreDelVendedorActionPerformed(evt);
            }
        });

        tblCarritoVenta.setColumns(20);
        tblCarritoVenta.setRows(5);
        jScrollPane1.setViewportView(tblCarritoVenta);

        jLabel5.setText("Productos");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel7.setText("DATOS GENERALES");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel8.setText("REGISTRO DE PRODUCTO");

        jLabel9.setText("Codigo de Barras");

        jLabel10.setText("Modo Manual");

        jButton3.setText("Buscar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel11.setText("Nombre Producto");

        jLabel12.setText("Precio Unitario");

        jLabel13.setText("Stock Disponible");

        jLabel14.setText("Cantidad");

        btnEliminarItem.setText("Eliminar");
        btnEliminarItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarItemActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel15.setText("DATOS CLIENTE");

        btnBuscarCliente.setText("Buscar");
        btnBuscarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarClienteActionPerformed(evt);
            }
        });

        jLabel6.setText("DNI");

        jLabel16.setText("Nombre");

        jLabel17.setText("Telefono");

        jButton4.setText("Escanear");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel18.setText("SubTotal");

        jLabel19.setText("IGV");

        jLabel20.setText("Total");

        jLabel21.setText("Efectivo Recibido");

        jLabel22.setText("Vuelto");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel23.setText("COMPRA");

        btnProcesarVenta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnProcesarVenta.setText("Procesar Venta");
        btnProcesarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesarVentaActionPerformed(evt);
            }
        });

        btnCancelarVenta.setText("Cancelar Venta");
        btnCancelarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarVentaActionPerformed(evt);
            }
        });

        btnImprimirBoleta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnImprimirBoleta.setText("Imprimir Boleta");
        btnImprimirBoleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirBoletaActionPerformed(evt);
            }
        });

        btnNuevaVenta.setText("Nueva Venta");
        btnNuevaVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevaVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 55, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtDniCliente, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtNombreCliente, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtTelefonoCliente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                                .addGap(100, 100, 100))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtNombreDelVendedor, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(cmbTipoPago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addComponent(jLabel2)
                                            .addComponent(txtNumeroBoleta)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnGenerarNumeroDeBoleta))
                                            .addComponent(txtFechaEmision, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel4)
                                            .addGap(50, 50, 50))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(jLabel15))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(188, 188, 188)
                        .addComponent(btnBuscarCliente)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel11)))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jButton3)
                                        .addComponent(txtNombreProducto, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jButton4)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtCodigoEscaneado))))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel13)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtStockDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel12)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtPrecioUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(125, 125, 125)
                                .addComponent(jLabel23)
                                .addGap(99, 99, 99)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(btnEliminarItem, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtSubtotal)
                                            .addComponent(txtIGV)
                                            .addComponent(txtTotal)
                                            .addComponent(txtEfectivoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel18)
                                                    .addComponent(jLabel19)
                                                    .addComponent(jLabel20)
                                                    .addComponent(jLabel21)
                                                    .addComponent(jLabel22))
                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                    .addGap(57, 57, 57)
                                                    .addComponent(btnProcesarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGap(2, 2, 2)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnImprimirBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(6, 6, 6)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(btnNuevaVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnCancelarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(99, 99, 99))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(18, 18, 18)
                        .addComponent(spnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(239, 239, 239))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(196, 196, 196))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(btnGenerarNumeroDeBoleta))
                        .addGap(13, 13, 13)
                        .addComponent(txtNumeroBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jButton4)
                            .addComponent(txtCodigoEscaneado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jButton3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtFechaEmision, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cmbTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtPrecioUnitario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtStockDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(spnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNombreDelVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDniCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNombreCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTelefonoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscarCliente))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(btnEliminarItem)
                            .addComponent(jLabel23))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18))
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtIGV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel20))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtEfectivoRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel21))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22))
                                .addGap(11, 11, 11)
                                .addComponent(btnProcesarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnImprimirBoleta, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnCancelarVenta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnNuevaVenta)))))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Busca un producto por código y lo agrega al carrito
     */
    private void buscarYAgregarProducto(String codigo) {
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un código de producto", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Producto producto = ventaFacade.buscarProducto(codigo);
        
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "Producto no encontrado: " + codigo, 
                "Error", JOptionPane.ERROR_MESSAGE);
            txtCodigoEscaneado.setText("");
            return;
        }
        
        // Mostrar datos del producto
        txtNombreProducto.setText(producto.getNombre());
        txtPrecioUnitario.setText(String.valueOf(producto.getPrecio()));
        txtStockDisponible.setText(String.valueOf(producto.getStock()));
        
        // Agregar al carrito automáticamente con cantidad 1
        agregarProductoAlCarrito(producto, 1);
        
        // Limpiar código escaneado
        txtCodigoEscaneado.setText("");
        txtCodigoEscaneado.requestFocus();
    }
    
    /**
     * Agrega un producto al carrito de ventas
     */
    private void agregarProductoAlCarrito(Producto producto, int cantidad) {
        if (cantidad <= 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!producto.tieneStockSuficiente(cantidad)) {
            JOptionPane.showMessageDialog(this, 
                "Stock insuficiente. Disponible: " + producto.getStock(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Agregar a la venta usando facade
        boolean agregado = ventaFacade.agregarProductoAVenta(ventaActual, producto.getCodigo(), cantidad);
        
        if (agregado) {
            // Actualizar tabla
            actualizarTablaCarrito();
            actualizarTotales();
            limpiarCamposProducto();
            
            System.out.println("✅ Producto agregado al carrito: " + producto.getNombre() + " x" + cantidad);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo agregar el producto al carrito", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Actualiza la tabla del carrito con los detalles de la venta
     */
    private void actualizarTablaCarrito() {
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Agregar cada detalle
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            Object[] fila = {
                detalle.getCodigoProducto(),
                detalle.getNombreProducto(),
                formatoMoneda.format(detalle.getPrecioUnitario()),
                detalle.getCantidad(),
                formatoMoneda.format(detalle.getSubtotal())
            };
            modeloTabla.addRow(fila);
        }
        
        // Actualizar JTextArea (temporal hasta cambiar a JTable)
        StringBuilder sb = new StringBuilder();
        sb.append("Código\tProducto\tPrecio\tCant.\tSubtotal\n");
        sb.append("=".repeat(60)).append("\n");
        
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            sb.append(detalle.getCodigoProducto()).append("\t");
            sb.append(detalle.getNombreProducto()).append("\t");
            sb.append("S/ ").append(formatoMoneda.format(detalle.getPrecioUnitario())).append("\t");
            sb.append(detalle.getCantidad()).append("\t");
            sb.append("S/ ").append(formatoMoneda.format(detalle.getSubtotal())).append("\n");
        }
        
        tblCarritoVenta.setText(sb.toString());
    }
    
    /**
     * Actualiza los campos de totales
     */
    private void actualizarTotales() {
        ventaActual.calcularTotales();
        
        txtSubtotal.setText(formatoMoneda.format(ventaActual.getSubtotal()));
        txtIGV.setText(formatoMoneda.format(ventaActual.getIgv()));
        txtTotal.setText(formatoMoneda.format(ventaActual.getTotal()));
        
        calcularVuelto();
    }
    
    /**
     * Calcula el vuelto automáticamente
     */
    private void calcularVuelto() {
        try {
            String efectivoTexto = txtEfectivoRecibido.getText().trim();
            if (!efectivoTexto.isEmpty()) {
                double efectivo = Double.parseDouble(efectivoTexto);
                double vuelto = efectivo - ventaActual.getTotal();
                txtVuelto.setText(formatoMoneda.format(vuelto));
            } else {
                txtVuelto.setText("");
            }
        } catch (NumberFormatException e) {
            txtVuelto.setText("");
        }
    }
    
    /**
     * Limpia los campos de producto
     */
    private void limpiarCamposProducto() {
        txtCodigoEscaneado.setText("");
        txtNombreProducto.setText("");
        txtPrecioUnitario.setText("");
        txtStockDisponible.setText("");
        spnCantidad.setValue(1);
    }
    
    /**
     * Limpia los campos de cliente
     */
    private void limpiarCamposCliente() {
        txtDniCliente.setText("");
        txtNombreCliente.setText("");
        txtTelefonoCliente.setText("");
    }
    
    /**
     * Activa la cámara para escanear códigos de barras
     */
    private void activarCamara() {
        if (camaraActiva) {
            detenerCamara();
            return;
        }
        
        try {
            scanner = new WebcamBarcodeScanner();
            
            scanner.startScanner((codigo, formato) -> {
                SwingUtilities.invokeLater(() -> {
                    System.out.println("📷 Código escaneado: " + codigo + " [" + formato + "]");
                    txtCodigoEscaneado.setText(codigo);
                    buscarYAgregarProducto(codigo);
                    
                    // Mostrar notificación temporal
                    mostrarNotificacion("Producto escaneado: " + codigo);
                });
            });
            
            camaraActiva = true;
            jButton4.setText("Detener");
            
            System.out.println("📷 Cámara activada");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al activar la cámara: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Detiene la cámara
     */
    private void detenerCamara() {
        if (scanner != null) {
            scanner.stopScanner();
            scanner = null;
            camaraActiva = false;
            jButton4.setText("Escanear");
            System.out.println("📷 Cámara detenida");
        }
    }
    
    /**
     * Muestra una notificación temporal
     */
    private void mostrarNotificacion(String mensaje) {
        JLabel lblNotificacion = new JLabel(mensaje);
        lblNotificacion.setOpaque(true);
        lblNotificacion.setBackground(new Color(144, 238, 144));
        lblNotificacion.setForeground(Color.BLACK);
        lblNotificacion.setFont(new Font("Arial", Font.BOLD, 12));
        lblNotificacion.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JWindow ventana = new JWindow();
        ventana.setContentPane(lblNotificacion);
        ventana.pack();
        ventana.setLocationRelativeTo(this);
        ventana.setVisible(true);
        
        Timer timer = new Timer(2000, e -> ventana.dispose());
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Busca un cliente por DNI automáticamente
     */
    private void buscarCliente() {
        System.out.println("🔍 Método buscarCliente() iniciado");
        String dni = txtDniCliente.getText().trim();
        System.out.println("📝 DNI ingresado: [" + dni + "]");
        
        if (dni.isEmpty()) {
            System.out.println("⚠️ DNI vacío");
            JOptionPane.showMessageDialog(this, 
                "Por favor, ingrese el DNI del cliente", 
                "DNI Requerido", 
                JOptionPane.WARNING_MESSAGE);
            txtDniCliente.requestFocus();
            return;
        }
        
        // Validar que el DNI sea numérico y tenga 8 dígitos
        if (!dni.matches("\\d{8}")) {
            System.out.println("⚠️ DNI inválido: " + dni);
            JOptionPane.showMessageDialog(this, 
                "El DNI debe tener 8 dígitos numéricos", 
                "DNI Inválido", 
                JOptionPane.WARNING_MESSAGE);
            txtDniCliente.requestFocus();
            txtDniCliente.selectAll();
            return;
        }
        
        System.out.println("✅ DNI válido, buscando en base de datos...");
        // Buscar en la base de datos
        Cliente cliente = clienteDAO.consultarCliente(dni);
        System.out.println("📦 Resultado de búsqueda: " + (cliente != null ? "Cliente encontrado" : "Cliente NO encontrado"));
        
        if (cliente != null) {
            // Autocompletar campos - Manejar valores vacíos correctamente
            String nombre = (cliente.getNombre() != null && !cliente.getNombre().isEmpty()) 
                ? cliente.getNombre() : "";
            String apellido = (cliente.getApellido() != null && !cliente.getApellido().isEmpty()) 
                ? cliente.getApellido() : "";
            String telefono = (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty()) 
                ? cliente.getTelefono() : "";
            
            // Concatenar nombre completo (evitar espacios dobles si alguno está vacío)
            String nombreCompleto = (nombre + " " + apellido).trim();
            if (nombreCompleto.isEmpty()) {
                nombreCompleto = "Sin nombre registrado";
            }
            
            System.out.println("👤 Nombre: " + nombreCompleto);
            System.out.println("📞 Teléfono: " + (telefono.isEmpty() ? "No registrado" : telefono));
            
            txtNombreCliente.setText(nombreCompleto);
            txtTelefonoCliente.setText(telefono);
            
            System.out.println("✅ Cliente encontrado con DNI: " + dni);
            
            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this, 
                "✅ Cliente encontrado:\n\n" +
                "Nombre: " + (nombreCompleto.equals("Sin nombre registrado") ? "No registrado" : nombreCompleto) + "\n" +
                "DNI: " + cliente.getDni() + "\n" +
                "Teléfono: " + (telefono.isEmpty() ? "No registrado" : telefono), 
                "Cliente Encontrado", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Limpiar campos si no se encuentra
            txtNombreCliente.setText("");
            txtTelefonoCliente.setText("");
            
            System.out.println("⚠️ Cliente no encontrado con DNI: " + dni);
            
            // Mostrar mensaje de cliente no encontrado
            int respuesta = JOptionPane.showConfirmDialog(this, 
                "❌ Cliente no encontrado con DNI: " + dni + "\n\n" +
                "¿Desea continuar la venta sin registrar el cliente?", 
                "Cliente No Encontrado", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
            
            if (respuesta == JOptionPane.NO_OPTION) {
                txtDniCliente.requestFocus();
                txtDniCliente.selectAll();
            }
        }
    }

    private void btnCancelarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarVentaActionPerformed
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro que desea cancelar esta venta?", 
            "Confirmar cancelación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            inicializarVenta();
            JOptionPane.showMessageDialog(this, "Venta cancelada", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnCancelarVentaActionPerformed

    private void btnImprimirBoletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirBoletaActionPerformed
        if (ventaActual.getNumeroVenta() == null || ventaActual.getDetalles().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay ninguna venta procesada para imprimir", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Generar contenido de la boleta
        String boleta = generarTextoBoleta();
        
        // Mostrar en un diálogo con scroll
        JTextArea textAreaBoleta = new JTextArea(boleta);
        textAreaBoleta.setEditable(false);
        textAreaBoleta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textAreaBoleta);
        scrollPane.setPreferredSize(new Dimension(500, 600));
        
        int opcion = JOptionPane.showOptionDialog(this,
            scrollPane,
            "Boleta de Venta - " + ventaActual.getNumeroVenta(),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            new Object[]{"Cerrar", "Copiar al Portapapeles"},
            "Cerrar");
        
        if (opcion == 1) {
            // Copiar al portapapeles
            java.awt.datatransfer.StringSelection stringSelection = 
                new java.awt.datatransfer.StringSelection(boleta);
            java.awt.datatransfer.Clipboard clipboard = 
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            
            JOptionPane.showMessageDialog(this, 
                "Boleta copiada al portapapeles", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnImprimirBoletaActionPerformed

    private void btnProcesarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesarVentaActionPerformed
        // Validar que hay productos en el carrito
        if (ventaActual.getDetalles().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Agregue al menos un producto a la venta", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validar vendedor
        String vendedor = txtNombreDelVendedor.getText().trim();
        if (vendedor.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingrese el nombre del vendedor", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtNombreDelVendedor.requestFocus();
            return;
        }
        
        // Validar tipo de pago
        String tipoPago = (String) cmbTipoPago.getSelectedItem();
        
        // Validar efectivo recibido si es pago en efectivo
        if ("Efectivo".equals(tipoPago)) {
            String efectivoTexto = txtEfectivoRecibido.getText().trim();
            if (efectivoTexto.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Ingrese el efectivo recibido", 
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
                txtEfectivoRecibido.requestFocus();
                return;
            }
            
            try {
                double efectivo = Double.parseDouble(efectivoTexto);
                if (efectivo < ventaActual.getTotal()) {
                    JOptionPane.showMessageDialog(this, 
                        "El efectivo recibido es menor al total de la venta", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    txtEfectivoRecibido.requestFocus();
                    return;
                }
                ventaActual.setEfectivoRecibido(efectivo);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "El efectivo recibido debe ser un número válido", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                txtEfectivoRecibido.requestFocus();
                return;
            }
        } else {
            ventaActual.setEfectivoRecibido(ventaActual.getTotal());
        }
        
        // Configurar datos de la venta
        ventaActual.setEmisorBoleta(vendedor);
        ventaActual.setTipoPago(tipoPago);
        
        // Datos del cliente (opcional)
        String dniCliente = txtDniCliente.getText().trim();
        String nombreCliente = txtNombreCliente.getText().trim();
        String telefonoCliente = txtTelefonoCliente.getText().trim();
        
        if (!dniCliente.isEmpty()) {
            ventaActual.setDniCliente(dniCliente);
        }
        if (!nombreCliente.isEmpty()) {
            ventaActual.setNombreCliente(nombreCliente);
        }
        if (!telefonoCliente.isEmpty()) {
            ventaActual.setTelefonoCliente(telefonoCliente);
        }
        
        // Calcular totales finales
        ventaActual.calcularTotales();
        ventaActual.calcularVuelto();
        
        // Confirmar procesamiento
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Confirmar procesamiento de la venta?\n" +
            "Total: S/ " + formatoMoneda.format(ventaActual.getTotal()) + "\n" +
            "Efectivo: S/ " + formatoMoneda.format(ventaActual.getEfectivoRecibido()) + "\n" +
            "Vuelto: S/ " + formatoMoneda.format(ventaActual.getVuelto()), 
            "Confirmar Venta", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            // Procesar la venta
            boolean exitoso = ventaFacade.procesarVenta(ventaActual);
            
            if (exitoso) {
                JOptionPane.showMessageDialog(this, 
                    "¡Venta procesada exitosamente!\n" +
                    "Número de venta: " + ventaActual.getNumeroVenta() + "\n" +
                    "Total: S/ " + formatoMoneda.format(ventaActual.getTotal()) + "\n" +
                    "Vuelto: S/ " + formatoMoneda.format(ventaActual.getVuelto()), 
                    "Venta Exitosa", JOptionPane.INFORMATION_MESSAGE);
                
                // Habilitar impresión
                btnImprimirBoleta.setEnabled(true);
                btnProcesarVenta.setEnabled(false);
                
                // Detener cámara si está activa
                detenerCamara();
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al procesar la venta.\n" +
                    "Verifique el stock de los productos y vuelva a intentarlo.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnProcesarVentaActionPerformed

    private void btnNuevaVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevaVentaActionPerformed
        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Iniciar una nueva venta?", 
            "Nueva Venta", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            inicializarVenta();
            JOptionPane.showMessageDialog(this, 
                "Nueva venta iniciada", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnNuevaVentaActionPerformed

    private void btnBuscarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarClienteActionPerformed
        System.out.println("🔍 Botón Buscar Cliente presionado");
        buscarCliente();
    }//GEN-LAST:event_btnBuscarClienteActionPerformed

    private void txtNombreDelVendedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreDelVendedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreDelVendedorActionPerformed

    private void btnGenerarNumeroDeBoletaActionPerformed(java.awt.event.ActionEvent evt) {
        String nuevoNumero = ventaFacade.generarNumeroVenta();
        ventaActual.setNumeroVenta(nuevoNumero);
        txtNumeroBoleta.setText(nuevoNumero);
        JOptionPane.showMessageDialog(this, 
            "Nuevo número generado: " + nuevoNumero, 
            "Número de Venta", JOptionPane.INFORMATION_MESSAGE);
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        // Botón "Buscar" - búsqueda manual de producto
        String codigo = JOptionPane.showInputDialog(this, 
            "Ingrese el código del producto:", 
            "Buscar Producto", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (codigo != null && !codigo.trim().isEmpty()) {
            buscarYAgregarProducto(codigo.trim());
        }
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        // Botón "Escanear" - activar/desactivar cámara
        activarCamara();
    }

    private void btnEliminarItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (ventaActual.getDetalles().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay productos en el carrito", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear array con nombres de productos para el diálogo
        String[] productos = new String[ventaActual.getDetalles().size()];
        for (int i = 0; i < ventaActual.getDetalles().size(); i++) {
            DetalleVenta detalle = ventaActual.getDetalles().get(i);
            productos[i] = detalle.getCodigoProducto() + " - " + detalle.getNombreProducto();
        }
        
        String seleccion = (String) JOptionPane.showInputDialog(
            this,
            "Seleccione el producto a eliminar:",
            "Eliminar Producto",
            JOptionPane.QUESTION_MESSAGE,
            null,
            productos,
            productos[0]);
        
        if (seleccion != null) {
            // Encontrar el índice del producto seleccionado
            for (int i = 0; i < ventaActual.getDetalles().size(); i++) {
                DetalleVenta detalle = ventaActual.getDetalles().get(i);
                String item = detalle.getCodigoProducto() + " - " + detalle.getNombreProducto();
                
                if (item.equals(seleccion)) {
                    ventaActual.eliminarDetalle(i);
                    actualizarTablaCarrito();
                    actualizarTotales();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Producto eliminado del carrito", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
        }
    }
    
    /**
     * Genera el texto de la boleta para impresión
     */
    private String generarTextoBoleta() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        // Encabezado
        sb.append("================================================\n");
        sb.append("                  NEXOKET                     \n");
        sb.append("              BOLETA DE VENTA                 \n");
        sb.append("================================================\n\n");
        
        // Información de la venta
        sb.append("Número de Boleta: ").append(ventaActual.getNumeroVenta()).append("\n");
        sb.append("Fecha: ").append(ventaActual.getFechaEmision().format(formatter)).append("\n");
        sb.append("Vendedor: ").append(ventaActual.getEmisorBoleta()).append("\n");
        sb.append("Tipo de Pago: ").append(ventaActual.getTipoPago()).append("\n\n");
        
        // Información del cliente
        if (ventaActual.getDniCliente() != null && !ventaActual.getDniCliente().isEmpty()) {
            sb.append("------------------------------------------------\n");
            sb.append("CLIENTE\n");
            sb.append("DNI: ").append(ventaActual.getDniCliente()).append("\n");
            sb.append("Nombre: ").append(ventaActual.getNombreCliente()).append("\n");
            if (ventaActual.getTelefonoCliente() != null && !ventaActual.getTelefonoCliente().isEmpty()) {
                sb.append("Teléfono: ").append(ventaActual.getTelefonoCliente()).append("\n");
            }
            sb.append("\n");
        }
        
        // Detalles de productos
        sb.append("================================================\n");
        sb.append("DESCRIPCIÓN                  CANT   P.U.  TOTAL\n");
        sb.append("================================================\n");
        
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            // Nombre del producto (max 24 caracteres)
            String nombre = detalle.getNombreProducto();
            if (nombre.length() > 24) {
                nombre = nombre.substring(0, 21) + "...";
            }
            
            // Formatear línea
            sb.append(String.format("%-24s %4d %6.2f %7.2f\n",
                nombre,
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()));
        }
        
        // Totales
        sb.append("================================================\n");
        sb.append(String.format("Subtotal:                         S/ %8.2f\n", ventaActual.getSubtotal()));
        sb.append(String.format("IGV (18%%):                        S/ %8.2f\n", ventaActual.getIgv()));
        sb.append(String.format("TOTAL:                            S/ %8.2f\n", ventaActual.getTotal()));
        sb.append("================================================\n\n");
        
        // Información de pago
        sb.append(String.format("Efectivo recibido:                S/ %8.2f\n", ventaActual.getEfectivoRecibido()));
        sb.append(String.format("Vuelto:                           S/ %8.2f\n", ventaActual.getVuelto()));
        sb.append("\n");
        
        // Pie
        sb.append("================================================\n");
        sb.append("        ¡GRACIAS POR SU COMPRA!              \n");
        sb.append("================================================\n");
        
        return sb.toString();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarCliente;
    private javax.swing.JButton btnCancelarVenta;
    private javax.swing.JButton btnEliminarItem;
    private javax.swing.JButton btnGenerarNumeroDeBoleta;
    private javax.swing.JButton btnImprimirBoleta;
    private javax.swing.JButton btnNuevaVenta;
    private javax.swing.JButton btnProcesarVenta;
    private javax.swing.JComboBox<String> cmbTipoPago;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spnCantidad;
    private javax.swing.JTextArea tblCarritoVenta;
    private javax.swing.JTextField txtCodigoEscaneado;
    private javax.swing.JTextField txtDniCliente;
    private javax.swing.JTextField txtEfectivoRecibido;
    private javax.swing.JTextField txtFechaEmision;
    private javax.swing.JTextField txtIGV;
    private javax.swing.JTextField txtNombreCliente;
    private javax.swing.JTextField txtNombreDelVendedor;
    private javax.swing.JTextField txtNombreProducto;
    private javax.swing.JTextField txtNumeroBoleta;
    private javax.swing.JTextField txtPrecioUnitario;
    private javax.swing.JTextField txtStockDisponible;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTelefonoCliente;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtVuelto;
    // End of variables declaration//GEN-END:variables
}
