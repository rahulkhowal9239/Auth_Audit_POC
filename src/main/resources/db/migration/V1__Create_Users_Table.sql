CREATE TABLE organization (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    organization_id VARCHAR(255) NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    organization_id VARCHAR(255) NOT NULL
);

CREATE TYPE AuditAction AS ENUM (
    'CREATE',
    'READ',
    'UPDATE',
    'DELETE'
);
CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    organization_id VARCHAR(255),
    table_name VARCHAR(50),
    record_id VARCHAR(50),
    action AuditAction,
    old_data TEXT,
    new_data TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    user_id VARCHAR(50),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO organization (name, description, created_at, updated_at, organization_id)
VALUES ('Tech Solutions Inc.', 'Providing IT solutions worldwide', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid());

INSERT INTO organization (name, description, created_at, updated_at, organization_id)
VALUES ('Global Logistics Ltd.', 'Logistics and supply chain management', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid());

INSERT INTO organization (name, description, created_at, updated_at, organization_id)
VALUES ('Creative Media Agency', 'Digital marketing and media services', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid());

INSERT INTO organization (name, description, created_at, updated_at, organization_id)
VALUES ('Healthcare Innovations Group', 'Healthcare technology solutions', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid());

INSERT INTO organization (name, description, created_at, updated_at, organization_id)
VALUES ('Education Foundation', 'Promoting education initiatives', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, gen_random_uuid());

