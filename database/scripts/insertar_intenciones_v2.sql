-- Insertar intenciones (con ON CONFLICT para evitar duplicados)
INSERT INTO intenciones (nombre, descripcion, prioridad, activa, fecha_creacion, fecha_actualizacion)
VALUES 
('CONSULTAR_EVENTOS', 'Consulta sobre eventos, actividades y reuniones', 10, true, NOW(), NOW()),
('CONSULTAR_NOTICIAS', 'Consulta sobre noticias, novedades y publicaciones', 9, true, NOW(), NOW()),
('CONSULTAR_DOCUMENTOS', 'Consulta sobre documentos, manuales y formatos', 8, true, NOW(), NOW()),
('CONSULTAR_SEDES', 'Consulta sobre ubicación y datos de sedes', 7, true, NOW(), NOW()),
('CONSULTAR_CONTACTOS', 'Consulta sobre contactos, teléfonos y correos', 7, true, NOW(), NOW()),
('CONSULTAR_HORARIOS', 'Consulta sobre horarios de atención', 6, true, NOW(), NOW()),
('CONSULTAR_SERVICIOS', 'Consulta sobre servicios institucionales', 5, true, NOW(), NOW()),
('CONSULTAR_INSTITUCION', 'Consulta sobre información general de la institución', 4, true, NOW(), NOW())
ON CONFLICT (nombre) DO UPDATE SET 
  descripcion = EXCLUDED.descripcion,
  prioridad = EXCLUDED.prioridad,
  activa = EXCLUDED.activa,
  fecha_actualizacion = NOW();

-- Insertar palabras clave para EVENTOS (usando WITH para obtener el ID)
WITH intencion_id AS (SELECT id FROM intenciones WHERE nombre = 'CONSULTAR_EVENTOS')
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT (SELECT id FROM intencion_id), palabra, 1 FROM (VALUES 
  ('evento'), ('eventos'), ('actividad'), ('actividades'), 
  ('reunion'), ('reuniones'), ('agenda'), ('calendario'), 
  ('proximo'), ('proximos'), ('feria'), ('taller')
) AS p(palabra)
ON CONFLICT (intencion_id, palabra) DO NOTHING;

-- Insertar palabras clave para NOTICIAS
WITH intencion_id AS (SELECT id FROM intenciones WHERE nombre = 'CONSULTAR_NOTICIAS')
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT (SELECT id FROM intencion_id), palabra, 1 FROM (VALUES 
  ('noticia'), ('noticias'), ('novedad'), ('novedades'), 
  ('publicacion'), ('publicaciones'), ('actualidad'), ('boletin')
) AS p(palabra)
ON CONFLICT (intencion_id, palabra) DO NOTHING;

-- Insertar palabras clave para DOCUMENTOS
WITH intencion_id AS (SELECT id FROM intenciones WHERE nombre = 'CONSULTAR_DOCUMENTOS')
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT (SELECT id FROM intencion_id), palabra, 1 FROM (VALUES 
  ('documento'), ('documentos'), ('manual'), ('manuales'), 
  ('circular'), ('circulares'), ('formato'), ('formatos'), 
  ('archivo'), ('archivos'), ('descargar'), ('pdf'), 
  ('guia'), ('guias')
) AS p(palabra)
ON CONFLICT (intencion_id, palabra) DO NOTHING;

-- Insertar palabras clave para SEDES
WITH intencion_id AS (SELECT id FROM intenciones WHERE nombre = 'CONSULTAR_SEDES')
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT (SELECT id FROM intencion_id), palabra, 1 FROM (VALUES 
  ('sede'), ('sedes'), ('ubicacion'), ('ubicación'), 
  ('campus'), ('direccion'), ('dirección'), 
  ('donde queda'), ('donde está')
) AS p(palabra)
ON CONFLICT (intencion_id, palabra) DO NOTHING;

-- Insertar palabras clave para CONTACTOS
WITH intencion_id AS (SELECT id FROM intenciones WHERE nombre = 'CONSULTAR_CONTACTOS')
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT (SELECT id FROM intencion_id), palabra, 1 FROM (VALUES 
  ('contacto'), ('contactos'), ('telefono'), ('teléfono'), 
  ('correo'), ('email'), ('llamar'), ('escribir'), 
  ('comunicarse'), ('mensaje'), ('whatsapp')
) AS p(palabra)
ON CONFLICT (intencion_id, palabra) DO NOTHING;

-- Insertar palabras clave para HORARIOS
WITH intencion_id AS (SELECT id FROM intenciones WHERE nombre = 'CONSULTAR_HORARIOS')
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT (SELECT id FROM intencion_id), palabra, 1 FROM (VALUES 
  ('horario'), ('horarios'), ('hora'), ('atencion'), 
  ('atención'), ('abre'), ('abren'), ('cierra'), 
  ('cierran'), ('jornada'), ('turno')
) AS p(palabra)
ON CONFLICT (intencion_id, palabra) DO NOTHING;

-- Insertar palabras clave para SERVICIOS
WITH intencion_id AS (SELECT id FROM intenciones WHERE nombre = 'CONSULTAR_SERVICIOS')
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT (SELECT id FROM intencion_id), palabra, 1 FROM (VALUES 
  ('servicio'), ('servicios'), ('biblioteca'), 
  ('cafeteria'), ('cafetería'), ('enfermeria'), 
  ('enfermería'), ('computo'), ('cómputo')
) AS p(palabra)
ON CONFLICT (intencion_id, palabra) DO NOTHING;

-- Insertar palabras clave para INSTITUCION
WITH intencion_id AS (SELECT id FROM intenciones WHERE nombre = 'CONSULTAR_INSTITUCION')
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT (SELECT id FROM intencion_id), palabra, 1 FROM (VALUES 
  ('institucion'), ('institución'), ('colegio'), 
  ('informacion'), ('información'), ('quienes somos'), 
  ('historia'), ('mision'), ('misión'), 
  ('vision'), ('visión'), ('valores')
) AS p(palabra)
ON CONFLICT (intencion_id, palabra) DO NOTHING;
