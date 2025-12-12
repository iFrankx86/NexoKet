# 🔍 DEBUG: Problema con IGV y Estado en ComboBox

## 📋 Resumen del Problema

Los valores seleccionados en los ComboBox de **IGV** y **Estado** no se están guardando correctamente al actualizar un producto. Después de actualizar y refrescar, los valores vuelven a su estado anterior.

## ✅ Implementación Actual (Código Revisado)

### 1. **Lectura de ComboBox (ItmProductos.java líneas 809-830)**
```java
// Obtener estado activo/inactivo y IGV con validación robusta
String estadoSeleccionado = (String) cmbEstadoDisponibilidad.getSelectedItem();
String igvSeleccionado = (String) cmbIGV.getSelectedItem();

// Validar que los valores no sean nulos
if (estadoSeleccionado == null || igvSeleccionado == null) {
    JOptionPane.showMessageDialog(this, 
        "Error: Estado o IGV no seleccionado correctamente", 
        "Error de Validación", 
        JOptionPane.ERROR_MESSAGE);
    return;
}

boolean activo = estadoSeleccionado.trim().equals("Activo");
boolean aplicaIGV = igvSeleccionado.trim().equals("Habilitado");

// Logging detallado para debugging
System.out.println("======= VALIDACIÓN ANTES DE ACTUALIZAR =======");
System.out.println("Estado ComboBox: '" + estadoSeleccionado + "'");
System.out.println("IGV ComboBox: '" + igvSeleccionado + "'");
System.out.println("Estado boolean: " + activo);
System.out.println("IGV boolean: " + aplicaIGV);
System.out.println("=============================================");
```

**Estado: ✅ CORRECTO** - Lee los ComboBox, valida null, aplica trim(), y convierte a boolean.

---

### 2. **Llamada al Facade (ItmProductos.java líneas 862-880)**
```java
boolean exito = productoFacade.actualizarProducto(
    codigo, nombre, marca, categoria,
    "", // subcategoría
    unidadMedida,
    1, // cantidadPorUnidad
    aplicaIGV,      // ← Parámetro 8
    descripcion,
    precioVenta,
    stock,
    stockMinimo,
    "", // proveedor
    null, // fechaVencimiento
    ubicacion,
    activo          // ← Parámetro 16
);
```

**Estado: ✅ CORRECTO** - Pasa los valores boolean en el orden correcto según la interfaz.

---

### 3. **ProductoFacade (ProductoFacade.java líneas 72-99)**
```java
public boolean actualizarProducto(..., boolean aplicaIGV, ..., boolean activo) {
    System.out.println("=== FACADE: Actualizando producto " + codigo + " ===");
    System.out.println("FACADE: aplicaIGV recibido = " + aplicaIGV);
    System.out.println("FACADE: activo recibido = " + activo);
    
    Producto producto = new Producto(...);
    producto.setAplicaIGV(aplicaIGV);
    producto.setActivo(activo);
    
    System.out.println("FACADE: Producto creado con aplicaIGV = " + producto.isAplicaIGV());
    System.out.println("FACADE: Producto creado con activo = " + producto.isActivo());
    
    productoDAO.actualizarProducto(codigo, producto);
    return true;
}
```

**Estado: ✅ CORRECTO** - Recibe parámetros, crea objeto Producto, y llama al DAO.

---

### 4. **ProductoDAO (ProductoDAO.java líneas 202-245) - ACTUALIZADO**
```java
public void actualizarProducto(String codigo, Producto productoActualizado) {
    System.out.println("=== DAO: Actualizando producto " + codigo + " ===");
    System.out.println("DAO: aplicaIGV = " + productoActualizado.isAplicaIGV());
    System.out.println("DAO: activo = " + productoActualizado.isActivo());
    
    Document query = new Document("codigo", codigo);
    Document nuevosDatos = new Document(...)
            .append("aplicaIGV", productoActualizado.isAplicaIGV())
            .append("activo", productoActualizado.isActivo())
            .append("fechaActualizacion", new Date());
    
    System.out.println("DAO: Documento ANTES de enviar a MongoDB:");
    System.out.println("  - aplicaIGV en documento: " + nuevosDatos.get("aplicaIGV"));
    System.out.println("  - activo en documento: " + nuevosDatos.get("activo"));
    System.out.println("DAO: JSON completo: " + nuevosDatos.toJson());
    
    collection.updateOne(query, new Document("$set", nuevosDatos));
    System.out.println("DAO: updateOne() ejecutado");
    
    // ⭐ VERIFICACIÓN INMEDIATA
    Document docVerificacion = collection.find(query).first();
    if (docVerificacion != null) {
        System.out.println("DAO VERIFICACIÓN: Documento DESPUÉS de actualizar en MongoDB:");
        System.out.println("  - aplicaIGV en MongoDB: " + docVerificacion.get("aplicaIGV"));
        System.out.println("  - activo en MongoDB: " + docVerificacion.get("activo"));
    }
    
    System.out.println("DAO: Actualización completada");
}
```

**Estado: ✅ MEJORADO** - Ahora incluye verificación inmediata post-MongoDB.

---

### 5. **Lectura desde MongoDB (ProductoDAO.java líneas 319-423)**
```java
private Producto documentToProducto(Document doc) {
    // ...
    Boolean aplicaIGV = doc.getBoolean("aplicaIGV");
    if (aplicaIGV == null) {
        Object igvObj = doc.get("aplicaIGV");
        if (igvObj instanceof String) {
            String igvStr = ((String) igvObj).trim().toLowerCase();
            aplicaIGV = !(igvStr.equals("deshabilitado") || igvStr.equals("false") || igvStr.equals("0"));
        } else {
            aplicaIGV = true;
        }
    }
    
    Boolean activo = doc.getBoolean("activo");
    if (activo == null) {
        Object actObj = doc.get("activo");
        if (actObj instanceof String) {
            String actStr = ((String) actObj).trim().toLowerCase();
            activo = !(actStr.equals("inactivo") || actStr.equals("false") || actStr.equals("0"));
        } else {
            activo = true;
        }
    }
    
    producto.setActivo(activo);
    producto.setAplicaIGV(aplicaIGV);
}
```

**Estado: ✅ CORRECTO** - Compatible con boolean y String (legacy).

---

### 6. **Refresco de Tabla (ItmProductos.java líneas 145-207) - ACTUALIZADO**
```java
private void refrescarTablaSilencioso() {
    // ...
    System.out.println("========== REFRESCO DE TABLA - INICIO ==========");
    for (Producto producto : productos) {
        Object[] fila = new Object[9];
        // ...
        fila[6] = producto.isAplicaIGV() ? "Habilitado" : "Deshabilitado";
        fila[7] = producto.isActivo() ? "Activo" : "Inactivo";
        // ...
        
        // Logging detallado para debugging
        System.out.println("TABLA: Producto " + producto.getCodigo() + 
                         " - isAplicaIGV()=" + producto.isAplicaIGV() + 
                         " - Columna[6]=" + fila[6]);
        System.out.println("TABLA: Producto " + producto.getCodigo() + 
                         " - isActivo()=" + producto.isActivo() + 
                         " - Columna[7]=" + fila[7]);
        
        modeloTabla.addRow(fila);
    }
    System.out.println("========== REFRESCO DE TABLA - FIN ==========");
}
```

**Estado: ✅ MEJORADO** - Ahora incluye logging detallado en cada fila.

---

## 🧪 PRUEBA DETALLADA PASO A PASO

### **Paso 1: Ejecutar la aplicación**
Compila y ejecuta el proyecto en NetBeans.

### **Paso 2: Abrir la ventana de Productos**
Navega a la pantalla "Registrar Producto" (ItmProductos).

### **Paso 3: Seleccionar un producto**
1. Selecciona un producto en la tabla (por ejemplo, **SNK007 - Cañonazo**)
2. Observa los valores actuales en la tabla:
   - **IGV:** Habilitado
   - **Estado:** Activo

### **Paso 4: Hacer click en "Actualizar" (Primera vez)**
Esto cargará los datos del producto en los campos del formulario.

### **Paso 5: Cambiar los ComboBox**
1. **Cambiar "Aplicar IGV"** de "Habilitado" a **"Deshabilitado"**
2. **Cambiar "Estado"** de "Activo" a **"Inactivo"**

### **Paso 6: Hacer click en "Actualizar" (Segunda vez)**
Esto guardará los cambios. Observa la consola.

### **Paso 7: Analizar el log de consola**

Deberías ver esta secuencia de logs:

```
======= VALIDACIÓN ANTES DE ACTUALIZAR =======
Estado ComboBox: 'Inactivo'
IGV ComboBox: 'Deshabilitado'
Estado boolean: false
IGV boolean: false
=============================================

======= ACTUALIZANDO PRODUCTO =======
Código: SNK007
IGV Aplicado: false (ComboBox: Deshabilitado)
Estado Activo: false (ComboBox: Inactivo)
=====================================

=== FACADE: Actualizando producto SNK007 ===
FACADE: aplicaIGV recibido = false
FACADE: activo recibido = false
FACADE: Producto creado con aplicaIGV = false
FACADE: Producto creado con activo = false

=== DAO: Actualizando producto SNK007 ===
DAO: aplicaIGV = false
DAO: activo = false
DAO: Documento ANTES de enviar a MongoDB:
  - aplicaIGV en documento: false
  - activo en documento: false
DAO: JSON completo: {..., "aplicaIGV": false, "activo": false, ...}
DAO: updateOne() ejecutado
DAO VERIFICACIÓN: Documento DESPUÉS de actualizar en MongoDB:
  - aplicaIGV en MongoDB: false
  - activo en MongoDB: false
DAO: Actualización completada

✓ Actualización exitosa en facade

========== REFRESCO DE TABLA - INICIO ==========
TABLA: Producto SNK007 - isAplicaIGV()=false - Columna[6]=Deshabilitado
TABLA: Producto SNK007 - isActivo()=false - Columna[7]=Inactivo
========== REFRESCO DE TABLA - FIN ==========

======= VERIFICACIÓN POST-ACTUALIZACIÓN =======
Código: SNK007
IGV en BD: false
Estado en BD: false
Tabla muestra IGV: Deshabilitado
Tabla muestra Estado: Inactivo
=============================================
```

### **Paso 8: Verificar la tabla visual**
La tabla debe mostrar:
- **IGV:** Deshabilitado
- **Estado:** Inactivo

### **Paso 9: Click en "Refrescar"**
Vuelve a hacer click en el botón "Refres..." para forzar un refresco.

Observa si los valores persisten o se revierten.

---

## 🔎 DIAGNÓSTICO POR SÍNTOMAS

### **Síntoma A: Los logs muestran valores correctos PERO la tabla muestra valores incorrectos**
**Diagnóstico:** Problema en la visualización de la tabla.
**Solución:** Revisar el método `refrescarTablaSilencioso()` y verificar las columnas.

---

### **Síntoma B: Los logs del DAO muestran valores CORRECTOS ANTES de MongoDB PERO INCORRECTOS DESPUÉS**
**Diagnóstico:** MongoDB no está guardando correctamente los valores boolean.
**Solución:** 
1. Verificar la conexión a MongoDB
2. Verificar permisos de escritura
3. Verificar que no haya triggers o validaciones en la base de datos

---

### **Síntoma C: Los logs del FACADE muestran valores INCORRECTOS**
**Diagnóstico:** El problema está en la lectura de los ComboBox o en el paso de parámetros.
**Solución:** Revisar la lectura de ComboBox en ItmProductos.java líneas 809-830.

---

### **Síntoma D: Los logs de VALIDACIÓN muestran valores CORRECTOS pero el FACADE recibe valores INCORRECTOS**
**Diagnóstico:** Error en el orden de los parámetros al llamar al facade.
**Solución:** Verificar el orden de parámetros en la llamada (líneas 862-880).

---

### **Síntoma E: La VERIFICACIÓN INMEDIATA en DAO muestra valores correctos PERO al refrescar la tabla los valores son incorrectos**
**Diagnóstico:** MongoDB está guardando correctamente, pero el método `documentToProducto()` no está leyendo correctamente.
**Solución:** Revisar ProductoDAO.java líneas 319-423.

---

### **Síntoma F: TODO el logging muestra valores correctos pero la tabla visual está incorrecta**
**Diagnóstico:** Problema con el índice de las columnas en la tabla o con el TableModel.
**Solución:** 
1. Verificar que la tabla tiene 9 columnas (no 8)
2. Verificar que IGV está en la columna 6 y Estado en la columna 7

---

## 📊 VERIFICACIÓN EN MongoDB DIRECTO

Si todos los logs muestran valores correctos pero la tabla no, verifica directamente en MongoDB:

```javascript
// En MongoDB Compass o mongosh
use NexoKet
db.productos.findOne({codigo: "SNK007"})
```

Deberías ver:
```json
{
  "codigo": "SNK007",
  "nombre": "Cañonazo",
  "aplicaIGV": false,
  "activo": false,
  ...
}
```

---

## 🚨 POSIBLES CAUSAS RAÍZ

1. **ComboBox devuelve null o whitespace**
   - ✅ SOLUCIONADO: Validación + trim() agregado

2. **Parámetros en orden incorrecto**
   - ✅ VERIFICADO: Orden correcto según interfaz

3. **MongoDB no persiste los valores**
   - ⏳ EN PRUEBA: Verificación inmediata agregada en DAO

4. **Tabla no se refresca correctamente**
   - ✅ MEJORADO: Logging agregado en refresco

5. **Índices de columna incorrectos**
   - ✅ VERIFICADO: IGV en columna 6, Estado en columna 7

6. **Legacy data (strings en lugar de boolean)**
   - ✅ MANEJADO: documentToProducto() tiene fallback

---

## 📝 PRÓXIMOS PASOS

1. **Ejecutar la prueba detallada** y copiar TODA la salida de consola
2. **Tomar screenshot** de la tabla ANTES y DESPUÉS de actualizar
3. **Verificar en MongoDB** directamente el documento
4. **Reportar** qué síntoma (A-F) coincide con tu caso

---

## 💡 CONSEJOS DE DEBUG ADICIONALES

- **Limpiar y reconstruir** el proyecto: `Clean and Build` en NetBeans
- **Reiniciar MongoDB** si está en local
- **Verificar firewall** si MongoDB está en remoto
- **Revisar permisos** de escritura en la colección
- **Probar con UN SOLO cambio** (solo IGV o solo Estado) para aislar el problema

---

**Generado:** 2025-01-12  
**Versión:** 2.0 - Con verificación inmediata en DAO y logging completo en tabla
