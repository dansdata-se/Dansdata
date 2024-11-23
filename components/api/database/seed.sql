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
