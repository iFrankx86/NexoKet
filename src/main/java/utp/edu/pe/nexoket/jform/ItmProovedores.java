/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package utp.edu.pe.nexoket.jform;

import utp.edu.pe.nexoket.Facade.ProveedorFacade;
import utp.edu.pe.nexoket.modelo.Proveedor;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.List;

/**
 *
 * @author User
 */
public class ItmProovedores extends javax.swing.JInternalFrame {

    // Variables de instancia
    private ProveedorFacade proveedorFacade;
    private DefaultTableModel modeloTabla;
    private String rucSeleccionado = null; // Para saber si estamos editando
    
    /**
     * Creates new form ItmProovedores
     */
    public ItmProovedores() {
        initComponents();
        inicializarComponentes();
    }
    
    /**
     * Inicializa los componentes personalizados
     */
    private void inicializarComponentes() {
        proveedorFacade = new ProveedorFacade();
        configurarTabla();
        cargarProveedores();
        agregarListenerFiltro();
        agregarListenerSeleccionTabla();
    }
    
    /**
     * Configura el modelo de la tabla
     */
    private void configurarTabla() {
        String[] columnas = {"RUC", "Razón Social", "Teléfono", "Email", "Dirección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        tblProovedores.setModel(modeloTabla);
        
        // Configurar anchos de columnas
        tblProovedores.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblProovedores.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblProovedores.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblProovedores.getColumnModel().getColumn(3).setPreferredWidth(200);
        tblProovedores.getColumnModel().getColumn(4).setPreferredWidth(250);
    }
    
    /**
     * Carga todos los proveedores en la tabla
     */
    private void cargarProveedores() {
        List<Proveedor> proveedores = proveedorFacade.obtenerTodos();
        actualizarTabla(proveedores);
    }
    
    /**
     * Actualiza la tabla con la lista de proveedores
     */
    private void actualizarTabla(List<Proveedor> proveedores) {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        for (Proveedor p : proveedores) {
            Object[] fila = {
                p.getRuc(),
                p.getRazonSocial(),
                p.getTelefono() != null ? p.getTelefono() : "",
                p.getEmail() != null ? p.getEmail() : "",
                p.getDireccion() != null ? p.getDireccion() : ""
            };
            modeloTabla.addRow(fila);
        }
    }
    
    /**
     * Agrega listener para el filtro en tiempo real
     */
    private void agregarListenerFiltro() {
        txtFiltroRucNombre.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarFiltro();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarFiltro();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarFiltro();
            }
        });
    }
    
    /**
     * Aplica el filtro de búsqueda
     */
    private void aplicarFiltro() {
        String filtro = txtFiltroRucNombre.getText().trim();
        List<Proveedor> proveedores = proveedorFacade.buscarPorRucONombre(filtro);
        actualizarTabla(proveedores);
    }
    
    /**
     * Agrega listener para selección en la tabla
     */
    private void agregarListenerSeleccionTabla() {
        tblProovedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarProveedorSeleccionado();
            }
        });
    }
    
    /**
     * Carga los datos del proveedor seleccionado en los campos
     */
    private void cargarProveedorSeleccionado() {
        int filaSeleccionada = tblProovedores.getSelectedRow();
        if (filaSeleccionada >= 0) {
            rucSeleccionado = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
            txtRUC.setText(rucSeleccionado);
            txtRazonSocial.setText(modeloTabla.getValueAt(filaSeleccionada, 1).toString());
            txtTelefono.setText(modeloTabla.getValueAt(filaSeleccionada, 2).toString());
            txtEmail.setText(modeloTabla.getValueAt(filaSeleccionada, 3).toString());
            txtDireccion.setText(modeloTabla.getValueAt(filaSeleccionada, 4).toString());
            
            txtRUC.setEditable(false); // RUC no se puede editar
            btnAgregar.setText("Guardar");
        }
    }
    
    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        txtRUC.setText("");
        txtRazonSocial.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        
        txtRUC.setEditable(true);
        rucSeleccionado = null;
        btnAgregar.setText("Agregar");
        tblProovedores.clearSelection();
    }
    
    /**
     * Valida los campos del formulario
     */
    private boolean validarCampos() {
        // Validar RUC
        String ruc = txtRUC.getText().trim();
        if (ruc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El RUC es obligatorio", "Validación", JOptionPane.WARNING_MESSAGE);
            txtRUC.requestFocus();
            return false;
        }
        if (!ruc.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "El RUC debe tener exactamente 11 dígitos", "Validación", JOptionPane.WARNING_MESSAGE);
            txtRUC.requestFocus();
            return false;
        }
        
        // Validar Razón Social
        String razonSocial = txtRazonSocial.getText().trim();
        if (razonSocial.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La Razón Social es obligatoria", "Validación", JOptionPane.WARNING_MESSAGE);
            txtRazonSocial.requestFocus();
            return false;
        }
        
        // Validar Email si no está vacío
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "El email no tiene un formato válido", "Validación", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Crea un objeto Proveedor desde los campos del formulario
     */
    private Proveedor obtenerProveedorDesdeFormulario() {
        Proveedor proveedor = new Proveedor();
        proveedor.setRuc(txtRUC.getText().trim());
        proveedor.setRazonSocial(txtRazonSocial.getText().trim());
        proveedor.setTelefono(txtTelefono.getText().trim());
        proveedor.setEmail(txtEmail.getText().trim());
        proveedor.setDireccion(txtDireccion.getText().trim());
        return proveedor;
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
        tblProovedores = new javax.swing.JTable();
        btnEditar = new javax.swing.JButton();
        btnAgregar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        txtRUC = new javax.swing.JTextField();
        txtRazonSocial = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtFiltroRucNombre = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnLimpiarTXT = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Proveedores");

        tblProovedores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "RUC", "Razon Social", "Telefono", "Email", "Direccion"
            }
        ));
        jScrollPane1.setViewportView(tblProovedores);

        btnEditar.setText("Editar");

        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");

        txtRUC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRUCActionPerformed(evt);
            }
        });

        jLabel1.setText("RUC");

        jLabel2.setText("Razon Social");

        jLabel3.setText("Telefono");

        jLabel4.setText("Email");

        jLabel5.setText("Direccion");

        jLabel6.setText("Filtrar");

        btnLimpiarTXT.setText("Limpiar");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 951, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtRUC, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgregar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFiltroRucNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnEditar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLimpiarTXT)
                                .addGap(12, 12, 12)
                                .addComponent(btnEliminar))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRUC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtRazonSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnAgregar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFiltroRucNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(btnEditar)
                    .addComponent(btnLimpiarTXT)
                    .addComponent(btnEliminar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        if (!validarCampos()) {
            return;
        }
        
        Proveedor proveedor = obtenerProveedorDesdeFormulario();
        
        boolean exito;
        if (rucSeleccionado != null) {
            // Estamos editando
            exito = proveedorFacade.actualizarProveedor(rucSeleccionado, proveedor);
            if (exito) {
                JOptionPane.showMessageDialog(this, 
                    "Proveedor actualizado correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al actualizar el proveedor", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Estamos agregando
            exito = proveedorFacade.registrarProveedor(proveedor);
            if (exito) {
                JOptionPane.showMessageDialog(this, 
                    "Proveedor registrado correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al registrar el proveedor. Puede que el RUC ya exista.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        if (exito) {
            limpiarCampos();
            cargarProveedores();
        }
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void txtRUCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRUCActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRUCActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = tblProovedores.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un proveedor de la tabla", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        // El listener de selección ya cargó los datos, solo necesitamos
        // enfocar el primer campo para que el usuario pueda editar
        txtRazonSocial.requestFocus();
    }
    
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = tblProovedores.getSelectedRow();
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar un proveedor de la tabla", 
                "Advertencia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ruc = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
        String razonSocial = modeloTabla.getValueAt(filaSeleccionada, 1).toString();
        
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar el proveedor?\n\n" +
            "RUC: " + ruc + "\n" +
            "Razón Social: " + razonSocial + "\n\n" +
            "Esta acción no se puede deshacer.", 
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean exito = proveedorFacade.eliminarProveedor(ruc);
            if (exito) {
                JOptionPane.showMessageDialog(this, 
                    "Proveedor eliminado correctamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarProveedores();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar el proveedor", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void btnLimpiarTXTActionPerformed(java.awt.event.ActionEvent evt) {
        limpiarCampos();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiarTXT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblProovedores;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFiltroRucNombre;
    private javax.swing.JTextField txtRUC;
    private javax.swing.JTextField txtRazonSocial;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
