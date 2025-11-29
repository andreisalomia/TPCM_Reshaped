-- TPCM app user
CREATE USER tpcm_app IDENTIFIED BY tpcm_pass
DEFAULT TABLESPACE tpcm_app_data
TEMPORARY TABLESPACE temp
QUOTA UNLIMITED ON tpcm_app_data;

GRANT CONNECT, RESOURCE, DBA TO tpcm_app;

-- Clients db user
CREATE USER tpcm_db IDENTIFIED BY tpcm_pass
DEFAULT TABLESPACE tpcm_clients_data
TEMPORARY TABLESPACE temp
QUOTA UNLIMITED ON tpcm_clients_data;

GRANT CONNECT, RESOURCE, DBA TO tpcm_db;

-- Voice Simulator user
CREATE USER tpcm_voice IDENTIFIED BY tpcm_pass
DEFAULT TABLESPACE tpcm_voice_data
TEMPORARY TABLESPACE temp
QUOTA UNLIMITED ON tpcm_voice_data;

GRANT CONNECT, RESOURCE, DBA TO tpcm_voice;