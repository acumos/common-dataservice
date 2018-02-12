package org.acumos.cds.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Model for an message severity type type, a code-name pair.
 */
@Entity
@Table(name = "C_MESSAGE_SEVERITY_TYPE")
public class MLPMessageSeverityType extends MLPTypeCodeEntity implements Serializable{

	private static final long serialVersionUID = 5553825913000333176L;
	
}
