# Fintech

## 1. Contexto
FinTech S.A. es un banco digital que necesita una plataforma backend para gestionar clientes, cuentas bancarias y operaciones financieras. Se pide desarrollar una API REST con Spring Boot que sea capaz de registrar clientes, abrir cuentas, realizar transferencias entre cuentas y consultar el historial de movimientos.

## 2. Nuevos conceptos que se practican
| Concepto nuevo | Qué aporta respecto al ejercicio anterior |
| ------------- | ------------- |
| @Transactional | Garantiza que una transferencia o un conjunto de operaciones se completa al 100% o se revierte (rollback) automáticamente si hay un error. |
| JPQL (Java Persistence Query Language) | Permite escribir consultas orientadas a objetos para filtrar movimientos por fecha, tipo o importe sin SQL puro. |
| Pageable (Spring Data) |	Devuelve los resultados paginados y ordenados en lugar de listas completas, imprescindible en producción. |
| Spring Security (Basic Auth) |	Protege los endpoints de administración con usuario y contraseña. Los endpoints públicos (consulta de saldo) no requieren autenticación. |
| Enums con lógica propia |	El estado de una cuenta o el tipo de movimiento incluyen métodos que encapsulan reglas de negocio. |
| Auditoría automática (@CreatedDate) |	Cada entidad registra automáticamente su fecha de creación y modificación sin código manual. |

## 3. Modelo de datos

El sistema debe modelar las siguientes entidades con sus relaciones:

| Entidad	Campos principales |	Relaciones |
| ------------- | ------------- |
| Cliente	id (Long), dni (único), nombre, apellidos, email (único), telefono, fechaAlta	| OneToMany → Cuenta |
| Cuenta	id (Long), numeroCuenta (único, 20 dígitos), tipoCuenta (CORRIENTE/AHORRO/NOMINA), saldo, estado (ACTIVA/BLOQUEADA/CANCELADA), fechaApertura	ManyToOne → Cliente | OneToMany → Movimiento |
| Movimiento	id (Long), tipo (INGRESO/RETIRO/TRANSFERENCIA_ENTRADA/TRANSFERENCIA_SALIDA), importe, saldoPostOperacion, descripcion, fechaMovimiento |	ManyToOne → Cuenta |
| Usuario (seguridad)	id (Long), username, passwordHash, rol (ADMIN/EMPLEADO) |	(independiente de Cliente) |


## 4. Requisitos funcionales

### 4.1 Gestión de Clientes
    • Registrar un nuevo cliente validando que el DNI y el email no existan previamente.
    • Consultar todos los clientes con paginación (10 por página, ordenados por apellidos).
    • Consultar un cliente por DNI incluyendo el listado de sus cuentas activas.
    • Actualizar los datos de contacto de un cliente (email, teléfono).
    • Dar de baja a un cliente solo si todas sus cuentas están en saldo cero y canceladas.

### 4.2 Gestión de Cuentas
    • Abrir una nueva cuenta asignándola a un cliente existente. El número de cuenta se genera automáticamente con formato ES + 18 dígitos aleatorios.
    • Consultar el saldo actual de una cuenta por su número.
    • Bloquear o cancelar una cuenta (solo si el saldo es 0 para cancelar).
    • Consultar todas las cuentas de un cliente por su DNI.
    • Listar cuentas con saldo superior a un importe indicado (consulta JPQL personalizada).

### 4.3 Operaciones Financieras
    • Realizar un ingreso en efectivo en una cuenta activa.
    • Realizar un retiro de una cuenta activa verificando que haya saldo suficiente.
    • Realizar una transferencia entre dos cuentas activas. Esta operación DEBE ser atómica (@Transactional): si falla cualquier paso (cuenta bloqueada, saldo insuficiente, cuenta no encontrada), toda la operación se revierte.
    • Consultar el historial de movimientos de una cuenta con paginación y filtrado opcional por tipo de movimiento y rango de fechas (JPQL con parámetros opcionales).
    • Obtener el resumen mensual de una cuenta: total ingresos, total gastos y saldo neto del mes indicado.

### 4.4 Seguridad (Spring Security)
    • Los endpoints de administración (crear/bloquear/cancelar cuentas, dar de baja clientes) requieren autenticación HTTP Basic con rol ADMIN.
    • Los endpoints de consulta pública (saldo, historial) son accesibles sin autenticación.
    • Las contraseñas de los usuarios deben almacenarse hasheadas con BCrypt.
    
## 5. Reglas de negocio críticas

> ⚠️  Estas reglas son el corazón del ejercicio. Su implementación incorrecta debe producir un error HTTP apropiado, nunca un estado inconsistente en la base de datos.

| Regla	| Comportamiento esperado	| Error si se viola |
| ------------- | ------------- | ------------- |
| Saldo insuficiente |	Un retiro o transferencia no puede dejar el saldo en negativo. | 400 Bad Request: saldo insuficiente |
| Cuenta no activa	| Ninguna operación financiera está permitida sobre cuentas BLOQUEADAS o CANCELADAS. |	409 Conflict: cuenta no operativa |
| Transferencia atómica |	Si el abono en la cuenta destino falla después del cargo en la origen, se hace rollback completo. |	500 + rollback automático |
| Cancelación de cuenta |	Solo se puede cancelar si el saldo es exactamente 0,00 €. |	400 Bad Request: saldo pendiente |
| Baja de cliente |	Solo si todas sus cuentas están canceladas.	| 409 Conflict: cuentas activas |
| DNI y email únicos |	No pueden existir dos clientes con el mismo DNI o email. |	409 Conflict: ya registrado |

## 6. Endpoints requeridos

| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | /api/clientes?page=0&size=10 | Listar clientes paginados | ADMIN |
| GET | /api/clientes/{dni} | Cliente + sus cuentas activas | ADMIN |
| POST | /api/clientes | Registrar nuevo cliente | ADMIN |
| PUT | /api/clientes/{dni}/contacto | Actualizar email y teléfono | ADMIN |
| DELETE | /api/clientes/{dni} | Dar de baja cliente | ADMIN |
| GET | /api/cuentas/{numeroCuenta}/saldo | Consultar saldo | Pública |
| GET | /api/cuentas/cliente/{dni} | Cuentas de un cliente | ADMIN |
| GET | /api/cuentas/saldo-superior?importe=X | Cuentas con saldo > X | ADMIN |
| POST | /api/cuentas | Abrir nueva cuenta | ADMIN |
| PATCH | /api/cuentas/{numeroCuenta}/estado | Cambiar estado | ADMIN |
| POST | /api/operaciones/ingreso | Ingresar efectivo | ADMIN |
| POST | /api/operaciones/retiro | Retirar efectivo | ADMIN |
| POST | /api/operaciones/transferencia | Transferencia entre cuentas | ADMIN |
| GET | /api/movimientos/{numeroCuenta}?page=0&tipo=X&desde=Y&hasta=Z | Historial paginado con filtros | ADMIN |
| GET | /api/movimientos/{numeroCuenta}/resumen-mensual?anio=X&mes=Y | Resumen mensual | ADMIN |

## 7. Requisitos técnicos adicionales
    • Uso de @Transactional en todos los métodos de servicio que modifiquen datos (imprescindible en transferencias).
    • Al menos 3 consultas JPQL personalizadas con @Query en los repositorios.
    • Todos los endpoints de listado deben devolver Page<DTO> en lugar de List<DTO>.
    • Spring Security configurado con SecurityFilterChain (estilo moderno, sin WebSecurityConfigurerAdapter).
    • Contraseñas hasheadas con BCryptPasswordEncoder.
    • Auditoría con @EntityListeners(AuditingEntityListener.class) y @CreatedDate / @LastModifiedDate.
    • Los movimientos se crean siempre de forma automática desde el servicio, nunca directamente por la API.
    • Tests de integración con @SpringBootTest para las operaciones de transferencia (al menos 3 casos: éxito, saldo insuficiente y cuenta bloqueada).

## 8. Estructura del proyecto

```bash
Fintech/
├── pom.xml
└── src/
    ├── main/java/com/nombre/fintech/
    │   ├── FintechApplication.java
    │   ├── config/
    │   │   ├── SecurityConfig.java
    │   │   ├── AuditConfig.java
    │   │   └── DataLoader.java
    │   ├── controller/
    │   │   ├── ClienteController.java
    │   │   ├── CuentaController.java
    │   │   ├── OperacionController.java
    │   │   └── MovimientoController.java
    │   ├── dto/
    │   │   ├── cliente/  (ClienteDTO, ClienteRequestDTO, ContactoUpdateDTO)
    │   │   ├── cuenta/   (CuentaDTO, CuentaRequestDTO)
    │   │   ├── operacion/(IngresoDTO, RetiroDTO, TransferenciaDTO)
    │   │   └── movimiento/(MovimientoDTO, ResumenMensualDTO)
    │   ├── entity/
    │   │   ├── Cliente.java
    │   │   ├── Cuenta.java
    │   │   ├── Movimiento.java
    │   │   └── Usuario.java
    │   ├── enums/
    │   │   ├── TipoCuenta.java
    │   │   ├── EstadoCuenta.java
    │   │   └── TipoMovimiento.java
    │   ├── exception/
    │   │   ├── GlobalExceptionHandler.java
    │   │   ├── RecursoNoEncontradoException.java
    │   │   ├── SaldoInsuficienteException.java
    │   │   └── CuentaNoOperativaException.java
    │   ├── mapper/
    │   │   ├── ClienteMapper.java
    │   │   └── CuentaMapper.java
    │   ├── repository/
    │   │   ├── ClienteRepository.java
    │   │   ├── CuentaRepository.java
    │   │   ├── MovimientoRepository.java
    │   │   └── UsuarioRepository.java
    │   └── service/
    │       ├── ClienteService.java
    │       ├── CuentaService.java
    │       ├── OperacionService.java
    │       └── MovimientoService.java
    └── test/java/com/nombre/fintech/
        └── service/
            └── TransferenciaIntegrationTest.java
```
