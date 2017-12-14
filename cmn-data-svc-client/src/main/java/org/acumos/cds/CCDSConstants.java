/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.cds;

/**
 * Publishes constants for the CCDS server and client.
 */
public abstract class CCDSConstants {

	// Seems like this should available somewhere
	public static final String APPLICATION_JSON = "application/json";

	public static final String JUNCTION_QUERY_PARAM = "j";

	// Metadata
	public static final String HEALTHCHECK_PATH = "healthcheck";
	public static final String VERSION_PATH = "version";

	// Entities
	public static final String ARTIFACT_PATH = "artifact";
	public static final String SOLUTION_PATH = "solution";
	public static final String REVISION_PATH = "revision";
	public static final String ROLE_PATH = "role";
	public static final String FUNCTION_PATH = "function";
	public static final String USER_PATH = "user";
	public static final String COUNT_PATH = "count";
	public static final String MODEL_PATH = "model";
	public static final String TYPE_PATH = "type";
	public static final String ACCESS_PATH = "access";
	public static final String TOOLKIT_PATH = "toolkit";
	public static final String VALIDATION_PATH = "validation";
	public static final String VAL_STAT_PATH = "valstat";
	public static final String VAL_TYPE_PATH = "valtype";
	public static final String DEP_STAT_PATH = "depstat";
	public static final String VAL_SEQ_PATH = "valseq";
	public static final String PEER_PATH = "peer";
	public static final String DOWNLOAD_PATH = "dnld";
	public static final String FAVORITE_PATH = "favorite";
	public static final String RATING_PATH = "rating";
	public static final String SUBSCRIPTION_PATH = "sub";
	public static final String LOGIN_PROVIDER_PATH = "logprov";
	public static final String TAG_PATH = "tag";
	public static final String NOTIFICATION_PATH = "notif";
	public static final String VIEW_PATH = "view";
	public static final String WEB_PATH = "web";
	public static final String DEPLOYMENT_PATH = "deploy";
	public static final String CONFIG_PATH = "config";
	public static final String THREAD_PATH = "thread";
	public static final String COMMENT_PATH = "comment";

	// Actions on entities
	public static final String LIKE_PATH = "like";
	public static final String SEARCH_PATH = "search";
	public static final String PORTAL_PATH = "portal";
	public static final String TERM_PATH = "term";
	public static final String LOGIN_PATH = "login";
	public static final String AVERAGE_PATH = "avg";
	public static final String CHPASS_PATH = "chgpw";

	// Portal search interface
	public static final String SEARCH_ACCESS_TYPES = "atc";
	public static final String SEARCH_ACTIVE = "active";
	public static final String SEARCH_AUTHOR = "author";
	public static final String SEARCH_DESC = "desc";
	public static final String SEARCH_MODEL_TYPES = "mtc";
	public static final String SEARCH_NAME = "name";
	public static final String SEARCH_TAGS = "tags";
	public static final String SEARCH_VAL_STATUSES = "vsc";

	// Sonar wants a private constructor, placed after all fields
	private CCDSConstants() {
	}

}
