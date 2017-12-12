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

/**
 * Tags have only a name.
 * 
 * This participates in a many-many relationship with solution via a join table.
 * The relationship is mapped via annotations in the Solution; not mapped here.
 */
@Entity
@Table(name = "C_SOLUTION_TAG")
public class MLPTag implements MLPEntity, Serializable {

	private static final long serialVersionUID = -8342728048884890037L;

	@Id
	@Column(name = "TAG", updatable = false, nullable = false, columnDefinition = "VARCHAR(32)")
	@Size(max = 32)
	private String tag;
	    
	/**
	 * No-arg constructor
	 */
	public MLPTag() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param tag
	 *            The tag
	 */
	public MLPTag(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPTag))
			return false;
		MLPTag thatObj = (MLPTag) that;
		return Objects.equals(tag, thatObj.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tag);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[tag=" + tag + "]";
	}

}
