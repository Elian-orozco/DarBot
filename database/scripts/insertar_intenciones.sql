-- Insertar intenciones
INSERT INTO intenciones (nombre, descripcion, prioridad, activa, fecha_creacion, fecha_actualizacion)
VALUES 
('CONSULTAR_EVENTOS', 'Consulta sobre eventos, actividades y reuniones', 10, true, NOW(), NOW()),
('CONSULTAR_NOTICIAS', 'Consulta sobre noticias, novedades y publicaciones', 9, true, NOW(), NOW()),
('CONSULTAR_DOCUMENTOS', 'Consulta sobre documentos, manuales y formatos', 8, true, NOW(), NOW()),
('CONSULTAR_SEDES', 'Consulta sobre ubicación y datos de sedes', 7, true, NOW(), NOW()),
('CONSULTAR_CONTACTOS', 'Consulta sobre contactos, teléfonos y correos', 7, true, NOW(), NOW()),
('CONSULTAR_HORARIOS', 'Consulta sobre horarios de atención', 6, true, NOW(), NOW()),
('CONSULTAR_SERVICIOS', 'Consulta sobre servicios institucionales', 5, true, NOW(), NOW()),
('CONSULTAR_INSTITUCION', 'Consulta sobre información general de la institución', 4, true, NOW(), NOW());

-- Insertar palabras clave para EVENTOS
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT id, palabra, 1 FROM intenciones WHERE nombre = 'CONSULTAR_EVENTOS'
CROSS JOIN (VALUES 
  ('evento'), ('eventos'), ('actividad'), ('actividades'), 
  ('reunion'), ('reuniones'), ('agenda'), ('calendario'), 
  ('proximo'), ('proximos'), ('feria'), ('taller')
) AS p(palabra);

-- Insertar palabras clave para NOTICIAS
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT id, palabra, 1 FROM intenciones WHERE nombre = 'CONSULTAR_NOTICIAS'
CROSS JOIN (VALUES 
  ('noticia'), ('noticias'), ('novedad'), ('novedades'), 
  ('publicacion'), ('publicaciones'), ('actualidad'), ('boletin')
) AS p(palabra);

-- Insertar palabras clave para DOCUMENTOS
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT id, palabra, 1 FROM intenciones WHERE nombre = 'CONSULTAR_DOCUMENTOS'
CROSS JOIN (VALUES 
  ('documento'), ('documentos'), ('manual'), ('manuales'), 
  ('circular'), ('circulares'), ('formato'), ('formatos'), 
  ('archivo'), ('archivos'), ('descargar'), ('pdf'), 
  ('guia'), ('guias')
) AS p(palabra);

-- Insertar palabras clave para SEDES
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT id, palabra, 1 FROM intenciones WHERE nombre = 'CONSULTAR_SEDES'
CROSS JOIN (VALUES 
  ('sede'), ('sedes'), ('ubicacion'), ('ubicación'), 
  ('campus'), ('direccion'), ('dirección'), 
  ('donde queda'), ('donde está')
) AS p(palabra);

-- Insertar palabras clave para CONTACTOS
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT id, palabra, 1 FROM intenciones WHERE nombre = 'CONSULTAR_CONTACTOS'
CROSS JOIN (VALUES 
  ('contacto'), ('contactos'), ('telefono'), ('teléfono'), 
  ('correo'), ('email'), ('llamar'), ('escribir'), 
  ('comunicarse'), ('mensaje'), ('whatsapp')
) AS p(palabra);

-- Insertar palabras clave para HORARIOS
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT id, palabra, 1 FROM intenciones WHERE nombre = 'CONSULTAR_HORARIOS'
CROSS JOIN (VALUES 
  ('horario'), ('horarios'), ('hora'), ('atencion'), 
  ('atención'), ('abre'), ('abren'), ('cierra'), 
  ('cierran'), ('jornada'), ('turno')
) AS p(palabra);

-- Insertar palabras clave para SERVICIOS
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT id, palabra, 1 FROM intenciones WHERE nombre = 'CONSULTAR_SERVICIOS'
CROSS JOIN (VALUES 
  ('servicio'), ('servicios'), ('biblioteca'), 
  ('cafeteria'), ('cafetería'), ('enfermeria'), 
  ('enfermería'), ('computo'), ('cómputo')
) AS p(palabra);

-- Insertar palabras clave para INSTITUCION
INSERT INTO palabras_clave_intencion (intencion_id, palabra, peso)
SELECT id, palabra, 1 FROM intenciones WHERE nombre = 'CONSULTAR_INSTITUCION'
CROSS JOIN (VALUES 
  ('institucion'), ('institución'), ('colegio'), 
  ('informacion'), ('información'), ('quienes somos'), 
  ('historia'), ('mision'), ('misión'), 
  ('vision'), ('visión'), ('valores')
) AS p(palabra);
