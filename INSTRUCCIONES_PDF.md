# 📄 Instrucciones para Implementar Generación de Boletas en PDF

## ✅ Lo que se ha implementado

1. **Clase `GeneradorBoletaPDF.java`** - Utilidad para generar PDFs profesionales
2. **Método modificado `btnImprimirBoletaActionPerformed`** - Ahora genera PDF en lugar de solo mostrar texto

## 🔧 Configuración Necesaria

### Paso 1: Agregar la dependencia iText

Necesitas agregar la librería **iText 5** a tu proyecto. Hay dos formas de hacerlo:

#### Opción A: Usando Maven (Recomendado)

Si tu proyecto usa Maven, agrega esto a tu `pom.xml`:

```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>
```

#### Opción B: Descarga manual del JAR

1. Descarga iText 5.5.13.3 desde:
   - Maven Central: https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.13.3/
   - O directamente: https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.13.3/itextpdf-5.5.13.3.jar

2. En NetBeans:
   - Clic derecho en tu proyecto → **Properties**
   - Selecciona **Libraries**
   - Clic en **Add JAR/Folder**
   - Selecciona el archivo `itextpdf-5.5.13.3.jar` descargado
   - Clic en **OK**

### Paso 2: Verificar la instalación

Una vez agregada la dependencia:

1. Limpia y compila el proyecto:
   - En NetBeans: **Run** → **Clean and Build Project**
   - O presiona `Shift + F11`

2. Los errores de compilación en `GeneradorBoletaPDF.java` e `ItmRegistrarVenta.java` deberían desaparecer

## 🎨 Características del PDF generado

El PDF incluye:

- ✅ **Encabezado profesional** con nombre de la empresa y título
- ✅ **Información de la venta**: número de boleta, fecha, vendedor, tipo de pago
- ✅ **Datos del cliente**: DNI, nombre, teléfono (si están disponibles)
- ✅ **Tabla de productos**: con código, descripción, cantidad, precio unitario y subtotal
- ✅ **Totales**: subtotal, IGV (18%), total
- ✅ **Información de pago**: efectivo recibido y vuelto
- ✅ **Diseño limpio y profesional** con colores y formato

## 📱 Cómo usar la nueva funcionalidad

1. **Procesa una venta** normalmente en el formulario
2. Haz clic en el botón **"Imprimir Boleta"**
3. Se abrirá un diálogo para **seleccionar dónde guardar el PDF**
4. El nombre sugerido es: `Boleta_[NumeroVenta].pdf`
5. Una vez guardado, se te preguntará si deseas **abrir el PDF automáticamente**

## 🔍 Solución de problemas

### Error: "package com.itextpdf.text does not exist"
- **Solución**: No se ha agregado la dependencia iText. Sigue el Paso 1.

### Error al generar el PDF
- Verifica que tienes **permisos de escritura** en la carpeta donde intentas guardar
- Asegúrate de que no haya otro programa con el archivo abierto

### El PDF no se abre automáticamente
- Es normal en algunos sistemas
- Navega manualmente a la ubicación donde guardaste el PDF

## 📋 Archivos modificados/creados

```
NexoKet/
├── src/main/java/utp/edu/pe/nexoket/
│   ├── util/
│   │   └── GeneradorBoletaPDF.java (NUEVO)
│   └── jform/
│       └── ItmRegistrarVenta.java (MODIFICADO)
```

## 🎯 Próximos pasos opcionales

Si quieres mejorar aún más la boleta:

1. **Agregar logo**: Puedes agregar el logo de NexoKet al PDF
2. **Código QR**: Generar un código QR con información de la venta
3. **Envío por email**: Implementar envío automático del PDF por correo
4. **Impresión directa**: Agregar opción para enviar directamente a la impresora

---

**Nota**: Esta implementación usa **iText 5** que es gratuito para uso comercial. Si necesitas funcionalidades más avanzadas, considera iText 7, pero requiere una licencia comercial para uso empresarial.
