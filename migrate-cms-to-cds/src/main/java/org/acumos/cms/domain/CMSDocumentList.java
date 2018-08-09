package org.acumos.cms.domain;

import java.util.List;

public class CMSDocumentList extends AbstractCMSResponse {

	public List<String> response_body;

	public List<String> getResponse_body() {
		return response_body;
	}

	public void setResponse_body(List<String> response_body) {
		this.response_body = response_body;
	}
	
	@Override
	public String toString() {
		return response_body.toString();
	}
}
