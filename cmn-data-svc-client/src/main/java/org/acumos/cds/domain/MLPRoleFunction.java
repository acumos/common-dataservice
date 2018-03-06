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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

/**
 * Model for a role function, which defines an action performed by a user.
 * 
 * Participates in many-one relationship with role.
 */
@Entity
@Table(name = "C_ROLE_FUNCTION")
public class MLPRoleFunction extends MLPTimestampedEntity implements Serializable {

	private static final long serialVersionUID = 8102107534266847669L;

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "ROLE_FUNCTION_ID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
	// Generated by DB; NotNull annotation not needed
	private String roleFunctionId;

	@Column(name = "ROLE_ID", nullable = false, columnDefinition = "CHAR(36)")
	@NotNull(message = "Parent role ID cannot be null")
	@Size(max = 36)
	private String roleId;

	@Column(name = "NAME", nullable = false, columnDefinition = "VARCHAR(100)")
	@NotNull(message = "Name cannot be null")
	@Size(max = 100)
	private String name;

	/**
	 * No-arg constructor
	 */
	public MLPRoleFunction() {
		// no-arg constructor
	}

	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param roleId
	 *            Role ID
	 * @param name
	 *            Role function name
	 */
	public MLPRoleFunction(String roleId, String name) {
		if (roleId == null || name == null)
			throw new IllegalArgumentException("Null not permitted");
		this.roleId = roleId;
		this.name = name;
	}

	/**
	 * Copy constructor
	 * 
	 * @param that
	 *            Instance to copy
	 */
	public MLPRoleFunction(MLPRoleFunction that) {
		super(that);
		this.name = that.name;
		this.roleFunctionId = that.roleFunctionId;
		this.roleId = that.roleId;
	}

	public String getRoleFunctionId() {
		return roleFunctionId;
	}

	public void setRoleFunctionId(String roleFunctionId) {
		this.roleFunctionId = roleFunctionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPRoleFunction))
			return false;
		MLPRoleFunction thatObj = (MLPRoleFunction) that;
		return Objects.equals(roleFunctionId, thatObj.roleFunctionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roleFunctionId, roleId, name);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[roleFunctionId=" + roleFunctionId + ", name=" + name + ", created="
				+ getCreated() + ", modified=" + getModified() + "]";
	}

}
