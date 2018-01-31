-- Script to downgrade database used by the Common Data Service
-- FROM version 1.14.x TO version 1.13.x.
-- No database is specified to allow flexible deployment!

-- 6
DROP TABLE C_PEER_PEER_ACC_MAP;
-- 5
DROP TABLE C_PEER_SOL_ACC_MAP;
-- 4
DROP TABLE C_SOL_GRP_MEM_MAP;
-- 3
DROP TABLE C_PEER_GRP_MEM_MAP;
-- 2
DROP TABLE C_SOLUTION_GROUP;
-- 1
DROP TABLE C_PEER_GROUP;
