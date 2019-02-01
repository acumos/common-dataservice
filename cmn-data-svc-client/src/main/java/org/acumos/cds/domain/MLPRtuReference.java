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

package org.acumos.cds.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * A right-to-use reference ID is a GUID.
 * 
 * This participates in a many-many relationship with RTU via a join table.
 */
@Entity
@Table(name = "C_RTU_REF")
public class MLPRtuReference implements MLPDomainModel, Serializable {

	private static final long serialVersionUID = 8135796672173674022L;

	// A GUID is always 36 char
	@Id
	@Column(name = "REF", nullable = false, updatable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	@ApiModelProperty(required = true, example = "GUID")
	private String ref;

	/**
	 * No-arg constructor
	 */
	public MLPRtuReference() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields.
	 * 
	 * @param ref
	 *                The reference ID
	 */
	public MLPRtuReference(String ref) {
		if (ref == null || ref.length() == 0)
			throw new IllegalArgumentException("Null/empty not permitted");
		this.ref = ref;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *                 Instance to copy
	 */
	public MLPRtuReference(MLPRtuReference that) {
		this.ref = that.ref;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPRtuReference))
			return false;
		MLPRtuReference thatObj = (MLPRtuReference) that;
		return Objects.equals(ref, thatObj.ref);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ref);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[ref=" + ref + "]";
	}

}
