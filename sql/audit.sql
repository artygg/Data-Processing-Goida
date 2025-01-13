-- 1) таблица audit_log
CREATE TABLE IF NOT EXISTS audit_log (
    audit_id     SERIAL PRIMARY KEY,
    table_name   TEXT NOT NULL,
    operation    TEXT NOT NULL,
    changed_by   TEXT NOT NULL,
    changed_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    old_data     JSONB,
    new_data     JSONB
);

-- 2) Функция fn_audit()
CREATE OR REPLACE FUNCTION fn_audit()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO audit_log (table_name, operation, changed_by, old_data)
        VALUES (TG_TABLE_NAME, TG_OP, CURRENT_USER, row_to_json(OLD));
        RETURN OLD;

    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO audit_log (table_name, operation, changed_by, old_data, new_data)
        VALUES (
            TG_TABLE_NAME,
            TG_OP,
            CURRENT_USER,
            row_to_json(OLD),
            row_to_json(NEW)
        );
        RETURN NEW;

    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO audit_log (table_name, operation, changed_by, new_data)
        VALUES (
            TG_TABLE_NAME,
            TG_OP,
            CURRENT_USER,
            row_to_json(NEW)
        );
        RETURN NEW;
    END IF;

    RETURN NULL;
END;
$$;

-- 3)Триггеры на все таблицы схемы public 
DO $$
DECLARE
    rec RECORD;
    trigger_name TEXT;
    table_full_name TEXT;
BEGIN
    FOR rec IN
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_type = 'BASE TABLE'
          AND table_name NOT IN ('audit_log')  -- Игнорируем audit_log
    LOOP
        trigger_name := 'trg_audit_' || rec.table_name;
        table_full_name := quote_ident('public') || '.' || quote_ident(rec.table_name);

        EXECUTE format('DROP TRIGGER IF EXISTS %I ON %s;', trigger_name, table_full_name);

        EXECUTE format(
            'CREATE TRIGGER %I
             AFTER INSERT OR UPDATE OR DELETE
             ON %s
             FOR EACH ROW
             EXECUTE FUNCTION fn_audit();',
            trigger_name,
            table_full_name
        );
        
        RAISE NOTICE 'Audit trigger created: % on table %', trigger_name, table_full_name;
    END LOOP;
END;
$$;
