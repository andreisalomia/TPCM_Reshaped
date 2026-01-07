-- TPCM tablespace
CREATE TABLESPACE tpcm_app_data
DATAFILE '/opt/oracle/oradata/FREE/FREEPDB1/tpcm_app_data01.dbf'
SIZE 100M
AUTOEXTEND ON
NEXT 10M
MAXSIZE UNLIMITED;

-- Clients tablespace
CREATE TABLESPACE tpcm_clients_data
DATAFILE '/opt/oracle/oradata/FREE/FREEPDB1/tpcm_clients_data01.dbf'
SIZE 100M
AUTOEXTEND ON
NEXT 10M
MAXSIZE UNLIMITED;

-- Voice Simulator tablespace
CREATE TABLESPACE tpcm_voice_data
DATAFILE '/opt/oracle/oradata/FREE/FREEPDB1/tpcm_voice_data01.dbf'
SIZE 100M
AUTOEXTEND ON
NEXT 10M
MAXSIZE UNLIMITED;