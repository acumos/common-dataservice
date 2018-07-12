package org.acumos.cds.transport;

import java.util.Objects;

/**
 * Trivial model to transport a pair of strings.
 */
public class AuthorTransport {
	private String name;
	private String contact;

	/**
	 * Builds an empty object.
	 */
	public AuthorTransport() {
		// no-arg constructor
	}

	/**
	 * Builds an object with the specified values.
	 * 
	 * @param name
	 *            name to transport.
	 * @param contact
	 *            contact to transport.
	 */
	public AuthorTransport(String name, String contact) {
		this.name = name;
		this.contact = contact;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof AuthorTransport))
			return false;
		AuthorTransport thatObj = (AuthorTransport) that;
		return Objects.equals(name, thatObj.name) && Objects.equals(contact, thatObj.contact);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, contact);
	}

}
