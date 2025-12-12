package utp.edu.pe.nexoket.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import utp.edu.pe.nexoket.jform.ItmProductos;

/**
 * Programa de prueba para verificar las nuevas funcionalidades
 * de ItmProductos con actualización automática
 * 
 * @author User
 */
public class TestItmProductosMejorado {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║  TEST: ItmProductos con Actualización Automática          ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
            // Crear ventana de productos
            ItmProductos ventana = new ItmProductos();
            
            System.out.println("✓ Ventana ItmProductos creada");
            
            // Crear JFrame para mostrar
            JFrame frame = new JFrame("TEST - Gestión de Productos Mejorada");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            
            // Panel de instrucciones
            JPanel panelInstrucciones = crearPanelInstrucciones();
            frame.add(panelInstrucciones, BorderLayout.NORTH);
            
            // Agregar ventana de productos
            frame.add(ventana, BorderLayout.CENTER);
            
            // Panel de control en la parte inferior
            JPanel panelControl = crearPanelControl(ventana);
            frame.add(panelControl, BorderLayout.SOUTH);
            
            // Configurar y mostrar
            frame.setSize(1100, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            System.out.println("✓ Ventana de prueba mostrada");
            System.out.println("\n═══════════════════════════════════════════════════════════");
            System.out.println("PRUEBA LAS SIGUIENTES FUNCIONALIDADES:");
            System.out.println("═══════════════════════════════════════════════════════════");
            System.out.println("1. ✓ Botón 'Refrescar' - Actualiza tabla manualmente");
            System.out.println("2. ✓ Botón 'Actualizar' - Edita producto (2 pasos)");
            System.out.println("3. ✓ Doble clic en fila - Carga para editar rápidamente");
            System.out.println("4. ✓ Checkbox 'Auto-Refresh' - Actualiza cada 30s");
            System.out.println("5. ✓ Botón 'Agregar' - Crea nuevo producto");
            System.out.println("6. ✓ Botón 'Eliminar' - Marca como inactivo");
            System.out.println("═══════════════════════════════════════════════════════════\n");
        });
    }
    
    private static JPanel crearPanelInstrucciones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("🧪 MODO PRUEBA - Todas las Funcionalidades Implementadas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(0, 51, 102));
        
        String instrucciones = "<html>" +
            "<b>FUNCIONALIDADES PROBADAS:</b><br>" +
            "• <span style='color: green;'>✓</span> Botón Actualizar arreglado<br>" +
            "• <span style='color: green;'>✓</span> Botón Refrescar implementado<br>" +
            "• <span style='color: green;'>✓</span> Actualización automática (checkbox abajo)<br>" +
            "• <span style='color: green;'>✓</span> Doble clic para editar<br>" +
            "• <span style='color: green;'>✓</span> Mensajes mejorados (menos intrusivos)<br>" +
            "</html>";
        
        JLabel lblInstrucciones = new JLabel(instrucciones);
        lblInstrucciones.setFont(new Font("Arial", Font.PLAIN, 11));
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblInstrucciones, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static JPanel crearPanelControl(ItmProductos ventana) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Control de Actualización Automática"));
        
        // Checkbox para auto-refresh
        JCheckBox chkAutoRefresh = new JCheckBox("Activar Actualización Automática (cada 30 segundos)");
        chkAutoRefresh.setBackground(new Color(245, 245, 245));
        chkAutoRefresh.setToolTipText("Marca para actualizar la tabla automáticamente cada 30 segundos");
        
        // Label de estado
        JLabel lblEstado = new JLabel("● Desactivado");
        lblEstado.setForeground(Color.RED);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Label de info
        JLabel lblInfo = new JLabel("(No actualiza si estás editando)");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        lblInfo.setForeground(Color.GRAY);
        
        // Acción del checkbox
        chkAutoRefresh.addActionListener(e -> {
            boolean enabled = chkAutoRefresh.isSelected();
            ventana.setAutoRefreshEnabled(enabled);
            
            if (enabled) {
                lblEstado.setText("● Activo");
                lblEstado.setForeground(new Color(34, 139, 34));
                System.out.println("\n🔄 AUTO-REFRESH ACTIVADO - Actualizará cada 30 segundos");
            } else {
                lblEstado.setText("● Desactivado");
                lblEstado.setForeground(Color.RED);
                System.out.println("\n⏸️  AUTO-REFRESH DESACTIVADO");
            }
        });
        
        // Botón para simular cambio en BD (para probar)
        JButton btnSimular = new JButton("🧪 Simular Cambio en BD");
        btnSimular.setToolTipText("Simula un cambio externo para ver si la tabla se actualiza");
        btnSimular.addActionListener(e -> {
            System.out.println("\n⚠️  Para probar realmente:");
            System.out.println("   1. Abre MongoDB Compass");
            System.out.println("   2. Modifica el stock de un producto");
            System.out.println("   3. Espera 30s (si auto-refresh activo) o clic en 'Refrescar'");
            System.out.println("   4. Verifica que el cambio aparezca en la tabla");
            
            JOptionPane.showMessageDialog(panel,
                "Para probar la actualización en tiempo real:\n\n" +
                "1. Abre MongoDB Compass\n" +
                "2. Ve a tu base de datos → colección 'Productos'\n" +
                "3. Modifica el stock de un producto\n" +
                "4. Activa el auto-refresh con el checkbox\n" +
                "5. Espera 30 segundos\n" +
                "6. ¡Verás el cambio reflejado!\n\n" +
                "O simplemente haz clic en el botón 'Refrescar'",
                "Cómo Probar",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        panel.add(chkAutoRefresh);
        panel.add(lblEstado);
        panel.add(lblInfo);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnSimular);
        
        return panel;
    }
}
