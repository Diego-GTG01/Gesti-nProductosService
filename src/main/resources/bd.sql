------------------------------------------------------------
-- LIMPIEZA (OPCIONAL)
------------------------------------------------------------

DROP TABLE AuditoriaProducto CASCADE CONSTRAINTS;
DROP TABLE Producto CASCADE CONSTRAINTS;
DROP TABLE TipoOperacion CASCADE CONSTRAINTS;
DROP TABLE Departamento CASCADE CONSTRAINTS;
DROP TABLE Usuario CASCADE CONSTRAINTS;

------------------------------------------------------------
-- TABLA USUARIO
------------------------------------------------------------

CREATE TABLE Usuario (

    idUsuario          NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre             VARCHAR2(100) NOT NULL,
    apellidoPaterno    VARCHAR2(100) NOT NULL,
    apellidoMaterno    VARCHAR2(100),
    username           VARCHAR2(100) NOT NULL UNIQUE,
    email              VARCHAR2(255) NOT NULL UNIQUE,
    celular            VARCHAR2(30) NOT NULL,
    telefono           VARCHAR2(30) NOT NULL,
    password           VARCHAR2(255) NOT NULL,

    PRIMARY KEY (idUsuario)

);

------------------------------------------------------------
-- CATÁLOGO DE DEPARTAMENTOS
------------------------------------------------------------

CREATE TABLE Departamento (

    idDepartamento     NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre             VARCHAR2(100) NOT NULL,
    prefijo            VARCHAR2(5) NOT NULL UNIQUE,
    descripcion        VARCHAR2(250),
    status             NUMBER(1) DEFAULT 1 NOT NULL,

    PRIMARY KEY (idDepartamento),

    CONSTRAINT chkDepartamentoStatus
        CHECK (status IN (0,1))

);

------------------------------------------------------------
-- CATÁLOGO DE TIPO DE OPERACIÓN
------------------------------------------------------------

CREATE TABLE TipoOperacion (

    idTipoOperacion    NUMBER PRIMARY KEY,
    nombre             VARCHAR2(50) NOT NULL UNIQUE

);

------------------------------------------------------------
-- TABLA PRODUCTO
------------------------------------------------------------

CREATE TABLE Producto (
    idProducto             NUMBER GENERATED ALWAYS AS IDENTITY,
    folio                  VARCHAR2(50) NOT NULL UNIQUE,
    clave                  VARCHAR2(10) NOT NULL,
    nombre                 VARCHAR2(200) NOT NULL,
    descripcion            VARCHAR2(500),
    precio                 NUMBER(12,2) NOT NULL,
    status                 NUMBER(1) DEFAULT 1 NOT NULL,
    fechaRegistro          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fechaActualizacion     TIMESTAMP NOT NULL,
    imagen                 CLOB,

    idDepartamento         NUMBER NOT NULL,
    idUsuario              NUMBER NOT NULL,

    PRIMARY KEY (idProducto),

    CONSTRAINT fkProductoDepartamento
        FOREIGN KEY (idDepartamento)
        REFERENCES Departamento(idDepartamento),

    CONSTRAINT fkProductoUsuario
        FOREIGN KEY (idUsuario)
        REFERENCES Usuario(idUsuario),

    CONSTRAINT chkProductoPrecio
        CHECK (precio >= 0)

);

------------------------------------------------------------
-- TABLA AUDITORÍA DE PRODUCTOS
------------------------------------------------------------

CREATE TABLE AuditoriaProducto (
    idAuditoria          NUMBER GENERATED ALWAYS AS IDENTITY,
    idProducto           NUMBER NOT NULL,
    idTipoOperacion      NUMBER NOT NULL,
    descripcionCambio    VARCHAR2(1000) NOT NULL,
    fechaOperacion       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    idUsuario            NUMBER NOT NULL,
    PRIMARY KEY (idAuditoria),
    CONSTRAINT fkAuditoriaProducto
        FOREIGN KEY (idProducto)
        REFERENCES Producto(idProducto),

    CONSTRAINT fkAuditoriaUsuario
        FOREIGN KEY (idUsuario)
        REFERENCES Usuario(idUsuario),

    CONSTRAINT fkAuditoriaTipoOperacion
        FOREIGN KEY (idTipoOperacion)
        REFERENCES TipoOperacion(idTipoOperacion)

);

------------------------------------------------------------
-- ÍNDICES
------------------------------------------------------------

CREATE INDEX idxProductoNombre
ON Producto(nombre);

CREATE INDEX idxProductoClave
ON Producto(clave);

CREATE INDEX idxProductoPrecio
ON Producto(precio);

CREATE INDEX idxProductoDepartamento
ON Producto(idDepartamento);

CREATE INDEX idxProductoStatus
ON Producto(status);

CREATE INDEX idxAuditoriaProducto
ON AuditoriaProducto(idProducto);

CREATE INDEX idxAuditoriaUsuario
ON AuditoriaProducto(idUsuario);

CREATE INDEX idxAuditoriaFecha
ON AuditoriaProducto(fechaOperacion);

------------------------------------------------------------
-- CATÁLOGO DE DEPARTAMENTOS
------------------------------------------------------------

INSERT INTO Departamento (nombre, prefijo, descripcion)
VALUES ('Tecnologías de la Información', 'TI', 'Equipo de cómputo y software');

INSERT INTO Departamento (nombre, prefijo, descripcion)
VALUES ('Recursos Humanos', 'RH', 'Material para personal');

INSERT INTO Departamento (nombre, prefijo, descripcion)
VALUES ('Finanzas', 'FIN', 'Equipamiento financiero');

INSERT INTO Departamento (nombre, prefijo, descripcion)
VALUES ('Servicios Generales', 'SG', 'Mobiliario e infraestructura');

INSERT INTO Departamento (nombre, prefijo, descripcion)
VALUES ('Compras', 'COM', 'Adquisiciones');

------------------------------------------------------------
-- CATÁLOGO TIPO DE OPERACIÓN
------------------------------------------------------------

INSERT INTO TipoOperacion VALUES (1, 'ALTA');
INSERT INTO TipoOperacion VALUES (2, 'MODIFICACION');
INSERT INTO TipoOperacion VALUES (3, 'CAMBIO ESTADO');
INSERT INTO TipoOperacion VALUES (4, 'ELIMINACION LOGICA');
INSERT INTO TipoOperacion VALUES (5, 'RESTAURACION');

------------------------------------------------------------
-- USUARIOS DE PRUEBA
------------------------------------------------------------

INSERT INTO Usuario (
    nombre,
    apellidoPaterno,
    apellidoMaterno,
    username,
    email,
    celular,
    telefono,
    password
)
VALUES (
    'Carlos',
    'Mendoza',
    'García',
    'carlos.mendoza',
    'carlos.mendoza@institucion.gob.mx',
    '5512345678',
    '5555112233',
    '$2a$10$vX9B21m1nE9uO7KpR6tYueFpQ1vR2oX8yZ3wK4vM5uN6tP7qR8sS2'
);

INSERT INTO Usuario (
    nombre,
    apellidoPaterno,
    apellidoMaterno,
    username,
    email,
    celular,
    telefono,
    password
)
VALUES (
    'Ana',
    'Martínez',
    NULL,
    'ana.martinez',
    'ana.martinez@institucion.gob.mx',
    '5587654321',
    '5555443322',
    '$2a$10$eI0Y2b3c4d5e6f7g8h9i0jK1lM2nO3pQ4rS5tU6vW7xY8zA9bC0dE'
);

------------------------------------------------------------
-- PRODUCTOS DE PRUEBA
------------------------------------------------------------

INSERT INTO Producto (
    folio,
    clave,
    nombre,
    descripcion,
    precio,
    status,
    fechaRegistro,
    fechaActualizacion,
    imagen,
    idDepartamento,
    idUsuario
)
VALUES (
    'TI-20260708153001',
    'LAP001',
    'Laptop Dell Latitude 5550',
    'Laptop institucional con procesador Intel Core i7, 16 GB de RAM, SSD de 512 GB y Windows 11 Pro.',
    18500.50,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL,
    1,
    1
);

INSERT INTO Producto (
    folio,
    clave,
    nombre,
    descripcion,
    precio,
    status,
    fechaRegistro,
    fechaActualizacion,
    imagen,
    idDepartamento,
    idUsuario
)
VALUES (
    'SG-20260708153245',
    'DESK001',
    'Escritorio Ejecutivo',
    'Escritorio ergonómico fabricado en melamina con estructura metálica para oficinas administrativas.',
    4200.00,
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    NULL,
    4,
    2
);

------------------------------------------------------------
-- AUDITORÍA DE EJEMPLO
------------------------------------------------------------

INSERT INTO AuditoriaProducto (
    idProducto,
    idTipoOperacion,
    descripcionCambio,
    fechaOperacion,
    idUsuario
)
VALUES (
    1,
    1,
    'Producto registrado correctamente.',
    CURRENT_TIMESTAMP,
    1
);

INSERT INTO AuditoriaProducto (
    idProducto,
    idTipoOperacion,
    descripcionCambio,
    fechaOperacion,
    idUsuario
)
VALUES (
    2,
    1,
    'Producto registrado correctamente.',
    CURRENT_TIMESTAMP,
    2
);

------------------------------------------------------------
-- CONFIRMAR CAMBIOS
------------------------------------------------------------

COMMIT;
