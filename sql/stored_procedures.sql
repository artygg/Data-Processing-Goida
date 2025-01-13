-- 1. Закрыть прямой INSERT/UPDATE/DELETE для PUBLIC
REVOKE INSERT, UPDATE, DELETE ON TABLE Users, profiles, Subscriptions, contents FROM PUBLIC;

-- 2. Пример: все CRUD-процедуры для Users

CREATE OR REPLACE PROCEDURE sp_admin_create_user (
    p_email        VARCHAR(255),
    p_password     VARCHAR(255),
    p_has_referral BOOLEAN DEFAULT FALSE
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    INSERT INTO Users (email, password, has_used_referral_link)
    VALUES (p_email, p_password, p_has_referral);

    RAISE NOTICE 'New user created with email: %', p_email;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_admin_update_user (
    p_user_id      INT,
    p_email        VARCHAR(255),
    p_password     VARCHAR(255),
    p_has_referral BOOLEAN
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    UPDATE Users
       SET email = p_email,
           password = p_password,
           has_used_referral_link = p_has_referral
     WHERE id = p_user_id;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'User with id % not found', p_user_id;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_admin_delete_user (
    p_user_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    DELETE FROM Users
     WHERE id = p_user_id;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'User with id % not found', p_user_id;
    END IF;
END;
$$;

-- даём все эти процедуры только администратору (и больше никому).
GRANT EXECUTE ON PROCEDURE
    sp_admin_create_user(VARCHAR, VARCHAR, BOOLEAN),
    sp_admin_update_user(INT, VARCHAR, VARCHAR, BOOLEAN),
    sp_admin_delete_user(INT)
TO app_admin;

-- Аналогичные процедуры для Profile, Content и т.д.
-- Покажем, как Content-менеджер может управлять контентом:

CREATE OR REPLACE PROCEDURE sp_create_content (
    p_title        VARCHAR(255),
    p_poster       VARCHAR(255),
    p_description  TEXT,
    p_video_link   VARCHAR(255),
    p_duration     DOUBLE PRECISION,
    p_type         VARCHAR(50),
    p_season       INT DEFAULT NULL,
    p_episode_num  INT DEFAULT NULL,
    p_series_id    INT DEFAULT NULL
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_new_id INT;
BEGIN
    INSERT INTO contents (title, poster, description, video_link, duration, type, season, episode_number, series_id)
    VALUES (p_title, p_poster, p_description, p_video_link, p_duration, p_type, p_season, p_episode_num, p_series_id)
    RETURNING id INTO v_new_id;

    RAISE NOTICE 'Content created with id %', v_new_id;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_delete_content (
    p_content_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    DELETE FROM contents
     WHERE id = p_content_id;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'Content with id % not found', p_content_id;
    END IF;
END;
$$;

GRANT EXECUTE ON PROCEDURE
    sp_create_content(VARCHAR, VARCHAR, TEXT, VARCHAR, DOUBLE PRECISION, VARCHAR, INT, INT, INT),
    sp_delete_content(INT)
TO content_manager;

