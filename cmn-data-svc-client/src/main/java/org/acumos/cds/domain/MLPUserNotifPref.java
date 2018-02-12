package org.acumos.cds.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "C_NOTIF_USER_PREF")
public class MLPUserNotifPref implements MLPEntity, Serializable {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2662643459576197438L;

	// Hibernate is weak on the ID column generator, the method is specific to
	// the backing database. For portability, specify AUTO and define the column
	// appropriately in the database, which in MySQL requires "AUTO_INCREMENT".
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID", updatable = false, nullable = false, columnDefinition = "INT")
	private Long userNotifPrefId;
	
	
	@Column(name = "USER_ID", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
	@Size(max = 36)
	private String userId;
	
	@Column(name = "NOTIF_DELV_MECH_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Notificxation Delivery Mechanism Code cannot be null")
	@Size(max = 2)
	private String notfDelvMechCode;
	
	@Column(name = "MSG_SEVERITY_CD", nullable = false, columnDefinition = "CHAR(2)")
	@NotNull(message = "Message Severity Code cannot be null")
	@Size(max = 2)
	private String msgSeverityCode;

	/**
	 * No-arg constructor.
	 */
	public MLPUserNotifPref() {
		// no-arg constructor
	}
	
	/**
	 * This constructor accepts the required fields; i.e., the minimum that the user
	 * must supply to create a valid instance.
	 * 
	 * @param userId
	 *            notification recipient
	 * @param notfDelvMechCode
	 *            user preferred notification delivery mechanism
	 * @param msgSeverityCode
	 *            message severity code 
	 */
	
	public MLPUserNotifPref(String userId, String notfDelvMechCode, String msgSeverityCode) {
		if (userId == null || notfDelvMechCode == null || msgSeverityCode == null)
			throw new IllegalArgumentException("Null not permitted");
		this.userId = userId;
		this.notfDelvMechCode = notfDelvMechCode;
		this.msgSeverityCode = msgSeverityCode;
	}

	public Long getUserNotifPrefId() {
		return userNotifPrefId;
	}

	public void setUserNotifPrefId(Long userNotifPrefId) {
		this.userNotifPrefId = userNotifPrefId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNotfDelvMechCode() {
		return notfDelvMechCode;
	}

	public void setNotfDelvMechCode(String notfDelvMechCode) {
		this.notfDelvMechCode = notfDelvMechCode;
	}

	public String getMsgSeverityCode() {
		return msgSeverityCode;
	}

	public void setMsgSeverityCode(String msgSeverityCode) {
		this.msgSeverityCode = msgSeverityCode;
	}
	
	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof MLPUserNotifPref))
			return false;
		MLPUserNotifPref thatObj = (MLPUserNotifPref) that;
		return Objects.equals(userNotifPrefId, thatObj.userNotifPrefId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userNotifPrefId);
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[userNotifPrefId=" + userNotifPrefId + ", userId=" + userId + ", notfDelvMechCode=" + notfDelvMechCode + ", msgSeverityCode=" + msgSeverityCode
				+ "]";
	}


}
