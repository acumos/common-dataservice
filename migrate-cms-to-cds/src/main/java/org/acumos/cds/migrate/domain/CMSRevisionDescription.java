/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package org.acumos.cds.migrate.domain;

/**
 * <pre>
 * {
 * "description":" Test",
 * "solutionId":"e85f4c75-439f-4e4f-8362-6d75187f198f",
 * "revisionId":"aae12a0c-ee4d-4494-b59d-493a0cc794ca"
 * }
 * </pre>
 */
public class CMSRevisionDescription {

	private String description;
	private String solutionId;
	private String revisionId;

	public CMSRevisionDescription() {
	}

	public CMSRevisionDescription(String solutionId, String revisionId, String description) {
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

	@Override
	public String toString() {
		int max = 100;
		String shortDesc = description == null ? ""
				: description.length() < max ? description : description.substring(0, max) + "..";
		return this.getClass().getName() + "[solutionId=" + solutionId + ", revisionId=" + revisionId + ", description="
				+ shortDesc + "]";
	}
}
