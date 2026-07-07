# Sistema de Gestión de Citas de Hospital

## Descripción

Este proyecto es un sistema desarrollado con Spring Boot para gestionar usuarios, médicos y disponibilidad horaria de médicos dentro de un hospital.

El objetivo del Avance I es dejar funcionando la base del sistema: autenticación, roles, gestión de usuarios/pacientes, gestión de médicos y administración de horarios disponibles.

## Módulos implementados en el Avance I

- Registro e inicio de sesión.
- Recuperación de contraseña.
- Gestión de pacientes/usuarios.
- Gestión de médicos.
- Gestión de disponibilidad horaria.
- Seguridad con roles USUARIO y ADMIN.

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

## Notas importantes

- Las contraseñas se guardan encriptadas con BCrypt.
- Los usuarios y médicos se desactivan mediante borrado lógico.
- Los usuarios normales pueden ver médicos y disponibilidades, pero no pueden administrar.
- El administrador puede gestionar usuarios, médicos y disponibilidades.
