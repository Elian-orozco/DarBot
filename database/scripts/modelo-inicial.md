# Modelo Relacional - DarBot

Este documento define la estructura física de la base de datos en PostgreSQL.

## 1. Seguridad y Usuarios

### Tabla: `usuarios`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `nombre` | VARCHAR(100) | NOT NULL | Nombre del usuario |
| `apellido` | VARCHAR(100) | NOT NULL | Apellido del usuario |
| `correo` | VARCHAR(150) | NOT NULL, UNIQUE | Correo electrónico |
| `password` | VARCHAR(255) | NOT NULL | Hash de la contraseña |
| `activo` | BOOLEAN | DEFAULT TRUE | Estado de la cuenta |
| `fecha_creacion` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Fecha de registro |
| `fecha_actualizacion`| TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Última modificación |

### Tabla: `roles`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `nombre` | VARCHAR(50) | NOT NULL, UNIQUE | Ej: ADMINISTRADOR, EDITOR |

### Tabla: `usuario_rol`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `usuario_id` | BIGINT | PK, FK(usuarios.id) | Referencia al usuario |
| `rol_id` | BIGINT | PK, FK(roles.id) | Referencia al rol |

---

## 2. Información Institucional

### Tabla: `informacion_institucional`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `nombre` | VARCHAR(200) | NOT NULL | Nombre de la institución |
| `historia` | TEXT | | Reseña histórica |
| `mision` | TEXT | | Misión institucional |
| `vision` | TEXT | | Visión institucional |
| `valores` | TEXT | | Valores corporativos |
| `filosofia` | TEXT | | Filosofía |
| `descripcion` | TEXT | | Descripción general |
| `logo_url` | VARCHAR(255) | | Ruta del logo |

### Tabla: `sedes`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `nombre` | VARCHAR(100) | NOT NULL | Nombre de la sede |
| `direccion` | VARCHAR(200) | NOT NULL | Dirección física |
| `telefono` | VARCHAR(50) | | Teléfono principal |
| `jornada` | VARCHAR(100) | | Ej: Mañana, Tarde, Única |
| `descripcion` | TEXT | | Detalles adicionales |
| `activa` | BOOLEAN | DEFAULT TRUE | Estado de la sede |

### Tabla: `areas`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `nombre` | VARCHAR(100) | NOT NULL | Ej: Rectoría, Coordinación |

### Tabla: `contactos`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `area_id` | BIGINT | FK(areas.id) | Área a la que pertenece |
| `tipo` | VARCHAR(50) | NOT NULL | Ej: Telefono, Correo, Horario |
| `valor` | VARCHAR(255) | NOT NULL | El dato de contacto |
| `descripcion` | VARCHAR(255) | | Detalle del contacto |
| `activo` | BOOLEAN | DEFAULT TRUE | Estado del contacto |

---

## 3. Contenido Institucional

### Tabla: `noticias`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `titulo` | VARCHAR(200) | NOT NULL | Titular |
| `resumen` | TEXT | | Breve descripción |
| `contenido` | TEXT | NOT NULL | Cuerpo de la noticia |
| `imagen_url` | VARCHAR(255) | | Ruta de la imagen destacada |
| `fecha_publicacion`| TIMESTAMP | | Cuándo se hace pública |
| `autor_id` | BIGINT | FK(usuarios.id) | Quién la redactó |
| `estado` | VARCHAR(20) | DEFAULT 'BORRADOR' | BORRADOR, PUBLICADA |
| `fecha_creacion` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |
| `fecha_actualizacion`| TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |

### Tabla: `eventos`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `titulo` | VARCHAR(200) | NOT NULL | Nombre del evento |
| `descripcion` | TEXT | | Detalles del evento |
| `fecha` | DATE | NOT NULL | Día del evento |
| `hora_inicio` | TIME | | |
| `hora_fin` | TIME | | |
| `lugar` | VARCHAR(200) | | Dónde se realiza |
| `estado` | VARCHAR(20) | DEFAULT 'PROGRAMADO'| PROGRAMADO, CANCELADO, REALIZADO|
| `fecha_creacion` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |
| `fecha_actualizacion`| TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |

### Tabla: `documentos`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `titulo` | VARCHAR(200) | NOT NULL | Nombre visible |
| `descripcion` | TEXT | | De qué trata |
| `nombre_archivo` | VARCHAR(255) | NOT NULL | Nombre real en disco |
| `ruta_archivo` | VARCHAR(255) | NOT NULL | Path de descarga |
| `tipo` | VARCHAR(50) | | Ej: Manual, Circular, PDF |
| `fecha_publicacion`| TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |
| `estado` | VARCHAR(20) | DEFAULT 'ACTIVO' | ACTIVO, INACTIVO |
| `fecha_creacion` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |

### Tabla: `servicios`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `nombre` | VARCHAR(150) | NOT NULL | Ej: Psicología, Enfermería |
| `descripcion` | TEXT | | |
| `area_id` | BIGINT | FK(areas.id) | Dependencia |
| `contacto` | VARCHAR(255) | | Cómo acceder al servicio |
| `horario` | VARCHAR(255) | | Disponibilidad |
| `activo` | BOOLEAN | DEFAULT TRUE | |

---

## 4. Base de Conocimiento del Chatbot

### Tabla: `faqs`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `pregunta` | TEXT | NOT NULL | La pregunta frecuente |
| `respuesta` | TEXT | NOT NULL | La respuesta fija |
| `categoria` | VARCHAR(100) | | Agrupación lógica |
| `activa` | BOOLEAN | DEFAULT TRUE | |
| `fecha_creacion` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |
| `fecha_actualizacion`| TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | |

### Tabla: `intenciones`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `nombre` | VARCHAR(100) | NOT NULL, UNIQUE | Ej: CONSULTAR_EVENTO |
| `descripcion` | VARCHAR(255) | | Para qué sirve esta intención |
| `activa` | BOOLEAN | DEFAULT TRUE | Si el bot la está procesando |

### Tabla: `preguntas_sin_respuesta`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `pregunta` | TEXT | NOT NULL | Lo que escribió el usuario |
| `fecha` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Cuándo ocurrió |
| `intento_intencion`| VARCHAR(100) | | Qué creyó el bot que era |
| `resuelta` | BOOLEAN | DEFAULT FALSE | Si ya se cubrió este hueco |

---

## 5. Conversaciones

### Tabla: `conversaciones`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `session_id` | VARCHAR(255) | NOT NULL, UNIQUE | ID de la sesión anónima o web |
| `fecha_inicio` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Inicio del chat |
| `fecha_fin` | TIMESTAMP | | Cierre del chat |
| `usuario_id` | BIGINT | FK(usuarios.id) | Opcional (si hizo login) |
| `estado` | VARCHAR(20) | DEFAULT 'ACTIVA' | ACTIVA, CERRADA |

### Tabla: `mensajes`
| Campo | Tipo | Restricciones | Descripción |
| :--- | :--- | :--- | :--- |
| `id` | BIGSERIAL | PK | Identificador único |
| `conversacion_id`| BIGINT | NOT NULL, FK(conversaciones.id)| A qué chat pertenece |
| `tipo` | VARCHAR(20) | NOT NULL | USER, BOT, SYSTEM |
| `contenido` | TEXT | NOT NULL | El texto enviado |
| `intencion_detectada`| VARCHAR(100)| | Para analíticas posteriores |
| `fecha` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Momento exacto |