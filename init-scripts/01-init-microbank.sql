-- Database initialization for MicroBank Core Banking Simulator
-- These scripts are executed automatically when the PostgreSQL container is first created

-- Create a schema if needed (though JPA often handles this)
-- CREATE SCHEMA IF NOT EXISTS microbank;

-- Example: Create initial records for testing
-- INSERT INTO microbank.accounts (id, balance, status) VALUES (1, 1000.0, 'ACTIVE');

-- Note: In this project, JPA's ddl-auto: update will handle schema creation,
-- but this script is useful for setting up specific roles, extensions, or base data.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Log the initialization
DO $$
BEGIN
   RAISE NOTICE 'Database initialization script completed for Microbank DB';
END $$;
