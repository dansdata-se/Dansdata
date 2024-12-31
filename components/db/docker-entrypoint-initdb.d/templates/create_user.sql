DROP USER IF EXISTS :"USERNAME";

CREATE USER :"USERNAME" WITH PASSWORD :'PASSWORD';

ALTER ROLE :"USERNAME" SET search_path = "$user", public, extensions, dance_api_public;
