# 🚀 Guía de Inicio Rápido - NexoKet

Esta guía te ayudará a poner en marcha NexoKet en **menos de 5 minutos**.

---

## ⚡ Inicio Rápido (3 Pasos)

### 1️⃣ Configurar Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto (o establece variables del sistema):

```bash
# Windows PowerShell
$env:MONGODB_URI = "tu_uri_de_mongodb"
$env:MONGODB_DATABASE = "NexoKet"

# Linux/Mac
export MONGODB_URI="tu_uri_de_mongodb"
export MONGODB_DATABASE="NexoKet"
```

### 2️⃣ Compilar e Instalar

```bash
mvn clean install
```

### 3️⃣ Ejecutar

```bash
mvn exec:java -Dexec.mainClass="utp.edu.pe.nexoket.NexoKet"
```

---

## 📋 Checklist Pre-Ejecución

Antes de ejecutar, verifica que tienes:

- [ ] Java 17 o superior instalado (`java -version`)
- [ ] Maven 3.8+ instalado (`mvn -version`)
- [ ] Cuenta de MongoDB Atlas o MongoDB local
- [ ] Variables de entorno configuradas
- [ ] Puertos libres (8080 por defecto)

---

## 🔐 Primer Usuario

Para crear el primer usuario administrador:

1. Ejecuta la aplicación
2. Ve a "Registrar" en la pantalla de login
3. Crea tu usuario con contraseña fuerte:
   - Mínimo 8 caracteres
   - Mayúsculas, minúsculas, números y símbolos
   - Ejemplo: `Admin123!`

---

## 🧪 Verificar Instalación

### Ejecutar Tests

```bash
mvn test
```

**Resultado esperado**: Todos los tests deben pasar ✅

### Verificar Logs

```bash
# Ver logs en tiempo real (PowerShell)
Get-Content logs\nexoket.log -Wait -Tail 50

# Linux/Mac
tail -f logs/nexoket.log
```

**Resultado esperado**: Debes ver mensajes como:
```
✓ Conexión a MongoDB establecida
✓ Monitor de rendimiento iniciado
✓ Servicio de backups iniciado
✓ Aplicación iniciada correctamente
```

### Verificar Servicios Activos

Busca en los logs:
- ✅ `Monitor de rendimiento iniciado`
- ✅ `Servicio de backups iniciado`
- ✅ `Servicio de mantenimiento iniciado`

---

## 🐛 Problemas Comunes

### ❌ "Cannot connect to MongoDB"

**Solución:**
1. Verifica que la URI de MongoDB es correcta
2. Comprueba que MongoDB Atlas permite tu IP
3. Verifica credenciales de usuario/password

### ❌ "Tests failing"

**Solución:**
```bash
# Saltar tests temporalmente
mvn install -DskipTests

# O ejecutar con detalles para ver el error
mvn test -X
```

### ❌ "OutOfMemoryError"

**Solución:**
```bash
# Aumentar memoria heap
java -Xms1g -Xmx2g -jar target/nexoket-1.0.0-SNAPSHOT.jar
```

### ❌ "Port already in use"

**Solución:**
- Cierra otras aplicaciones que usen el puerto 8080
- O configura un puerto diferente en `application.properties`

---

## 📊 Verificar Funcionalidades

### ✅ Checklist de Funcionalidades

Una vez dentro de la aplicación, verifica:

- [ ] Login funciona
- [ ] Puedes crear clientes
- [ ] Puedes crear productos
- [ ] Puedes registrar una venta
- [ ] Se genera PDF de boleta
- [ ] Los logs se están escribiendo en `logs/`
- [ ] El sistema muestra el monitor de rendimiento

---

## 🎯 Siguiente Pasos

1. **Configurar backup automático**
   - Verifica que `backups/` se está creando
   - El primer backup se hará 1 hora después de iniciar

2. **Revisar logs de rendimiento**
   ```bash
   cat logs/performance.log
   ```

3. **Ejecutar tests de seguridad**
   ```bash
   mvn test -Dtest=PasswordServiceTest
   ```

4. **Generar reporte de cobertura**
   ```bash
   mvn test jacoco:report
   # Ver en: target/site/jacoco/index.html
   ```

---

## 📞 Ayuda Adicional

- **README completo**: [README.md](README.md)
- **Documentación técnica**: Ver carpeta `docs/`
- **Issues**: GitHub Issues
- **Email**: soporte@nexoket.utp.edu.pe

---

## ✨ Características Implementadas

Este proyecto incluye:

✅ **Seguridad**
- Contraseñas encriptadas (BCrypt)
- Validación de fortaleza de contraseñas
- Variables de entorno para credenciales

✅ **Monitoreo**
- Logs estructurados (SLF4J + Logback)
- Monitoreo de rendimiento en tiempo real
- Alertas automáticas

✅ **Mantenimiento**
- Backups automáticos cada 24h
- Limpieza de logs antiguos
- Verificación de salud del sistema

✅ **Calidad**
- Tests unitarios (JUnit 5)
- Cobertura > 60% (JaCoCo)
- Arquitectura limpia (MVC + DAO + Facade)

---

**¡Listo! 🎉 Tu sistema NexoKet está funcionando**

¿Problemas? Consulta el [README.md](README.md) completo o abre un Issue.
