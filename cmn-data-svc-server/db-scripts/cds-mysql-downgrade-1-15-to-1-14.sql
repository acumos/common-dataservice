-- Script to downgrade database used by the Common Data Service
-- FROM version 1.15.x TO version 1.14.x.
-- No database is specified to allow flexible deployment!

-- 3
ALTER TABLE C_SOLUTION
  ADD COLUMN ACCESS_TYPE_CD CHAR(2) NULL DEFAULT 'PR';
-- 2
-- UPDATE STATEMENT TODO
-- 1
ALTER TABLE C_SOLUTION_REV
  DROP COLUMN ACCESS_TYPE_CD;
