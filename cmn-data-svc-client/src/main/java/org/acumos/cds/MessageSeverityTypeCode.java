package org.acumos.cds;

/**
 * This enum defines Acumos message severity type codes.
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
