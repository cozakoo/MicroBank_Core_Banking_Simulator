# Dashboard MicroBank — v1.2.0

## Descripción General

Dashboard bancario de MicroBank. Interfaz moderna para gestionar cuentas, realizar operaciones financieras y visualizar transacciones en tiempo real. Requiere autenticación JWT — cada usuario gestiona sus propias cuentas y puede transferir a cualquier cuenta del sistema.

## Funcionalidades

### ✅ Autenticación JWT
- **Registro** — Crear usuario con nombre y contraseña (mínimo 3 caracteres)
- **Login** — Iniciar sesión → recibe token JWT válido 24h
- **Logout** — Botón en navbar, limpia el token del navegador
- **Protección automática** — Sin token, redirige a `/login.html`
- Página: `http://localhost:8080/login.html`

### ✅ Estadísticas del Dashboard
- **N° Cuentas** — Cuentas del usuario autenticado
- **Cuentas Activas** — Conteo de cuentas en estado ACTIVO
- **Cuentas Suspendidas** — Conteo de cuentas suspendidas

### ✅ Gestión de Cuentas

#### Listar Cuentas
- Tabla responsiva con las cuentas del usuario autenticado
- Columnas: Número de Cuenta (+ alias si tiene), Tipo, Saldo, Estado, Fecha de Creación, Acciones
- Búsqueda/filtrado en tiempo real por número de cuenta
- Badges de estado con código de colores:
  - 🟢 **ACTIVO** — Verde
  - 🟡 **SUSPENDIDO** — Amarillo
  - ⚫ **CERRADO** — Rojo
  - ⚪ **INACTIVO** — Gris

#### Crear Cuenta
- Formulario modal para crear nueva cuenta
- Campos:
  - **Tipo de Cuenta** (requerido: CORRIENTE, AHORRO, CRÉDITO)
  - **Saldo Inicial** (requerido, debe ser > 0)
- Validación en cliente
- Notificación de éxito con número de cuenta

#### Depósitos (💰)
- Modal para ingresar dinero en una cuenta
- Muestra: Número de cuenta y saldo actual
- Campo de monto requerido (> 0)
- Llamada a `POST /api/v1/accounts/{id}/deposit`
- Recarga tabla al completarse

#### Retiros (💸)
- Modal para extraer dinero de una cuenta
- Muestra: Número de cuenta y saldo actual
- Validación de monto disponible (en backend)
- Llamada a `POST /api/v1/accounts/{id}/withdraw`
- Actualización automática del saldo

#### Alias de Cuenta (🏷️)
- Cada cuenta puede tener un alias único (ej: `mi-ahorro`, `gastos-fijos`)
- Si no tiene alias, aparece el link `+ alias` en la tabla para asignarlo rápido
- Reglas: solo minúsculas, números y guiones (`a-z`, `0-9`, `-`), 3–30 caracteres
- Endpoint: `PUT /api/v1/accounts/{id}/alias`

#### Transferencias (🔄)
- Modal con dos modos de selección de destino:
  - **Mis cuentas** — Dropdown con tus cuentas ACTIVO
  - **Número / Alias** — Campo libre para cualquier cuenta del sistema
- Podés transferir a cuentas de otros usuarios usando su número o alias
- Validaciones:
  - Monto > 0
  - Cuenta destino ≠ cuenta origen
  - Fondos suficientes (backend)
- Llamada a `POST /api/v1/transfers`
- Sincronización ACID garantizada

#### Ver Transacciones (📊)
- Modal que lista todas las operaciones de una cuenta
- Columnas: Tipo (TRANSFERENCIA/DEPOSITO/RETIRO), Monto, Estado, Fecha, Descripción
- Muestra transacciones de entrada y salida
- Mensaje si la cuenta no tiene transacciones

#### Cambiar Estado de Cuenta (⚙️)
- Modal para actualizar estado de cuenta
- Opciones: ACTIVO, INACTIVO, SUSPENDIDO, CERRADO
- **Advertencias:**
  - SUSPENDIDO: "Suspender una cuenta bloqueará todas las transacciones"
  - CERRADO: "Cerrar una cuenta es irreversible. Se bloquearán todas las transacciones"
- Requiere confirmación antes de actualizar

### ✅ Experiencia de Usuario
- **Notificaciones en Tiempo Real** — Toasts para éxito/error/advertencia
- **Estados de Carga** — Spinners durante llamadas a API
- **Manejo de Errores** — Mensajes amigables al usuario
- **Diseño Responsivo** — Funciona en desktop, tablet, móvil
- **Accesibilidad** — Etiquetas ARIA, HTML semántico
- **Footer** — Créditos de desarrolladores y stack técnico

## Dirección de Diseño

**"Interfaz Bancaria Refinada"** — Moderna, elegante, profesional

- **Paleta de Colores:** Azul marino (#1e3a8a) + Gris (#6b7280) + Blanco
- **Tipografía:** Fuentes display para encabezados, monoespaciada para datos financieros
- **Espaciado:** Espacio en blanco generoso, grid de 12px
- **Microinteracciones:** Transiciones suaves, efectos hover, estados de botón
- **Detalles Visuales:** Sombras sutiles, bordes superiores en gradiente, badges elegantes
- **Sin desorden visual** — Los datos son los protagonistas

## Estructura de Archivos

```
src/main/resources/static/
├── index.html              # Página principal del dashboard
├── css/
│   └── dashboard.css       # Todos los estilos (1400+ líneas)
└── js/
    └── app.js              # Lógica de la aplicación
```

## Endpoints de API Utilizados

| Método | Endpoint | Auth | Propósito |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Público | Registrar nuevo usuario |
| POST | `/api/v1/auth/login` | Público | Login → retorna JWT |
| GET | `/api/v1/accounts` | JWT | Listar mis cuentas |
| POST | `/api/v1/accounts` | JWT | Crear nueva cuenta |
| GET | `/api/v1/accounts/{id}` | JWT | Detalles de cuenta |
| PUT | `/api/v1/accounts/{id}/status` | JWT | Cambiar estado |
| PUT | `/api/v1/accounts/{id}/alias` | JWT | Asignar alias |
| GET | `/api/v1/accounts/search/{id}` | JWT | Buscar por número o alias |
| POST | `/api/v1/accounts/{id}/deposit` | JWT | Depositar fondos |
| POST | `/api/v1/accounts/{id}/withdraw` | JWT | Retirar fondos |
| POST | `/api/v1/transfers` | JWT | Transferir por UUID/número/alias |
| GET | `/api/v1/transfers/account/{id}` | JWT | Historial de transacciones |

## Formatos de Solicitud/Respuesta

### Crear Cuenta
```json
POST /api/v1/accounts
{
  "accountType": "CORRIENTE",
  "initialBalance": 5000.00
}

Respuesta: 201 CREATED
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "accountNumber": "ACC1234567890123",
    "accountType": "CORRIENTE",
    "balance": 5000.00,
    "status": "ACTIVO",
    "createdAt": "2026-04-11T10:30:00",
    "updatedAt": "2026-04-11T10:30:00"
  }
}
```

### Realizar Depósito
```json
POST /api/v1/accounts/{id}/deposit
{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 1000.00
}

Respuesta: 201 CREATED
{
  "id": "uuid",
  "sourceAccountId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 1000.00,
  "type": "DEPOSITO",
  "status": "COMPLETADA",
  "createdAt": "2026-04-11T11:00:00"
}
```

### Realizar Retiro
```json
POST /api/v1/accounts/{id}/withdraw
{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 500.00
}

Respuesta: 200 OK
{
  "id": "uuid",
  "sourceAccountId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 500.00,
  "type": "RETIRO",
  "status": "COMPLETADA",
  "createdAt": "2026-04-11T11:05:00"
}
```

### Realizar Transferencia
```json
POST /api/v1/transfers
{
  "sourceAccountId": "550e8400-e29b-41d4-a716-446655440001",
  "targetAccountId": "550e8400-e29b-41d4-a716-446655440002",
  "amount": 2000.00
}

Respuesta: 201 CREATED
{
  "id": "uuid",
  "sourceAccountId": "550e8400-e29b-41d4-a716-446655440001",
  "targetAccountId": "550e8400-e29b-41d4-a716-446655440002",
  "amount": 2000.00,
  "type": "TRANSFERENCIA",
  "status": "COMPLETADA",
  "createdAt": "2026-04-11T11:10:00"
}
```

### Cambiar Estado de Cuenta
```json
PUT /api/v1/accounts/{id}/status
{
  "newStatus": "SUSPENDIDO"
}

Respuesta: 200 OK
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "accountNumber": "ACC1234567890123",
    "status": "SUSPENDIDO",
    ...
  }
}
```

## Ejecutar el Dashboard

### 1. Iniciar el Backend
```bash
# Terminal en la raíz del proyecto
mvn clean spring-boot:run
```

La aplicación se inicia en `http://localhost:8080`

### 2. Abrir el Dashboard
```bash
# En el navegador
http://localhost:8080/
```

Deberías ver el Dashboard Administrativo con:
- Cards de estadísticas (Total, Activas, Suspendidas)
- Barra de búsqueda
- Botón "Crear cuenta"
- Tabla vacía de cuentas (con loader)

### 3. Probar las Funcionalidades

#### Crear Cuenta de Prueba
1. Click en botón **"Crear cuenta"**
2. Seleccionar tipo: CORRIENTE
3. Ingresar saldo inicial: 5000
4. Click en **"Crear Cuenta"**
5. Ver toast de éxito con número de cuenta
6. Ver cuenta nueva en la tabla

#### Buscar Cuentas
1. Escribir "ACC" en la barra de búsqueda
2. La tabla filtra en tiempo real

#### Depositar Dinero
1. Click en 💰 en una cuenta
2. Ingresar monto: 1000
3. Confirmar depósito
4. Ver toast de éxito
5. Saldo actualizado en la tabla

#### Retirar Dinero
1. Click en 💸 en una cuenta
2. Ingresar monto: 500
3. Confirmar retiro
4. Ver toast de éxito y saldo actualizado

#### Realizar Transferencia
1. Click en 🔄 en una cuenta origen
2. Seleccionar cuenta destino del dropdown
3. Ingresar monto: 1500
4. Confirmar transferencia
5. Ver ambas cuentas con saldos actualizados

#### Ver Transacciones
1. Click en 📊 en cualquier cuenta
2. Ver modal con todas las operaciones
3. Columnas: Tipo, Monto, Estado, Fecha, Descripción

#### Cambiar Estado
1. Click en ⚙️ en cualquier cuenta
2. Seleccionar "SUSPENDIDO" del dropdown
3. Ver advertencia en rojo
4. Click en "Actualizar Estado"
5. Ver toast de éxito
6. Badge de estado actualizado en la tabla

## Destacados Técnicos

### Frontend
- **HTML5** markup semántico
- **Bootstrap 5** para grid responsivo
- **JavaScript Vanilla** (sin jQuery, sin React)
- **Fetch API** para solicitudes asincrónicas
- **CSS Variables** para theming
- **CSS Grid + Flexbox** para layout
- **Animaciones CSS** para microinteracciones

### Calidad del Código
- Estructura modular con separación de responsabilidades
- Manejo exhaustivo de errores (try-catch, validación)
- Prevención de XSS con `escapeHtml()`
- Seguridad CSRF con headers Content-Type
- Validación accesible de formularios
- HTML semántico con etiquetas ARIA

### Rendimiento
- Dependencias mínimas (solo Bootstrap CDN)
- Sin proceso de build requerido
- Carga rápida de página
- Re-renderización eficiente (solo actualizar filas modificadas)
- Delegación de eventos para elementos dinámicos

## Compatibilidad de Navegadores

- ✅ Chrome/Chromium (últimas versiones)
- ✅ Firefox (últimas versiones)
- ✅ Safari (últimas versiones)
- ✅ Edge (últimas versiones)
- ✅ Navegadores móviles (diseño responsivo probado)

## Mejoras Futuras

### Fase 2
- [ ] Modo oscuro
- [ ] Soporte multiidioma (i18n)
- [ ] Exportar cuentas a CSV
- [ ] Filtrado avanzado (por tipo, estado, rango de fechas)
- [ ] Paginación para grandes volúmenes de datos
- [ ] Búsqueda avanzada por saldo

### Fase 3
- [ ] Gráficos del dashboard (crecimiento de cuentas, volumen de transacciones)
- [ ] Autenticación de usuarios
- [ ] Control de acceso basado en roles (admin vs. operador)
- [ ] Visor de log de auditoría
- [ ] Herramientas de conciliación de cuentas
- [ ] Reportes de transacciones

## Notas para Martín

1. **Ubicación de Archivos Estáticos:** `/src/main/resources/static/` — Spring Boot los sirve automáticamente en `/`
2. **Sin Asset Pipeline:** HTML/CSS/JS se sirven tal cual, sin proceso de build
3. **URL Base de API:** Configurada como `/api/v1` en `app.js` — actualizar si cambia la ruta
4. **Bootstrap CDN:** Usa Bootstrap 5.3.0 — agregar fallback offline si es necesario
5. **Despliegue en Producción:** Los archivos ya están en la ubicación correcta para despliegue WAR/JAR
6. **Footer:** Créditos de desarrolladores (Martín Arcos Vargas y Lucas) con links a GitHub

## Checklist de Pruebas

**Auth:**
- [ ] Ir a `localhost:8080` → redirige a `/login.html`
- [ ] Registrarse con usuario/contraseña (mínimo 3 caracteres)
- [ ] Login → entra al dashboard
- [ ] Logout → vuelve al login

**Cuentas:**
- [ ] Dashboard carga solo las cuentas del usuario autenticado
- [ ] Crear cuenta: vinculada al usuario logueado
- [ ] Asignar alias: click en `+ alias`, ingresar nombre válido
- [ ] Alias aparece como `@nombre` debajo del número

**Operaciones:**
- [ ] Depositar: modal abre, procesa, saldo actualiza
- [ ] Retirar: validación de saldo, actualización correcta
- [ ] Transferencia tab "Mis cuentas": dropdown de destino, ambas cuentas actualizadas
- [ ] Transferencia tab "Número / Alias": ingresar número o alias de otra cuenta, transferir
- [ ] Modal de transacciones: carga datos correctamente
- [ ] Cambio de estado: advertencias visibles, actualización funciona

**General:**
- [ ] Cards de estadísticas muestran conteos correctos
- [ ] Búsqueda/filtrado funciona en tiempo real
- [ ] Notificaciones toast: aparecen y desaparecen
- [ ] Sin errores en consola (F12 → Consola)

---

**Stack:** HTML5 + Bootstrap 5 + JavaScript Vanilla + Spring Boot REST APIs + JWT
**Desarrolladores:** Martín Arcos Vargas + Lucas
**Estado:** ✅ Producción
**Versión:** v1.2.0
**Última Actualización:** 11 de abril, 2026
