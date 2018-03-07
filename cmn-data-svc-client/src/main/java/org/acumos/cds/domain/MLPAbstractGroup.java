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

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

/**
 * Abstract model for a group to support access control. The group definition is
 * an ID-name pair. The ID is used in another table to map entities to groups.
 * Factored out to avoid code repetitions and Sonar complaints.
 * 
 * Spring has a bit of magic for everything, must use @MappedSuperclass here.
 */
@MappedSuperclass
public abstract class MLPAbstractGroup extends MLPTimestampedEntity {

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "GROUP_ID", updatable = false, nullable = false, columnDefinition = "INT")
	private Long groupId;

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(50)")
	@Size(max = 50)
	private String name;

	@Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(512)")
	@Size(max = 512)
	private String description;

	public MLPAbstractGroup() {
		// no-arg constructor
	}

	public MLPAbstractGroup(String name) {
		if (name == null)
			throw new IllegalArgumentException("Null not permitted");
		this.name = name;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPAbstractGroup(MLPAbstractGroup that) {
		super(that);
		this.description = that.description;
		this.groupId = that.groupId;
		this.name = that.name;
	}

	/**
	 * @return the groupId
	 */
	public Long getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPAbstractGroup))
			return false;
		MLPAbstractGroup thatObj = (MLPAbstractGroup) that;
		return Objects.equals(groupId, thatObj.groupId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupId, name);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[groupId=" + groupId + ", name=" + name + "]";
	}
}
