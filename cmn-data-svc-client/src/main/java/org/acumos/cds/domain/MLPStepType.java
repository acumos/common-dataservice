package org.acumos.cds.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "C_STEP_TYPE")
public class MLPStepType implements MLPEntity, Serializable {

	private static final long serialVersionUID = -8342728048884890037L;

	@Id
	@Column(name = "STEP_CD", updatable = false, nullable = false, columnDefinition = "CHAR(2)")
	@Size(max = 2)
	private String stepCode;

	@Column(name = "STEP_NAME", columnDefinition = "VARCHAR(100)")
	@Size(max = 100)
	private String stepName;

	public String getStepCode() {
		return stepCode;
	}

	public void setStepCode(String stepCode) {
		this.stepCode = stepCode;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPStepType))
			return false;
		MLPStepType thatObj = (MLPStepType) that;
		return Objects.equals(stepCode, thatObj.stepCode) && Objects.equals(stepName, thatObj.stepName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(stepCode, stepName);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[code=" + stepCode + ", name=" + stepName + "]";
	}

}
