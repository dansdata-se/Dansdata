-------------------------------------------------
-- System roles
-------------------------------------------------
-- This file defines the roles used as basic
-- building blocks for RBAC.
--
-- General API access is typically assigned to
-- the PUBLIC role.

CREATE ROLE event_emitter;

CREATE ROLE translations_owner;
GRANT event_emitter TO translations_owner;

-------------------------------------------------
-- dance_information
-------------------------------------------------
-- Roles related to the dance information in the
-- system.
-------------------------------------------------
-- Editors are able to create, update and delete
-- events, provided they are associated with a
-- profile which is itself associated with the
-- relevant event.
CREATE ROLE edit_dance_info;

-- Moderators are trusted users who are able to
-- manage all events, including data which is
-- otherwise inaccessible to editors and
-- metadata.
CREATE ROLE moderate_dance_info;
GRANT edit_dance_info TO moderate_dance_info;
