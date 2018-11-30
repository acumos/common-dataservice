-- ===============LICENSE_START=======================================================
-- Acumos Apache-2.0
-- ===================================================================================
-- Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
-- ===================================================================================
-- This Acumos software file is distributed by AT&T and Tech Mahindra
-- under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--      http://www.apache.org/licenses/LICENSE-2.0
--
-- This file is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- ===============LICENSE_END=========================================================

-- Script to upgrade database used by the Common Data Service
-- FROM version 1.18.x TO version 2.0.x.
-- No database name is set to allow flexible deployment.

DROP TABLE C_SOLUTION_VALIDATION;
DROP TABLE C_SOL_VAL_SEQ;
ALTER TABLE C_PEER DROP COLUMN VALIDATION_STATUS_CD;
ALTER TABLE C_SOLUTION_REV DROP COLUMN VALIDATION_STATUS_CD;
ALTER TABLE C_USER MODIFY COLUMN LOGIN_PASS_EXPIRE_DATE TIMESTAMP;
ALTER TABLE C_USER MODIFY COLUMN LAST_LOGIN_DATE TIMESTAMP;
ALTER TABLE C_USER MODIFY COLUMN LOGIN_FAIL_DATE TIMESTAMP;
ALTER TABLE C_USER MODIFY COLUMN VERIFY_EXPIRE_DATE TIMESTAMP;
ALTER TABLE C_NOTIFICATION MODIFY COLUMN START_DATE TIMESTAMP;
ALTER TABLE C_NOTIFICATION MODIFY COLUMN END_DATE TIMESTAMP;
ALTER TABLE C_NOTIF_USER_MAP MODIFY COLUMN VIEWED_DATE TIMESTAMP;
