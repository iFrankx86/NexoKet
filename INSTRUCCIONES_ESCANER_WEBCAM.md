# 📷 Escáner de Códigos de Barras con Webcam - Instrucciones

## 📦 Dependencias Necesarias (Maven)

Agrega estas dependencias a tu archivo `pom.xml`:

```xml
<!-- ZXing: Librería para lectura de códigos de barras y QR -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.2</version>
</dependency>

<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.2</version>
</dependency>

<!-- Webcam Capture: Para acceder a la webcam -->
<dependency>
    <groupId>com.github.sarxos</groupId>
    <artifactId>webcam-capture</artifactId>
    <version>0.3.12</version>
</dependency>
```

## 🚀 Pasos para Agregar las Dependencias

### **Opción 1: Si usas Maven (recomendado)**

1. Abre el archivo `pom.xml` en la raíz de tu proyecto
2. Busca la sección `<dependencies>`
3. Agrega las 3 dependencias mostradas arriba
4. Guarda el archivo
5. Click derecho en el proyecto → **Build with Dependencies** o **Clean and Build**
6. Maven descargará automáticamente las librerías

### **Opción 2: Si NO usas Maven (descarga manual)**

1. Descarga los JARs manualmente:
   - **ZXing Core**: https://repo1.maven.org/maven2/com/google/zxing/core/3.5.2/core-3.5.2.jar
   - **ZXing JavaSE**: https://repo1.maven.org/maven2/com/google/zxing/javase/3.5.2/javase-3.5.2.jar
   - **Webcam Capture**: https://repo1.maven.org/maven2/com/github/sarxos/webcam-capture/0.3.12/webcam-capture-0.3.12.jar
   - **Bridj** (dependencia de webcam): https://repo1.maven.org/maven2/com/nativelibs4java/bridj/0.7.0/bridj-0.7.0.jar
   - **SLF4J API**: https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar

2. En NetBeans:
   - Click derecho en tu proyecto → **Properties**
   - **Libraries** → **Add JAR/Folder**
   - Selecciona todos los JARs descargados
   - Click **OK**

## 🎯 Funcionalidades Implementadas

✅ **Escáner con Webcam en Tiempo Real**
- Abre la webcam automáticamente
- Detecta códigos de barras: EAN-13, UPC-A, Code 128, Code 39, etc.
- Detecta códigos QR
- Vista previa en vivo de la cámara

✅ **Búsqueda Automática de Productos**
- Al detectar un código, busca automáticamente en MongoDB
- Muestra información del producto encontrado
- Alerta si el código no está registrado

✅ **Registro de Stock**
- Selecciona cantidad a agregar con spinner
- Actualiza stock en MongoDB
- Registra fecha de actualización
- Muestra confirmación con stock anterior y nuevo

✅ **Interfaz Amigable**
- Botones grandes y claros
- Vista previa de la cámara
- Indicadores visuales de estado
- Mensajes informativos

## 🎮 Cómo Usar

1. **Abrir el formulario**: Menu → Registrar Stock
2. **Activar cámara**: Click en botón "📷 Activar Cámara"
3. **Escanear código**: Coloca el código de barras frente a la webcam
4. **Ajustar cantidad**: Usa el spinner para la cantidad
5. **Registrar**: Click en "✓ Registrar Stock"

## 🔧 Solución de Problemas

### **Error: No se detecta la webcam**
- Verifica que la webcam esté conectada
- Cierra otras aplicaciones que usen la cámara (Zoom, Skype, etc.)
- Reinicia NetBeans

### **Error: No se leen los códigos**
- Asegúrate de tener buena iluminación
- Mantén el código a 15-30 cm de la cámara
- El código debe estar enfocado y sin reflejos

### **Error: Dependencias no encontradas**
- Verifica que las dependencias estén en `pom.xml`
- Ejecuta: **Clean and Build**
- Si persiste, descarga manualmente los JARs

## 📝 Notas Importantes

- La primera vez que abras la cámara, Windows puede pedir permisos
- La detección puede tardar 1-2 segundos
- Funciona con códigos impresos y en pantallas (menos recomendado)
- Si tienes múltiples webcams, se usará la predeterminada

## 🎨 Tipos de Códigos Soportados

✅ EAN-13 (códigos de productos comerciales)
✅ UPC-A (códigos de productos USA)
✅ Code 128 (códigos industriales)
✅ Code 39 (códigos alfanuméricos)
✅ QR Code (códigos QR)
✅ DataMatrix
✅ PDF417

## 🚀 Próximas Mejoras Posibles

- [ ] Seleccionar webcam específica si hay múltiples
- [ ] Ajustar resolución de la cámara
- [ ] Guardar historial de escaneos
- [ ] Modo batch (escanear múltiples productos)
- [ ] Exportar reporte de stock registrado
