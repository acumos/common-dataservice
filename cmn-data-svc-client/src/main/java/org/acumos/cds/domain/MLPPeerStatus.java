package org.acumos.cds.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "C_PEER_STATUS")
public class MLPPeerStatus extends MLPStatusCodeEntity implements Serializable {

	private static final long serialVersionUID = 5341647455351536720L;

}
