package org.acumos.cms.domain;

/**
 * <pre>
 * {
 * "status":null,
 * "status_code":0,
 * "response_detail":"Solutions fetched Successfully",
 * "response_code":null,
 * "response_body":["NJBedminsterOneATTWaySiteMap.ppt"],
 * "error_code":"100"
 * }
 * </pre>
 */
public abstract class AbstractCMSResponse {

	private String status;
	private Integer status_code;
	private String response_detail;
	// Actually a number?
	private String response_code;
	// Why is this number in quotes?
	private String error_code;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getStatus_code() {
		return status_code;
	}

	public void setStatus_code(Integer status_code) {
		this.status_code = status_code;
	}

	public String getResponse_detail() {
		return response_detail;
	}

	public void setResponse_detail(String response_detail) {
		this.response_detail = response_detail;
	}

	public String getResponse_code() {
		return response_code;
	}

	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}

	public String getError_code() {
		return error_code;
	}

	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
}
