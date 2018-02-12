package org.acumos.cds;

/**
 * This enum defines Acumos notification delivery mechanism type codes.
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
