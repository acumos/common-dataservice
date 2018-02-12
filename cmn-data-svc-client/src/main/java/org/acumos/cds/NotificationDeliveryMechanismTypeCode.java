package org.acumos.cds;

/**
 * This enum provides MLP notification delivery mechanism type codes for developer convenience. It
 * replicates the values maintained in a database table modeled by the class
 * {@link org.acumos.cds.domain.MLPNotificationDeliveryMechanismType}.
 */

public enum NotificationDeliveryMechanismTypeCode {
	EM("Email"), //
	TX("Text"); //
	

	private String typeName;

	private NotificationDeliveryMechanismTypeCode(final String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

}
