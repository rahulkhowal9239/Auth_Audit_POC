CREATE OR REPLACE FUNCTION log_audit_trigger_function()
RETURNS TRIGGER AS $$
DECLARE
    action_text AuditAction;
    v_old_data JSON;
    v_new_data JSON;
BEGIN
    CASE TG_OP
        WHEN 'INSERT' THEN
            action_text := 'CREATE'::AuditAction;
            v_new_data := row_to_json(NEW);
        WHEN 'UPDATE' THEN
            action_text := 'UPDATE'::AuditAction;
            v_old_data := row_to_json(OLD);
            v_new_data := row_to_json(NEW);
        WHEN 'DELETE' THEN
            action_text := 'DELETE'::AuditAction;
            v_old_data := row_to_json(OLD);
        ELSE
            RAISE EXCEPTION 'Unsupported operation type';
    END CASE;

    -- Insert into audit_logs table
    INSERT INTO audit_logs (
        organization_id, table_name, record_id, action, old_data, new_data, ip_address, user_agent, user_id
    ) VALUES (
        CASE WHEN TG_OP = 'DELETE' THEN OLD.organization_id ELSE NEW.organization_id END,
        TG_TABLE_NAME,
        CASE WHEN TG_OP = 'DELETE' THEN OLD.id::VARCHAR ELSE NEW.id::VARCHAR END,
        action_text,
        v_old_data,
        v_new_data,
        inet_client_addr(),
        current_setting('application_name'),
        session_user::VARCHAR
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
