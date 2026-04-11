-- Vincula cada cuenta a un usuario propietario
ALTER TABLE cuentas ADD COLUMN IF NOT EXISTS owner_id UUID;
ALTER TABLE cuentas ADD CONSTRAINT fk_cuentas_owner
    FOREIGN KEY (owner_id) REFERENCES usuarios(id);
CREATE INDEX IF NOT EXISTS idx_cuentas_owner_id ON cuentas(owner_id);
