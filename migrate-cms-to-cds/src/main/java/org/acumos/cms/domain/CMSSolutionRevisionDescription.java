package org.acumos.cms.domain;

/**
 * <pre>
 * {
 * "description":" Test",
 * "solutionId":"e85f4c75-439f-4e4f-8362-6d75187f198f",
 * "revisionId":"aae12a0c-ee4d-4494-b59d-493a0cc794ca"
 * }
 * </pre>
 */
public class CMSSolutionRevisionDescription {

	private String description;
	private String solutionId;
	private String revisionId;
	
	public CMSSolutionRevisionDescription() { }
	
	public CMSSolutionRevisionDescription(String solutionId, String revisionId, String description) {
		this.solutionId = solutionId;
		this.revisionId = revisionId;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSolutionId() {
		return solutionId;
	}
	public void setSolutionId(String solutionId) {
		this.solutionId = solutionId;
	}
	public String getRevisionId() {
		return revisionId;
	}
	public void setRevisionId(String revisionId) {
		this.revisionId = revisionId;
	}
	
}
