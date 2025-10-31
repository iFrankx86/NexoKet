# 📦 Funcionalidades CRUD - Gestión de Productos

## 🎯 Descripción General

El módulo de gestión de productos (`ItmProductos`) implementa un sistema completo de CRUD (Crear, Leer, Actualizar, Eliminar) con interfaz gráfica Swing y persistencia en MongoDB.

---

## ✨ Funcionalidades Implementadas

### 1️⃣ **CREAR - Botón "Agregar"** (`btnAgregar`)

#### 📝 Descripción
Permite agregar nuevos productos a la base de datos MongoDB con generación automática de códigos por categoría.

#### 🔧 Funcionamiento
1. **Generación Automática de Código**:
   - Al seleccionar una categoría, se genera automáticamente un código único
   - Formato: `[PREFIJO][NÚMERO]` (ej: SNK001, BEB002, ABA003)
   - Prefijos por categoría:
     - Snacks → `SNK`
     - Bebidas → `BEB`
     - Abarrotes → `ABA`

2. **Validaciones Implementadas**:
   - ✅ Código obligatorio (generado automáticamente)
   - ✅ Nombre obligatorio
   - ✅ Precio de venta > 0
   - ✅ Stock >= 0
   - ✅ Validación de números (precio y stock)
   - ✅ Verificación de código duplicado

3. **Campos del Formulario**:
   - **Código**: Generado automáticamente (solo lectura)
   - **Nombre**: Texto obligatorio
   - **Marca**: Texto opcional
   - **Categoría**: ComboBox (Abarrotes, Bebidas, Snacks)
   - **Descripción**: Texto opcional
   - **Unidad/Medida**: Texto opcional (kg, litro, paquete, etc.)
   - **Precio de Compra**: Número opcional
   - **Precio de Venta**: Número obligatorio
   - **Stock**: Número entero obligatorio
   - **Límite de Stock**: Número entero opcional (default: 5)
   - **Ubicación**: Texto opcional
   - **Estado**: ComboBox (Activo, Inactivo)
   - **Aplicar IGV**: ComboBox (Habilitado, Deshabilitado)

4. **Proceso de Agregado**:
   ```
   Usuario llena campos → Validaciones → Guardar en MongoDB → Limpiar campos → Actualizar tabla
   ```

5. **Mensajes al Usuario**:
   - ✅ "Producto agregado exitosamente"
   - ❌ "El código ya existe o hay datos inválidos"
   - ⚠️ Advertencias de validación específicas

---

### 2️⃣ **LEER - Tabla de Productos** (`tblProductos`)

#### 📊 Descripción
Visualiza todos los productos almacenados en MongoDB con columnas informativas.

#### 🔧 Funcionamiento
1. **Carga Automática**:
   - Se cargan todos los productos al abrir la ventana
   - Actualización automática después de cada operación CRUD

2. **Columnas de la Tabla**:
   | Columna | Descripción | Ancho |
   |---------|-------------|-------|
   | Código | Identificador único (SNK001, BEB002) | 80px |
   | Nombre | Nombre del producto | 200px |
   | Marca | Marca del producto | 120px |
   | Categoría | Categoría (Snacks, Bebidas, etc.) | 120px |
   | Stock | Cantidad disponible | 80px |
   | Precio | Precio formateado (S/. XX.XX) | 100px |
   | Estado | Activo/Inactivo | 80px |
   | Ubicación | Ubicación física | 120px |

3. **Interacción con la Tabla**:
   - **Selección de fila**: Al hacer clic en una fila, los datos se cargan automáticamente en los campos
   - **Preparación para editar**: Facilita la actualización de productos
   - **Selección única**: Solo se puede seleccionar un producto a la vez

4. **Actualización de Datos**:
   - Automática después de agregar producto
   - Automática después de actualizar producto
   - Automática después de eliminar producto
   - Manual con el botón "Actualizar"

---

### 3️⃣ **ACTUALIZAR - Botón "Actualizar"** (`btnActualizar`)

#### 🔄 Descripción
Permite modificar los datos de un producto existente y registra la fecha de actualización en MongoDB.

#### 🔧 Funcionamiento
1. **Proceso de Actualización**:
   ```
   Seleccionar producto en tabla → Datos cargan en campos → Modificar campos → Clic en Actualizar → Confirmación → Guardar cambios
   ```

2. **Validaciones Implementadas**:
   - ✅ Debe haber un producto seleccionado
   - ✅ Nombre obligatorio
   - ✅ Precio de venta > 0
   - ✅ Stock >= 0
   - ✅ Validación de números
   - ✅ Confirmación antes de actualizar

3. **Campos Editables**:
   - ✅ Nombre
   - ✅ Marca
   - ✅ Categoría
   - ✅ Descripción
   - ✅ Unidad/Medida
   - ✅ Precio de Venta
   - ✅ Stock
   - ✅ Límite de Stock
   - ✅ Ubicación
   - ✅ Estado (Activo/Inactivo)
   - ❌ Código (NO editable - es la clave primaria)

4. **Registro de Auditoría**:
   - 📅 **Fecha de Actualización**: Se registra automáticamente en MongoDB
   - El campo `fechaActualizacion` se actualiza cada vez que se modifica el producto

5. **Mensajes al Usuario**:
   - 🔔 Confirmación: "¿Está seguro de actualizar el producto [código]?"
   - ✅ "Producto actualizado exitosamente. Fecha de actualización registrada."
   - ⚠️ "Por favor, seleccione un producto de la tabla para actualizar"
   - ❌ "Error al actualizar el producto"

---

### 4️⃣ **ELIMINAR - Botón "Eliminar"** (`btnEliminar`)

#### 🗑️ Descripción
Realiza una **eliminación lógica** del producto, marcándolo como inactivo en lugar de borrarlo físicamente de la base de datos.

#### 🔧 Funcionamiento
1. **Eliminación Lógica**:
   - No se borra el registro de MongoDB
   - Se marca el campo `activo: false`
   - Se actualiza `fechaActualizacion`
   - Mantiene el historial de productos

2. **Proceso de Eliminación**:
   ```
   Seleccionar producto en tabla → Clic en Eliminar → Confirmación con detalles → Marcar como inactivo → Actualizar tabla
   ```

3. **Validaciones Implementadas**:
   - ✅ Debe haber un producto seleccionado
   - ✅ Confirmación obligatoria antes de eliminar
   - ✅ Muestra información del producto a eliminar

4. **Ventana de Confirmación**:
   ```
   ¿Está seguro de eliminar el producto?
   
   Código: SNK003
   Nombre: Doritos Nacho
   
   Esta acción marcará el producto como inactivo.
   ```

5. **Mensajes al Usuario**:
   - ⚠️ "Por favor, seleccione un producto de la tabla para eliminar"
   - 🔔 Confirmación con detalles del producto
   - ✅ "Producto eliminado exitosamente. El producto [código] ha sido marcado como inactivo."
   - ❌ "Error al eliminar el producto"

---

## 🔄 Flujos de Trabajo Completos

### 📝 Agregar Nuevo Producto
```
1. Seleccionar categoría (ej: Snacks)
2. Código se genera automáticamente (SNK004)
3. Llenar nombre: "Papas Lays"
4. Llenar precio: 2.50
5. Llenar stock: 100
6. Llenar otros campos opcionales
7. Clic en "Agregar"
8. ✅ Mensaje de éxito
9. Tabla se actualiza automáticamente
10. Campos se limpian
11. Nuevo código se genera
```

### ✏️ Actualizar Producto Existente
```
1. Hacer clic en una fila de la tabla
2. Datos se cargan automáticamente en los campos
3. Modificar los campos deseados (ej: cambiar precio de 2.50 a 3.00)
4. Clic en "Actualizar"
5. Confirmar actualización
6. ✅ Producto actualizado con fecha registrada
7. Tabla se actualiza automáticamente
8. Campos se limpian
```

### 🗑️ Eliminar Producto
```
1. Hacer clic en una fila de la tabla
2. Clic en "Eliminar"
3. Ventana de confirmación muestra:
   - Código del producto
   - Nombre del producto
   - Advertencia de eliminación lógica
4. Confirmar eliminación
5. ✅ Producto marcado como inactivo
6. Tabla se actualiza (producto desaparece o se muestra como inactivo)
7. Campos se limpian
```

---

## 🎨 Características Adicionales

### 🔄 Auto-carga al Seleccionar
- Al hacer clic en cualquier fila de la tabla, los datos se cargan automáticamente en los campos
- Facilita la edición rápida de productos
- No requiere búsqueda manual

### 🧹 Limpieza Automática
- Después de cada operación exitosa, los campos se limpian automáticamente
- Se genera un nuevo código para el próximo producto
- El foco se coloca en el campo "Nombre" para agilizar el ingreso

### 🔍 Validaciones en Tiempo Real
- Validación de tipos de datos (números, texto)
- Validación de campos obligatorios
- Mensajes descriptivos de error
- Prevención de datos inválidos

### 📊 Actualización Automática de Tabla
- Después de agregar: tabla se actualiza
- Después de modificar: tabla se actualiza
- Después de eliminar: tabla se actualiza
- No requiere refrescar manualmente

---

## 💾 Persistencia en MongoDB

### Estructura del Documento
```javascript
{
  "_id": ObjectId("..."),
  "codigo": "SNK001",
  "nombre": "Doritos Nacho",
  "marca": "Frito Lay",
  "categoria": "Snacks",
  "subcategoria": "",
  "descripcion": "Papas fritas sabor queso nacho",
  "unidadMedida": "Paquete",
  "cantidadPorUnidad": 1,
  "precioCompra": 1.80,
  "precioVenta": 2.50,
  "margenGanancia": 0.70,
  "aplicaIGV": true,
  "stock": 100,
  "stockMinimo": 10,
  "proveedor": "Distribuidora XYZ",
  "ubicacion": "Estante A2",
  "activo": true,
  "fechaCreacion": ISODate("2025-10-22T15:30:00Z"),
  "fechaActualizacion": ISODate("2025-10-22T16:45:00Z")
}
```

### Campos de Auditoría
- **fechaCreacion**: Se establece al crear el producto
- **fechaActualizacion**: Se actualiza en cada modificación
- **activo**: Controla la eliminación lógica

---

## 🎯 Ventajas del Diseño

### ✅ Ventajas de Eliminación Lógica
1. **Preservación de Datos**: No se pierde información histórica
2. **Auditoría**: Se puede rastrear productos eliminados
3. **Reversibilidad**: Posibilidad de reactivar productos
4. **Integridad Referencial**: Mantiene relaciones con otras entidades (ventas, etc.)

### ✅ Validaciones Robustas
1. **Prevención de Errores**: Validación antes de guardar
2. **Mensajes Claros**: El usuario sabe exactamente qué corregir
3. **Datos Consistentes**: Solo se guardan datos válidos

### ✅ Usabilidad
1. **Auto-carga**: Edición rápida sin búsquedas
2. **Auto-limpieza**: Preparado para el siguiente registro
3. **Códigos Automáticos**: No hay duplicados ni errores de tipeo

---

## 🔐 Seguridad y Validación

### Validaciones de Entrada
- ✅ Código único (verificado antes de insertar)
- ✅ Tipos de datos correctos
- ✅ Rangos válidos (precio > 0, stock >= 0)
- ✅ Campos obligatorios no vacíos

### Confirmaciones de Seguridad
- 🔔 Confirmación al actualizar
- 🔔 Confirmación al eliminar
- 🔔 Mensajes informativos de todas las operaciones

---

## 📈 Próximas Mejoras Sugeridas

1. **Búsqueda y Filtros**
   - Buscar por código, nombre, categoría
   - Filtrar por estado (activo/inactivo)
   - Filtrar por stock bajo

2. **Exportación**
   - Exportar tabla a Excel
   - Generar reportes PDF
   - Exportar inventario completo

3. **Gestión Avanzada**
   - Historial de cambios
   - Control de usuarios (quién modificó qué)
   - Importación masiva de productos

4. **Alertas**
   - Notificación de stock bajo
   - Alertas de productos por vencer
   - Notificaciones de productos inactivos

---

**Desarrollado por**: Equipo NexoKet  
**Fecha**: Octubre 2025  
**Tecnología**: Java Swing + MongoDB + Patrón Facade + DAO
