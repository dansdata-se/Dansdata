CREATE SCHEMA events_public;

ALTER SCHEMA events_public OWNER TO dansdata;

GRANT USAGE ON SCHEMA events_public TO PUBLIC;
CREATE OR REPLACE PROCEDURE events_public.notify_entity_deleted(entity_uri TEXT)
    LANGUAGE sql AS
$$
SELECT pg_notify('dansdata_events_v1'::TEXT, JSON_BUILD_OBJECT('type', 'delete', 'entityUri', entity_uri)::TEXT);
$$;

COMMENT ON PROCEDURE events_public.notify_entity_deleted(entity_uri TEXT) IS 'Emit a notification that the specified entity was deleted.';

GRANT EXECUTE ON PROCEDURE events_public.notify_entity_deleted TO event_emitter;
CREATE SCHEMA translations_private;

ALTER SCHEMA translations_private OWNER TO translations_owner;
CREATE TABLE IF NOT EXISTS translations_private.ext_refs (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT external_refs_pk
            PRIMARY KEY,
    uri TEXT NOT NULL
        UNIQUE
        CHECK (uri LIKE 'dansdata.entity://%')
);

COMMENT ON TABLE translations_private.ext_refs IS 'References to externally owned entities.';

COMMENT ON COLUMN translations_private.ext_refs.id IS 'Internal identifier for this reference to an external entity.';

COMMENT ON COLUMN translations_private.ext_refs.uri IS 'Globally unique URI reference for the external entity.

The expected protocol is `dansdata.entity://`';


ALTER TABLE translations_private.ext_refs
    OWNER TO translations_owner;
CREATE OR REPLACE FUNCTION translations_private.get_or_create_ext_ref(entity_uri TEXT) RETURNS INTEGER
    LANGUAGE sql AS
$$
WITH
    allocate AS ( INSERT INTO translations_private.ext_refs (uri) VALUES
                                                                      (
                                                                          get_or_create_ext_ref.entity_uri
                                                                      ) ON CONFLICT DO NOTHING RETURNING id
                )
SELECT *
    FROM
        allocate
UNION
SELECT
    id
    FROM
        translations_private.ext_refs
    WHERE
        translations_private.ext_refs.uri = get_or_create_ext_ref.entity_uri;
$$;

COMMENT ON FUNCTION translations_private.get_or_create_ext_ref(entity_uri TEXT) IS 'Retrieves the internal id to use for the given external entity, generating one if such an id does not already exist.';

ALTER FUNCTION translations_private.get_or_create_ext_ref(entity_uri TEXT) OWNER TO translations_owner;
CREATE TABLE IF NOT EXISTS translations_private.metadatas (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT metadatas_pk
            PRIMARY KEY,
    external_id uuid DEFAULT gen_random_uuid() NOT NULL
        CONSTRAINT metadatas_ext_id_uniq
            UNIQUE,
    owner_id INTEGER NOT NULL
        CONSTRAINT metadatas_owner_id_external_refs_id_fk
            REFERENCES translations_private.ext_refs
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    override_value TEXT
);

COMMENT ON TABLE translations_private.metadatas IS 'Contains metadata about a given translation.';

COMMENT ON COLUMN translations_private.metadatas.external_id IS 'Id that can be used by external entities to refer to this translation.';

COMMENT ON COLUMN translations_private.metadatas.owner_id IS 'Reference to the external owner of this translation.';

COMMENT ON COLUMN translations_private.metadatas.override_value IS 'Translation to use fo all languages, or null to use a language-specific string.';

ALTER TABLE translations_private.metadatas
    OWNER TO translations_owner;

CREATE OR REPLACE FUNCTION translations_private.metadatas_trigger_notify_translation_was_deleted() RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    CALL events_public.notify_entity_deleted(('dansdata.entity://dansdata.se/v1/translations/translation?id='
            || old.external_id)::TEXT);
    RETURN NULL;
END;
$$;

CREATE OR REPLACE TRIGGER metadatas_trigger_notify_translation_was_deleted
    AFTER DELETE
    ON translations_private.metadatas
    FOR EACH ROW
EXECUTE PROCEDURE translations_private.metadatas_trigger_notify_translation_was_deleted();
CREATE TABLE IF NOT EXISTS translations_private.languages (
    id INTEGER GENERATED ALWAYS AS IDENTITY
        CONSTRAINT languages_pk
            PRIMARY KEY,
    code TEXT NOT NULL
        CONSTRAINT languages_code_uniq
            UNIQUE,
    name_id INTEGER NOT NULL
        CONSTRAINT languages_name_id_metadatas_id_fk
            REFERENCES translations_private.metadatas
            ON UPDATE CASCADE
            ON DELETE CASCADE
);

COMMENT ON TABLE translations_private.languages IS 'A list of all languages known to the system.';

COMMENT ON COLUMN translations_private.languages.code IS 'ISO 639-1 Alpha-2 language code.';

COMMENT ON COLUMN translations_private.languages.name_id IS 'Reference to a translated string containing the name of this language.';

ALTER TABLE translations_private.languages
    OWNER TO translations_owner;
CREATE TABLE IF NOT EXISTS translations_private.values (
    metadata_id INTEGER NOT NULL
        CONSTRAINT values_metadata_id_metadatas_id_fk
            REFERENCES translations_private.metadatas
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    language_id INTEGER NOT NULL
        CONSTRAINT values_language_id_languages_id_fk
            REFERENCES translations_private.languages
            ON UPDATE CASCADE
            ON DELETE CASCADE,
    value TEXT NOT NULL,
    CONSTRAINT values_pk
        PRIMARY KEY (language_id, metadata_id)
);

COMMENT ON TABLE translations_private.values IS 'Contains the translated texts for translations_private.';

COMMENT ON COLUMN translations_private.values.language_id IS 'Reference to the language this text is written in.';

COMMENT ON COLUMN translations_private.values.value IS 'The translated text.';

ALTER TABLE translations_private.values
    OWNER TO translations_owner;
CREATE SCHEMA translations_public;

ALTER SCHEMA translations_public OWNER TO translations_owner;

GRANT USAGE ON SCHEMA translations_public TO PUBLIC;
CREATE OR REPLACE PROCEDURE translations_public.drop_ext_ref(entity_uri TEXT)
    SECURITY DEFINER
    LANGUAGE sql AS
$$
DELETE
    FROM
        translations_private.ext_refs
    WHERE
        uri = entity_uri;
$$;

COMMENT ON PROCEDURE translations_public.drop_ext_ref(entity_uri TEXT) IS 'Delete any references to an external entity and release all associated resources.';

ALTER PROCEDURE translations_public.drop_ext_ref(entity_uri TEXT) OWNER TO translations_owner;
CREATE OR REPLACE VIEW translations_public.translations(id, language_code, value) AS
    SELECT
        m.external_id AS id,
        l.code AS language_code,
        COALESCE(m.override_value, v.value, ''::TEXT) AS value
        FROM
            translations_private.metadatas m
                CROSS JOIN translations_private.languages l
                LEFT JOIN translations_private.values v
                    ON v.metadata_id = m.id AND v.language_id = l.id;

COMMENT ON VIEW translations_public.translations IS 'A view of all translated strings, with values for all languages.';

ALTER TABLE translations_public.translations
    OWNER TO translations_owner;

GRANT SELECT ON translations_public.translations TO PUBLIC;
CREATE OR REPLACE FUNCTION translations_public.allocate_translation(entity_uri TEXT) RETURNS uuid
    SECURITY DEFINER
    LANGUAGE sql AS
$$
INSERT INTO translations_private.metadatas(
    owner_id
)
SELECT
    translations_private.get_or_create_ext_ref(entity_uri)
    RETURNING external_id;
$$;

COMMENT ON FUNCTION translations_public.allocate_translation(entity_uri TEXT) IS 'Allocates a new translation id for use by the given owner.';

ALTER FUNCTION translations_public.allocate_translation(entity_uri TEXT) OWNER TO translations_owner;
CREATE OR REPLACE PROCEDURE translations_public.set_translation_override(
    translation_id uuid,
    value TEXT
)
    SECURITY DEFINER
    LANGUAGE sql AS
$$
UPDATE translations_private.metadatas
SET
    override_value = set_translation_override.value
    WHERE
        external_id = translation_id;


    -- Clean up any existing per-language definitions.
DELETE
    FROM
        translations_private.values
    WHERE
        metadata_id = (
                      SELECT
                          id
                          FROM
                              translations_private.metadatas m
                          WHERE
                              m.external_id = translation_id
                      );
$$;

COMMENT ON PROCEDURE translations_public.set_translation_override(translation_id UUID, value TEXT) IS 'Set the text to use as the translation for all languages.';

ALTER PROCEDURE translations_public.set_translation_override(translation_id UUID, "value" TEXT) OWNER TO translations_owner;

GRANT EXECUTE ON PROCEDURE translations_public.set_translation_override(translation_id UUID, "value" TEXT) TO edit_dance_info, moderate_dance_info;
CREATE OR REPLACE PROCEDURE translations_public.set_translation(
    translation_id uuid,
    language_code TEXT,
    value TEXT
)
    SECURITY DEFINER
    LANGUAGE sql AS
$$
INSERT INTO translations_private.values AS v
    (
        metadata_id,
        language_id,
        value
    )
SELECT
    m.id,
    l.id,
    CASE
        WHEN l.code = set_translation.language_code
            THEN set_translation.value
        ELSE t.value
    END
    FROM
        translations_private.metadatas m
            INNER JOIN translations_public.translations t
                ON m.external_id = t.id
            INNER JOIN translations_private.languages l
                ON l.code = t.language_code
    WHERE
        external_id = set_translation.translation_id
ON CONFLICT(metadata_id, language_id) DO UPDATE SET
    value = set_translation.value
    WHERE
        v.language_id = (
                        SELECT
                            id
                            FROM
                                translations_private.languages l
                            WHERE
                                l.code = set_translation.language_code
                        );

    -- Clean up any existing override translation.
UPDATE translations_private.metadatas
SET
    override_value = NULL
    WHERE
        external_id = translation_id;
$$;

COMMENT ON PROCEDURE translations_public.set_translation(translation_id UUID, language_code TEXT, value TEXT) IS 'Set the text to use as the translation for a single language.

If an override text exists for the translation, this text will be converted
into language-specific translations first.';

ALTER PROCEDURE translations_public.set_translation(translation_id UUID, language_code TEXT, "value" TEXT) OWNER TO translations_owner;

GRANT EXECUTE ON PROCEDURE translations_public.set_translation(translation_id UUID, language_code TEXT, "value" TEXT) TO edit_dance_info, moderate_dance_info;
CREATE OR REPLACE PROCEDURE translations_public.drop_translation(translation_id uuid)
    SECURITY DEFINER
    LANGUAGE sql AS
$$
DELETE
    FROM
        translations_private.metadatas
    WHERE
        external_id = translation_id;
$$;

COMMENT ON PROCEDURE translations_public.drop_translation(translation_id uuid) IS 'Delete the given translation.';

ALTER PROCEDURE translations_public.drop_translation(translation_id uuid) OWNER TO translations_owner;
--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0 (Debian 17.0-1.pgdg120+1)
-- Dumped by pg_dump version 17.0 (Debian 17.0-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: ext_refs; Type: TABLE DATA; Schema: translations_private; Owner: dansdata
--

INSERT INTO translations_private.ext_refs (id, uri) OVERRIDING SYSTEM VALUE VALUES (1, 'dansdata.entity://dansdata.se/v1/translations/language?code=sv');
INSERT INTO translations_private.ext_refs (id, uri) OVERRIDING SYSTEM VALUE VALUES (2, 'dansdata.entity://dansdata.se/v1/translations/language?code=en');
INSERT INTO translations_private.ext_refs (id, uri) OVERRIDING SYSTEM VALUE VALUES (3, 'dansdata.entity://dansdata.se/v1/translations/language?code=no');


--
-- Data for Name: metadatas; Type: TABLE DATA; Schema: translations_private; Owner: dansdata
--

INSERT INTO translations_private.metadatas (id, owner_id, override_value) OVERRIDING SYSTEM VALUE VALUES (1, 1, NULL);
INSERT INTO translations_private.metadatas (id, owner_id, override_value) OVERRIDING SYSTEM VALUE VALUES (2, 2, NULL);
INSERT INTO translations_private.metadatas (id, owner_id, override_value) OVERRIDING SYSTEM VALUE VALUES (3, 3, NULL);


--
-- Data for Name: languages; Type: TABLE DATA; Schema: translations_private; Owner: dansdata
--

INSERT INTO translations_private.languages (id, code, name_id) OVERRIDING SYSTEM VALUE VALUES (1, 'sv', 1);
INSERT INTO translations_private.languages (id, code, name_id) OVERRIDING SYSTEM VALUE VALUES (2, 'en', 2);
INSERT INTO translations_private.languages (id, code, name_id) OVERRIDING SYSTEM VALUE VALUES (3, 'no', 3);


--
-- Data for Name: values; Type: TABLE DATA; Schema: translations_private; Owner: dansdata
--

INSERT INTO translations_private."values" (metadata_id, language_id, value) VALUES (1, 1, 'Svenska');
INSERT INTO translations_private."values" (metadata_id, language_id, value) VALUES (1, 2, 'Swedish');
INSERT INTO translations_private."values" (metadata_id, language_id, value) VALUES (1, 3, 'Svensk');
INSERT INTO translations_private."values" (metadata_id, language_id, value) VALUES (2, 1, 'Engelska');
INSERT INTO translations_private."values" (metadata_id, language_id, value) VALUES (2, 2, 'English');
INSERT INTO translations_private."values" (metadata_id, language_id, value) VALUES (2, 3, 'Engelsk');
INSERT INTO translations_private."values" (metadata_id, language_id, value) VALUES (3, 1, 'Norska');
INSERT INTO translations_private."values" (metadata_id, language_id, value) VALUES (3, 2, 'Norwegian');
INSERT INTO translations_private."values" (metadata_id, language_id, value) VALUES (3, 3, 'Norsk');


--
-- Name: ext_refs_id_seq; Type: SEQUENCE SET; Schema: translations_private; Owner: dansdata
--

SELECT pg_catalog.setval('translations_private.ext_refs_id_seq', 3, true);


--
-- Name: languages_id_seq; Type: SEQUENCE SET; Schema: translations_private; Owner: dansdata
--

SELECT pg_catalog.setval('translations_private.languages_id_seq', 3, true);


--
-- Name: metadatas_id_seq; Type: SEQUENCE SET; Schema: translations_private; Owner: dansdata
--

SELECT pg_catalog.setval('translations_private.metadatas_id_seq', 3, true);


--
-- PostgreSQL database dump complete
--
