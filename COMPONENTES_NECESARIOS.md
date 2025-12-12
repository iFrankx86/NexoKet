# 📋 COMPONENTES NECESARIOS PARA FUNCIONALIDADES FALTANTES

## 🔍 **1. ESCÁNER DE CÓDIGO DE BARRAS**

### Componentes:
```java
// Panel de escáner
JPanel panelEscaner
JButton btnIniciarEscaner
JButton btnDetenerEscaner
JLabel lblEstadoEscaner
JTextField txtCodigoEscaneado (read-only)

// Vista previa de cámara
JPanel panelVistaPrevia (para mostrar feed de cámara)
JLabel lblImagenCamara
```

### Librerías necesarias:
```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.github.sarxos</groupId>
    <artifactId>webcam-capture</artifactId>
    <version>0.3.12</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.1</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.1</version>
</dependency>
```

---

## 📊 **2. EXPORTACIÓN DE DATOS**

### Componentes:
```java
// Menú o Panel de exportación
JButton btnExportarExcel
JButton btnExportarPDF
JButton btnExportarCSV
JButton btnImprimirLista

// Diálogo de opciones
JDialog dialogoExportacion
JCheckBox chkIncluirInactivos
JCheckBox chkIncluirStockCero
JComboBox<String> cmbFormatoFecha
JRadioButton rbTodosProductos
JRadioButton rbProductosSeleccionados
JRadioButton rbFiltroActual
ButtonGroup grupoSeleccion
```

### Librerías necesarias:
```xml
<!-- Apache POI para Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>

<!-- iText para PDF -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
</dependency>

<!-- OpenCSV para CSV -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.7.1</version>
</dependency>
```

---

## 📥 **3. IMPORTACIÓN MASIVA**

### Componentes:
```java
// Ventana de importación
JInternalFrame frmImportarProductos
JButton btnSeleccionarArchivo
JTextField txtRutaArchivo (read-only)
JButton btnImportar
JProgressBar progressBar
JTextArea txtLogImportacion (read-only, scroll)
JLabel lblEstadoImportacion

// Tabla de vista previa
JTable tblVistaPrevia
JScrollPane scrollVistaPrevia
JButton btnValidarDatos
JLabel lblErrores
JLabel lblAdvertencias
JLabel lblRegistrosValidos
```

### Formato del archivo Excel/CSV:
```
Código | Nombre | Marca | Categoría | Descripción | Precio | Stock | Stock Mínimo | Ubicación | IGV | Estado
```

---

## 🔎 **4. BÚSQUEDA AVANZADA**

### Componentes:
```java
// Panel de búsqueda avanzada
JDialog dialogoBusquedaAvanzada
JTextField txtBusquedaNombre
JTextField txtBusquedaMarca
JComboBox<String> cmbBusquedaCategoria

// Rango de precios
JLabel lblPrecioDesde
JTextField txtPrecioDesde
JLabel lblPrecioHasta
JTextField txtPrecioHasta
JSlider sliderPrecio

// Rango de stock
JLabel lblStockMinimo
JTextField txtStockMinimo
JLabel lblStockMaximo
JTextField txtStockMaximo

// Filtros adicionales
JCheckBox chkStockBajo
JCheckBox chkIGVHabilitado
JCheckBox chkSoloActivos
JCheckBox chkSoloInactivos

// Botones
JButton btnBuscar
JButton btnLimpiarFiltros
JButton btnCerrar
```

---

## 🖼️ **5. GESTIÓN DE IMÁGENES**

### Componentes:
```java
// Panel de imagen
JPanel panelImagen
JLabel lblImagenProducto (para mostrar foto)
JButton btnCargarImagen
JButton btnEliminarImagen
JButton btnVerImagenCompleta
JButton btnTomarFoto

// Vista miniatura
JPanel panelMiniaturas
JScrollPane scrollMiniaturas
JList<ImageIcon> listMiniaturas

// Diálogo de imagen completa
JDialog dialogoImagenCompleta
JLabel lblImagenGrande
JButton btnRotarIzquierda
JButton btnRotarDerecha
JButton btnZoomMas
JButton btnZoomMenos
```

### Librerías necesarias:
```xml
<!-- Para redimensionar imágenes -->
<dependency>
    <groupId>org.imgscalr</groupId>
    <artifactId>imgscalr-lib</artifactId>
    <version>4.2</version>
</dependency>
```

---

## 🔔 **6. ALERTAS Y NOTIFICACIONES**

### Componentes:
```java
// Panel de alertas (en ventana principal)
JPanel panelAlertas
JLabel lblNumeroAlertas
JButton btnVerAlertas
JList<String> listaAlertas
JScrollPane scrollAlertas

// Tipos de alertas con iconos
JLabel lblIconoAlertaCritica (🔴)
JLabel lblIconoAlertaAdvertencia (🟡)
JLabel lblIconoAlertaInfo (🔵)

// Diálogo de alertas
JDialog dialogoAlertas
JTable tblAlertas (columnas: Tipo, Producto, Mensaje, Fecha)
JButton btnActualizarAlertas
JButton btnMarcarVistas
JComboBox<String> cmbFiltroAlertas

// Configuración de alertas
JCheckBox chkAlertarStockBajo
JSpinner spinUmbralStockBajo
JCheckBox chkAlertarVencimiento
JSpinner spinDiasVencimiento
```

---

## 📝 **7. HISTORIAL DE CAMBIOS (AUDITORÍA)**

### Componentes:
```java
// Ventana de historial
JInternalFrame frmHistorial
JTable tblHistorial
JScrollPane scrollHistorial

// Columnas de la tabla:
// - Fecha/Hora
// - Usuario
// - Acción (Crear/Modificar/Eliminar)
// - Producto
// - Campo Modificado
// - Valor Anterior
// - Valor Nuevo

// Filtros
JDateChooser dateDesde
JDateChooser dateHasta
JComboBox<String> cmbUsuario
JComboBox<String> cmbAccion
JTextField txtBuscarProducto
JButton btnFiltrar
JButton btnLimpiar
JButton btnExportarHistorial
```

### Librería necesaria:
```xml
<!-- JCalendar para JDateChooser -->
<dependency>
    <groupId>com.toedter</groupId>
    <artifactId>jcalendar</artifactId>
    <version>1.4</version>
</dependency>
```

---

## 📦 **8. GESTIÓN DE LOTES Y VENCIMIENTOS**

### Componentes:
```java
// Panel de lotes
JDialog dialogoGestionLotes
JTable tblLotes
JScrollPane scrollLotes

// Columnas:
// - Número de Lote
// - Fecha de Fabricación
// - Fecha de Vencimiento
// - Cantidad
// - Estado

// Agregar lote
JTextField txtNumeroLote
JDateChooser dateFabricacion
JDateChooser dateVencimiento
JSpinner spinCantidadLote
JButton btnAgregarLote

// Alertas de vencimiento
JLabel lblProductosVencidos
JLabel lblProductosPorVencer
JComboBox<String> cmbDiasVencimiento (7, 15, 30, 60 días)
```

---

## 🏷️ **9. CATEGORÍAS DINÁMICAS**

### Componentes:
```java
// Ventana de gestión de categorías
JInternalFrame frmCategorias
JTree treeCategorias (árbol jerárquico)
JTextField txtNombreCategoria
JTextField txtDescripcionCategoria
JColorChooser colorCategoria
JButton btnAgregarCategoria
JButton btnEditarCategoria
JButton btnEliminarCategoria

// Subcategorías
JButton btnAgregarSubcategoria
JList<String> listaSubcategorias
```

---

## 👤 **10. GESTIÓN DE PROVEEDORES INTEGRADA**

### Componentes:
```java
// Ventana de proveedores
JInternalFrame frmProveedores
JTable tblProveedores
JScrollPane scrollProveedores

// Datos del proveedor
JTextField txtRUC
JTextField txtRazonSocial
JTextField txtNombreContacto
JTextField txtTelefono
JTextField txtEmail
JTextArea txtDireccion
JComboBox<String> cmbTipoProveedor

// En el formulario de producto
JComboBox<String> cmbProveedor (dinámico)
JButton btnAgregarProveedorRapido
JButton btnVerDetalleProveedor
```

---

## 💰 **11. MÚLTIPLES PRECIOS**

### Componentes:
```java
// Panel de precios
JPanel panelPrecios
JLabel lblPrecioMinorista
JTextField txtPrecioMinorista
JLabel lblPrecioMayorista
JTextField txtPrecioMayorista
JLabel lblPrecioEspecial
JTextField txtPrecioEspecial

// Descuentos por cantidad
JTable tblDescuentos
// Columnas: Cantidad Mínima | Porcentaje | Precio Final

JButton btnAgregarDescuento
JButton btnEliminarDescuento
JSpinner spinCantidadMin
JSpinner spinPorcentajeDesc
```

---

## 📈 **12. ESTADÍSTICAS Y DASHBOARD**

### Componentes:
```java
// Panel principal del dashboard
JPanel panelDashboard
JTabbedPane tabbedPaneDashboard

// Tarjetas de resumen
JPanel panelTotalProductos
JLabel lblTotalProductos
JLabel lblValorTotal

JPanel panelStockBajo
JLabel lblProductosStockBajo
JButton btnVerProductosStockBajo

JPanel panelProductosActivos
JLabel lblProductosActivos
JLabel lblProductosInactivos

// Gráficos (usando JFreeChart)
ChartPanel panelGraficoVentas
ChartPanel panelGraficoInventario
ChartPanel panelGraficoCategorias

// Tablas de top productos
JTable tblProductosMasVendidos
JTable tblProductosMenosStock
JTable tblProductosSinMovimiento
```

### Librería necesaria:
```xml
<!-- JFreeChart para gráficos -->
<dependency>
    <groupId>org.jfree</groupId>
    <artifactId>jfreechart</artifactId>
    <version>1.5.4</version>
</dependency>
```

---

## 🏷️ **13. CÓDIGOS DE BARRAS GENERADOS**

### Componentes:
```java
// Panel de código de barras
JPanel panelCodigoBarras
JLabel lblCodigoBarrasImagen
JComboBox<String> cmbTipoCodigoBarras (EAN-13, Code 128, QR)
JButton btnGenerarCodigo
JButton btnImprimirEtiqueta
JButton btnImprimirMasivo

// Configuración de etiqueta
JSpinner spinAnchoEtiqueta
JSpinner spinAltoEtiqueta
JCheckBox chkIncluirNombre
JCheckBox chkIncluirPrecio
JCheckBox chkIncluirCodigo
JSpinner spinCantidadEtiquetas
```

### Librería necesaria:
```xml
<!-- Barcode4J para códigos de barras -->
<dependency>
    <groupId>net.sf.barcode4j</groupId>
    <artifactId>barcode4j</artifactId>
    <version>2.1</version>
</dependency>
```

---

## 🔐 **14. BACKUP Y RESTAURACIÓN**

### Componentes:
```java
// Ventana de backup
JInternalFrame frmBackup
JButton btnCrearBackup
JButton btnRestaurarBackup
JButton btnProgramarBackupAutomatico

// Lista de backups
JTable tblBackups
// Columnas: Fecha | Hora | Tamaño | Registros | Estado

JButton btnEliminarBackup
JButton btnVerDetalles

// Configuración automática
JCheckBox chkBackupAutomatico
JComboBox<String> cmbFrecuencia (Diario, Semanal, Mensual)
JSpinner spinHoraBackup
JTextField txtRutaBackup
JButton btnSeleccionarRuta
JProgressBar progressBackup
```

---

## 🔄 **COMPONENTES COMUNES A TODOS**

### Componentes generales que se repiten:
```java
// Botones de acción estándar
JButton btnGuardar
JButton btnCancelar
JButton btnAceptar
JButton btnCerrar
JButton btnAyuda

// Validación
JLabel lblMensajeError
JLabel lblMensajeExito
JProgressBar progressOperacion

// Navegación
JButton btnPrimero
JButton btnAnterior
JButton btnSiguiente
JButton btnUltimo
JLabel lblRegistroActual

// Búsqueda rápida
JTextField txtBusquedaRapida
JButton btnBuscar
JButton btnLimpiarBusqueda
```

---

## 🎨 **TIPS DE DISEÑO**

### Paleta de colores recomendada:
```java
Color AZUL_PRIMARIO = new Color(52, 152, 219);
Color VERDE_EXITO = new Color(46, 204, 113);
Color ROJO_PELIGRO = new Color(231, 76, 60);
Color AMARILLO_ADVERTENCIA = new Color(241, 196, 15);
Color GRIS_FONDO = new Color(236, 240, 241);
```

### Fuentes:
```java
Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 16);
Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 12);
Font FUENTE_PEQUEÑA = new Font("Segoe UI", Font.PLAIN, 10);
```

### Iconos recomendados:
- FontAwesome Icons (vía FlatLaf)
- Material Design Icons
- Custom SVG icons

---

## 📦 **DEPENDENCIAS COMPLETAS (pom.xml)**

```xml
<dependencies>
    <!-- MongoDB -->
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>4.11.1</version>
    </dependency>
    
    <!-- Apache POI (Excel) -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.3</version>
    </dependency>
    
    <!-- iText (PDF) -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itext7-core</artifactId>
        <version>7.2.5</version>
    </dependency>
    
    <!-- OpenCSV -->
    <dependency>
        <groupId>com.opencsv</groupId>
        <artifactId>opencsv</artifactId>
        <version>5.7.1</version>
    </dependency>
    
    <!-- JCalendar (DateChooser) -->
    <dependency>
        <groupId>com.toedter</groupId>
        <artifactId>jcalendar</artifactId>
        <version>1.4</version>
    </dependency>
    
    <!-- Webcam Capture -->
    <dependency>
        <groupId>com.github.sarxos</groupId>
        <artifactId>webcam-capture</artifactId>
        <version>0.3.12</version>
    </dependency>
    
    <!-- ZXing (Barcode) -->
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
        <version>3.5.1</version>
    </dependency>
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>javase</artifactId>
        <version>3.5.1</version>
    </dependency>
    
    <!-- Barcode4J -->
    <dependency>
        <groupId>net.sf.barcode4j</groupId>
        <artifactId>barcode4j</artifactId>
        <version>2.1</version>
    </dependency>
    
    <!-- JFreeChart (Gráficos) -->
    <dependency>
        <groupId>org.jfree</groupId>
        <artifactId>jfreechart</artifactId>
        <version>1.5.4</version>
    </dependency>
    
    <!-- ImgScalr (Redimensionar imágenes) -->
    <dependency>
        <groupId>org.imgscalr</groupId>
        <artifactId>imgscalr-lib</artifactId>
        <version>4.2</version>
    </dependency>
    
    <!-- FlatLaf (Look and Feel moderno) -->
    <dependency>
        <groupId>com.formdev</groupId>
        <artifactId>flatlaf</artifactId>
        <version>3.2.5</version>
    </dependency>
</dependencies>
```

---

## 🎯 **PRIORIDAD DE IMPLEMENTACIÓN**

1. **URGENTE** (Implementar esta semana):
   - Exportación a Excel ✅
   - Dashboard básico ✅
   - Alertas de stock bajo ✅

2. **IMPORTANTE** (Próximo mes):
   - Escáner de código de barras ✅
   - Importación masiva ✅
   - Búsqueda avanzada ✅

3. **MEJORAS** (Cuando haya tiempo):
   - Todo lo demás ✅
