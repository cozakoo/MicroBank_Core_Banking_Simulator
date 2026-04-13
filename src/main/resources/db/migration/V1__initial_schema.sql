-- 1. Tabla principal de usuarios
CREATE TABLE usuarios (
                          id UUID PRIMARY KEY,
                          username VARCHAR(50) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL
);

-- 2. Tabla secundaria para los roles (ElementCollection)
CREATE TABLE usuario_roles (
                               usuario_id UUID NOT NULL,
                               rol_nombre VARCHAR(50) NOT NULL,
                               CONSTRAINT fk_usuario_roles_usuario
                                   FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. Índices para optimizar búsquedas (como definiste en la entidad)
CREATE UNIQUE INDEX idx_usuarios_username ON usuarios (username);
CREATE INDEX idx_usuario_roles_id ON usuario_roles (usuario_id);

CREATE TABLE cuentas (
                         id UUID PRIMARY KEY,
                         numero_cuenta VARCHAR(20) NOT NULL UNIQUE,
                         tipo_cuenta VARCHAR(20) NOT NULL,
                         balance NUMERIC(19, 4) NOT NULL,
                         status VARCHAR(20) NOT NULL,
                         created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                         updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_cuentas_numero ON cuentas (numero_cuenta);
CREATE INDEX idx_cuentas_status ON cuentas (status);

CREATE TABLE transacciones (
                               id UUID PRIMARY KEY,
                               source_account_id UUID NOT NULL,
                               target_account_id UUID,
                               amount NUMERIC(38, 2) NOT NULL,
                               type VARCHAR(50) NOT NULL,
                               status VARCHAR(50),
                               description VARCHAR(255),
                               created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_source ON transacciones (source_account_id);
CREATE INDEX idx_target ON transacciones (target_account_id);