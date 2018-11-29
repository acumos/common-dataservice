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

CREATE TABLE C_SITE_CONTENT (
  CONTENT_KEY VARCHAR(50) NOT NULL PRIMARY KEY,
  CONTENT_VAL LONGBLOB NOT NULL,
  MIME_TYPE VARCHAR(50) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL DEFAULT 0,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;