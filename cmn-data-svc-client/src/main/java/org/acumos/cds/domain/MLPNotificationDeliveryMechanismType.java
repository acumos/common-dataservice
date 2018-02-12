package org.acumos.cds.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Model for an message severity type type, a code-name pair.
 */
@Entity
@Table(name = "NOTIF_DELV_MECH_TYPE")
public class MLPNotificationDeliveryMechanismType extends MLPTypeCodeEntity implements Serializable{
	
	private static final long serialVersionUID = -8062583843763021240L;
}
