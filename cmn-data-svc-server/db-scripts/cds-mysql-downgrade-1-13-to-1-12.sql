-- Script to downgrade database used by the Common Data Service
-- FROM version 1.13.x TO version 1.12.x.
-- No database is created or specified to allow flexible deployment!

-- Undo: 1 Add rows
DELETE FROM C_TOOLKIT_TYPE WHERE TYPE_CD = 'TC';
DELETE FROM C_TOOLKIT_TYPE WHERE TYPE_CD = 'BR';
