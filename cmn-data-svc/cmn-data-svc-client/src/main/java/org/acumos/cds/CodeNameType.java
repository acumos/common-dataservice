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
 * Provides a constant for every code-name value set as configured by property
 * files.
 */
public enum CodeNameType {

	// Why does the Eclipse formatter want these all on a single line?
	ACCESS_TYPE, ARTIFACT_TYPE, DEPLOYMENT_STATUS, KERNEL_TYPE, LOGIN_PROVIDER, //
	MESSAGE_SEVERITY, MODEL_TYPE, NOTEBOOK_TYPE, NOTIFICATION_DELIVERY_MECHANISM, //
	PEER_STATUS, PUBLISH_REQUEST_STATUS, SERVICE_STATUS, TASK_STEP_STATUS, //
	TASK_TYPE, TOOLKIT_TYPE, VERIFIED_LICENSE, VERIFIED_VULNERABILITY;

}
