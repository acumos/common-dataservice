-- Script to upgrade database used by the Common Data Service
-- FROM version 1.14.x TO version 1.15.x.
-- No database is specified to allow flexible deployment!

-- 1
ALTER TABLE C_SOLUTION_REV
  ADD COLUMN ACCESS_TYPE_CD CHAR(2) NOT NULL DEFAULT 'PR';
-- 2
-- UPDATE STATEMENT TODO
-- 3
ALTER TABLE C_SOLUTION
  DROP COLUMN ACCESS_TYPE_CD;
