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

package org.acumos.cds.util;

/**
 * Copied from com.att.eelf.configuration.Configuration
 * 
 * TODO: switch to ONAP library when they finally release one.
 */
public interface LoggingConstants {

	/**
	 * The requestID is primarily used to track the processing of a request
	 * referencing a single instance of a service through the various
	 * sub-components.
	 */
	public String MDC_KEY_REQUEST_ID = "RequestId";

	/**
	 * The serviceInstanceID can be used to uniquely identity a service instance.
	 */
	public String MDC_SERVICE_INSTANCE_ID = "ServiceInstanceId";

	/**
	 * The serviceName can be used identify the name of the service.
	 */
	public String MDC_SERVICE_NAME = "ServiceName";

	/**
	 * The instanceUUID can be used to differentiate between multiple instances of
	 * the same (named), log writing service/application
	 */
	public String MDC_INSTANCE_UUID = "InstanceUUID";

	/**
	 * The serverIPAddress can be used to log the host server's IP address. (e.g.
	 * Jetty container's listening IP address)
	 */
	public String MDC_SERVER_IP_ADDRESS = "ServerIPAddress";

	/**
	 * The serverFQDN can be used to log the host server's FQDN.
	 */
	public String MDC_SERVER_FQDN = "ServerFQDN";

	/**
	 * The remote host name/ip address making the request
	 */
	public static final String MDC_REMOTE_HOST = "RemoteHost";

	/**
	 * The severity can be used to map to severity in alert messages eg. nagios
	 * alerts
	 */
	public String MDC_ALERT_SEVERITY = "AlertSeverity";

	/**
	 * Date-time of the start of a request activity
	 */
	public String MDC_BEGIN_TIMESTAMP = "BeginTimestamp";

	/**
	 * Date-time of the end of a request activity
	 */
	public String MDC_END_TIMESTAMP = "EndTimestamp";

	/**
	 * Client or user invoking the API
	 */
	public String MDC_PARTNER_NAME = "PartnerName";

	/**
	 * High level success or failure of the request (COMPLETE or ERROR)
	 */
	public String MDC_STATUS_CODE = "StatusCode";

	/**
	 * Application specific response code
	 */
	public String MDC_RESPONSE_CODE = "ResponseCode";

	/**
	 * Human readable description of the application specific response code
	 */
	public String MDC_RESPONSE_DESC = "ResponseDescription";

	/**
	 * Elapsed time to complete processing of an API or request at the granularity
	 * available to the component system. This value should be the difference
	 * between BeginTimestamp and EndTimestamp fields
	 */
	public String MDC_ELAPSED_TIME = "ElapsedTime";

	/**
	 * Optional field
	 */
	public String MDC_PROCESS_KEY = "ProcessKey";

	/**
	 * component, subcomponent or entity which is invoked for this suboperation
	 */
	public String MDC_TARGET_ENTITY = "TargetEntity";

	/**
	 * External API/operation activities invoked on TargetEntity
	 * component/subcomponent or other entity)
	 */
	public String MDC_TARGET_SERVICE_NAME = "TargetServiceName";

	/**
	 * Target VNF or VM being acted upon by the component
	 */
	public String MDC_TARGET_VIRTUAL_ENTITY = "TargetVirtualEntity";

}
