/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
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

package org.acumos.cds.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import io.swagger.annotations.ApiModelProperty;

/**
 * A pipeline is created and managed by the ML Workbench.
 */
@Entity
@Table(name = "C_PIPELINE")
public class MLPPipeline extends MLPAbstractWorkbenchArtifact implements Serializable {

	private static final long serialVersionUID = -6741059656292398954L;

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "PIPELINE_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	// Users MAY submit an ID; readOnly annotation must NOT be used
	@ApiModelProperty(value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String pipelineId;

	/**
	 * No-arg constructor.
	 */
	public MLPPipeline() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Omits ID, which is generated on save.
	 * 
	 * @param name
	 *                   Name
	 * @param active
	 *                   True or false
	 * @param userId
	 *                   ID of valid user
	 * @throws IllegalArgumentException
	 *                                      if any argument is null
	 */
	public MLPPipeline(String name, boolean active, String userId) {
		super(name, active, userId);
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPPipeline(MLPPipeline that) {
		super(that);
		this.pipelineId = that.pipelineId;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}

}
