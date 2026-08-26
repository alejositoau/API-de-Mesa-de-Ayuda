# API de Mesa de Ayuda (Helpdesk) con SLA — Spring Boot + JWT

API REST para gestión de tickets de soporte técnico con cálculo automático de SLA por prioridad, autenticación con JWT (access token + refresh token) y autorización por rol (RBAC).

## Stack tecnológico

| Componente     | Tecnología                         |
|----------------|-------------------------------------|
| Lenguaje       | Java 21                             |
| Framework      | Spring Boot 3.3.4                   |
| Seguridad      | Spring Security + JWT (jjwt 0.12.6) |
| Persistencia   | Spring Data JPA                     |
| Base de datos  | H2 (en memoria)                     |
| Build          | Maven                               |

## Cómo ejecutar el proyecto

1. Clonar el repositorio y ubicarse en la carpeta del proyecto:
   ```bash
   cd spring-security-example
   ```
2. Ejecutar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```
   En Windows (CMD/PowerShell):
   ```powershell
   mvnw.cmd spring-boot:run
   ```
3. La API queda disponible en `http://localhost:8080`.
4. Consola de H2 disponible en `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:securitydb`, usuario `sa`, sin contraseña).

Para compilar y correr las pruebas sin levantar el servidor:
```bash
./mvnw clean verify
```

### Usuarios semilla

Al iniciar la aplicación se crean automáticamente tres usuarios de prueba:

| Email                        | Password     | Rol      |
|------------------------------|--------------|----------|
| admin@mesadeayuda.com        | admin123     | ADMIN    |
| soporte@mesadeayuda.com      | soporte123   | SOPORTE  |
| usuario@mesadeayuda.com      | usuario123   | USUARIO  |

## Modelo de datos

**Usuario**: `id`, `nombre`, `email` (único), `password` (cifrado con BCrypt), `rol` (`USUARIO`, `SOPORTE`, `ADMIN`).

**Ticket**: `id`, `titulo`, `descripcion`, `prioridad` (`BAJA`/`MEDIA`/`ALTA`), `estado` (`ABIERTO`/`EN_PROCESO`/`RESUELTO`, por defecto `ABIERTO`), `creadoEn`, `slaVenceEn`, `creadoPor`. El campo `vencido` se calcula en cada respuesta (no se persiste): es `true` si la fecha actual superó `slaVenceEn` y el ticket no está `RESUELTO`.

**Regla de negocio del SLA** — al crear un ticket, el servidor calcula `slaVenceEn` sumando a la fecha de creación:

| Prioridad | Horas de SLA |
|-----------|--------------|
| ALTA      | 4            |
| MEDIA     | 24           |
| BAJA      | 72           |

El cliente nunca envía `estado`, `slaVenceEn` ni `creadoPor`; el servidor los define siempre, sin importar lo que llegue en el body.

## Estrategia de autenticación: Access Token + Refresh Token

El sistema usa dos tokens con propósitos distintos, tal como pide la guía:

| Token          | Vida útil | Uso                                                  |
|----------------|-----------|-------------------------------------------------------|
| Access token   | 15 min    | Va en cada petición protegida (`Authorization: Bearer`) |
| Refresh token  | 7 días    | Solo sirve para pedir un nuevo access token en `/api/auth/refresh` |

### Opción elegida: Opción A — Refresh token persistido en base de datos

Se creó la entidad `RefreshToken` (`id`, `token` UUID único, `usuario`, `expiraEn`, `revocado`). Se eligió esta opción sobre el refresh token como JWT stateless (Opción B) por lo siguiente:

- **Permite revocación real**: en el `logout`, se marca `revocado = true` en base de datos, así que un `refreshToken` usado después de cerrar sesión responde `401` de inmediato. Con un refresh token JWT stateless esto no sería posible sin mantener una *denylist* aparte (que en la práctica termina siendo lo mismo que esta tabla, pero menos explícito).
- **Rotación en cada refresh**: cada vez que se usa `/api/auth/refresh`, el token anterior se revoca y se emite uno nuevo. Esto limita el daño si un refresh token se filtra: solo es válido hasta el próximo uso legítimo.
- **Es la opción más didáctica**: obliga a razonar explícitamente sobre el ciclo de vida del token (creación, expiración, revocación), que es justo lo que pide el objetivo de aprendizaje del taller.
- El costo (una tabla y una consulta extra por request de refresh) es asumible para el tamaño de este proyecto.

## Endpoints

### Rutas públicas

| Método | Ruta                  | Descripción                              |
|--------|-----------------------|-------------------------------------------|
| POST   | `/api/auth/registro`  | Registra un usuario con rol `USUARIO`    |
| POST   | `/api/auth/login`     | Autentica y devuelve `accessToken` + `refreshToken` |
| POST   | `/api/auth/refresh`   | Renueva el `accessToken` (rota el refresh) |
| GET    | `/api/ping`           | Verifica que la API está viva (`pong`)   |

### Rutas protegidas (cualquier usuario autenticado)

| Método | Ruta                  | Descripción                              |
|--------|-----------------------|-------------------------------------------|
| POST   | `/api/auth/logout`    | Revoca los refresh tokens del usuario autenticado |
| POST   | `/api/tickets`        | Crea un ticket (el creador es el usuario autenticado) |
| GET    | `/api/tickets/mios`   | Lista los tickets creados por el usuario autenticado |
| GET    | `/api/tickets/{id}`   | Consulta un ticket (solo dueño o rol `SOPORTE`/`ADMIN`) |

### Rutas protegidas por rol

| Método | Ruta                          | Rol requerido      | Descripción                       |
|--------|-------------------------------|---------------------|-------------------------------------|
| GET    | `/api/tickets`                | `SOPORTE`, `ADMIN`  | Lista todos los tickets            |
| PATCH  | `/api/tickets/{id}/estado`    | `SOPORTE`, `ADMIN`  | Cambia el estado de un ticket      |
| GET    | `/api/tickets/vencidos`       | `SOPORTE`, `ADMIN`  | Lista los tickets que superaron su SLA |
| POST   | `/api/admin/soporte`          | `ADMIN`             | Asciende a un usuario existente al rol `SOPORTE` |

## Códigos de respuesta

| Situación                                  | Código |
|---------------------------------------------|--------|
| Creación exitosa                             | 201    |
| Consulta exitosa                             | 200    |
| Datos inválidos                              | 400    |
| Sin token / token inválido / refresh inválido| 401    |
| Rol insuficiente / acceso a recurso ajeno    | 403    |
| Recurso no encontrado                        | 404    |
| Email ya registrado                          | 409    |

## Pruebas

En la carpeta `postman/` se incluye la colección `API-Mesa-de-Ayuda.postman_collection.json` y el entorno `API-Mesa-de-Ayuda.postman_environment.json` con pruebas automatizadas (`pm.test`) para cada endpoint, incluyendo los casos de error (401, 403, 404, 409, 400).
