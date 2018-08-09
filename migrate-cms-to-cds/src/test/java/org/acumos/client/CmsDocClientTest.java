package org.acumos.client;

import java.net.URL;

import org.acumos.cms.client.CMSDocumentClient;
import org.acumos.cms.domain.CMSDocumentList;
import org.junit.Test;

public class CmsDocClientTest {

	// don't commit these values to any open Gerrit repo
	private static String hostname = "cognita-dev1-vm01-core.eastus.cloudapp.azure.com";
	private static final String contextPath = "/site";
	private static final int port = 8085;
	private static final String user = "admin";
	private static final String pass = "admin";
	String solId = "0074ddeb-d13d-4f9f-89fa-5418859d9d59";
	String revId = "dae46be9-a2f6-461f-b09b-fcb17e30c13d";

	@Test
	public void testGetDocumentNames() throws Exception {
		URL url = new URL("http", hostname, port, contextPath);
		CMSDocumentClient client = new CMSDocumentClient(url.toString(), user, pass);
		CMSDocumentList docNameResponse = client.getDocumentNames(solId, revId, CMSDocumentClient.ORG_WS);
		System.out.println("Document names:" + docNameResponse);
	}
	
}
