# DarBot

Plataforma institucional con asistente conversacional para la I.E. Darío Torregroza Pérez. El proyecto está orientado a ofrecer información institucional, gestión de contenido y un chatbot capaz de responder preguntas frecuentes sobre eventos, noticias, sedes, contactos, servicios, horarios e información general de la institución.

## Descripción general

DarBot combina un backend en Java con Spring Boot, autenticación JWT, base de datos PostgreSQL y un módulo de chatbot basado en intenciones y palabras clave. La idea principal es centralizar la información institucional y facilitar el acceso a ella mediante una API REST que puede ser consumida por una interfaz web o por otros servicios.

El proyecto actual está enfocado principalmente en el backend, aunque la estructura contempla la posibilidad de integrar una interfaz frontend en el futuro. Actualmente la carpeta frontend está vacía y el sistema funcional se centra en la API y la lógica de negocio del backend.

## Objetivo del proyecto

- Proveer información institucional a la comunidad educativa.
- Dar acceso a contenidos, sedes, contact information, servicios y horarios.
- Implementar un asistente conversacional para responder preguntas frecuentes.
- Gestionar usuarios y roles con autenticación segura.
- Servir como base para una solución web más completa y escalable.

## Stack tecnológico

- Java 17
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security
- JWT (Json Web Token)
- PostgreSQL 15
- Maven
- Lombok
- Docker / Docker Compose
- JUnit + Spring Boot Test

## Arquitectura del proyecto

El backend está organizado por módulos funcionales dentro del paquete `com.darbot`:

- `auth`: login, registro, autenticación, JWT y gestión del usuario actual.
- `chatbot`: lógica del asistente conversacional, intenciones, palabras clave y respuestas.
- `contenidos`: gestión de contenidos públicos o administrativos.
- `institucional`: información institucional, sedes, contactos, horarios y datos generales.
- `usuarios`: entidades, repositorios y servicios de usuarios.
- `common`: manejo centralizado de excepciones y utilidades.
- `config`: configuración de seguridad, base de datos y datos iniciales del chatbot.

## Estructura del repositorio

```text
DarBot/
├── README.md
├── docker-compose.yml
├── backend/
│   ├── Dockerfile
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/darbot/
│   │   │   │   ├── auth/
│   │   │   │   ├── chatbot/
│   │   │   │   ├── common/
│   │   │   │   ├── config/
│   │   │   │   ├── contenidos/
│   │   │   │   ├── institucional/
│   │   │   │   └── usuarios/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-local.properties
│   │   │       ├── application.yaml
│   │   │       └── static/
│   │   └── test/java/
│   └── target/
├── database/
│   └── scripts/
│       ├── insertar_intenciones.sql
│       └── insertar_intenciones_v2.sql
├── frontend/
│   └── (vacío por ahora)
└── docs/
    ├── arquitectura/
    ├── base-datos/
    └── requisitos/
```

## Funcionalidades actuales

### 1. Autenticación y usuarios

- Registro de usuarios.
- Inicio de sesión con credenciales y retorno de JWT.
- Consulta del usuario autenticado mediante `/api/auth/me`.
- Seguridad basada en roles y filtros JWT.
- Endpoints públicos para login, registro y logout.

### 2. Chatbot institucional

El módulo `chatbot` está diseñado para responder preguntas frecuentes usando intenciones y palabras clave. Al iniciar la aplicación, si la base de datos está vacía, se cargan intenciones predeterminadas como:

- `CONSULTAR_EVENTOS`
- `CONSULTAR_NOTICIAS`
- `CONSULTAR_DOCUMENTOS`
- `CONSULTAR_SEDES`
- `CONSULTAR_CONTACTOS`
- `CONSULTAR_HORARIOS`
- `CONSULTAR_SERVICIOS`
- `CONSULTAR_INSTITUCION`

Esto se hace a través de `ChatbotDataInitializer`, que crea intenciones con palabras clave asociadas. En el futuro este sistema se puede complementar con IA, NLP más avanzado o un modelo generativo.

### 3. Información institucional

Se exponen endpoints para consultar:

- Información general de la institución.
- Sedes activas.
- Contactos y medios de comunicación.

### 4. Gestión de contenidos

El proyecto incluye módulos para contenidos y administración de información institucional, con separación entre acceso público y administración.

### 5. Base de datos y datos semilla

La aplicación usa PostgreSQL y el patrón `update` de Hibernate para crear o actualizar tablas automáticamente. Además, se incluye SQL de ejemplo para insertar intenciones del chatbot.

## Seguridad

La aplicación usa Spring Security con sesiones sin estado (`STATELESS`) y filtros JWT.

### rutas abiertas por defecto

- `/api/auth/login`
- `/api/auth/register`
- `/api/auth/logout`
- `/api/institucional/**`
- `/api/contenidos/**`
- `/api/chatbot/pregunta`
- `/actuator/**`

### rutas protegidas

- `/api/auth/me`
- `/api/admin/**`
- cualquier otra ruta que no esté explícitamente permitida

## Requisitos previos

Para trabajar con el proyecto necesitas:

- Java 17 o superior
- Maven o el wrapper incluido (`./mvnw`)
- Docker y Docker Compose (opcional, pero recomendado)
- PostgreSQL 15 o una base equivalente
- Git para control de versiones

## Configuración del proyecto

El archivo principal de configuración es:

- `backend/src/main/resources/application.properties`

Configuración por defecto:

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://darbot_postgres_container:5432/darbot_db}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:darbot_user}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:darbot123}
server.port=${SERVER_PORT:8080}

jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000
jwt.issuer=DarBot
```

## Ejecutar el proyecto

### Opción 1: con Docker Compose (recomendado)

Desde la raíz del proyecto:

```bash
docker compose up --build
```

Esto levanta:

- Base de datos PostgreSQL en `localhost:5432`
- Backend en `http://localhost:8080`

### Opción 2: ejecutar el backend localmente

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

Si quieres usar PostgreSQL local, asegúrate de tenerlo ejecutándose y configurar las variables de entorno o la URL de conexión en `application.properties`.

## Variables de entorno útiles

```bash
SERVER_PORT=8080
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/darbot_db
SPRING_DATASOURCE_USERNAME=darbot_user
SPRING_DATASOURCE_PASSWORD=darbot123
```

## Endpoints principales de la API

### Autenticación

#### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "usuario",
  "password": "password"
}
```

Respuesta esperada:

```json
{
  "token": "jwt-token",
  "username": "usuario",
  "email": "usuario@correo.com",
  "rol": "ADMIN",
  "userId": 1
}
```

#### Registro

```http
POST /api/auth/register
```

#### Usuario autenticado

```http
GET /api/auth/me
Authorization: Bearer <token>
```

### Información institucional

```http
GET /api/institucional/info
GET /api/institucional/sedes
GET /api/institucional/contactos
```

### Chatbot

```http
POST /api/chatbot/pregunta
Content-Type: application/json

{
  "sessionId": "session-123",
  "mensaje": "¿Cuáles son los horarios de atención?"
}
```

## Base de datos

El sistema usa PostgreSQL como base principal. El contenedor definido en `docker-compose.yml` crea la base `darbot_db` y el usuario `darbot_user` con la contraseña `darbot123`.

El archivo SQL ubicado en la carpeta `database/scripts` sirve como referencia para cargar intenciones o datos semilla del chatbot. Es recomendable revisarlos antes de hacer cambios en el comportamiento del asistente.

## Pruebas

El proyecto incluye pruebas en:

- `backend/src/test/java`

Se pueden ejecutar con:

```bash
cd backend
./mvnw test
```

## Recomendaciones para trabajar en el proyecto

1. Mantén una copia del esquema de base de datos y de los scripts de semilla.
2. Antes de modificar la lógica del chatbot, revisa la inicialización de intenciones y palabras clave.
3. Mantén la seguridad de JWT y roles en cada nuevo endpoint.
4. Si agregas nuevas rutas, documenta los permisos en `SecurityConfig`.
5. Si vas a implementar una interfaz frontend, conviene consumir la API REST del backend y mantener una estructura clara de autenticación y sesión.

## Tareas de mejora sugeridas

- Completar el frontend para consumir la API.
- Mejorar el motor del chatbot con un modelo de IA o procesamiento más inteligente del lenguaje.
- Agregar dashboard administrativo para gestión de contenidos e institucional.
- Mejorar validaciones, trazabilidad y auditoría de acciones.
- Añadir pruebas e2e para cubrir autenticación, chatbot e información institucional.
- Configurar despliegue en entornos cloud con CI/CD.

## Estado actual del proyecto

El proyecto ya tiene una base sólida de backend con autenticación, seguridad, base de datos y chatbot institucional. Está listo para seguir desarrollándose, ampliarse y conectarse a una interface de usuario o a nuevos servicios.

## Nota para colaboradores

Si un amigo o nuevo colaborador quiere trabajar en este proyecto, debe entender que el corazón del sistema está en el backend de Spring Boot y que la lógica de negocio principal gira en torno a:

- autenticación
- usuario/roles
- contenidos institucionales
- datos de la institución
- chatbot con intenciones por palabras clave

Con esta documentación, cualquier persona puede entrar al proyecto, entender su alcance, ponerlo en marcha y continuar mejorándolo sin perder contexto.

## Licencia

Este proyecto no especifica una licencia en el `pom.xml`. Si se va a compartir públicamente o reutilizar en otro entorno, conviene definir una licencia adecuada antes de distribuirlo.
