CREATE TABLE auditoria_logs (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL,
    details TEXT,
    user_id VARCHAR(50),
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_audit_account ON auditoria_logs (account_id);
CREATE INDEX idx_audit_action ON auditoria_logs (action);
CREATE INDEX idx_audit_timestamp ON auditoria_logs (timestamp);
