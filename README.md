# Sistema de Gestión de Citas de Hospital

## Descripción

Aplicación web desarrollada con Spring Boot para gestionar la autenticación, usuarios, médicos, disponibilidades médicas, reservas de citas, historial y administración de citas de un hospital.

El Avance II integra la Gestión de Citas con los módulos del Avance I, de manera que los usuarios pueden reservar espacios disponibles y los administradores pueden gestionar y dar seguimiento a las citas.

## Módulos implementados

### Autenticación y usuarios

- Registro de usuarios.
- Inicio y cierre de sesión.
- Recuperación de contraseña.
- Roles ADMIN y USUARIO.
- Actualización de perfil.
- Gestión y desactivación lógica de usuarios.
- Contraseñas protegidas con BCrypt.

### Gestión de médicos y disponibilidades

- Creación y edición de médicos.
- Registro de especialidades.
- Creación y edición de disponibilidades.
- Prevención de disponibilidades duplicadas.
- Prevención de horarios solapados para un mismo médico.
- Rechazo de horarios pasados.
- Visualización del estado de una disponibilidad como ocupada o disponible.

Los horarios consecutivos sí son válidos. Por ejemplo:

```text
08:00–09:00
09:00–10:00
```

### Gestión de citas

- Consulta de espacios futuros disponibles.
- Reserva por parte del usuario autenticado.
- Estado inicial PENDIENTE.
- Confirmación por administrador.
- Cancelación por usuario o administrador.
- Historial completo de citas.
- Conservación de citas canceladas.
- Liberación inmediata del espacio al cancelar.
- Rechazo de reservas en espacios ocupados.
- Prevención de citas activas solapadas para un mismo usuario.
- Bloqueo transaccional para evitar reservas simultáneas.

### Administración de citas

El administrador puede consultar, confirmar y cancelar citas. También puede filtrar las citas por:

- Estado.
- Médico.
- Especialidad.
- Rango de fechas.

## Estados de una cita

Estados disponibles:

```text
PENDIENTE
CONFIRMADA
CANCELADA
```

Transiciones permitidas:

```text
PENDIENTE -> CONFIRMADA
PENDIENTE -> CANCELADA
CONFIRMADA -> CANCELADA
```

Una cita cancelada no puede reactivarse.

## Tecnologías utilizadas

- Java 26.
- Spring Boot.
- Spring Security.
- Spring Data JPA.
- Hibernate.
- Thymeleaf.
- H2 Database.
- Maven.
- HTML.
- CSS.

## Estructura principal

- `config`: seguridad e inicialización de datos básicos.
- `controller`: manejo de solicitudes web.
- `model`: entidades y estados del sistema.
- `repository`: persistencia y consultas JPA.
- `service`: reglas de negocio y transacciones.
- `templates`: vistas Thymeleaf.
- `static`: estilos CSS.

## Requisitos para ejecutar

- JDK 26 o una versión compatible con el `pom.xml`.
- Windows PowerShell.
- Maven Wrapper incluido en el proyecto.

El proyecto Maven está dentro de:

```text
Proyecto-Programado-Final-\Citas
```

## Ejecución

Desde la raíz del repositorio:

```powershell
cd .\Citas
.\mvnw.cmd spring-boot:run
```

Si la terminal ya está dentro de `Citas`, no se debe ejecutar otra vez `cd .\Citas`; basta con:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación se abre en:

```text
http://localhost:8080/login
```

Para detenerla:

```text
Ctrl + C
```

## Credenciales de administrador

DataInitializer crea únicamente esta cuenta si no existe:

```text
Correo: admin@hospital.com
Contraseña: admin123
```

## Usuario regular

El usuario regular debe crearse manualmente desde la opción de registro. No existe un usuario normal inicializado automáticamente.

## Base de datos H2

Consola:

```text
http://localhost:8080/h2-console
```

Datos de conexión:

```text
JDBC URL: jdbc:h2:mem:hospitaldb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
User Name: sa
Password: vacía
```

H2 se ejecuta en memoria, por lo que los datos se pierden cuando se detiene la aplicación.

## Rutas principales

Públicas:

- `/login`
- `/registro`
- `/recuperar-password`

Autenticadas:

- `/inicio`
- `/perfil`
- `/medicos`
- `/citas/disponibles`
- `/citas/mis-citas`

Administrativas:

- `/usuarios`
- `/disponibilidades`
- `/admin/citas`

## Flujo de demostración del Avance II

1. Iniciar como administrador.
2. Crear un médico activo.
3. Crear una o más disponibilidades futuras.
4. Cerrar sesión.
5. Registrar un usuario normal.
6. Iniciar sesión como usuario.
7. Reservar un espacio.
8. Comprobar que la cita queda PENDIENTE.
9. Comprobar que el espacio deja de estar disponible.
10. Iniciar sesión como administrador.
11. Filtrar o localizar la cita.
12. Confirmar la cita.
13. Cancelar la cita.
14. Comprobar que permanece en el historial como CANCELADA.
15. Comprobar que la disponibilidad vuelve a estar libre.

## Reglas importantes

- No se permiten disponibilidades duplicadas o solapadas para el mismo médico.
- No se crean disponibilidades ni citas en horarios pasados.
- Una disponibilidad representa un espacio indivisible.
- Una cita siempre pertenece a un usuario, médico y disponibilidad.
- El médico se obtiene desde la disponibilidad.
- Una disponibilidad solo puede tener una cita activa.
- El usuario no puede tener citas activas solapadas.
- Las reservas utilizan transacciones y bloqueo pesimista.
- Las citas canceladas no se eliminan.
- Una disponibilidad con historial de citas no puede eliminarse físicamente.
- Las reglas de negocio se encuentran en la capa `service`.

## Notas

- La base H2 es temporal.
- Solo se inicializa el administrador.
- Los médicos, usuarios regulares y disponibilidades se crean manualmente.
- Los formularios de reserva, confirmación y cancelación utilizan POST y CSRF.
- El proyecto mantiene arquitectura por capas.
