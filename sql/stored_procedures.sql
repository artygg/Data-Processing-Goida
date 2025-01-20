REVOKE INSERT, UPDATE, DELETE ON TABLE Users, profiles, Subscriptions, contents FROM PUBLIC;

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
    RETURNING content_id INTO v_new_id;

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
    WHERE content_id = p_content_id;

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
    IF EXISTS (SELECT 1 FROM public.contents WHERE contents.content_id = p_id) THEN
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
        WHERE contents.content_id = p_id;
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
CREATE OR REPLACE PROCEDURE remove_from_watch_later(
    profile_id_in UUID,
    content_id_in BIGINT
)
    LANGUAGE plpgsql
AS $$
DECLARE
    profile_exists BOOLEAN;
    content_exists BOOLEAN;
BEGIN
    SELECT EXISTS (SELECT 1 FROM public.profiles WHERE id = profile_id_in) INTO profile_exists;

    IF NOT profile_exists THEN
        RAISE EXCEPTION 'Profile with ID % not found', profile_id_in;
    END IF;

    SELECT EXISTS (SELECT 1 FROM public.contents WHERE content_id = content_id_in) INTO content_exists;

    IF NOT content_exists THEN
        RAISE EXCEPTION 'Content with ID % not found', content_id_in;
    END IF;

    DELETE FROM public.profiles_watch_later
    WHERE profile_id = profile_id_in AND profiles_watch_later.watch_later_content_id = content_id_in;

    RAISE NOTICE 'Successfully removed content with ID % from watch later list.', content_id_in;
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
    IN user_id_field UUID,
    IN new_profile_name TEXT,
    IN new_profile_photo TEXT,
    IN new_age DATE,
    IN new_language TEXT
)
    LANGUAGE plpgsql
AS $$
DECLARE
    valid_language BOOLEAN;
    is_child_declaration BOOLEAN;
    calculated_age INT;
    calculated_birth_date DATE;
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM public.profiles WHERE user_id = user_id_field
    ) THEN
        INSERT INTO public.profiles (id, user_id, profile_name, profile_photo, age, is_child, language)
        VALUES (
                   gen_random_uuid(),
                   user_id_field,
                   COALESCE(new_profile_name, 'Unknown'),
                   new_profile_photo,
                   NULL,
                   NULL,
                   CASE
                       WHEN new_language IS NOT NULL AND UPPER(new_language) IN ('ENGLISH', 'RUSSIAN', 'ARABIC', 'FRENCH', 'JAPANESE') THEN UPPER(new_language)
                       ELSE NULL
                       END
               );
        RAISE NOTICE 'New profile created for User ID: %', user_id_field;
    END IF;

    IF new_age IS NOT NULL THEN
        calculated_age := DATE_PART('year', AGE(new_age));
        is_child_declaration := calculated_age < 18 AND calculated_age >= 0;
        calculated_birth_date := CURRENT_DATE - (calculated_age * INTERVAL '1 year');

        UPDATE public.profiles
        SET
            age = calculated_birth_date,
            is_child = is_child_declaration
        WHERE user_id = user_id_field;
    END IF;

    IF new_profile_name IS NOT NULL AND new_profile_name <> '' THEN
        UPDATE public.profiles
        SET profile_name = new_profile_name
        WHERE user_id = user_id_field;
    END IF;

    IF new_profile_photo IS NOT NULL AND new_profile_photo <> '' THEN
        UPDATE public.profiles
        SET profile_photo = new_profile_photo
        WHERE user_id = user_id_field;
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
            WHERE user_id = user_id_field;
        END IF;
    END IF;

    RAISE NOTICE 'Profile updated successfully for User ID: %', user_id_field;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE save_referral(
    host_id_out UUID,
    invited_id_out UUID
)
    LANGUAGE plpgsql
AS $$
DECLARE
    new_referral RECORD;
BEGIN
    INSERT INTO public.referrals (id, host_id, invited_id)
    VALUES (nextval('referrals_seq'), host_id_out, invited_id_out)
    RETURNING id, host_id, invited_id INTO new_referral;

    RAISE NOTICE 'Referral saved successfully with ID: %, Host ID: %, Invited ID: %',
        new_referral.id, new_referral.host_id, new_referral.invited_id;

EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Error saving referral: %', SQLERRM;
END;
$$;

----------------------
CREATE OR REPLACE FUNCTION get_referral_by_invited_id(
    invited_id_data UUID
)
    RETURNS TABLE (
                      referral_id_out BIGINT,
                      host_id_out UUID,
                      invited_id_out UUID
                  )
    LANGUAGE plpgsql
AS
$$
BEGIN
    RETURN QUERY
        SELECT id, host_id, invited_id
        FROM public.referrals
        WHERE invited_id = invited_id_data;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Referral with invited_id % not found', invited_id_data;
    END IF;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE get_resolution_by_id(
    resolution_id_param INTEGER,
    OUT resolution_id_out INTEGER,
    OUT name_out VARCHAR
)
    LANGUAGE plpgsql
AS
$$
BEGIN
    SELECT r.resolution_id, r.name
    INTO resolution_id_out, name_out
    FROM public.resolutions r
    WHERE r.resolution_id = resolution_id_param;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Resolution with id % not found', resolution_id_param;
    END IF;
END;
$$;

----------------------
CREATE OR REPLACE PROCEDURE create_trial_subscription(profile_id UUID)
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
    VALUES (gen_random_uuid(), start_date, end_date, profile_id, 0.0);
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE create_subscription(
    profile_id UUID,
    price_id INTEGER,
    start_date_insert DATE,
    end_date_insert DATE,
    subscription_cost DOUBLE PRECISION
)
    LANGUAGE plpgsql
AS $$
DECLARE
    profile_exists BOOLEAN;
    price_amount DOUBLE PRECISION;
BEGIN
    SELECT EXISTS(SELECT 1 FROM public.profiles WHERE id = profile_id) INTO profile_exists;
    IF NOT profile_exists THEN
        RAISE EXCEPTION 'Profile with ID % not found', profile_id;
    END IF;

    SELECT price INTO price_amount FROM public.prices WHERE id = price_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Price not found for the given price_id: %', price_id;
    END IF;

    IF end_date_insert <= start_date_insert THEN
        RAISE EXCEPTION 'End date must be after the start date';
    END IF;

    INSERT INTO public.subscriptions (id, start_date, end_date, profile_id, price_id)
    VALUES (gen_random_uuid(), start_date_insert, end_date_insert, profile_id, price_id);

    subscription_cost := price_amount;

    RAISE NOTICE 'Subscription created successfully for Profile ID: %, Cost: %', profile_id, subscription_cost;
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

    INSERT INTO public.referrals (id, host_id, invited_id)
    VALUES (nextval('public.referrals_seq'), host_user_id, invited_user_id);

    UPDATE public.users
    SET has_used_referral_link = TRUE
    WHERE id = host_user_id;

END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE update_profile_preferences(
    profile_id_field UUID,
    classifications TEXT[],
    genres TEXT[],
    interested_in_films BOOLEAN,
    interested_in_series_in BOOLEAN,
    interested_in_films_with_min_age BOOLEAN
)
    LANGUAGE plpgsql
AS $$
DECLARE
    profile_exists BOOLEAN;
    converted_classifications TEXT[];
    genre JSONB;
    preference_id INT;
BEGIN
    SELECT EXISTS (SELECT 1 FROM public.profiles WHERE id = profile_id_field) INTO profile_exists;

    IF NOT profile_exists THEN
        RAISE EXCEPTION 'Profile with ID % not found', profile_id_field;
    END IF;

    IF classifications IS NOT NULL AND array_length(classifications, 1) > 0 THEN
        converted_classifications := ARRAY(
                SELECT UPPER(classification)
                FROM unnest(classifications) classification
                                     );
    ELSE
        converted_classifications := NULL;
    END IF;
    preference_id = nextval('public.preferences_id_seq');
    INSERT INTO public.preferences (id, is_interested_in_films, is_interested_in_films_with_minimum_age, is_interested_in_series, profile_id)
    VALUES (preference_id, interested_in_films, interested_in_films_with_min_age, interested_in_series_in, profile_id_field);

    SELECT id
    INTO preference_id
    FROM public.preferences
    WHERE profile_id = profile_id_field;

    IF genres IS NOT NULL THEN
        FOREACH genre IN ARRAY genres
            LOOP
                genre := genre::JSONB;
                INSERT INTO public.preferences_genres (preferences_id, genres_id)
                VALUES (
                           preference_id,
                           (genre->>'id')::bigint
                       );
            END LOOP;
    END IF;

    RAISE NOTICE 'Profile % updated successfully.', profile_id_field;
END;
$$;
----------------------
CREATE OR REPLACE PROCEDURE register_user(
    IN email_in VARCHAR(255),
    IN password_in VARCHAR(255),
    IN token_in VARCHAR(512)
)
    LANGUAGE plpgsql
AS $$
DECLARE
    new_user_id uuid;
BEGIN
    IF EXISTS (SELECT 1 FROM users WHERE email = email_in) THEN
        RAISE EXCEPTION 'Email is already taken';
    ELSE
        new_user_id := gen_random_uuid();
        INSERT INTO public.users (id, email, password, token, is_banned, has_used_referral_link)
        VALUES (new_user_id, email_in, password_in, token_in, false, false);
        INSERT INTO warnings (user_id, login_faults)
        VALUES (new_user_id, 0);
    END IF;
END;
$$;
----------------------


