package org.acumos.cds.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;


@Entity
@Table(name = "C_STEP_STATUS")
public class MLPStepStatus implements MLPEntity, Serializable {

	private static final long serialVersionUID = -8342728048884890038L;

	@Id
	@Column(name = "STATUS_CD", updatable = false, nullable = false, columnDefinition = "CHAR(2)")
	@Size(max = 2)
	private String statusCode;

	@Column(name = "STATUS_NAME", columnDefinition = "VARCHAR(100)")
	@Size(max = 100)
	private String statusName;



	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPStepStatus))
			return false;
		MLPStepStatus thatObj = (MLPStepStatus) that;
		return Objects.equals(statusCode, thatObj.statusCode) && Objects.equals(statusName, thatObj.statusName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(statusCode, statusName);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[code=" + statusCode + ", name=" + statusName + "]";
	}

}
	