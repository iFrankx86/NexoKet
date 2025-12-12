# 🏪 NexoKet - Sistema de Gestión de Bodega

[![Java](https://img.shields.io/badge/Java-22-orange.svg)](https://www.oracle.com/java/)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green.svg)](https://www.mongodb.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-UTP-red.svg)](https://www.utp.edu.pe/)

Sistema integral de gestión de inventario y ventas para bodegas, desarrollado como proyecto académico de la Universidad Tecnológica del Perú (UTP) 2025.

---

## 📋 Descripción del Proyecto

**NexoKet** es un sistema de gestión de bodega completo que permite administrar productos, clientes, proveedores, ventas y generar reportes. El sistema cuenta con características avanzadas como:

- 🔐 **Autenticación segura** con encriptación de contraseñas (BCrypt)
- 📦 **Gestión de inventario** con control de stock y alertas de stock mínimo
- 🛒 **Sistema de ventas** con generación automática de boletas en PDF
- 👥 **Administración de clientes** y proveedores
- 📊 **Reportes y estadísticas** de ventas e inventario
- 💾 **Backups automáticos** de la base de datos
- 🔍 **Monitoreo de rendimiento** del sistema
- 🧹 **Mantenimiento automático** de datos

---

## 🚀 Tecnologías Utilizadas

### Backend
- **Java 22** - Lenguaje de programación principal
- **MongoDB Atlas** - Base de datos NoSQL en la nube
- **Maven 3.9+** - Gestor de dependencias y construcción

### Frameworks y Librerías
- **Swing/AWT** - Interfaz gráfica de usuario
- **SLF4J + Logback** - Sistema de logging
- **MongoDB Java Driver 4.9.1** - Conexión con MongoDB
- **iText 5.5.13.3** - Generación de PDF (boletas de venta)
- **jBCrypt 0.4** - Encriptación de contraseñas
- **ZXing** - Lectura de códigos de barras
- **Webcam Capture** - Captura de imágenes de cámara

### Arquitectura
- **Patrón DAO** (Data Access Object)
- **Patrón Facade** para lógica de negocio
- **Patrón Singleton** para servicios
- **MVC** (Model-View-Controller)

---

## 📁 Estructura del Proyecto

```
NexoKet/
├── src/main/java/utp/edu/pe/nexoket/
│   ├── config/              # Gestión de configuración
│   ├── dao/                 # Data Access Objects
│   ├── db/                  # Conexión a MongoDB
│   ├── Facade/              # Capa de lógica de negocio
│   │   └── INexoKet/        # Interfaces
│   ├── jform/               # Ventanas de la interfaz gráfica
│   ├── modelo/              # Modelos de datos
│   ├── security/            # Servicios de seguridad
│   ├── test/                # Clases de prueba
│   └── util/                # Utilidades (Backup, PDF, etc.)
├── src/main/resources/
│   └── application.properties  # Configuración de la aplicación
├── target/
│   └── reports/apidocs/     # Documentación Javadoc
├── backups/                 # Backups automáticos de BD
├── pom.xml                  # Configuración Maven
└── README.md
```

---

## 🛠️ Requisitos del Sistema

### Software Necesario
- **JDK 22** o superior
- **Apache Maven 3.9+** o superior
- **MongoDB Atlas** (cuenta gratuita) o MongoDB local
- **IDE**: Apache NetBeans 25+ o Visual Studio Code con extensiones de Java

### Requisitos de Hardware
- **RAM**: Mínimo 4GB (recomendado 8GB)
- **Disco**: 500MB de espacio libre
- **Conexión a Internet**: Necesaria para MongoDB Atlas

---

## ⚙️ Instalación y Configuración

### 1️⃣ Clonar o Descargar el Proyecto

```bash
git clone https://github.com/tu-usuario/nexoket.git
cd nexoket
```

O descarga el ZIP y extráelo en tu directorio de proyectos.

### 2️⃣ Configurar MongoDB

#### Opción A: MongoDB Atlas (Recomendado)
1. Crea una cuenta gratuita en [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Crea un cluster gratuito
3. Crea un usuario de base de datos
4. Obtén tu Connection String (URI)
5. Agrega tu IP a la lista blanca (Network Access)

#### Opción B: MongoDB Local
```bash
# Instalar MongoDB Community Server
# Iniciar servicio
mongod --dbpath C:\data\db
```

### 3️⃣ Configurar Variables de Entorno

Edita el archivo `src/main/resources/application.properties`:

```properties
# MongoDB Atlas
mongodb.uri=mongodb+srv://usuario:contraseña@cluster.mongodb.net/?retryWrites=true&w=majority
mongodb.database=NexoKet

# O MongoDB Local
# mongodb.uri=mongodb://localhost:27017
# mongodb.database=NexoKet

# Backup Configuration
backup.enabled=true
backup.interval.hours=24
backup.retention.days=7
backup.path=./backups
```

**Importante:** Nunca subas credenciales reales a repositorios públicos. Usa variables de entorno:

```bash
# Windows
set MONGODB_URI=mongodb+srv://usuario:contraseña@cluster.mongodb.net/
set MONGODB_DATABASE=NexoKet

# Linux/Mac
export MONGODB_URI=mongodb+srv://usuario:contraseña@cluster.mongodb.net/
export MONGODB_DATABASE=NexoKet
```

### 4️⃣ Instalar Dependencias

```bash
mvn clean install
```

---

## 🚀 Ejecutar el Proyecto

### Opción 1: Apache NetBeans IDE

1. **Abrir el proyecto:**
   - `File` → `Open Project`
   - Navega a la carpeta del proyecto
   - Selecciona `NexoKet` y haz clic en `Open Project`

2. **Configurar JDK:**
   - Click derecho en el proyecto → `Properties`
   - `Build` → `Compile` → Selecciona JDK 22
   - `Apply` → `OK`

3. **Ejecutar:**
   - Click derecho en `NexoKet.java` → `Run File` (Shift+F6)
   - O presiona `F6` para ejecutar el proyecto completo

4. **Ver Javadoc:**
   - `Run` → `Generate Javadoc for "NexoKet"`
   - Abre: `target/reports/apidocs/index.html`

### Opción 2: Visual Studio Code

1. **Instalar Extensiones:**
   - Extension Pack for Java (Microsoft)
   - Maven for Java
   - Debugger for Java

2. **Abrir el proyecto:**
   ```bash
   code .
   ```

3. **Configurar Java:**
   - `Ctrl+Shift+P` → "Java: Configure Java Runtime"
   - Selecciona JDK 22

4. **Ejecutar:**
   - Abre `NexoKet.java`
   - Click en `Run` sobre el método `main`
   - O presiona `F5` para debug

5. **Maven:**
   - Abre la vista de Maven (barra lateral)
   - Expande el proyecto → `Lifecycle`
   - Click en `compile` o `install`

### Opción 3: Línea de Comandos

```bash
# Compilar
mvn compile

# Ejecutar clase principal
mvn exec:java -Dexec.mainClass="utp.edu.pe.nexoket.NexoKet"

# Generar Javadoc
mvn javadoc:javadoc

# Crear JAR ejecutable
mvn package
java -jar target/NexoKet-1.0-SNAPSHOT.jar
```

---

## 👤 Usuarios de Prueba

El sistema viene con usuarios de prueba (si ya fueron creados en la BD):

```
Usuario: admin
Contraseña: admin123
```

Para crear un nuevo usuario, utiliza la opción "Registrarse" en la pantalla de inicio.

---

## 📊 Funcionalidades Principales

### 1. Gestión de Productos
- ✅ Registro de productos con categorías (Lácteos, Bebidas, Snacks, Abarrotes)
- ✅ Control de stock con alertas de stock mínimo
- ✅ Búsqueda y filtrado avanzado
- ✅ Cálculo automático de precios con margen de ganancia
- ✅ Escaneo de códigos de barras

### 2. Gestión de Ventas
- ✅ Registro de ventas con múltiples productos
- ✅ Aplicación de descuentos por cliente
- ✅ Generación automática de boletas en PDF
- ✅ Historial de ventas
- ✅ Reportes de ventas por periodo

### 3. Gestión de Clientes
- ✅ Registro de clientes con datos personales
- ✅ Control de descuentos especiales
- ✅ Historial de compras

### 4. Gestión de Proveedores
- ✅ Registro de proveedores
- ✅ Control de productos por proveedor

### 5. Reportes
- ✅ Reporte de inventario
- ✅ Productos más vendidos
- ✅ Valor total del inventario
- ✅ Productos con stock bajo
- ✅ Productos próximos a vencer

### 6. Servicios Automáticos
- ✅ Backup automático de base de datos (cada 24 horas)
- ✅ Monitoreo de rendimiento del sistema
- ✅ Mantenimiento automático de datos
- ✅ Limpieza de backups antiguos (> 7 días)

---

## 🧪 Ejecutar Pruebas

### Test de Backup Manual
```bash
mvn exec:java -Dexec.mainClass="utp.edu.pe.nexoket.test.TestBackup"
```

### Test de Productos
```bash
mvn exec:java -Dexec.mainClass="utp.edu.pe.nexoket.test.EjemploProductosReales"
```

### Test de Monitoreo
```bash
mvn exec:java -Dexec.mainClass="utp.edu.pe.nexoket.test.TestMonitoreo"
```

---

## 📚 Documentación

### Javadoc
La documentación completa de las clases está disponible en formato Javadoc:

1. Generar documentación:
   ```bash
   mvn javadoc:javadoc
   ```

2. Abrir en navegador:
   ```
   target/reports/apidocs/index.html
   ```

### Estructura de Paquetes

- **`config`**: Gestión de configuración del sistema
- **`dao`**: Acceso a datos (MongoDB)
- **`db`**: Conexión a base de datos
- **`Facade`**: Lógica de negocio y patrones Facade
- **`jform`**: Ventanas de interfaz gráfica (Swing)
- **`modelo`**: Entidades y modelos de datos
- **`security`**: Servicios de seguridad y encriptación
- **`util`**: Utilidades (Backup, PDF, Monitoreo, etc.)

---

## 🐛 Solución de Problemas

### Error de Conexión a MongoDB
```
Error: MongoTimeoutException
```
**Solución:**
- Verifica tu conexión a Internet
- Confirma que la IP está en la lista blanca de MongoDB Atlas
- Verifica las credenciales en `application.properties`

### Error de JDK
```
Error: Release version 22 not supported
```
**Solución:**
- Instala JDK 22 o superior
- Configura `JAVA_HOME`:
  ```bash
  set JAVA_HOME=C:\Program Files\Java\jdk-22
  ```

### Error de Maven
```
Error: JAVA_HOME not found
```
**Solución:**
- Configura la variable de entorno `JAVA_HOME`
- Reinicia el IDE o terminal

### Problemas con Dependencias
```bash
# Limpiar y reinstalar
mvn clean install -U
```

---

## 📝 Licencia

Este proyecto es desarrollado con fines académicos para la Universidad Tecnológica del Perú (UTP).

**© 2025 NexoKet Team - UTP**

---

## 👥 Equipo de Desarrollo

**NexoKet Team - UTP 2025**

Desarrollado como proyecto del curso de Programación Orientada a Objetos.

---

## 🤝 Contribuciones

Este es un proyecto académico. Si deseas contribuir:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📞 Soporte

Para preguntas o problemas:
- 📧 Email: nexoket@utp.edu.pe
- 🌐 Universidad Tecnológica del Perú

---

## 🎯 Roadmap Futuro

- [ ] API REST para integración con otras aplicaciones
- [ ] Aplicación móvil (Android/iOS)
- [ ] Dashboard web con estadísticas en tiempo real
- [ ] Integración con pasarelas de pago
- [ ] Soporte multi-tienda
- [ ] Reportes avanzados con gráficos
- [ ] Notificaciones por email/SMS

---

**🚀 ¡Gracias por usar NexoKet! 🚀**
