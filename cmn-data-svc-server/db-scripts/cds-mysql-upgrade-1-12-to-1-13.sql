-- Script to upgrade database used by the Common Data Service
-- FROM version 1.12.x TO version 1.13.x.
-- No database is created or specified to allow flexible deployment!

-- 1 Add rows
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TC', 'Training Client')
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('BR', 'Data Broker')
