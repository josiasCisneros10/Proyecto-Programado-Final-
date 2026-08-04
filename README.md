# Sistema de Gestión de Citas de Hospital

## Descripción

Este proyecto es un sistema web desarrollado con Spring Boot para gestionar usuarios, médicos, disponibilidades horarias y citas médicas dentro de un hospital.

El Avance I implementa la autenticación, los roles, la gestión de usuarios, médicos y disponibilidades. El Avance II incorpora la reserva, consulta, confirmación y cancelación de citas, integrando estos procesos con los módulos anteriores.

## Módulos implementados

- Registro e inicio de sesión.
- Recuperación de contraseña.
- Gestión de pacientes y usuarios.
- Gestión de médicos y especialidades.
- Gestión de disponibilidades horarias.
- Reserva y cancelación de citas.
- Historial de citas del usuario.
- Administración y filtrado de citas.
- Seguridad con roles `USUARIO` y `ADMIN`.

## Gestión de citas

Los usuarios autenticados pueden consultar los espacios médicos disponibles, reservar una cita, revisar su historial y cancelar sus propias citas antes de la fecha y hora programadas.

Toda cita nueva se registra inicialmente como `PENDIENTE`. El administrador puede confirmar las citas pendientes o cancelar cualquier cita del sistema. Cuando una cita se cancela, el espacio médico vuelve a quedar disponible, mientras que la cita permanece guardada en el historial con estado `CANCELADA`.

El sistema evita que una disponibilidad ocupada sea reservada nuevamente y que un mismo usuario mantenga citas activas en horarios que se cruzan.

## Estados de una cita

- `PENDIENTE`: la cita fue reservada y está a la espera de confirmación.
- `CONFIRMADA`: la cita fue aprobada por un administrador.
- `CANCELADA`: la cita fue cancelada por el usuario o por un administrador.

Las transiciones permitidas son:

- `PENDIENTE` a `CONFIRMADA`.
- `PENDIENTE` a `CANCELADA`.
- `CONFIRMADA` a `CANCELADA`.

Una cita cancelada no puede volver a activarse.

## Administración de citas

El administrador puede visualizar todas las citas, confirmarlas, cancelarlas y filtrarlas por:

- Estado.
- Médico.
- Especialidad.
- Rango de fechas.

## Gestión de disponibilidades

Las disponibilidades representan los espacios de fecha y hora que un médico ofrece para recibir una cita.

El sistema impide registrar disponibilidades duplicadas o con horarios que se crucen para un mismo médico. Al reservar una cita, la disponibilidad queda ocupada; al cancelarla, vuelve a quedar disponible.

## Tecnologías utilizadas

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Thymeleaf
- H2 Database
- Maven
- HTML/CSS

## Requisitos para ejecutar

- Tener instalado JDK 26 o una versión compatible con la configurada en el proyecto.
- Tener Maven instalado o utilizar el wrapper incluido en el proyecto.
- Ejecutar el proyecto desde la carpeta raíz, donde se encuentra el archivo `pom.xml`.

## Pasos de ejecución

Opción 1: desde Visual Studio Code.

1. Abrir el proyecto en Visual Studio Code.
2. Buscar la clase principal:

```text
src/main/java/com/CitasHospital/Citas/CitasApplication.java
```

3. Presionar el botón de ejecutar sobre la clase `CitasApplication`.

Opción 2: desde Windows PowerShell.

Ubicarse en la carpeta raíz del proyecto y ejecutar:

```powershell
.\mvnw spring-boot:run
```

Luego abrir en el navegador:

```text
http://localhost:8080/login
```

## Credenciales de prueba

Administrador:

```text
Correo: admin@hospital.com
Contraseña: admin123
```

Usuario normal:

```text
Puede registrarse desde la opción "Registrarse" en la pantalla de login.
```

El sistema inicializa únicamente la cuenta administradora. Los usuarios regulares, médicos y disponibilidades se crean desde la aplicación.

## Base de datos H2

La consola de H2 se puede abrir en:

```text
http://localhost:8080/h2-console
```

Datos de conexión:

```text
JDBC URL: jdbc:h2:mem:hospitaldb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
User Name: sa
Password:
```

La contraseña se deja vacía.

## Rutas principales

- `/login`
- `/registro`
- `/recuperar-password`
- `/inicio`
- `/perfil`
- `/usuarios`
- `/medicos`
- `/disponibilidades`
- `/citas/disponibles`
- `/citas/mis-citas`
- `/admin/citas`

## Notas importantes

- Las contraseñas se guardan encriptadas con BCrypt.
- Los usuarios y médicos se desactivan mediante borrado lógico.
- Los usuarios regulares solo pueden consultar y gestionar sus propias citas.
- El administrador puede gestionar usuarios, médicos, disponibilidades y todas las citas.
- Las citas canceladas permanecen almacenadas como parte del historial.
- La base de datos H2 se ejecuta en memoria, por lo que los datos se pierden al detener la aplicación.
