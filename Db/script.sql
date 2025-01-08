------------------------------------------------------------------------------
-- 1. Удаляем старые роли (если существуют) и все принадлежащие им объекты
------------------------------------------------------------------------------

-- Для каждой роли сначала удаляем объекты, принадлежащие роли.
-- Затем удаляем саму роль.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_catalog.pg_roles WHERE rolname = 'app_admin') THEN
        RAISE NOTICE 'Dropping role app_admin and owned objects...';
        EXECUTE 'DROP OWNED BY app_admin CASCADE';
        EXECUTE 'DROP ROLE app_admin';
    ELSE
        RAISE NOTICE 'Role app_admin does not exist, skipping.';
    END IF;
END;
$$;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_catalog.pg_roles WHERE rolname = 'content_manager') THEN
        RAISE NOTICE 'Dropping role content_manager and owned objects...';
        EXECUTE 'DROP OWNED BY content_manager CASCADE';
        EXECUTE 'DROP ROLE content_manager';
    ELSE
        RAISE NOTICE 'Role content_manager does not exist, skipping.';
    END IF;
END;
$$;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_catalog.pg_roles WHERE rolname = 'analytics_viewer') THEN
        RAISE NOTICE 'Dropping role analytics_viewer and owned objects...';
        EXECUTE 'DROP OWNED BY analytics_viewer CASCADE';
        EXECUTE 'DROP ROLE analytics_viewer';
    ELSE
        RAISE NOTICE 'Role analytics_viewer does not exist, skipping.';
    END IF;
END;
$$;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_catalog.pg_roles WHERE rolname = 'app_user') THEN
        RAISE NOTICE 'Dropping role app_user and owned objects...';
        EXECUTE 'DROP OWNED BY app_user CASCADE';
        EXECUTE 'DROP ROLE app_user';
    ELSE
        RAISE NOTICE 'Role app_user does not exist, skipping.';
    END IF;
END;
$$;

------------------------------------------------------------------------------
-- 2. Создаём роли (через DO-блок, чтобы не было ошибки "already exists")
------------------------------------------------------------------------------

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_catalog.pg_roles WHERE rolname = 'app_admin') THEN
        CREATE ROLE app_admin
            WITH LOGIN
            PASSWORD 'admin_password'
            SUPERUSER
            CREATEDB
            CREATEROLE
            INHERIT
            NOREPLICATION;
    END IF;
END;
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_catalog.pg_roles WHERE rolname = 'content_manager') THEN
        CREATE ROLE content_manager
            WITH LOGIN
            PASSWORD 'manager_password'
            INHERIT
            NOSUPERUSER
            NOCREATEDB
            NOCREATEROLE
            NOREPLICATION;
    END IF;
END;
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_catalog.pg_roles WHERE rolname = 'analytics_viewer') THEN
        CREATE ROLE analytics_viewer
            WITH LOGIN
            PASSWORD 'viewer_password'
            INHERIT
            NOSUPERUSER
            NOCREATEDB
            NOCREATEROLE
            NOREPLICATION;
    END IF;
END;
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_catalog.pg_roles WHERE rolname = 'app_user') THEN
        CREATE ROLE app_user
            WITH LOGIN
            PASSWORD 'user_password'
            INHERIT
            NOSUPERUSER
            NOCREATEDB
            NOCREATEROLE
            NOREPLICATION;
    END IF;
END;
$$;

------------------------------------------------------------------------------
-- 3. Создаём (или обновляем) представления (Views)
--    Используем CREATE OR REPLACE, чтобы не было ошибки "view already exists"
------------------------------------------------------------------------------

-- Внимание: предполагается, что таблицы (Users, Profile, Subscriptions и т.д.)
-- уже созданы в схеме public. Если нет — создайте их до запуска данного скрипта.

CREATE OR REPLACE VIEW v_user_subscriptions AS
SELECT
    s.id                 AS subscription_id,
    s.profile_id,
    pf.user_id,
    u.email,
    p.price,
    r.resolution_name,
    s.start_date,
    s.end_date
FROM Subscriptions s
JOIN Profile       pf ON pf.id = s.profile_id
JOIN Users         u  ON u.id = pf.user_id
JOIN Prices        p  ON p.id = s.price_id
JOIN Resolutions   r  ON r.resolution = p.resolution_id
WHERE s.end_date IS NULL
   OR s.end_date > CURRENT_DATE;

CREATE OR REPLACE VIEW v_watch_history_detailed AS
SELECT
    wh.id                  AS watch_history_id,
    pf.id                  AS profile_id,
    u.id                   AS user_id,
    u.email                AS user_email,
    c.id                   AS content_id,
    c.title                AS content_title,
    wh.stopped_at,
    wh.watching_times
FROM Watch_histories wh
JOIN Profile         pf ON pf.id = wh.profile_id
JOIN Users           u  ON u.id = pf.user_id
JOIN Content         c  ON c.id = wh.content_id;

CREATE OR REPLACE VIEW v_profile_preferences AS
SELECT
    pf.id       AS profile_id,
    u.id        AS user_id,
    u.email     AS user_email,
    t.name      AS tag_name,
    t.type      AS tag_type
FROM Preferences pr
JOIN Profile     pf ON pf.id = pr.profile_id
JOIN Users       u  ON u.id = pf.user_id
JOIN Tag         t  ON t.id = pr.classification_id;

-- Пример "объединяющего" представления для контента
CREATE OR REPLACE VIEW v_content_overview AS
WITH genre_list AS (
    SELECT
        gb.content_id,
        STRING_AGG(g.name, ', ') AS genres
    FROM Genre_bridge gb
    JOIN Genre g ON g.id = gb.genre_id
    GROUP BY gb.content_id
),
subtitle_list AS (
    SELECT
        s.content_id,
        STRING_AGG(s.language, ', ') AS subtitles
    FROM Subtitle s
    GROUP BY s.content_id
),
resolution_list AS (
    SELECT
        qr.content_id,
        STRING_AGG(r.resolution_name, ', ') AS available_resolutions
    FROM Quality_ranges qr
    JOIN Resolutions r ON r.resolution = qr.resolution_id
    GROUP BY qr.content_id
)
SELECT
    c.id               AS content_id,
    c.title,
    c.poster,
    c.description,
    c.video_link,
    c.duration,
    c.type,
    c.season,
    c.episode_number,
    c.series_id,
    genre_list.genres,
    subtitle_list.subtitles,
    resolution_list.available_resolutions
FROM Content c
LEFT JOIN genre_list      ON genre_list.content_id = c.id
LEFT JOIN subtitle_list   ON subtitle_list.content_id = c.id
LEFT JOIN resolution_list ON resolution_list.content_id = c.id;

------------------------------------------------------------------------------
-- 4. Выдаём привилегии (GRANT)
------------------------------------------------------------------------------

-- 4.1. Администратор приложения: полный доступ на все таблицы/последовательности
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO app_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO app_admin;
GRANT ALL PRIVILEGES ON SCHEMA public TO app_admin;

-- 4.2. Менеджер контента: управляет только таблицами, связанными с контентом/жанрами
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE 
    Content, Genre, Genre_bridge, Subtitle, Quality_ranges
TO content_manager;

-- Если нужно, чтобы мог создавать объекты в схеме public:
-- GRANT CREATE ON SCHEMA public TO content_manager;

-- 4.3. Аналитик: только чтение (SELECT) по таблицам и представлениям
GRANT SELECT ON ALL TABLES IN SCHEMA public TO analytics_viewer;
-- Явно дадим SELECT на представления
GRANT SELECT ON v_user_subscriptions,
                     v_watch_history_detailed,
                     v_profile_preferences,
                     v_content_overview
TO analytics_viewer;

-- 4.4. Обычный пользователь (app_user)
-- По умолчанию не даём никаких привилегий, либо минимум, если нужно
-- Например, только читать Content:
-- GRANT SELECT ON TABLE Content TO app_user;

-- Если есть процедуры, которыми app_user должен пользоваться:
-- GRANT EXECUTE ON PROCEDURE sp_create_subscription(...) TO app_user;

------------------------------------------------------------------------------
-- Готово!
------------------------------------------------------------------------------
