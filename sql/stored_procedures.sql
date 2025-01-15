REVOKE INSERT, UPDATE, DELETE ON TABLE Users, profiles, Subscriptions, contents FROM PUBLIC;

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

-- GRANT EXECUTE ON PROCEDURE
--     sp_admin_create_user(VARCHAR, VARCHAR, BOOLEAN),
--     sp_admin_update_user(INT, VARCHAR, VARCHAR, BOOLEAN),
--     sp_admin_delete_user(INT)
-- TO app_admin;

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

-- GRANT EXECUTE ON PROCEDURE
--     sp_create_content(VARCHAR, VARCHAR, TEXT, VARCHAR, DOUBLE PRECISION, VARCHAR, INT, INT, INT),
--     sp_delete_content(INT)
-- TO content_manager;
----------------------
CREATE OR REPLACE PROCEDURE update_content_by_id(
    p_id BIGINT,
    p_title VARCHAR(255),
    p_description VARCHAR(255),
    p_video_link VARCHAR(255),
    p_duration DOUBLE PRECISION,
    p_type VARCHAR(255),
    p_season INTEGER,
    p_episode_number INTEGER,
    p_series_id INTEGER
)
LANGUAGE plpgsql
AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM public.contents WHERE id = p_id) THEN
UPDATE public.contents
SET
    title = p_title,
    description = p_description,
    video_link = p_video_link,
    duration = p_duration,
    type = p_type,
    season = p_season,
    episode_number = p_episode_number,
    series_id = p_series_id,
    updated_at = CURRENT_TIMESTAMP
WHERE id = p_id;
RAISE NOTICE 'Content with ID % successfully updated.', p_id;
ELSE
        RAISE EXCEPTION 'Content with ID % does not exist.', p_id;
END IF;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE update_user_credentials(
    user_id UUID,
    new_email VARCHAR,
    new_password VARCHAR
)
    LANGUAGE plpgsql
AS $$
DECLARE
    existing_user_id UUID;
    existing_is_banned BOOLEAN;
BEGIN
    -- Fetch user data, including banned status, in one go
    SELECT id, is_banned INTO existing_user_id, existing_is_banned
    FROM public.users
    WHERE id = user_id;

    -- Handle user not found scenario
    IF existing_user_id IS NULL THEN
        RAISE EXCEPTION 'User with id % not found', user_id;
    END IF;

    -- Handle banned user scenario
    IF existing_is_banned THEN
        RAISE EXCEPTION 'User with id % is banned', user_id;
    END IF;

    -- Update user credentials if user is found and not banned
    UPDATE public.users
    SET
        email = COALESCE(new_email, email),
        password = COALESCE(new_password, password)
    WHERE id = user_id;

    -- Optionally, check the number of rows affected to handle updates
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Failed to update user with id %', user_id;
    END IF;

END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE remove_from_watch_later(
    profile_id UUID,
    content_id BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
profile_exists BOOLEAN;
    content_exists BOOLEAN;
    watch_later JSONB;
    updated_watch_later JSONB;
BEGIN
SELECT EXISTS (SELECT 1 FROM public.profiles WHERE id = profile_id) INTO profile_exists;

IF NOT profile_exists THEN
        RAISE EXCEPTION 'Profile with ID % not found', profile_id;
END IF;

SELECT EXISTS (SELECT 1 FROM public.contents WHERE id = content_id) INTO content_exists;

IF NOT content_exists THEN
        RAISE EXCEPTION 'Content with ID % not found', content_id;
END IF;

SELECT watch_later INTO watch_later
FROM public.profiles
WHERE id = profile_id;

IF watch_later IS NOT NULL THEN
        updated_watch_later := (
            SELECT jsonb_agg(elem)
            FROM jsonb_array_elements(watch_later) elem
            WHERE (elem->>'id')::BIGINT != content_id
        );
ELSE
        updated_watch_later := '[]'::JSONB;
END IF;

UPDATE public.profiles
SET watch_later = updated_watch_later
WHERE id = profile_id;

RAISE NOTICE 'Successfully removed content with ID % from watch later list.', content_id;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE update_profile(
    profile_id UUID,
    new_profile_name TEXT,
    new_profile_photo TEXT,
    new_age DATE,
    new_language TEXT
)
    LANGUAGE plpgsql
AS $$
DECLARE
    existing_profile RECORD;
    valid_language BOOLEAN;
BEGIN
    -- Check if the profile exists
    SELECT * INTO existing_profile
    FROM public.profiles
    WHERE id = profile_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Profile with ID % not found', profile_id;
    END IF;

    -- Update the profile fields if new values are provided
    UPDATE public.profiles
    SET
        profile_name = COALESCE(NULLIF(new_profile_name, ''), profile_name),
        profile_photo = COALESCE(NULLIF(new_profile_photo, ''), profile_photo),
        age = COALESCE(new_age, age),
        language = CASE
                       WHEN new_language IS NOT NULL AND EXISTS (
                           SELECT 1
                           FROM unnest(ARRAY['ENGLISH', 'RUSSIAN', 'ARABIC', 'FRENCH', 'JAPANESE']) AS lang
                           WHERE lang = UPPER(new_language)
                       ) THEN UPPER(new_language)
                       ELSE language
            END
    WHERE id = profile_id;

    RAISE NOTICE 'Profile updated successfully for ID: %', profile_id;
END;
$$;

----------------------
CREATE OR REPLACE PROCEDURE create_profile(
    user_id_field UUID,
    new_profile_name TEXT,
    new_profile_photo TEXT,
    new_age DATE,
    new_language TEXT
)
    LANGUAGE plpgsql
AS $$
DECLARE
    existing_profile RECORD;
    valid_language BOOLEAN;
    is_child BOOLEAN;
    calculated_age INT;
BEGIN
    -- Check if the profile exists
    SELECT * INTO existing_profile
    FROM public.profiles
    WHERE user_id = user_id_field;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Profile with user ID % not found', user_id_field;
    END IF;

    -- Handle age and calculate is_child
    IF new_age IS NULL THEN
        UPDATE public.profiles
        SET age = null
        WHERE user_id = user_id_field;
    ELSE
        -- Calculate age in years
        calculated_age := DATE_PART('year', AGE(new_age));
        IF calculated_age < 18 AND calculated_age >= 0 THEN
            is_child := TRUE;
        ELSE
            is_child := FALSE;
        END IF;

        -- Update age and is_child
        UPDATE public.profiles
        SET age = calculated_age,
            is_child = is_child
        WHERE user_id = user_id_field;
    END IF;

    -- Update profile name if provided
    IF new_profile_name IS NOT NULL AND new_profile_name <> '' THEN
        UPDATE public.profiles
        SET profile_name = new_profile_name
        WHERE user_id = user_id_field;
    END IF;

    -- Update profile photo if provided
    IF new_profile_photo IS NOT NULL AND new_profile_photo <> '' THEN
        UPDATE public.profiles
        SET profile_photo = new_profile_photo
        WHERE user_id = user_id_field;
    END IF;

    -- Validate and update language
    valid_language := EXISTS (
        SELECT 1
        FROM unnest(ARRAY['ENGLISH', 'RUSSIAN', 'ARABIC', 'FRENCH', 'JAPANESE']) AS lang
        WHERE lang = UPPER(new_language)
    );

    IF new_language IS NOT NULL THEN
        IF NOT valid_language THEN
            RAISE EXCEPTION 'Invalid language value: %', new_language;
        ELSE
            UPDATE public.profiles
            SET language = UPPER(new_language)
            WHERE user_id = user_id_field;
        END IF;
    END IF;

    RAISE NOTICE 'Profile updated successfully for User ID: %', user_id_field;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE save_referral(
    host_id UUID,
    invited_id UUID
)
LANGUAGE plpgsql
AS $$
DECLARE
new_referral RECORD;
BEGIN
INSERT INTO public.referral (host_id, invited_id)
VALUES (host_id, invited_id)
    RETURNING * INTO new_referral;

RAISE NOTICE 'Referral saved successfully with ID: %, Host ID: %, Invited ID: %',
        new_referral.id, host_id, invited_id;

EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Error saving referral: %', SQLERRM;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE get_referral_by_invited_id(
    invited_id UUID,
    OUT referral_id BIGINT,
    OUT host_id UUID,
    OUT invited_id_out UUID
)
LANGUAGE plpgsql
AS
$$
BEGIN
SELECT id, host_id, invited_id
INTO referral_id, host_id, invited_id_out
FROM public.referral
WHERE invited_id = invited_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Referral with invited_id % not found', invited_id;
END IF;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE get_resolution_by_id(
    resolution_id_param INTEGER,
    OUT resolution_id INTEGER,
    OUT name VARCHAR
)
    LANGUAGE plpgsql
AS
$$
BEGIN
    -- Explicitly qualify the table column with the table name
    SELECT r.resolution_id, r.name
    INTO resolution_id, name
    FROM public.resolution r
    WHERE r.resolution_id = resolution_id_param;

    -- Check if no rows were found
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Resolution with id % not found', resolution_id_param;
    END IF;
END;
$$;

----------------------
CREATE OR REPLACE PROCEDURE create_trial_subscription(profile_id UUID, OUT subscription_id UUID)
LANGUAGE plpgsql
AS $$
DECLARE
profile_exists BOOLEAN;
    start_date DATE := CURRENT_DATE;
    end_date DATE := CURRENT_DATE + INTERVAL '7 days';
BEGIN
SELECT EXISTS(SELECT 1 FROM public.profiles WHERE id = profile_id) INTO profile_exists;

IF NOT profile_exists THEN
        RAISE EXCEPTION 'Profile not found';
END IF;

INSERT INTO public.subscriptions (id, start_date, end_date, profile_id, price_id)
VALUES (uuid_generate_v4(), start_date, end_date, profile_id, NULL)
    RETURNING id INTO subscription_id;
END;
$$;

----------------------
CREATE OR REPLACE PROCEDURE create_subscription(
    profile_id UUID,
    price_id INTEGER,
    start_date DATE,
    end_date DATE,
    OUT subscription_id UUID,
    OUT subscription_cost DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
DECLARE
profile_exists BOOLEAN;
    cost DOUBLE PRECISION;
BEGIN

SELECT EXISTS(SELECT 1 FROM public.profiles WHERE id = profile_id) INTO profile_exists;

IF NOT profile_exists THEN
        RAISE EXCEPTION 'Profile not found';
END IF;

SELECT price INTO cost
FROM public.price
WHERE id = price_id;

IF cost IS NULL THEN
        RAISE EXCEPTION 'Price not found for the given price_id';
END IF;

INSERT INTO public.subscriptions (id, start_date, end_date, profile_id, price_id)
VALUES (uuid_generate_v4(), start_date, end_date, profile_id, price_id)
    RETURNING id INTO subscription_id;

subscription_cost := cost;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE apply_discount_for_invitation(
    inviter_profile_id UUID,
    invitee_profile_id UUID
)
LANGUAGE plpgsql
AS $$
DECLARE
inviter_subscription_id UUID;
    invitee_subscription_id UUID;
    inviter_price_id INTEGER;
    invitee_price_id INTEGER;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM public.profiles WHERE id = inviter_profile_id) THEN
        RAISE EXCEPTION 'Inviter profile not found';
END IF;

    IF NOT EXISTS (SELECT 1 FROM public.profiles WHERE id = invitee_profile_id) THEN
        RAISE EXCEPTION 'Invitee profile not found';
END IF;

SELECT id, price_id INTO inviter_subscription_id, inviter_price_id
FROM public.subscriptions
WHERE profile_id = inviter_profile_id
ORDER BY start_date DESC LIMIT 1;

SELECT id, price_id INTO invitee_subscription_id, invitee_price_id
FROM public.subscriptions
WHERE profile_id = invitee_profile_id
ORDER BY start_date DESC LIMIT 1;

IF inviter_subscription_id IS NULL OR invitee_subscription_id IS NULL THEN
        RAISE EXCEPTION 'One or both profiles do not have active subscriptions';
END IF;

    IF inviter_price_id >= 2 AND invitee_price_id >= 2 THEN
UPDATE public.subscriptions
SET price_id = price_id - 2
WHERE id = inviter_subscription_id;

UPDATE public.subscriptions
SET price_id = price_id - 2
WHERE id = invitee_subscription_id;
ELSE
        RAISE EXCEPTION 'One or both subscriptions are not eligible for a discount';
END IF;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE update_user_credentials(
    user_id UUID,
    new_email VARCHAR,
    new_password VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
existing_user_id UUID;
    existing_is_banned BOOLEAN;
BEGIN
SELECT id, is_banned INTO existing_user_id, existing_is_banned
FROM public.users
WHERE id = user_id;

IF existing_user_id IS NULL THEN
        RAISE EXCEPTION 'User with id % not found', user_id;
END IF;

    IF existing_is_banned THEN
        RAISE EXCEPTION 'User with id % is banned', user_id;
END IF;

UPDATE public.users
SET
    email = COALESCE(new_email, email),
    password = COALESCE(new_password, password)
WHERE id = user_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Failed to update user with id %', user_id;
END IF;

END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE create_referral(
    host_user_id UUID,
    invited_user_id UUID
)
LANGUAGE plpgsql
AS $$
DECLARE
host_user_has_used_referral BOOLEAN;
    invited_user_exists BOOLEAN;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM public.users WHERE id = host_user_id) THEN
        RAISE EXCEPTION 'Requested host was not found';
END IF;

    IF NOT EXISTS (SELECT 1 FROM public.users WHERE id = invited_user_id) THEN
        RAISE EXCEPTION 'Requested user to invite was not found';
END IF;

SELECT has_used_referral_link INTO host_user_has_used_referral
FROM public.users WHERE id = host_user_id;

IF host_user_has_used_referral THEN
        RAISE EXCEPTION 'Referral link had been used';
END IF;

INSERT INTO public.referral (id, host_id, invited_id)
VALUES (nextval('public.referral_seq'), host_user_id, invited_user_id);

UPDATE public.users
SET has_used_referral_link = TRUE
WHERE id = host_user_id;

END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE update_profile_preferences(
    profile_id_field UUID,
    classifications TEXT[],
    genres TEXT[],  -- Assuming genres are passed as TEXT[]
    interested_in_films BOOLEAN,
    interested_in_series BOOLEAN,
    interested_in_films_with_min_age BOOLEAN
)
    LANGUAGE plpgsql
AS $$
DECLARE
    profile_exists BOOLEAN;
    converted_classifications TEXT[];
    genre JSONB;  -- Declare genre as JSONB to handle it as JSONB object
    preference_id INT;
BEGIN
    -- Check if the profile exists
    SELECT EXISTS (SELECT 1 FROM public.profiles WHERE id = profile_id_field) INTO profile_exists;

    IF NOT profile_exists THEN
        RAISE EXCEPTION 'Profile with ID % not found', profile_id_field;
    END IF;

    -- Convert classifications to uppercase if present
    IF classifications IS NOT NULL AND array_length(classifications, 1) > 0 THEN
        converted_classifications := ARRAY(
                SELECT UPPER(classification)
                FROM unnest(classifications) classification
                                     );
    ELSE
        converted_classifications := NULL;
    END IF;

    -- Update preferences for the profile
    UPDATE public.preferences
    SET
        is_interested_in_films = interested_in_films,
        is_interested_in_films_with_minimum_age = interested_in_films_with_min_age,
        is_interested_in_series = interested_in_series
    WHERE profile_id = profile_id_field;

    -- Get preference ID
    SELECT id
    INTO preference_id
    FROM public.preferences
    WHERE profile_id = profile_id_field;

    -- Delete old genres for the profile
    DELETE FROM public.preferences_genres WHERE preferences_id = preference_id;

    -- Insert new genres
    IF genres IS NOT NULL THEN
        FOREACH genre IN ARRAY genres
            LOOP
                -- Cast each text genre to JSONB for extracting 'id' and 'name'
                genre := genre::JSONB;  -- Convert genre from text to JSONB
                -- Extract 'id' and 'name' from the genre JSONB object
                INSERT INTO public.preferences_genres (preferences_id, genres, genres_id)
                VALUES (
                           preference_id,          -- Use the preference_id for the preferences_id
                           genre->>'name',         -- Extract genre 'name' as text
                           (genre->>'id')::bigint     -- Extract genre 'id' and cast it to INT
                       );
            END LOOP;
    END IF;

    -- Raise notice
    RAISE NOTICE 'Profile % updated successfully.', profile_id_field;
END;
$$;




