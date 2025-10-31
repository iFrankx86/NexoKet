# 🛒 Sistema de Ventas NexoKet - Guía de Uso

## ✅ Implementación Completada

### 📦 **Componentes del Sistema**

#### **1. Modelos de Datos**
- ✅ `Venta.java` - Modelo principal de transacciones
- ✅ `DetalleVenta.java` - Ítems individuales de cada venta
- ✅ `Cliente.java` - Información de clientes
- ✅ `Producto.java` - Catálogo de productos

#### **2. Capa de Acceso a Datos (DAO)**
- ✅ `VentaDAO.java` - CRUD de ventas en MongoDB
- ✅ `ProductoDAO.java` - Gestión de productos (con `buscarPorCodigo()`)
- ✅ `ClienteDAO.java` - Gestión de clientes

#### **3. Capa de Lógica de Negocio (Facade)**
- ✅ `VentaFacade.java` - Orquestación de operaciones de venta
  - Validación de stock
  - Reducción automática de inventario
  - Cálculo de totales e IGV (18%)
  - Procesamiento completo de ventas

#### **4. Interfaz Gráfica**
- ✅ `ItmRegistrarVenta.java` - Formulario principal de ventas

---

## 🚀 **Cómo Usar el Sistema de Ventas**

### **Paso 1: Abrir el Módulo de Ventas**
1. En el menú principal, haz clic en **Menú → Registrar Venta**
2. Se abrirá el formulario `ItmRegistrarVenta`

### **Paso 2: Datos Generales (Automáticos)**
- **Número de Boleta**: Se genera automáticamente al abrir
- **Fecha de Emisión**: Se establece automáticamente (fecha/hora actual)
- **Tipo de Pago**: Selecciona **Efectivo**, **Tarjeta** o **Yape**
- **Emisor de la Boleta**: Ingresa el nombre del vendedor

### **Paso 3: Agregar Productos al Carrito**

#### **Opción A: Escanear con Cámara (Recomendado)** 📷
1. Haz clic en el botón **"Escanear"**
2. La cámara se activará automáticamente
3. Enfoca el código de barras del producto
4. El producto se agregará automáticamente al carrito
5. Repite para cada producto
6. Haz clic en **"Detener"** para apagar la cámara

#### **Opción B: Búsqueda Manual** 🔍
1. Haz clic en el botón **"Buscar"**
2. Ingresa el código del producto en el cuadro de diálogo
3. El producto se agregará al carrito
4. Se mostrará: Nombre, Precio y Stock disponible

#### **Opción C: Código Manual**
1. Escribe el código en el campo **"Codigo de Barras"**
2. Presiona **Enter**
3. El producto se agregará automáticamente

### **Paso 4: Datos del Cliente (Opcional)**
1. Ingresa el **DNI** del cliente
2. Haz clic en **"Buscar"** para autocompletar sus datos
3. Si no existe, ingresa manualmente:
   - Nombre
   - Teléfono

### **Paso 5: Revisar el Carrito**
- El carrito muestra:
  - Código del producto
  - Nombre del producto
  - Precio unitario
  - Cantidad
  - Subtotal
- Para **eliminar un producto**, haz clic en **"Eliminar"** y selecciona el ítem

### **Paso 6: Verificar Totales**
El sistema calcula automáticamente:
- **Subtotal**: Suma de todos los productos
- **IGV (18%)**: Impuesto General a las Ventas
- **Total**: Subtotal + IGV

### **Paso 7: Procesar el Pago**

#### **Si es Pago en Efectivo:**
1. Ingresa el monto en **"Efectivo Recibido"**
2. El **Vuelto** se calcula automáticamente
3. Valida que el efectivo sea mayor o igual al total

#### **Si es Tarjeta/Yape:**
- No es necesario ingresar efectivo recibido

### **Paso 8: Procesar la Venta**
1. Haz clic en **"Procesar Venta"**
2. Confirma los datos en el cuadro de diálogo:
   - Total
   - Efectivo recibido
   - Vuelto
3. Haz clic en **"Sí"** para confirmar

### **Paso 9: Resultado**
✅ **Si la venta es exitosa:**
- Se muestra un mensaje con el número de venta
- El stock de los productos se reduce automáticamente
- La venta se guarda en MongoDB (colección `Ventas`)
- Se habilita el botón **"Imprimir Boleta"** (en desarrollo)

❌ **Si hay un error:**
- Se muestra un mensaje de error
- La venta NO se procesa
- El stock NO se modifica

### **Paso 10: Opciones Posteriores**

#### **Imprimir Boleta** 🖨️
- Haz clic en **"Imprimir Boleta"**
- *(Funcionalidad de PDF en desarrollo - Prioridad 3)*

#### **Nueva Venta** 🆕
- Haz clic en **"Nueva Venta"**
- Se limpiará el formulario
- Se generará un nuevo número de venta

#### **Cancelar Venta** ❌
- Haz clic en **"Cancelar Venta"**
- Confirma la acción
- Se reiniciará el formulario

---

## 🔧 **Características Técnicas**

### **Validaciones Implementadas**
✅ Verifica que hay productos en el carrito  
✅ Valida que el vendedor esté registrado  
✅ Valida stock disponible antes de agregar productos  
✅ Valida efectivo suficiente en pagos en efectivo  
✅ Evita agregar productos sin stock  
✅ Cálculo automático de IGV (18%)  

### **Operaciones Automáticas**
✅ Reducción de stock al procesar venta  
✅ Generación de número de venta correlativo (V000001, V000002...)  
✅ Cálculo automático de totales  
✅ Cálculo automático de vuelto  
✅ Detección de productos duplicados en carrito (suma cantidades)  

### **Base de Datos MongoDB**
```javascript
// Colección: Ventas
{
  "numeroVenta": "V000001",
  "fechaEmision": ISODate("2025-10-30T10:30:00Z"),
  "emisorBoleta": "Juan Pérez",
  "tipoPago": "Efectivo",
  "efectivoRecibido": 100.00,
  "vuelto": 15.50,
  "estado": "Completada",
  "dniCliente": "12345678",
  "nombreCliente": "María García",
  "telefonoCliente": "987654321",
  "detalles": [
    {
      "codigoProducto": "PROD001",
      "nombreProducto": "Coca Cola 500ml",
      "categoria": "Bebidas",
      "precioUnitario": 3.50,
      "cantidad": 2,
      "subtotal": 7.00
    }
  ],
  "subtotal": 71.61,
  "igv": 12.89,
  "total": 84.50
}
```

---

## 📊 **Estructura del Carrito**

| Columna | Descripción |
|---------|-------------|
| **Código** | Código del producto (ejemplo: PROD001) |
| **Producto** | Nombre completo del producto |
| **Precio** | Precio unitario (S/ 3.50) |
| **Cantidad** | Cantidad de unidades |
| **Subtotal** | Precio × Cantidad |

---

## 🎯 **Flujo de Trabajo Completo**

```
1. Usuario abre ItmRegistrarVenta
   ↓
2. Sistema genera número de venta automático
   ↓
3. Usuario escanea productos con cámara
   ↓
4. VentaFacade.agregarProductoAVenta()
   ├─ Valida stock disponible
   ├─ Agrega al carrito (o aumenta cantidad si ya existe)
   └─ Calcula subtotal del producto
   ↓
5. Sistema actualiza tabla del carrito
   ↓
6. Sistema calcula totales automáticamente
   ├─ Subtotal = Σ(subtotales)
   ├─ IGV = Subtotal × 0.18
   └─ Total = Subtotal + IGV
   ↓
7. Usuario ingresa efectivo recibido
   ↓
8. Sistema calcula vuelto = Efectivo - Total
   ↓
9. Usuario hace clic en "Procesar Venta"
   ↓
10. VentaFacade.procesarVenta()
    ├─ Valida todos los detalles
    ├─ Valida stock de TODOS los productos
    ├─ Reduce stock en ProductoDAO
    ├─ Guarda venta en VentaDAO (MongoDB)
    └─ Retorna éxito/error
    ↓
11. Sistema muestra mensaje de confirmación
    ↓
12. Stock actualizado en MongoDB (colección Productos)
    └─ Venta guardada en MongoDB (colección Ventas)
```

---

## ⚠️ **Mensajes de Error Comunes**

| Error | Causa | Solución |
|-------|-------|----------|
| "Agregue al menos un producto a la venta" | Carrito vacío | Escanea o busca productos |
| "Stock insuficiente" | No hay inventario | Verifica stock en ItmProductos |
| "Producto no encontrado" | Código inválido | Verifica el código en la base de datos |
| "El efectivo recibido es menor al total" | Pago insuficiente | Ingresa un monto mayor o igual al total |
| "Error al activar la cámara" | No hay webcam conectada | Conecta una cámara o usa búsqueda manual |

---

## 🔮 **Funcionalidades Pendientes (Prioridad 3)**

### **PDF y Reportes**
- [ ] Generar PDF de boleta con iText/JasperReports
- [ ] Diseño de plantilla de boleta
- [ ] Impresión directa a impresora térmica
- [ ] Envío de boleta por correo electrónico

### **Historial de Ventas**
- [ ] Formulario ItmHistorialVentas
- [ ] Filtros por fecha, cliente, vendedor
- [ ] Exportar a Excel/PDF
- [ ] Gráficos de ventas diarias/mensuales

### **Reportes Avanzados**
- [ ] Productos más vendidos
- [ ] Ventas por categoría
- [ ] Ventas por vendedor
- [ ] Dashboard con estadísticas

---

## 🎉 **¡Sistema Listo para Usar!**

El módulo de ventas está **100% funcional** con:
- ✅ Scanner de códigos de barras con webcam
- ✅ Validación completa de stock
- ✅ Cálculo automático de totales e IGV
- ✅ Reducción automática de inventario
- ✅ Persistencia en MongoDB
- ✅ Interfaz intuitiva y fácil de usar

**Para probarlo:**
1. Abre el menú principal
2. Ve a **Menú → Registrar Venta**
3. Escanea productos con la cámara
4. ¡Procesa tu primera venta! 🚀
