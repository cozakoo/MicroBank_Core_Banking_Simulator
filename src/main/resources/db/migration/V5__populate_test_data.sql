-- ============================================================================
-- V5 - Populate Test Data: 2 Users with 3 Accounts Each
-- ============================================================================
-- Users:
--   1. martin / password: martin123
--   2. lucas / password: lucas123
-- Each user has 3 account types (CORRIENTE, AHORRO, CREDITO) with memorable aliases
-- ============================================================================

-- Insert Users
INSERT INTO usuarios (id, username, password) VALUES
  ('550e8400-e29b-41d4-a716-446655440001', 'martin', '$2a$10$W9JqZV9/PjJY/T9IczKY4Op5FqsaX3dAJ8bE5Zy8HvFQvLOq2p3Cm'),  -- password: martin123
  ('550e8400-e29b-41d4-a716-446655440002', 'lucas', '$2a$10$k2bYLcN8Pu2YZjFs3RVcVeMU7FtqX5mKzV9X8b0K1m9c5xQl7sJ.C');   -- password: lucas123

-- Insert Roles for Users
INSERT INTO usuario_roles (usuario_id, rol_nombre) VALUES
  ('550e8400-e29b-41d4-a716-446655440001', 'ROLE_USER'),
  ('550e8400-e29b-41d4-a716-446655440002', 'ROLE_USER');

-- ============================================================================
-- MARTIN'S ACCOUNTS
-- ============================================================================
INSERT INTO cuentas (id, numero_cuenta, tipo_cuenta, balance, status, owner_id, alias, created_at, updated_at) VALUES
  -- Cuenta Corriente de Martín
  ('650e8400-e29b-41d4-a716-446655440001', 'ACC-MARTIN-00001', 'CORRIENTE', 5000.00, 'ACTIVO', '550e8400-e29b-41d4-a716-446655440001', 'martin-corriente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  -- Cuenta de Ahorro de Martín
  ('650e8400-e29b-41d4-a716-446655440002', 'ACC-MARTIN-00002', 'AHORRO', 15000.00, 'ACTIVO', '550e8400-e29b-41d4-a716-446655440001', 'martin-ahorro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  -- Cuenta de Crédito de Martín
  ('650e8400-e29b-41d4-a716-446655440003', 'ACC-MARTIN-00003', 'CREDITO', 50000.00, 'ACTIVO', '550e8400-e29b-41d4-a716-446655440001', 'martin-credito', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================================
-- LUCAS'S ACCOUNTS
-- ============================================================================
INSERT INTO cuentas (id, numero_cuenta, tipo_cuenta, balance, status, owner_id, alias, created_at, updated_at) VALUES
  -- Cuenta Corriente de Lucas
  ('650e8400-e29b-41d4-a716-446655440004', 'ACC-LUCAS-00001', 'CORRIENTE', 8000.00, 'ACTIVO', '550e8400-e29b-41d4-a716-446655440002', 'lucas-corriente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  -- Cuenta de Ahorro de Lucas
  ('650e8400-e29b-41d4-a716-446655440005', 'ACC-LUCAS-00002', 'AHORRO', 25000.00, 'ACTIVO', '550e8400-e29b-41d4-a716-446655440002', 'lucas-ahorro', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  -- Cuenta de Crédito de Lucas
  ('650e8400-e29b-41d4-a716-446655440006', 'ACC-LUCAS-00003', 'CREDITO', 100000.00, 'ACTIVO', '550e8400-e29b-41d4-a716-446655440002', 'lucas-credito', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================================
-- Verification Queries (uncomment to verify):
-- SELECT * FROM usuarios;
-- SELECT u.username, COUNT(c.id) as num_cuentas FROM usuarios u LEFT JOIN cuentas c ON u.id = c.owner_id GROUP BY u.id, u.username;
-- SELECT owner_id, tipo_cuenta, alias, numero_cuenta, balance FROM cuentas ORDER BY owner_id, tipo_cuenta;
-- ============================================================================
