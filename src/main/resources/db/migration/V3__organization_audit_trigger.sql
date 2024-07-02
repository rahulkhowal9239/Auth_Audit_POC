-- For organization table
CREATE TRIGGER organization_audit_trigger
AFTER INSERT OR UPDATE OR DELETE
ON organization
FOR EACH ROW
EXECUTE FUNCTION log_audit_trigger_function();

-- For users table
CREATE TRIGGER users_audit_trigger
AFTER INSERT OR UPDATE OR DELETE
ON users
FOR EACH ROW
EXECUTE FUNCTION log_audit_trigger_function();
