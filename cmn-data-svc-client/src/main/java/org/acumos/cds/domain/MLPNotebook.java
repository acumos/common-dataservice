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
 * A notebook is created and managed by the ML Workbench.
 */
@Entity
@Table(name = "C_NOTEBOOK")
public class MLPNotebook extends MLPAbstractWorkbenchArtifact implements Serializable {

	private static final long serialVersionUID = -1753710763840921788L;

	@Id
	@GeneratedValue(generator = "customUseOrGenerate")
	@GenericGenerator(name = "customUseOrGenerate", strategy = "org.acumos.cds.util.UseExistingOrNewUUIDGenerator")
	@Column(name = "NOTEBOOK_ID", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	// Users MAY submit an ID; readOnly annotation must NOT be used
	@ApiModelProperty(value = "UUID; omit for system-generated value", example = "12345678-abcd-90ab-cdef-1234567890ab")
	private String notebookId;

	@Column(name = "NOTEBOOK_TYPE_CD", columnDefinition = "CHAR(2)")
	@Size(max = 2)
	@ApiModelProperty(value = "Two-character notebook type code", example = "AB")
	private String notebookTypeCode;

	@Column(name = "KERNEL_TYPE_CD", columnDefinition = "CHAR(2)")
	@Size(max = 2)
	@ApiModelProperty(value = "Two-character kernel type code", example = "AB")
	private String kernelTypeCode;

	/**
	 * No-arg constructor.
	 */
	public MLPNotebook() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance. Defaults active to true. Omits ID,
	 * which is generated on save.
	 * 
	 * @param name
	 *                   Name
	 * @param userId
	 *                   ID of valid user
	 * @throws IllegalArgumentException
	 *                                      if any argument is null
	 */
	public MLPNotebook(String name, String userId) {
		super(name, true, userId);
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPNotebook(MLPNotebook that) {
		super(that);
		this.kernelTypeCode = that.kernelTypeCode;
		this.notebookId = that.notebookId;
		this.notebookTypeCode = that.notebookTypeCode;
	}

	public String getNotebookId() {
		return notebookId;
	}

	public void setNotebookId(String notebookId) {
		this.notebookId = notebookId;
	}

	public String getNotebookTypeCode() {
		return notebookTypeCode;
	}

	public void setNotebookTypeCode(String notebookTypeCode) {
		this.notebookTypeCode = notebookTypeCode;
	}

	public String getKernelTypeCode() {
		return kernelTypeCode;
	}

	public void setKernelTypeCode(String kernelTypeCode) {
		this.kernelTypeCode = kernelTypeCode;
	}

}
