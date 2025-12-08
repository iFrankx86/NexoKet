# Sistema de Monitoreo de Aplicaciones - NexoKet

## 📋 Descripción

Sistema completo de monitoreo para aplicaciones Java en producción que implementa las mejores prácticas:

- ✅ **Sistema de Logs** con rotación automática y archivos separados
- ✅ **Monitoreo de Rendimiento** automático y periódico
- ✅ **Alertas de Salud del Sistema** inteligentes
- ✅ **Indicador Visual** discreto en la interfaz gráfica
- ✅ **Métricas en Tiempo Real** (memoria, hilos, CPU)

## 🏗️ Arquitectura

### Componentes Implementados

1. **MonitorRendimiento.java** (`util/`)
   - Monitor singleton con scheduler automático
   - Recolección de métricas cada 5 minutos
   - Alertas inteligentes con umbrales configurables
   - Detección automática de estados: NORMAL, ADVERTENCIA, CRÍTICO

2. **logback.xml** (`src/main/resources/`)
   - Configuración profesional de logs con Logback
   - Archivos separados: general, errores, rendimiento
   - Rotación diaria automática con compresión
   - Retención configurable (90 días)

3. **ItmHistorialVentas.java** (actualizado)
   - Logs detallados de todas las operaciones
   - Indicador visual del estado del sistema
   - Métricas de rendimiento en tiempo real
   - Monitoreo de tiempos de carga

4. **TestMonitoreo.java** (`test/`)
   - Suite de pruebas completa
   - Verificación de logs y métricas
   - Simulación de carga del sistema

## 📦 Dependencias

Agregar en `pom.xml`:

```xml
<dependencies>
    <!-- SLF4J API -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.9</version>
    </dependency>
    
    <!-- Logback Classic (implementación) -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.11</version>
    </dependency>
</dependencies>
```

## 🚀 Cómo Usar

### 1. Probar el Sistema de Monitoreo

Ejecutar la clase de prueba:

```bash
java utp.edu.pe.nexoket.test.TestMonitoreo
```

Esto verificará:
- ✓ Logs funcionando correctamente
- ✓ Métricas del sistema
- ✓ Alertas de salud
- ✓ Monitoreo continuo

### 2. Ver Logs en Tiempo Real

Los logs se guardan automáticamente en:

```
logs/
├── nexoket.log          # Logs generales
├── error.log            # Solo errores
├── performance.log      # Métricas de rendimiento
└── archive/             # Histórico comprimido
    ├── nexoket-2025-12-08.log.gz
    ├── error-2025-12-08.log.gz
    └── performance-2025-12-08.log.gz
```

### 3. Usar en Otras Ventanas

Para agregar monitoreo a cualquier `JInternalFrame`:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utp.edu.pe.nexoket.util.MonitorRendimiento;

public class TuVentana extends JInternalFrame {
    private static final Logger logger = LoggerFactory.getLogger(TuVentana.class);
    private MonitorRendimiento monitor;
    
    public TuVentana() {
        logger.info("Iniciando TuVentana");
        long inicio = System.currentTimeMillis();
        
        try {
            // Tu código aquí
            
            monitor = MonitorRendimiento.getInstancia();
            monitor.verificarSaludSistema();
            
            logger.info("TuVentana cargada en {} ms", 
                System.currentTimeMillis() - inicio);
                
        } catch (Exception e) {
            logger.error("Error al inicializar TuVentana", e);
        }
    }
    
    private void metodoImportante() {
        logger.info("Ejecutando operación importante");
        
        try {
            // Tu lógica aquí
            logger.info("✓ Operación completada");
        } catch (Exception e) {
            logger.error("✗ Error en operación", e);
        }
    }
}
```

## 📊 Indicador Visual

La ventana `ItmHistorialVentas` muestra un indicador discreto:

- 🟢 **Verde**: Sistema normal
- 🟡 **Amarillo**: Advertencia (memoria > 75%)
- 🔴 **Rojo**: Crítico (memoria > 90%)

**Hacer click en el indicador** para ver métricas detalladas.

## ⚙️ Configuración

### Ajustar Umbrales de Alerta

En `MonitorRendimiento.java`:

```java
private static final double UMBRAL_MEMORIA_CRITICO = 90.0;  // %
private static final double UMBRAL_MEMORIA_ALTO = 75.0;     // %
private static final int UMBRAL_HILOS_ALTO = 50;
```

### Cambiar Frecuencia de Monitoreo

En `MonitorRendimiento.java` (constructor):

```java
scheduler.scheduleAtFixedRate(
    this::monitoreoAutomatico,
    1,      // Delay inicial (minutos)
    5,      // Período (minutos) <- CAMBIAR AQUÍ
    TimeUnit.MINUTES
);
```

### Ajustar Nivel de Logs

En `logback.xml`:

```xml
<root level="INFO">  <!-- Cambiar a DEBUG para más detalle -->
    <appender-ref ref="FILE" />
    <appender-ref ref="ERROR_FILE" />
</root>
```

Niveles disponibles: `TRACE` < `DEBUG` < `INFO` < `WARN` < `ERROR`

### Producción vs Desarrollo

**Para PRODUCCIÓN**: Comentar el appender de consola en `logback.xml`:

```xml
<root level="INFO">
    <appender-ref ref="FILE" />
    <appender-ref ref="ERROR_FILE" />
    <!-- <appender-ref ref="CONSOLE" /> -->  ← Comentar esta línea
</root>
```

**Para DESARROLLO**: Mantener el appender de consola activo.

## 📈 Métricas Monitoreadas

### Memoria
- Memoria usada (MB)
- Memoria máxima (MB)
- Porcentaje de uso
- Memoria libre

### Hilos
- Hilos activos
- Hilos daemon
- Pico de hilos
- Total de hilos iniciados

### Sistema
- Número de procesadores
- Carga del sistema
- Tiempo de ejecución
- Estado general (NORMAL/ADVERTENCIA/CRÍTICO)

## 🔧 Solución de Problemas

### Los logs no se generan

1. Verificar que existe el directorio `logs/`
2. Verificar dependencias en `pom.xml`
3. Verificar que `logback.xml` está en `src/main/resources/`

### El monitor no funciona

1. Ejecutar `TestMonitoreo` para diagnosticar
2. Revisar logs de error en `logs/error.log`
3. Verificar que la instancia se obtiene correctamente:
   ```java
   MonitorRendimiento monitor = MonitorRendimiento.getInstancia();
   ```

### Logs muy grandes

1. Ajustar retención en `logback.xml`:
   ```xml
   <maxHistory>30</maxHistory>  <!-- Reducir días -->
   <totalSizeCap>500MB</totalSizeCap>  <!-- Reducir tamaño -->
   ```

2. Cambiar nivel de log a `WARN` en producción

## 📝 Buenas Prácticas

### Niveles de Log Apropiados

```java
// TRACE: Información muy detallada (desarrollo)
logger.trace("Entrando al método calcular con valor: {}", valor);

// DEBUG: Información de depuración
logger.debug("Aplicando filtros - Estado: {}, Fecha: {}", estado, fecha);

// INFO: Información general de operaciones
logger.info("✓ Ventas cargadas: {} registros en {} ms", total, tiempo);

// WARN: Advertencias que no son errores
logger.warn("⚠️ Rendimiento: Carga lenta ({} ms)", tiempo);

// ERROR: Errores que requieren atención
logger.error("✗ Error al cargar ventas", exception);
```

### Nombrar Loggers

```java
// ✓ BIEN - Logger por clase
private static final Logger logger = LoggerFactory.getLogger(MiClase.class);

// ✗ MAL - Logger genérico
private static final Logger logger = LoggerFactory.getLogger("app");
```

### Estructurar Mensajes

```java
// ✓ BIEN - Usar placeholders
logger.info("Usuario {} realizó operación {} en {} ms", user, op, time);

// ✗ MAL - Concatenación de strings
logger.info("Usuario " + user + " realizó operación " + op + " en " + time + " ms");
```

## 🎯 Ventajas de esta Implementación

| Característica | Beneficio |
|----------------|-----------|
| **Logs en archivos** | Trazabilidad completa sin afectar rendimiento |
| **Rotación automática** | No llena el disco, mantiene histórico |
| **Logs separados** | Fácil análisis por tipo (errores, rendimiento) |
| **Monitoreo automático** | Detecta problemas sin intervención manual |
| **UI discreta** | No molesta al usuario, disponible cuando se necesita |
| **Alertas inteligentes** | Solo notifica cambios importantes, evita spam |
| **Thread-safe** | Singleton seguro para ambientes multi-hilo |
| **Sin impacto visual** | Pequeño indicador no distrae |
| **Análisis offline** | Logs pueden revisarse después en cualquier momento |

## 📚 Referencias

- [SLF4J Documentation](http://www.slf4j.org/)
- [Logback Manual](https://logback.qos.ch/manual/)
- [Java Management Extensions (JMX)](https://docs.oracle.com/javase/tutorial/jmx/)

---

**Autor**: NexoKet Team  
**Versión**: 1.0  
**Fecha**: Diciembre 2025
