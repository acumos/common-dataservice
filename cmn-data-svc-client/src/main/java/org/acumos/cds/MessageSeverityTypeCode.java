package org.acumos.cds;

/**
 * This enum provides MLP message severity type codes for developer convenience. It
 * replicates the values maintained in a database table modeled by the class
 * {@link org.acumos.cds.domain.MLPMessageSeverityType}.
 */


public enum MessageSeverityTypeCode {

	HI("High"), //
	ME("Medium"), //
	LO("Low");

	private String typeName;

	private MessageSeverityTypeCode(final String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}


}
