PGDMP  %    2                 }            Netflix    17.2    17.2 �    �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            �           1262    16633    Netflix    DATABASE     k   CREATE DATABASE "Netflix" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'C';
    DROP DATABASE "Netflix";
                     postgres    false            �           0    0    SCHEMA public    ACL     )   GRANT ALL ON SCHEMA public TO app_admin;
                        pg_database_owner    false    5                       1255    16884 
   fn_audit()    FUNCTION     t  CREATE FUNCTION public.fn_audit() RETURNS trigger
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
 !   DROP FUNCTION public.fn_audit();
       public               postgres    false            �            1255    16865    fn_audit_subscriptions_delete()    FUNCTION     t  CREATE FUNCTION public.fn_audit_subscriptions_delete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    INSERT INTO Subscriptions_log
        (subscription_id, profile_id, price_id, start_date, end_date, deleted_at, deleted_by)
    VALUES
        (OLD.id, OLD.profile_id, OLD.price_id, OLD.start_date, OLD.end_date, NOW(), CURRENT_USER);

    RETURN OLD;
END;
$$;
 6   DROP FUNCTION public.fn_audit_subscriptions_delete();
       public               postgres    false            �            1255    16851    fn_block_on_login_faults()    FUNCTION     �  CREATE FUNCTION public.fn_block_on_login_faults() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.login_faults >= 3 THEN
        -- Блокируем на 1 день
        NEW.block_end := NOW() + INTERVAL '1 day';
    ELSE
        -- Если сбросили ниже 3, убираем блокировку
        NEW.block_end := NULL;
    END IF;
    RETURN NEW;
END;
$$;
 1   DROP FUNCTION public.fn_block_on_login_faults();
       public               postgres    false            �            1255    16853    fn_check_subscription_dates()    FUNCTION     �  CREATE FUNCTION public.fn_check_subscription_dates() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;
 4   DROP FUNCTION public.fn_check_subscription_dates();
       public               postgres    false                       1255    16867    fn_enforce_31day_subscription()    FUNCTION     �  CREATE FUNCTION public.fn_enforce_31day_subscription() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    -- Если указали start_date, но не указали end_date, 
    -- автоматически проставим end_date = start_date + 31 дней
    IF NEW.start_date IS NOT NULL
       AND NEW.end_date IS NULL
    THEN
        NEW.end_date := NEW.start_date + INTERVAL '31 day';
    END IF;

    -- Если обе даты указаны, проверим их корректность
    IF NEW.start_date IS NOT NULL 
       AND NEW.end_date IS NOT NULL
    THEN
        -- 1) end_date >= start_date
        IF NEW.end_date < NEW.start_date THEN
            RAISE EXCEPTION 'end_date (%) не может быть раньше start_date (%)',
                NEW.end_date, NEW.start_date;
        END IF;

        -- 2) Разница не должна превышать 31 день
        IF NEW.end_date > (NEW.start_date + INTERVAL '31 day') THEN
            RAISE EXCEPTION 'Период подписки не может превышать 31 день (start_date=%, end_date=%).',
                NEW.start_date, NEW.end_date;
        END IF;
    END IF;

    RETURN NEW;
END;
$$;
 6   DROP FUNCTION public.fn_enforce_31day_subscription();
       public               postgres    false            	           1255    16869 C   sp_admin_create_user(character varying, character varying, boolean) 	   PROCEDURE     �  CREATE PROCEDURE public.sp_admin_create_user(IN p_email character varying, IN p_password character varying, IN p_has_referral boolean DEFAULT false)
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
    INSERT INTO Users (email, password, has_used_referral_link)
    VALUES (p_email, p_password, p_has_referral);

    RAISE NOTICE 'New user created with email: %', p_email;
END;
$$;
 �   DROP PROCEDURE public.sp_admin_create_user(IN p_email character varying, IN p_password character varying, IN p_has_referral boolean);
       public               postgres    false            �           0    0 x   PROCEDURE sp_admin_create_user(IN p_email character varying, IN p_password character varying, IN p_has_referral boolean)    ACL     �   GRANT ALL ON PROCEDURE public.sp_admin_create_user(IN p_email character varying, IN p_password character varying, IN p_has_referral boolean) TO app_admin;
          public               postgres    false    265                       1255    16871    sp_admin_delete_user(integer) 	   PROCEDURE       CREATE PROCEDURE public.sp_admin_delete_user(IN p_user_id integer)
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
    DELETE FROM Users
     WHERE id = p_user_id;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'User with id % not found', p_user_id;
    END IF;
END;
$$;
 B   DROP PROCEDURE public.sp_admin_delete_user(IN p_user_id integer);
       public               postgres    false            �           0    0 4   PROCEDURE sp_admin_delete_user(IN p_user_id integer)    ACL     W   GRANT ALL ON PROCEDURE public.sp_admin_delete_user(IN p_user_id integer) TO app_admin;
          public               postgres    false    267            
           1255    16870 L   sp_admin_update_user(integer, character varying, character varying, boolean) 	   PROCEDURE     �  CREATE PROCEDURE public.sp_admin_update_user(IN p_user_id integer, IN p_email character varying, IN p_password character varying, IN p_has_referral boolean)
    LANGUAGE plpgsql SECURITY DEFINER
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
 �   DROP PROCEDURE public.sp_admin_update_user(IN p_user_id integer, IN p_email character varying, IN p_password character varying, IN p_has_referral boolean);
       public               postgres    false            �           0    0 �   PROCEDURE sp_admin_update_user(IN p_user_id integer, IN p_email character varying, IN p_password character varying, IN p_has_referral boolean)    ACL     �   GRANT ALL ON PROCEDURE public.sp_admin_update_user(IN p_user_id integer, IN p_email character varying, IN p_password character varying, IN p_has_referral boolean) TO app_admin;
          public               postgres    false    266            �            1255    16872 �   sp_create_content(character varying, character varying, text, character varying, double precision, character varying, integer, integer, integer) 	   PROCEDURE       CREATE PROCEDURE public.sp_create_content(IN p_title character varying, IN p_poster character varying, IN p_description text, IN p_video_link character varying, IN p_duration double precision, IN p_type character varying, IN p_season integer DEFAULT NULL::integer, IN p_episode_num integer DEFAULT NULL::integer, IN p_series_id integer DEFAULT NULL::integer)
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
DECLARE
    v_new_id INT;
BEGIN
    INSERT INTO Content (title, poster, description, video_link, duration, type, season, episode_number, series_id)
    VALUES (p_title, p_poster, p_description, p_video_link, p_duration, p_type, p_season, p_episode_num, p_series_id)
    RETURNING id INTO v_new_id;

    RAISE NOTICE 'Content created with id %', v_new_id;
END;
$$;
 $  DROP PROCEDURE public.sp_create_content(IN p_title character varying, IN p_poster character varying, IN p_description text, IN p_video_link character varying, IN p_duration double precision, IN p_type character varying, IN p_season integer, IN p_episode_num integer, IN p_series_id integer);
       public               postgres    false            �           0    0   PROCEDURE sp_create_content(IN p_title character varying, IN p_poster character varying, IN p_description text, IN p_video_link character varying, IN p_duration double precision, IN p_type character varying, IN p_season integer, IN p_episode_num integer, IN p_series_id integer)    ACL     ?  GRANT ALL ON PROCEDURE public.sp_create_content(IN p_title character varying, IN p_poster character varying, IN p_description text, IN p_video_link character varying, IN p_duration double precision, IN p_type character varying, IN p_season integer, IN p_episode_num integer, IN p_series_id integer) TO content_manager;
          public               postgres    false    251            �            1255    16873    sp_delete_content(integer) 	   PROCEDURE       CREATE PROCEDURE public.sp_delete_content(IN p_content_id integer)
    LANGUAGE plpgsql SECURITY DEFINER
    AS $$
BEGIN
    DELETE FROM Content
     WHERE id = p_content_id;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'Content with id % not found', p_content_id;
    END IF;
END;
$$;
 B   DROP PROCEDURE public.sp_delete_content(IN p_content_id integer);
       public               postgres    false            �           0    0 4   PROCEDURE sp_delete_content(IN p_content_id integer)    ACL     ]   GRANT ALL ON PROCEDURE public.sp_delete_content(IN p_content_id integer) TO content_manager;
          public               postgres    false    252            �            1259    16875 	   audit_log    TABLE       CREATE TABLE public.audit_log (
    audit_id integer NOT NULL,
    table_name text NOT NULL,
    operation text NOT NULL,
    changed_by text NOT NULL,
    changed_at timestamp without time zone DEFAULT now() NOT NULL,
    old_data jsonb,
    new_data jsonb
);
    DROP TABLE public.audit_log;
       public         heap r       postgres    false            �            1259    16874    audit_log_audit_id_seq    SEQUENCE     �   CREATE SEQUENCE public.audit_log_audit_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 -   DROP SEQUENCE public.audit_log_audit_id_seq;
       public               postgres    false    247            �           0    0    audit_log_audit_id_seq    SEQUENCE OWNED BY     Q   ALTER SEQUENCE public.audit_log_audit_id_seq OWNED BY public.audit_log.audit_id;
          public               postgres    false    246            �            1259    16741    content    TABLE     J  CREATE TABLE public.content (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    poster character varying(255),
    description text,
    video_link character varying(255),
    duration double precision,
    type character varying(50),
    season integer,
    episode_number integer,
    series_id integer
);
    DROP TABLE public.content;
       public         heap r       postgres    false            �           0    0    TABLE content    ACL     �   GRANT ALL ON TABLE public.content TO app_admin;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.content TO content_manager;
GRANT SELECT ON TABLE public.content TO analytics_viewer;
          public               postgres    false    231            �            1259    16782    genre    TABLE     a   CREATE TABLE public.genre (
    id integer NOT NULL,
    name character varying(100) NOT NULL
);
    DROP TABLE public.genre;
       public         heap r       postgres    false            �           0    0    TABLE genre    ACL     �   GRANT ALL ON TABLE public.genre TO app_admin;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.genre TO content_manager;
GRANT SELECT ON TABLE public.genre TO analytics_viewer;
          public               postgres    false    236            �            1259    16788    genre_bridge    TABLE     e   CREATE TABLE public.genre_bridge (
    content_id integer NOT NULL,
    genre_id integer NOT NULL
);
     DROP TABLE public.genre_bridge;
       public         heap r       postgres    false            �           0    0    TABLE genre_bridge    ACL     �   GRANT ALL ON TABLE public.genre_bridge TO app_admin;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.genre_bridge TO content_manager;
GRANT SELECT ON TABLE public.genre_bridge TO analytics_viewer;
          public               postgres    false    237            �            1259    16781    genre_id_seq    SEQUENCE     �   CREATE SEQUENCE public.genre_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.genre_id_seq;
       public               postgres    false    236            �           0    0    genre_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.genre_id_seq OWNED BY public.genre.id;
          public               postgres    false    235            �           0    0    SEQUENCE genre_id_seq    ACL     8   GRANT ALL ON SEQUENCE public.genre_id_seq TO app_admin;
          public               postgres    false    235            �            1259    16709    preferences    TABLE     m   CREATE TABLE public.preferences (
    profile_id integer NOT NULL,
    classification_id integer NOT NULL
);
    DROP TABLE public.preferences;
       public         heap r       postgres    false            �           0    0    TABLE preferences    ACL     r   GRANT ALL ON TABLE public.preferences TO app_admin;
GRANT SELECT ON TABLE public.preferences TO analytics_viewer;
          public               postgres    false    228            �            1259    16640    prices    TABLE     u   CREATE TABLE public.prices (
    id integer NOT NULL,
    resolution_id integer NOT NULL,
    price numeric(10,2)
);
    DROP TABLE public.prices;
       public         heap r       postgres    false            �           0    0    TABLE prices    ACL     h   GRANT ALL ON TABLE public.prices TO app_admin;
GRANT SELECT ON TABLE public.prices TO analytics_viewer;
          public               postgres    false    219            �            1259    16639    prices_id_seq    SEQUENCE     �   CREATE SEQUENCE public.prices_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 $   DROP SEQUENCE public.prices_id_seq;
       public               postgres    false    219                        0    0    prices_id_seq    SEQUENCE OWNED BY     ?   ALTER SEQUENCE public.prices_id_seq OWNED BY public.prices.id;
          public               postgres    false    218                       0    0    SEQUENCE prices_id_seq    ACL     9   GRANT ALL ON SEQUENCE public.prices_id_seq TO app_admin;
          public               postgres    false    218            �            1259    16690    profile    TABLE     �   CREATE TABLE public.profile (
    id integer NOT NULL,
    user_id integer NOT NULL,
    profile_image_link character varying(255),
    is_child boolean DEFAULT false,
    language character varying(50)
);
    DROP TABLE public.profile;
       public         heap r       postgres    false                       0    0    TABLE profile    ACL     j   GRANT ALL ON TABLE public.profile TO app_admin;
GRANT SELECT ON TABLE public.profile TO analytics_viewer;
          public               postgres    false    225            �            1259    16689    profile_id_seq    SEQUENCE     �   CREATE SEQUENCE public.profile_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 %   DROP SEQUENCE public.profile_id_seq;
       public               postgres    false    225                       0    0    profile_id_seq    SEQUENCE OWNED BY     A   ALTER SEQUENCE public.profile_id_seq OWNED BY public.profile.id;
          public               postgres    false    224                       0    0    SEQUENCE profile_id_seq    ACL     :   GRANT ALL ON SEQUENCE public.profile_id_seq TO app_admin;
          public               postgres    false    224            �            1259    16813    quality_ranges    TABLE     l   CREATE TABLE public.quality_ranges (
    content_id integer NOT NULL,
    resolution_id integer NOT NULL
);
 "   DROP TABLE public.quality_ranges;
       public         heap r       postgres    false                       0    0    TABLE quality_ranges    ACL     �   GRANT ALL ON TABLE public.quality_ranges TO app_admin;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.quality_ranges TO content_manager;
GRANT SELECT ON TABLE public.quality_ranges TO analytics_viewer;
          public               postgres    false    239            �            1259    16674 	   referrals    TABLE     a   CREATE TABLE public.referrals (
    host_id integer NOT NULL,
    invited_id integer NOT NULL
);
    DROP TABLE public.referrals;
       public         heap r       postgres    false                       0    0    TABLE referrals    ACL     n   GRANT ALL ON TABLE public.referrals TO app_admin;
GRANT SELECT ON TABLE public.referrals TO analytics_viewer;
          public               postgres    false    223            �            1259    16634    resolutions    TABLE     y   CREATE TABLE public.resolutions (
    resolution integer NOT NULL,
    resolution_name character varying(50) NOT NULL
);
    DROP TABLE public.resolutions;
       public         heap r       postgres    false                       0    0    TABLE resolutions    ACL     r   GRANT ALL ON TABLE public.resolutions TO app_admin;
GRANT SELECT ON TABLE public.resolutions TO analytics_viewer;
          public               postgres    false    217            �            1259    16725    subscriptions    TABLE     �   CREATE TABLE public.subscriptions (
    id integer NOT NULL,
    profile_id integer NOT NULL,
    price_id integer NOT NULL,
    start_date date NOT NULL,
    end_date date
);
 !   DROP TABLE public.subscriptions;
       public         heap r       postgres    false                       0    0    TABLE subscriptions    ACL     v   GRANT ALL ON TABLE public.subscriptions TO app_admin;
GRANT SELECT ON TABLE public.subscriptions TO analytics_viewer;
          public               postgres    false    230            �            1259    16724    subscriptions_id_seq    SEQUENCE     �   CREATE SEQUENCE public.subscriptions_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE public.subscriptions_id_seq;
       public               postgres    false    230            	           0    0    subscriptions_id_seq    SEQUENCE OWNED BY     M   ALTER SEQUENCE public.subscriptions_id_seq OWNED BY public.subscriptions.id;
          public               postgres    false    229            
           0    0    SEQUENCE subscriptions_id_seq    ACL     @   GRANT ALL ON SEQUENCE public.subscriptions_id_seq TO app_admin;
          public               postgres    false    229            �            1259    16856    subscriptions_log    TABLE     	  CREATE TABLE public.subscriptions_log (
    log_id integer NOT NULL,
    subscription_id integer,
    profile_id integer,
    price_id integer,
    start_date date,
    end_date date,
    deleted_at timestamp without time zone DEFAULT now(),
    deleted_by text
);
 %   DROP TABLE public.subscriptions_log;
       public         heap r       postgres    false            �            1259    16855    subscriptions_log_log_id_seq    SEQUENCE     �   CREATE SEQUENCE public.subscriptions_log_log_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 3   DROP SEQUENCE public.subscriptions_log_log_id_seq;
       public               postgres    false    245                       0    0    subscriptions_log_log_id_seq    SEQUENCE OWNED BY     ]   ALTER SEQUENCE public.subscriptions_log_log_id_seq OWNED BY public.subscriptions_log.log_id;
          public               postgres    false    244            �            1259    16803    subtitle    TABLE     o   CREATE TABLE public.subtitle (
    content_id integer NOT NULL,
    language character varying(50) NOT NULL
);
    DROP TABLE public.subtitle;
       public         heap r       postgres    false                       0    0    TABLE subtitle    ACL     �   GRANT ALL ON TABLE public.subtitle TO app_admin;
GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE public.subtitle TO content_manager;
GRANT SELECT ON TABLE public.subtitle TO analytics_viewer;
          public               postgres    false    238            �            1259    16703    tag    TABLE     �   CREATE TABLE public.tag (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    type character varying(100)
);
    DROP TABLE public.tag;
       public         heap r       postgres    false                       0    0 	   TABLE tag    ACL     b   GRANT ALL ON TABLE public.tag TO app_admin;
GRANT SELECT ON TABLE public.tag TO analytics_viewer;
          public               postgres    false    227            �            1259    16702 
   tag_id_seq    SEQUENCE     �   CREATE SEQUENCE public.tag_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 !   DROP SEQUENCE public.tag_id_seq;
       public               postgres    false    227                       0    0 
   tag_id_seq    SEQUENCE OWNED BY     9   ALTER SEQUENCE public.tag_id_seq OWNED BY public.tag.id;
          public               postgres    false    226                       0    0    SEQUENCE tag_id_seq    ACL     6   GRANT ALL ON SEQUENCE public.tag_id_seq TO app_admin;
          public               postgres    false    226            �            1259    16652    users    TABLE     �   CREATE TABLE public.users (
    id integer NOT NULL,
    email character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    has_used_referral_link boolean DEFAULT false
);
    DROP TABLE public.users;
       public         heap r       postgres    false                       0    0    TABLE users    ACL     f   GRANT ALL ON TABLE public.users TO app_admin;
GRANT SELECT ON TABLE public.users TO analytics_viewer;
          public               postgres    false    221            �            1259    16651    users_id_seq    SEQUENCE     �   CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.users_id_seq;
       public               postgres    false    221                       0    0    users_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;
          public               postgres    false    220                       0    0    SEQUENCE users_id_seq    ACL     8   GRANT ALL ON SEQUENCE public.users_id_seq TO app_admin;
          public               postgres    false    220            �            1259    16846    v_content_overview    VIEW       CREATE VIEW public.v_content_overview AS
 WITH genre_list AS (
         SELECT gb.content_id,
            string_agg((g.name)::text, ', '::text) AS genres
           FROM (public.genre_bridge gb
             JOIN public.genre g ON ((g.id = gb.genre_id)))
          GROUP BY gb.content_id
        ), subtitle_list AS (
         SELECT s.content_id,
            string_agg((s.language)::text, ', '::text) AS subtitles
           FROM public.subtitle s
          GROUP BY s.content_id
        ), resolution_list AS (
         SELECT qr.content_id,
            string_agg((r.resolution_name)::text, ', '::text) AS available_resolutions
           FROM (public.quality_ranges qr
             JOIN public.resolutions r ON ((r.resolution = qr.resolution_id)))
          GROUP BY qr.content_id
        )
 SELECT c.id AS content_id,
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
   FROM (((public.content c
     LEFT JOIN genre_list ON ((genre_list.content_id = c.id)))
     LEFT JOIN subtitle_list ON ((subtitle_list.content_id = c.id)))
     LEFT JOIN resolution_list ON ((resolution_list.content_id = c.id)));
 %   DROP VIEW public.v_content_overview;
       public       v       postgres    false    231    238    239    239    231    231    231    237    231    231    238    231    231    217    217    231    231    236    236    237                       0    0    TABLE v_content_overview    ACL     �   GRANT ALL ON TABLE public.v_content_overview TO app_admin;
GRANT SELECT ON TABLE public.v_content_overview TO analytics_viewer;
          public               postgres    false    243            �            1259    16842    v_profile_preferences    VIEW     r  CREATE VIEW public.v_profile_preferences AS
 SELECT pf.id AS profile_id,
    u.id AS user_id,
    u.email AS user_email,
    t.name AS tag_name,
    t.type AS tag_type
   FROM (((public.preferences pr
     JOIN public.profile pf ON ((pf.id = pr.profile_id)))
     JOIN public.users u ON ((u.id = pf.user_id)))
     JOIN public.tag t ON ((t.id = pr.classification_id)));
 (   DROP VIEW public.v_profile_preferences;
       public       v       postgres    false    227    221    221    225    228    228    227    225    227                       0    0    TABLE v_profile_preferences    ACL     �   GRANT ALL ON TABLE public.v_profile_preferences TO app_admin;
GRANT SELECT ON TABLE public.v_profile_preferences TO analytics_viewer;
          public               postgres    false    242            �            1259    16832    v_user_subscriptions    VIEW       CREATE VIEW public.v_user_subscriptions AS
 SELECT s.id AS subscription_id,
    s.profile_id,
    pf.user_id,
    u.email,
    p.price,
    r.resolution_name,
    s.start_date,
    s.end_date
   FROM ((((public.subscriptions s
     JOIN public.profile pf ON ((pf.id = s.profile_id)))
     JOIN public.users u ON ((u.id = pf.user_id)))
     JOIN public.prices p ON ((p.id = s.price_id)))
     JOIN public.resolutions r ON ((r.resolution = p.resolution_id)))
  WHERE ((s.end_date IS NULL) OR (s.end_date > CURRENT_DATE));
 '   DROP VIEW public.v_user_subscriptions;
       public       v       postgres    false    230    230    230    230    230    225    225    221    221    219    219    219    217    217                       0    0    TABLE v_user_subscriptions    ACL     �   GRANT ALL ON TABLE public.v_user_subscriptions TO app_admin;
GRANT SELECT ON TABLE public.v_user_subscriptions TO analytics_viewer;
          public               postgres    false    240            �            1259    16749    watch_histories    TABLE     �   CREATE TABLE public.watch_histories (
    id integer NOT NULL,
    profile_id integer NOT NULL,
    content_id integer NOT NULL,
    stopped_at double precision,
    watching_times integer DEFAULT 1
);
 #   DROP TABLE public.watch_histories;
       public         heap r       postgres    false                       0    0    TABLE watch_histories    ACL     z   GRANT ALL ON TABLE public.watch_histories TO app_admin;
GRANT SELECT ON TABLE public.watch_histories TO analytics_viewer;
          public               postgres    false    233            �            1259    16837    v_watch_history_detailed    VIEW     �  CREATE VIEW public.v_watch_history_detailed AS
 SELECT wh.id AS watch_history_id,
    pf.id AS profile_id,
    u.id AS user_id,
    u.email AS user_email,
    c.id AS content_id,
    c.title AS content_title,
    wh.stopped_at,
    wh.watching_times
   FROM (((public.watch_histories wh
     JOIN public.profile pf ON ((pf.id = wh.profile_id)))
     JOIN public.users u ON ((u.id = pf.user_id)))
     JOIN public.content c ON ((c.id = wh.content_id)));
 +   DROP VIEW public.v_watch_history_detailed;
       public       v       postgres    false    233    221    221    225    225    231    231    233    233    233    233                       0    0    TABLE v_watch_history_detailed    ACL     �   GRANT ALL ON TABLE public.v_watch_history_detailed TO app_admin;
GRANT SELECT ON TABLE public.v_watch_history_detailed TO analytics_viewer;
          public               postgres    false    241            �            1259    16663    warnings    TABLE     �   CREATE TABLE public.warnings (
    user_id integer NOT NULL,
    login_faults integer DEFAULT 0,
    block_end timestamp without time zone
);
    DROP TABLE public.warnings;
       public         heap r       postgres    false                       0    0    TABLE warnings    ACL     l   GRANT ALL ON TABLE public.warnings TO app_admin;
GRANT SELECT ON TABLE public.warnings TO analytics_viewer;
          public               postgres    false    222            �            1259    16748    watch_histories_id_seq    SEQUENCE     �   CREATE SEQUENCE public.watch_histories_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 -   DROP SEQUENCE public.watch_histories_id_seq;
       public               postgres    false    233                       0    0    watch_histories_id_seq    SEQUENCE OWNED BY     Q   ALTER SEQUENCE public.watch_histories_id_seq OWNED BY public.watch_histories.id;
          public               postgres    false    232                       0    0    SEQUENCE watch_histories_id_seq    ACL     B   GRANT ALL ON SEQUENCE public.watch_histories_id_seq TO app_admin;
          public               postgres    false    232            �            1259    16766    watch_later    TABLE     f   CREATE TABLE public.watch_later (
    profile_id integer NOT NULL,
    content_id integer NOT NULL
);
    DROP TABLE public.watch_later;
       public         heap r       postgres    false                       0    0    TABLE watch_later    ACL     r   GRANT ALL ON TABLE public.watch_later TO app_admin;
GRANT SELECT ON TABLE public.watch_later TO analytics_viewer;
          public               postgres    false    234            �           2604    16878    audit_log audit_id    DEFAULT     x   ALTER TABLE ONLY public.audit_log ALTER COLUMN audit_id SET DEFAULT nextval('public.audit_log_audit_id_seq'::regclass);
 A   ALTER TABLE public.audit_log ALTER COLUMN audit_id DROP DEFAULT;
       public               postgres    false    247    246    247            �           2604    16785    genre id    DEFAULT     d   ALTER TABLE ONLY public.genre ALTER COLUMN id SET DEFAULT nextval('public.genre_id_seq'::regclass);
 7   ALTER TABLE public.genre ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    235    236    236            �           2604    16643 	   prices id    DEFAULT     f   ALTER TABLE ONLY public.prices ALTER COLUMN id SET DEFAULT nextval('public.prices_id_seq'::regclass);
 8   ALTER TABLE public.prices ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    218    219    219            �           2604    16693 
   profile id    DEFAULT     h   ALTER TABLE ONLY public.profile ALTER COLUMN id SET DEFAULT nextval('public.profile_id_seq'::regclass);
 9   ALTER TABLE public.profile ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    224    225    225            �           2604    16728    subscriptions id    DEFAULT     t   ALTER TABLE ONLY public.subscriptions ALTER COLUMN id SET DEFAULT nextval('public.subscriptions_id_seq'::regclass);
 ?   ALTER TABLE public.subscriptions ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    230    229    230            �           2604    16859    subscriptions_log log_id    DEFAULT     �   ALTER TABLE ONLY public.subscriptions_log ALTER COLUMN log_id SET DEFAULT nextval('public.subscriptions_log_log_id_seq'::regclass);
 G   ALTER TABLE public.subscriptions_log ALTER COLUMN log_id DROP DEFAULT;
       public               postgres    false    245    244    245            �           2604    16706    tag id    DEFAULT     `   ALTER TABLE ONLY public.tag ALTER COLUMN id SET DEFAULT nextval('public.tag_id_seq'::regclass);
 5   ALTER TABLE public.tag ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    226    227    227            �           2604    16655    users id    DEFAULT     d   ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);
 7   ALTER TABLE public.users ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    220    221    221            �           2604    16752    watch_histories id    DEFAULT     x   ALTER TABLE ONLY public.watch_histories ALTER COLUMN id SET DEFAULT nextval('public.watch_histories_id_seq'::regclass);
 A   ALTER TABLE public.watch_histories ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    233    232    233            �          0    16875 	   audit_log 
   TABLE DATA           p   COPY public.audit_log (audit_id, table_name, operation, changed_by, changed_at, old_data, new_data) FROM stdin;
    public               postgres    false    247   ��       �          0    16741    content 
   TABLE DATA           �   COPY public.content (id, title, poster, description, video_link, duration, type, season, episode_number, series_id) FROM stdin;
    public               postgres    false    231   ��       �          0    16782    genre 
   TABLE DATA           )   COPY public.genre (id, name) FROM stdin;
    public               postgres    false    236   ��       �          0    16788    genre_bridge 
   TABLE DATA           <   COPY public.genre_bridge (content_id, genre_id) FROM stdin;
    public               postgres    false    237   �       �          0    16709    preferences 
   TABLE DATA           D   COPY public.preferences (profile_id, classification_id) FROM stdin;
    public               postgres    false    228   2�       �          0    16640    prices 
   TABLE DATA           :   COPY public.prices (id, resolution_id, price) FROM stdin;
    public               postgres    false    219   O�       �          0    16690    profile 
   TABLE DATA           V   COPY public.profile (id, user_id, profile_image_link, is_child, language) FROM stdin;
    public               postgres    false    225   l�       �          0    16813    quality_ranges 
   TABLE DATA           C   COPY public.quality_ranges (content_id, resolution_id) FROM stdin;
    public               postgres    false    239   ��       �          0    16674 	   referrals 
   TABLE DATA           8   COPY public.referrals (host_id, invited_id) FROM stdin;
    public               postgres    false    223   ��       �          0    16634    resolutions 
   TABLE DATA           B   COPY public.resolutions (resolution, resolution_name) FROM stdin;
    public               postgres    false    217   ��       �          0    16725    subscriptions 
   TABLE DATA           W   COPY public.subscriptions (id, profile_id, price_id, start_date, end_date) FROM stdin;
    public               postgres    false    230   ��       �          0    16856    subscriptions_log 
   TABLE DATA           �   COPY public.subscriptions_log (log_id, subscription_id, profile_id, price_id, start_date, end_date, deleted_at, deleted_by) FROM stdin;
    public               postgres    false    245   ��       �          0    16803    subtitle 
   TABLE DATA           8   COPY public.subtitle (content_id, language) FROM stdin;
    public               postgres    false    238   �       �          0    16703    tag 
   TABLE DATA           -   COPY public.tag (id, name, type) FROM stdin;
    public               postgres    false    227   7�       �          0    16652    users 
   TABLE DATA           L   COPY public.users (id, email, password, has_used_referral_link) FROM stdin;
    public               postgres    false    221   T�       �          0    16663    warnings 
   TABLE DATA           D   COPY public.warnings (user_id, login_faults, block_end) FROM stdin;
    public               postgres    false    222   q�       �          0    16749    watch_histories 
   TABLE DATA           a   COPY public.watch_histories (id, profile_id, content_id, stopped_at, watching_times) FROM stdin;
    public               postgres    false    233   ��       �          0    16766    watch_later 
   TABLE DATA           =   COPY public.watch_later (profile_id, content_id) FROM stdin;
    public               postgres    false    234   ��                  0    0    audit_log_audit_id_seq    SEQUENCE SET     E   SELECT pg_catalog.setval('public.audit_log_audit_id_seq', 1, false);
          public               postgres    false    246                       0    0    genre_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.genre_id_seq', 1, false);
          public               postgres    false    235                       0    0    prices_id_seq    SEQUENCE SET     <   SELECT pg_catalog.setval('public.prices_id_seq', 1, false);
          public               postgres    false    218                       0    0    profile_id_seq    SEQUENCE SET     =   SELECT pg_catalog.setval('public.profile_id_seq', 1, false);
          public               postgres    false    224                        0    0    subscriptions_id_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('public.subscriptions_id_seq', 1, false);
          public               postgres    false    229            !           0    0    subscriptions_log_log_id_seq    SEQUENCE SET     K   SELECT pg_catalog.setval('public.subscriptions_log_log_id_seq', 1, false);
          public               postgres    false    244            "           0    0 
   tag_id_seq    SEQUENCE SET     9   SELECT pg_catalog.setval('public.tag_id_seq', 1, false);
          public               postgres    false    226            #           0    0    users_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.users_id_seq', 1, false);
          public               postgres    false    220            $           0    0    watch_histories_id_seq    SEQUENCE SET     E   SELECT pg_catalog.setval('public.watch_histories_id_seq', 1, false);
          public               postgres    false    232                       2606    16883    audit_log audit_log_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.audit_log
    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (audit_id);
 B   ALTER TABLE ONLY public.audit_log DROP CONSTRAINT audit_log_pkey;
       public                 postgres    false    247                       2606    16747    content content_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.content
    ADD CONSTRAINT content_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.content DROP CONSTRAINT content_pkey;
       public                 postgres    false    231                       2606    16792    genre_bridge genre_bridge_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY public.genre_bridge
    ADD CONSTRAINT genre_bridge_pkey PRIMARY KEY (content_id, genre_id);
 H   ALTER TABLE ONLY public.genre_bridge DROP CONSTRAINT genre_bridge_pkey;
       public                 postgres    false    237    237            
           2606    16787    genre genre_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.genre
    ADD CONSTRAINT genre_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.genre DROP CONSTRAINT genre_pkey;
       public                 postgres    false    236                        2606    16713    preferences preferences_pkey 
   CONSTRAINT     u   ALTER TABLE ONLY public.preferences
    ADD CONSTRAINT preferences_pkey PRIMARY KEY (profile_id, classification_id);
 F   ALTER TABLE ONLY public.preferences DROP CONSTRAINT preferences_pkey;
       public                 postgres    false    228    228            �           2606    16645    prices prices_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.prices
    ADD CONSTRAINT prices_pkey PRIMARY KEY (id);
 <   ALTER TABLE ONLY public.prices DROP CONSTRAINT prices_pkey;
       public                 postgres    false    219            �           2606    16696    profile profile_pkey 
   CONSTRAINT     R   ALTER TABLE ONLY public.profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (id);
 >   ALTER TABLE ONLY public.profile DROP CONSTRAINT profile_pkey;
       public                 postgres    false    225                       2606    16817 "   quality_ranges quality_ranges_pkey 
   CONSTRAINT     w   ALTER TABLE ONLY public.quality_ranges
    ADD CONSTRAINT quality_ranges_pkey PRIMARY KEY (content_id, resolution_id);
 L   ALTER TABLE ONLY public.quality_ranges DROP CONSTRAINT quality_ranges_pkey;
       public                 postgres    false    239    239            �           2606    16678    referrals referrals_pkey 
   CONSTRAINT     g   ALTER TABLE ONLY public.referrals
    ADD CONSTRAINT referrals_pkey PRIMARY KEY (host_id, invited_id);
 B   ALTER TABLE ONLY public.referrals DROP CONSTRAINT referrals_pkey;
       public                 postgres    false    223    223            �           2606    16638    resolutions resolutions_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY public.resolutions
    ADD CONSTRAINT resolutions_pkey PRIMARY KEY (resolution);
 F   ALTER TABLE ONLY public.resolutions DROP CONSTRAINT resolutions_pkey;
       public                 postgres    false    217                       2606    16864 (   subscriptions_log subscriptions_log_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY public.subscriptions_log
    ADD CONSTRAINT subscriptions_log_pkey PRIMARY KEY (log_id);
 R   ALTER TABLE ONLY public.subscriptions_log DROP CONSTRAINT subscriptions_log_pkey;
       public                 postgres    false    245                       2606    16730     subscriptions subscriptions_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT subscriptions_pkey PRIMARY KEY (id);
 J   ALTER TABLE ONLY public.subscriptions DROP CONSTRAINT subscriptions_pkey;
       public                 postgres    false    230                       2606    16807    subtitle subtitle_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY public.subtitle
    ADD CONSTRAINT subtitle_pkey PRIMARY KEY (content_id, language);
 @   ALTER TABLE ONLY public.subtitle DROP CONSTRAINT subtitle_pkey;
       public                 postgres    false    238    238            �           2606    16708    tag tag_pkey 
   CONSTRAINT     J   ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);
 6   ALTER TABLE ONLY public.tag DROP CONSTRAINT tag_pkey;
       public                 postgres    false    227            �           2606    16662    users users_email_key 
   CONSTRAINT     Q   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);
 ?   ALTER TABLE ONLY public.users DROP CONSTRAINT users_email_key;
       public                 postgres    false    221            �           2606    16660    users users_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
       public                 postgres    false    221            �           2606    16668    warnings warnings_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY public.warnings
    ADD CONSTRAINT warnings_pkey PRIMARY KEY (user_id);
 @   ALTER TABLE ONLY public.warnings DROP CONSTRAINT warnings_pkey;
       public                 postgres    false    222                       2606    16755 $   watch_histories watch_histories_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY public.watch_histories
    ADD CONSTRAINT watch_histories_pkey PRIMARY KEY (id);
 N   ALTER TABLE ONLY public.watch_histories DROP CONSTRAINT watch_histories_pkey;
       public                 postgres    false    233                       2606    16770    watch_later watch_later_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY public.watch_later
    ADD CONSTRAINT watch_later_pkey PRIMARY KEY (profile_id, content_id);
 F   ALTER TABLE ONLY public.watch_later DROP CONSTRAINT watch_later_pkey;
       public                 postgres    false    234    234            4           2620    16901    content trg_audit_content    TRIGGER     �   CREATE TRIGGER trg_audit_content AFTER INSERT OR DELETE OR UPDATE ON public.content FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 2   DROP TRIGGER trg_audit_content ON public.content;
       public               postgres    false    268    231            7           2620    16894    genre trg_audit_genre    TRIGGER     �   CREATE TRIGGER trg_audit_genre AFTER INSERT OR DELETE OR UPDATE ON public.genre FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 .   DROP TRIGGER trg_audit_genre ON public.genre;
       public               postgres    false    268    236            8           2620    16895 #   genre_bridge trg_audit_genre_bridge    TRIGGER     �   CREATE TRIGGER trg_audit_genre_bridge AFTER INSERT OR DELETE OR UPDATE ON public.genre_bridge FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 <   DROP TRIGGER trg_audit_genre_bridge ON public.genre_bridge;
       public               postgres    false    237    268            /           2620    16890 !   preferences trg_audit_preferences    TRIGGER     �   CREATE TRIGGER trg_audit_preferences AFTER INSERT OR DELETE OR UPDATE ON public.preferences FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 :   DROP TRIGGER trg_audit_preferences ON public.preferences;
       public               postgres    false    268    228            (           2620    16887    prices trg_audit_prices    TRIGGER     �   CREATE TRIGGER trg_audit_prices AFTER INSERT OR DELETE OR UPDATE ON public.prices FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 0   DROP TRIGGER trg_audit_prices ON public.prices;
       public               postgres    false    219    268            -           2620    16899    profile trg_audit_profile    TRIGGER     �   CREATE TRIGGER trg_audit_profile AFTER INSERT OR DELETE OR UPDATE ON public.profile FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 2   DROP TRIGGER trg_audit_profile ON public.profile;
       public               postgres    false    225    268            :           2620    16897 '   quality_ranges trg_audit_quality_ranges    TRIGGER     �   CREATE TRIGGER trg_audit_quality_ranges AFTER INSERT OR DELETE OR UPDATE ON public.quality_ranges FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 @   DROP TRIGGER trg_audit_quality_ranges ON public.quality_ranges;
       public               postgres    false    268    239            ,           2620    16889    referrals trg_audit_referrals    TRIGGER     �   CREATE TRIGGER trg_audit_referrals AFTER INSERT OR DELETE OR UPDATE ON public.referrals FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 6   DROP TRIGGER trg_audit_referrals ON public.referrals;
       public               postgres    false    268    223            '           2620    16886 !   resolutions trg_audit_resolutions    TRIGGER     �   CREATE TRIGGER trg_audit_resolutions AFTER INSERT OR DELETE OR UPDATE ON public.resolutions FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 :   DROP TRIGGER trg_audit_resolutions ON public.resolutions;
       public               postgres    false    268    217            0           2620    16900 %   subscriptions trg_audit_subscriptions    TRIGGER     �   CREATE TRIGGER trg_audit_subscriptions AFTER INSERT OR DELETE OR UPDATE ON public.subscriptions FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 >   DROP TRIGGER trg_audit_subscriptions ON public.subscriptions;
       public               postgres    false    268    230            1           2620    16866 ,   subscriptions trg_audit_subscriptions_delete    TRIGGER     �   CREATE TRIGGER trg_audit_subscriptions_delete AFTER DELETE ON public.subscriptions FOR EACH ROW EXECUTE FUNCTION public.fn_audit_subscriptions_delete();
 E   DROP TRIGGER trg_audit_subscriptions_delete ON public.subscriptions;
       public               postgres    false    230    250            ;           2620    16902 -   subscriptions_log trg_audit_subscriptions_log    TRIGGER     �   CREATE TRIGGER trg_audit_subscriptions_log AFTER INSERT OR DELETE OR UPDATE ON public.subscriptions_log FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 F   DROP TRIGGER trg_audit_subscriptions_log ON public.subscriptions_log;
       public               postgres    false    268    245            9           2620    16896    subtitle trg_audit_subtitle    TRIGGER     �   CREATE TRIGGER trg_audit_subtitle AFTER INSERT OR DELETE OR UPDATE ON public.subtitle FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 4   DROP TRIGGER trg_audit_subtitle ON public.subtitle;
       public               postgres    false    268    238            .           2620    16891    tag trg_audit_tag    TRIGGER     }   CREATE TRIGGER trg_audit_tag AFTER INSERT OR DELETE OR UPDATE ON public.tag FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 *   DROP TRIGGER trg_audit_tag ON public.tag;
       public               postgres    false    227    268            )           2620    16898    users trg_audit_users    TRIGGER     �   CREATE TRIGGER trg_audit_users AFTER INSERT OR DELETE OR UPDATE ON public.users FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 .   DROP TRIGGER trg_audit_users ON public.users;
       public               postgres    false    268    221            *           2620    16888    warnings trg_audit_warnings    TRIGGER     �   CREATE TRIGGER trg_audit_warnings AFTER INSERT OR DELETE OR UPDATE ON public.warnings FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 4   DROP TRIGGER trg_audit_warnings ON public.warnings;
       public               postgres    false    268    222            5           2620    16892 )   watch_histories trg_audit_watch_histories    TRIGGER     �   CREATE TRIGGER trg_audit_watch_histories AFTER INSERT OR DELETE OR UPDATE ON public.watch_histories FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 B   DROP TRIGGER trg_audit_watch_histories ON public.watch_histories;
       public               postgres    false    233    268            6           2620    16893 !   watch_later trg_audit_watch_later    TRIGGER     �   CREATE TRIGGER trg_audit_watch_later AFTER INSERT OR DELETE OR UPDATE ON public.watch_later FOR EACH ROW EXECUTE FUNCTION public.fn_audit();
 :   DROP TRIGGER trg_audit_watch_later ON public.watch_later;
       public               postgres    false    268    234            +           2620    16852 "   warnings trg_block_on_login_faults    TRIGGER     �   CREATE TRIGGER trg_block_on_login_faults BEFORE INSERT OR UPDATE ON public.warnings FOR EACH ROW EXECUTE FUNCTION public.fn_block_on_login_faults();
 ;   DROP TRIGGER trg_block_on_login_faults ON public.warnings;
       public               postgres    false    248    222            2           2620    16854 *   subscriptions trg_check_subscription_dates    TRIGGER     �   CREATE TRIGGER trg_check_subscription_dates BEFORE INSERT OR UPDATE ON public.subscriptions FOR EACH ROW EXECUTE FUNCTION public.fn_check_subscription_dates();
 C   DROP TRIGGER trg_check_subscription_dates ON public.subscriptions;
       public               postgres    false    249    230            3           2620    16868 ,   subscriptions trg_enforce_31day_subscription    TRIGGER     �   CREATE TRIGGER trg_enforce_31day_subscription BEFORE INSERT OR UPDATE ON public.subscriptions FOR EACH ROW EXECUTE FUNCTION public.fn_enforce_31day_subscription();
 E   DROP TRIGGER trg_enforce_31day_subscription ON public.subscriptions;
       public               postgres    false    230    264            "           2606    16793    genre_bridge fk_gb_content    FK CONSTRAINT     �   ALTER TABLE ONLY public.genre_bridge
    ADD CONSTRAINT fk_gb_content FOREIGN KEY (content_id) REFERENCES public.content(id) ON DELETE CASCADE;
 D   ALTER TABLE ONLY public.genre_bridge DROP CONSTRAINT fk_gb_content;
       public               postgres    false    237    3588    231            #           2606    16798    genre_bridge fk_gb_genre    FK CONSTRAINT     �   ALTER TABLE ONLY public.genre_bridge
    ADD CONSTRAINT fk_gb_genre FOREIGN KEY (genre_id) REFERENCES public.genre(id) ON DELETE CASCADE;
 B   ALTER TABLE ONLY public.genre_bridge DROP CONSTRAINT fk_gb_genre;
       public               postgres    false    3594    236    237                       2606    16714 "   preferences fk_preferences_profile    FK CONSTRAINT     �   ALTER TABLE ONLY public.preferences
    ADD CONSTRAINT fk_preferences_profile FOREIGN KEY (profile_id) REFERENCES public.profile(id) ON DELETE CASCADE;
 L   ALTER TABLE ONLY public.preferences DROP CONSTRAINT fk_preferences_profile;
       public               postgres    false    228    225    3580                       2606    16719    preferences fk_preferences_tag    FK CONSTRAINT     �   ALTER TABLE ONLY public.preferences
    ADD CONSTRAINT fk_preferences_tag FOREIGN KEY (classification_id) REFERENCES public.tag(id) ON DELETE CASCADE;
 H   ALTER TABLE ONLY public.preferences DROP CONSTRAINT fk_preferences_tag;
       public               postgres    false    227    228    3582                       2606    16646    prices fk_prices_resolution    FK CONSTRAINT     �   ALTER TABLE ONLY public.prices
    ADD CONSTRAINT fk_prices_resolution FOREIGN KEY (resolution_id) REFERENCES public.resolutions(resolution) ON DELETE RESTRICT;
 E   ALTER TABLE ONLY public.prices DROP CONSTRAINT fk_prices_resolution;
       public               postgres    false    217    3568    219                       2606    16697    profile fk_profile_user    FK CONSTRAINT     �   ALTER TABLE ONLY public.profile
    ADD CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;
 A   ALTER TABLE ONLY public.profile DROP CONSTRAINT fk_profile_user;
       public               postgres    false    3574    225    221            %           2606    16818 !   quality_ranges fk_quality_content    FK CONSTRAINT     �   ALTER TABLE ONLY public.quality_ranges
    ADD CONSTRAINT fk_quality_content FOREIGN KEY (content_id) REFERENCES public.content(id) ON DELETE CASCADE;
 K   ALTER TABLE ONLY public.quality_ranges DROP CONSTRAINT fk_quality_content;
       public               postgres    false    239    231    3588            &           2606    16823 $   quality_ranges fk_quality_resolution    FK CONSTRAINT     �   ALTER TABLE ONLY public.quality_ranges
    ADD CONSTRAINT fk_quality_resolution FOREIGN KEY (resolution_id) REFERENCES public.resolutions(resolution) ON DELETE RESTRICT;
 N   ALTER TABLE ONLY public.quality_ranges DROP CONSTRAINT fk_quality_resolution;
       public               postgres    false    3568    239    217                       2606    16679    referrals fk_referrals_host    FK CONSTRAINT     �   ALTER TABLE ONLY public.referrals
    ADD CONSTRAINT fk_referrals_host FOREIGN KEY (host_id) REFERENCES public.users(id) ON DELETE CASCADE;
 E   ALTER TABLE ONLY public.referrals DROP CONSTRAINT fk_referrals_host;
       public               postgres    false    3574    221    223                       2606    16684    referrals fk_referrals_invited    FK CONSTRAINT     �   ALTER TABLE ONLY public.referrals
    ADD CONSTRAINT fk_referrals_invited FOREIGN KEY (invited_id) REFERENCES public.users(id) ON DELETE CASCADE;
 H   ALTER TABLE ONLY public.referrals DROP CONSTRAINT fk_referrals_invited;
       public               postgres    false    221    223    3574                       2606    16736 $   subscriptions fk_subscriptions_price    FK CONSTRAINT     �   ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT fk_subscriptions_price FOREIGN KEY (price_id) REFERENCES public.prices(id) ON DELETE RESTRICT;
 N   ALTER TABLE ONLY public.subscriptions DROP CONSTRAINT fk_subscriptions_price;
       public               postgres    false    3570    219    230                       2606    16731 &   subscriptions fk_subscriptions_profile    FK CONSTRAINT     �   ALTER TABLE ONLY public.subscriptions
    ADD CONSTRAINT fk_subscriptions_profile FOREIGN KEY (profile_id) REFERENCES public.profile(id) ON DELETE CASCADE;
 P   ALTER TABLE ONLY public.subscriptions DROP CONSTRAINT fk_subscriptions_profile;
       public               postgres    false    225    230    3580            $           2606    16808    subtitle fk_subtitle_content    FK CONSTRAINT     �   ALTER TABLE ONLY public.subtitle
    ADD CONSTRAINT fk_subtitle_content FOREIGN KEY (content_id) REFERENCES public.content(id) ON DELETE CASCADE;
 F   ALTER TABLE ONLY public.subtitle DROP CONSTRAINT fk_subtitle_content;
       public               postgres    false    231    3588    238                       2606    16669    warnings fk_warnings_user    FK CONSTRAINT     �   ALTER TABLE ONLY public.warnings
    ADD CONSTRAINT fk_warnings_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;
 C   ALTER TABLE ONLY public.warnings DROP CONSTRAINT fk_warnings_user;
       public               postgres    false    221    3574    222                       2606    16761 %   watch_histories fk_watch_hist_content    FK CONSTRAINT     �   ALTER TABLE ONLY public.watch_histories
    ADD CONSTRAINT fk_watch_hist_content FOREIGN KEY (content_id) REFERENCES public.content(id) ON DELETE CASCADE;
 O   ALTER TABLE ONLY public.watch_histories DROP CONSTRAINT fk_watch_hist_content;
       public               postgres    false    233    3588    231                       2606    16756 %   watch_histories fk_watch_hist_profile    FK CONSTRAINT     �   ALTER TABLE ONLY public.watch_histories
    ADD CONSTRAINT fk_watch_hist_profile FOREIGN KEY (profile_id) REFERENCES public.profile(id) ON DELETE CASCADE;
 O   ALTER TABLE ONLY public.watch_histories DROP CONSTRAINT fk_watch_hist_profile;
       public               postgres    false    225    3580    233                        2606    16776 "   watch_later fk_watch_later_content    FK CONSTRAINT     �   ALTER TABLE ONLY public.watch_later
    ADD CONSTRAINT fk_watch_later_content FOREIGN KEY (content_id) REFERENCES public.content(id) ON DELETE CASCADE;
 L   ALTER TABLE ONLY public.watch_later DROP CONSTRAINT fk_watch_later_content;
       public               postgres    false    234    3588    231            !           2606    16771 "   watch_later fk_watch_later_profile    FK CONSTRAINT     �   ALTER TABLE ONLY public.watch_later
    ADD CONSTRAINT fk_watch_later_profile FOREIGN KEY (profile_id) REFERENCES public.profile(id) ON DELETE CASCADE;
 L   ALTER TABLE ONLY public.watch_later DROP CONSTRAINT fk_watch_later_profile;
       public               postgres    false    234    3580    225            �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �     