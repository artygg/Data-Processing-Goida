
-- 1. Триггер и функция при 3 login_faults

CREATE OR REPLACE FUNCTION fn_block_on_login_faults()
RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.login_faults >= 3 THEN
        NEW.block_end := NOW() + INTERVAL '1 day';
    ELSE
        NEW.block_end := NULL;
    END IF;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_block_on_login_faults
BEFORE INSERT OR UPDATE
ON Warnings
FOR EACH ROW
EXECUTE FUNCTION fn_block_on_login_faults();


-- 2. Триггер и функция (start_date <= end_date)


CREATE OR REPLACE FUNCTION fn_check_subscription_dates()
RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.end_date IS NOT NULL 
       AND NEW.start_date IS NOT NULL
       AND NEW.end_date < NEW.start_date
    THEN
        RAISE EXCEPTION 'end_date (%) не может быть раньше start_date (%)',
            NEW.end_date, NEW.start_date;
    END IF;
    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_check_subscription_dates
BEFORE INSERT OR UPDATE
ON Subscriptions
FOR EACH ROW
EXECUTE FUNCTION fn_check_subscription_dates();

-- 3. Триггер и функция для логирования (AUDIT)


CREATE TABLE IF NOT EXISTS Subscriptions_log (
    log_id       SERIAL PRIMARY KEY,
    subscription_id INT,
    profile_id   INT,
    price_id     INT,
    start_date   DATE,
    end_date     DATE,
    deleted_at   TIMESTAMP DEFAULT NOW(),
    deleted_by   TEXT
);

CREATE OR REPLACE FUNCTION fn_audit_subscriptions_delete()
RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO Subscriptions_log
        (subscription_id, profile_id, price_id, start_date, end_date, deleted_at, deleted_by)
    VALUES
        (OLD.id, OLD.profile_id, OLD.price_id, OLD.start_date, OLD.end_date, NOW(), CURRENT_USER);

    RETURN OLD;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trg_audit_subscriptions_delete
AFTER DELETE
ON Subscriptions
FOR EACH ROW
EXECUTE FUNCTION fn_audit_subscriptions_delete();

-- 3. Триггер и функция для end date

CREATE OR REPLACE FUNCTION fn_enforce_31day_subscription()
RETURNS TRIGGER AS
$$
BEGIN
    -- end_date = start_date + 31 дней
    IF NEW.start_date IS NOT NULL
       AND NEW.end_date IS NULL
    THEN
        NEW.end_date := NEW.start_date + INTERVAL '31 day';
    END IF;

    IF NEW.start_date IS NOT NULL 
       AND NEW.end_date IS NOT NULL
    THEN
        -- 1) end_date >= start_date
        IF NEW.end_date < NEW.start_date THEN
            RAISE EXCEPTION 'end_date (%) не может быть раньше start_date (%)',
                NEW.end_date, NEW.start_date;
        END IF;

        -- 2) Разница не больше 31
        IF NEW.end_date > (NEW.start_date + INTERVAL '31 day') THEN
            RAISE EXCEPTION 'Период подписки не может превышать 31 день (start_date=%, end_date=%).',
                NEW.start_date, NEW.end_date;
        END IF;
    END IF;

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;


-- 3.1. Макс (31 день)


CREATE TRIGGER trg_enforce_31day_subscription
BEFORE INSERT OR UPDATE
ON Subscriptions
FOR EACH ROW
EXECUTE FUNCTION fn_enforce_31day_subscription();


