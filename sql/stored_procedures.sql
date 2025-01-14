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

GRANT EXECUTE ON PROCEDURE
    sp_admin_create_user(VARCHAR, VARCHAR, BOOLEAN),
    sp_admin_update_user(INT, VARCHAR, VARCHAR, BOOLEAN),
    sp_admin_delete_user(INT)
TO app_admin;

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
CREATE OR REPLACE PROCEDURE update_profile_preferences(
    profile_id UUID,
    classifications TEXT[],
    genres TEXT[],
    interested_in_films BOOLEAN,
    interested_in_series BOOLEAN,
    interested_in_films_with_min_age BOOLEAN
)
LANGUAGE plpgsql
AS $$
DECLARE
profile_exists BOOLEAN;
    converted_classifications TEXT[];
BEGIN
SELECT EXISTS (SELECT 1 FROM public.profiles WHERE id = profile_id) INTO profile_exists;

IF NOT profile_exists THEN
        RAISE EXCEPTION 'Profile with ID % not found', profile_id;
END IF;
    IF classifications IS NOT NULL AND array_length(classifications, 1) > 0 THEN
        converted_classifications := ARRAY(
            SELECT UPPER(classification)
            FROM unnest(classifications) classification
        );
ELSE
        converted_classifications := '{}';
END IF;
UPDATE public.profiles
SET
    preferences = jsonb_build_object(
            'classifications', converted_classifications,
            'genres', genres,
            'interestedInFilms', interested_in_films,
            'interestedInSeries', interested_in_series,
            'interestedInFilmsWithMinAge', interested_in_films_with_min_age
                  )
WHERE id = profile_id;
RAISE NOTICE 'Profile % updated successfully.', profile_id;
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
SELECT * INTO existing_profile
FROM public.profiles
WHERE id = profile_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Profile with ID % not found', profile_id;
END IF;

    IF new_profile_name IS NOT NULL AND new_profile_name <> '' THEN
UPDATE public.profiles
SET profile_name = new_profile_name
WHERE id = profile_id;
END IF;

    IF new_profile_photo IS NOT NULL AND new_profile_photo <> '' THEN
UPDATE public.profiles
SET profile_photo = new_profile_photo
WHERE id = profile_id;
END IF;

    IF new_age IS NOT NULL THEN
UPDATE public.profiles
SET age = new_age
WHERE id = profile_id;
END IF;

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
WHERE id = profile_id;
END IF;
END IF;

    RAISE NOTICE 'Profile updated successfully for ID: %', profile_id;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE create_profile(
    user_id UUID,
    new_profile_name TEXT,
    new_profile_photo TEXT,
    new_age DATE,
    new_language TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
existing_user RECORD;
    profile_count INT;
    valid_language BOOLEAN;
    new_profile RECORD;
BEGIN
SELECT * INTO existing_user
FROM public.users
WHERE id = user_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'User with ID % not found', user_id;
END IF;

SELECT COUNT(*) INTO profile_count
FROM public.profiles
WHERE user_id = user_id;

IF profile_count >= 4 THEN
        RAISE EXCEPTION 'User has reached the profile limit of 4';
END IF;

INSERT INTO public.profiles (profile_name, profile_photo, user_id, age, language)
VALUES (
           new_profile_name,
           new_profile_photo,
           user_id,
           new_age,
           UPPER(new_language)
       )
    RETURNING * INTO new_profile;

valid_language := EXISTS (
        SELECT 1
        FROM unnest(ARRAY['ENGLISH', 'RUSSIAN', 'ARABIC', 'FRENCH', 'JAPANESE']) AS lang
        WHERE lang = UPPER(new_language)
    );

    IF new_language IS NOT NULL AND NOT valid_language THEN
        RAISE EXCEPTION 'Invalid language value: %', new_language;
END IF;

    RAISE NOTICE 'Profile created successfully for User ID: %, Profile ID: %', user_id, new_profile.id;
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
SELECT resolution_id, name
INTO resolution_id, name
FROM public.resolution
WHERE resolution_id = resolution_id_param;

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
        RAISE EXCEPTION 'User not found';
END IF;

    IF existing_is_banned THEN
        RAISE EXCEPTION 'User is banned';
END IF;

UPDATE public.users
SET
    email = COALESCE(new_email, email),
    password = COALESCE(new_password, password)
WHERE id = user_id;

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

INSERT INTO public.referrals (host_user_id, invited_user_id)
VALUES (host_user_id, invited_user_id);

UPDATE public.users
SET has_used_referral_link = TRUE
WHERE id = host_user_id;

END;
$$;
----------------------