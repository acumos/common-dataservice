-- Script to upgrade database used by the Common Data Service
-- FROM version 1.11.x TO version 1.12.x.
-- No database is created or specified to allow flexible deployment!

-- 1 add the column
ALTER TABLE C_PEER_SUB ADD COLUMN USER_ID CHAR(36) NOT NULL;
-- 2 add the constraint
ALTER TABLE C_PEER_SUB ADD CONSTRAINT C_PEER_SUB_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID);
-- 3 add the column
ALTER TABLE C_SOLUTION ADD COLUMN PEER_ID CHAR(36) NULL;
-- 4 add the constraint
ALTER TABLE C_SOLUTION ADD CONSTRAINT C_SOLUTION_C_PEER FOREIGN KEY (PEER_ID) REFERENCES C_PEER (PEER_ID);
