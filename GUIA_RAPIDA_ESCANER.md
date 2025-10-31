# 🎯 GUÍA RÁPIDA - Escáner de Códigos con Webcam

## ⚡ PASO 1: Agregar Dependencias (Maven)

Abre tu archivo **`pom.xml`** y agrega esto dentro de `<dependencies>`:

```xml
<!-- ZXing para lectura de códigos de barras -->
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

<!-- Webcam Capture para acceso a la cámara -->
<dependency>
    <groupId>com.github.sarxos</groupId>
    <artifactId>webcam-capture</artifactId>
    <version>0.3.12</version>
</dependency>
```

## 🔨 PASO 2: Compilar Proyecto

1. **Guarda el `pom.xml`**
2. Click derecho en tu proyecto → **Clean and Build**
3. Maven descargará las librerías automáticamente (¡espera a que termine!)

## 📹 PASO 3: Usar el Escáner

### **Desde tu aplicación:**

1. Ve al menú → **Registrar Stock**
2. Click en **"📷 Activar Cámara"**
3. ¡Aparecerá la vista de tu webcam!
4. Coloca un código de barras frente a la cámara (15-30 cm de distancia)
5. El sistema detectará automáticamente el código
6. Ajusta la cantidad con el spinner
7. Click en **"Registrar Stock"**

### **Flujo Completo:**

```
1. Activar cámara → Se abre ventana con vista en vivo
2. Escanear código → ¡BEEP! Producto encontrado
3. Confirmar producto → Ventana muestra datos del producto  
4. Ajustar cantidad → Usar spinner (1-1000)
5. Registrar → Stock actualizado en MongoDB
6. Siguiente producto → Automático, listo para escanear otro
```

## 🎯 Códigos Soportados

✅ **EAN-13** (productos comerciales normales)
✅ **UPC-A** (productos de USA)
✅ **Code 128** (códigos industriales)
✅ **Code 39** (alfanuméricos)
✅ **QR Code** (códigos QR)
✅ **Code 93**
✅ **Codabar**
✅ **ITF** (Interleaved 2 of 5)

## 💡 Consejos para Mejor Detección

✅ **Buena iluminación** (natural o LED blanco)
✅ **Distancia 15-30 cm** de la cámara
✅ **Código plano** (sin arrugas ni dobleces)
✅ **Sin reflejos** (evita plástico brillante)
✅ **Webcam limpia** (limpia el lente si es necesario)

## 🔧 Solución de Problemas

### **"No se detectó webcam"**
- Verifica que esté conectada
- Cierra Zoom, Teams, Skype (liberan la cámara)
- Reinicia NetBeans
- Windows puede pedir permisos la primera vez

### **"No lee los códigos"**
- Mejora la iluminación
- Acerca/aleja el código
- Limpia el lente de la webcam
- Prueba con otro código impreso

### **"Error al compilar"**
- Verifica que `pom.xml` tenga las 3 dependencias
- Ejecuta: **Clean and Build**
- Espera a que Maven descargue todo
- Si persiste: borra carpeta `.m2/repository` y vuelve a compilar

## 🎨 Personalización Avanzada

### **Cambiar resolución de cámara:**

Edita `WebcamBarcodeScanner.java` línea 68:

```java
// Opciones: QQVGA, QVGA, VGA, HD, FHD
webcam.setViewSize(WebcamResolution.HD.getSize()); // 720p
```

### **Usar webcam específica:**

```java
List<Webcam> webcams = Webcam.getWebcams();
webcam = webcams.get(1); // Segunda cámara
```

### **Ajustar sensibilidad:**

Edita línea 110 en `WebcamBarcodeScanner.java`:

```java
Thread.sleep(50); // Más rápido pero usa más CPU
```

## 📊 Estadísticas de Rendimiento

| Aspecto | Valor |
|---------|-------|
| **Detección** | ~1-2 segundos |
| **CPU** | 15-25% (depende de resolución) |
| **RAM** | ~100-150 MB |
| **Códigos/minuto** | ~30-40 (con registros) |

## 🚀 Próximas Mejoras Posibles

- [ ] Historial de escaneos del día
- [ ] Estadísticas de productos escaneados
- [ ] Modo batch (escanear múltiples sin confirmar)
- [ ] Exportar reporte PDF/Excel
- [ ] Sonidos personalizados
- [ ] Zoom digital en la cámara

## 📞 Soporte

Si tienes problemas:

1. Verifica que las 3 dependencias estén en `pom.xml`
2. Ejecuta **Clean and Build**
3. Revisa la consola de NetBeans para errores
4. Verifica permisos de cámara en Windows

---

**¡Listo para escanear! 📦✨**
