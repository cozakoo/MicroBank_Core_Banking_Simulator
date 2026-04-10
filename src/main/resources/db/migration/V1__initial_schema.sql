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
