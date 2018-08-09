package org.acumos.cms.client;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.acumos.cms.domain.CMSDocumentList;
import org.acumos.cms.domain.CMSSolutionRevisionDescription;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * https://wiki.acumos.org/display/PUX/CMS+API
 */
public class CMSDocumentClient {

	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Workspace for organization-level access
	 */
	public static final String ORG_WS = "org";
	/**
	 * Workspace for public-level access
	 */
	public static final String PUBLIC_WS = "public";

	/** URL components after the context */
	private static final String ACUMOSCMS_PATH = "acumoscms";
	private static final String API_MANUAL_PATH = "api-manual";
	private static final String ASSETS_PATH = "assets";
	private static final String BINARIES_PATH = "binaries";
	private static final String CONTENT_PATH = "content";
	private static final String GALLERY_PATH = "gallery";
	private static final String DESCRIPTION_PATH = "description";
	private static final String SOLUTION_PATH = "Solution";
	private static final String SOLUTION_ASSETS_PATH = "solutionAssets";
	private static final String SOLUTIONDOCS_PATH = "solutiondocs";
	private static final String SOLUTION_IMAGES_PATH = "solutionImages";

	// how confusing is this
	private static final String PATH_PARAM = "path";

	private String baseUrl;
	private RestTemplate restTemplate;

	public CMSDocumentClient(final String webapiUrl, final String user, final String pass) {
		if (webapiUrl == null)
			throw new IllegalArgumentException("Null URL not permitted");

		// Validate the URLs
		URL url = null;
		try {
			url = new URL(webapiUrl);
			baseUrl = url.toExternalForm();
		} catch (MalformedURLException ex) {
			throw new IllegalArgumentException("Failed to parse URL: " + webapiUrl, ex);
		}
		final HttpHost httpHost = new HttpHost(url.getHost(), url.getPort());
		// Build a client with a credentials provider
		HttpClientBuilder builder = HttpClientBuilder.create();
		if (user != null && pass != null) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(user, pass));
			builder.setDefaultCredentialsProvider(credsProvider);
		}
		CloseableHttpClient httpClient = builder.build();
		// Create request factory with the client
		HttpComponentsClientHttpRequestFactoryBasicAuth requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(
				httpHost);
		requestFactory.setHttpClient(httpClient);

		// Put the factory in the template
		restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(requestFactory);
	}

	/**
	 * Builds URI by adding specified path segments and query parameters to the base
	 * URL. Converts an array of values to a series of parameters with the same
	 * name; e.g., "find foo in list [a,b]" becomes request parameters
	 * "foo=a&amp;foo=b".
	 * 
	 * @param path
	 *            Array of path segments
	 * @param queryParams
	 *            key-value pairs; ignored if null or empty. Gives special treatment
	 *            to Date-type values, Array values, and null values inside arrays.
	 * @param pageRequest
	 *            page, size and sort specification; ignored if null.
	 * @return URI with the specified path segments and query parameters
	 */
	protected URI buildUri(final String[] path, final Map<String, Object> queryParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.baseUrl);
		for (int p = 0; p < path.length; ++p)
			builder.pathSegment(path[p]);
		if (queryParams != null && queryParams.size() > 0) {
			for (Map.Entry<String, ? extends Object> entry : queryParams.entrySet()) {
				if (entry.getValue() instanceof Date) {
					// Server expects Date type as Long (not String)
					builder.queryParam(entry.getKey(), ((Date) entry.getValue()).getTime());
				} else if (entry.getValue().getClass().isArray()) {
					Object[] array = (Object[]) entry.getValue();
					for (Object o : array) {
						if (o == null)
							builder.queryParam(entry.getKey(), "null");
						else if (o instanceof Date)
							builder.queryParam(entry.getKey(), ((Date) o).getTime());
						else
							builder.queryParam(entry.getKey(), o.toString());
					}
				} else {
					builder.queryParam(entry.getKey(), entry.getValue().toString());
				}
			}
		}
		return builder.build().encode().toUri();
	}

	/**
	 * POST http://localhost:8085/site/api-manual/Solution/description/{workspace}
	 * 
	 * <pre>
	 * {
	 * "description":"Test",
	 * "solutionId":"e85f4c75-439f-4e4f-8362-6d75187f198f",
	 * "revisionId":"aae12a0c-ee4d-4494-b59d-493a0cc794ca"
	 * }
	 * </pre>
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param revisionId
	 *            Revision ID
	 * @param workspace
	 *            "workspace" can be "public" or "org"
	 * @param description
	 *            Text
	 * @return Location
	 */
	public URI saveSolutionRevisionDescription(String solutionId, String revisionId, String workspace,
			String description) {
		URI uri = buildUri(new String[] { API_MANUAL_PATH, SOLUTION_PATH, DESCRIPTION_PATH, workspace }, null);
		logger.debug("saveSolutionRevisionDescription: uri {}", uri);
		CMSSolutionRevisionDescription d = new CMSSolutionRevisionDescription(solutionId, revisionId, description);
		return restTemplate.postForLocation(uri, d);
	}

	/**
	 * Get Solution Revision Description : GET
	 * http://localhost:8085/site/api-manual/Solution/description/{workspace}/{solutionId}/{revisionId}
	 * Returns
	 * 
	 * <pre>
	  {
	  "description":"<p>Test</p>",
	  "solutionId":"e85f4c75-439f-4e4f-8362-6d75187f198f",
	  "revisionId":"aae12a0c-ee4d-4494-b59d-493a0cc794ca"
	  }
	 * </pre>
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param revisionId
	 *            Revision ID
	 * @param workspace
	 *            "workspace" can be "public" or "org"
	 * @return Description object
	 * 
	 */
	public CMSSolutionRevisionDescription getSolutionRevisionDescription(String solutionId, String revisionId,
			String workspace) {
		URI uri = buildUri(
				new String[] { API_MANUAL_PATH, SOLUTION_PATH, DESCRIPTION_PATH, workspace, solutionId, revisionId },
				null);
		logger.debug("getSolutionRevisionDescription: uri {}", uri);
		ResponseEntity<CMSSolutionRevisionDescription> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CMSSolutionRevisionDescription>() {
				});
		return response.getBody();
	}

	/**
	 * Save Solution Image : POST :
	 * http://localhost:8085/site/api-manual/solutionImages/{solutionId} Request
	 * Body : Image as attachment
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param image
	 *            Image bytes
	 * @return Location
	 */
	public URI saveSolutionImage(String solutionId, byte[] image) {
		URI uri = buildUri(new String[] { API_MANUAL_PATH, SOLUTION_PATH, SOLUTION_IMAGES_PATH, solutionId }, null);
		logger.debug("saveSolutionImage: uri {}", uri);
		return restTemplate.postForLocation(uri, image);
	}

	/**
	 * Get Solution image name : GET
	 * http://localhost:8085/site/api-manual/solutionImages/{solutionId} Response
	 * Body : [image.jpg]
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @return Image name
	 */
	public String getSolutionImageName(String solutionId) {
		URI uri = buildUri(new String[] { API_MANUAL_PATH, SOLUTION_IMAGES_PATH, solutionId }, null);
		logger.debug("getSolutionImageName: uri {}", uri);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				});
		return response.getBody();
	}

	/**
	 * Get Binary image:
	 * http://localhost:9080/site/binaries/content/gallery/acumoscms/solution/{solutionId}/{imageName}
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param imageName
	 *            Image name
	 * @return Image
	 */
	public String getBinaryImage(String solutionId, String imageName) {
		URI uri = buildUri(new String[] { BINARIES_PATH, CONTENT_PATH, GALLERY_PATH, ACUMOSCMS_PATH, SOLUTION_PATH,
				solutionId, imageName }, null);
		logger.debug("getBinaryImage: uri {}", uri);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<String>() {
				});
		return response.getBody();
	}

	/**
	 * Save solution Revision Documents : POST
	 * http://localhost:8085/site/api-manual/Solution/solutionAssets/{solutionId}/{revisionId}?path={workspace}
	 * Request Body : Document as attachment
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param revisionId
	 *            Revision ID
	 * @param workspace
	 *            "workspace" can be "public" or "org"
	 * @param image
	 *            Image
	 * @return Location
	 */
	public URI saveSolutionRevisionDocument(String solutionId, String revisionId, String workspace, byte[] image) {
		HashMap<String, Object> params = new HashMap<>();
		params.put(PATH_PARAM, workspace);
		URI uri = buildUri(
				new String[] { API_MANUAL_PATH, SOLUTION_PATH, SOLUTION_ASSETS_PATH, solutionId, revisionId }, params);
		logger.debug("saveSolutionRevisionDocument: uri {}", uri);
		return restTemplate.postForLocation(uri, image);
	}

	/**
	 * Download Solution Revision Document : GET
	 * http://cognita-dev1-vm01-core.eastus.cloudapp.azure.com/site/binaries/content/assets/solutiondocs/solution/{solutionId}/{RevisionId}/{workspace}/{documentName}
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param revisionId
	 *            Revision ID
	 * @param workspace
	 *            "workspace" can be "public" or "org"
	 * @param documentName
	 *            document name
	 * @return Document content
	 */
	public byte[] getSolutionRevisionDocument(String solutionId, String revisionId, String workspace, String documentName) {
		URI uri = buildUri(new String[] { BINARIES_PATH, CONTENT_PATH, ASSETS_PATH, SOLUTIONDOCS_PATH, solutionId,
				revisionId, workspace, documentName }, null);
		logger.debug("getSolutionRevisionDocument: uri {}", uri);
		ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<byte[]>() {
				});
		return response.getBody();
	}

	/**
	 * GET
	 * http://host.com/site/api-manual/Solution/solutionAssets/{solutionId}/{revisionId}?path={workspace}
	 * 
	 * @param solutionId
	 *            Solution ID
	 * @param revisionId
	 *            Revision ID
	 * @param workspace
	 *            "workspace" can be "public" or "org"
	 * @return List of document names
	 */
	public CMSDocumentList getDocumentNames(String solutionId, String revisionId, String workspace) {
		HashMap<String, Object> params = new HashMap<>();
		params.put(PATH_PARAM, workspace);
		URI uri = buildUri(
				new String[] { API_MANUAL_PATH, SOLUTION_PATH, SOLUTION_ASSETS_PATH, solutionId, revisionId }, params);
		logger.debug("getDocumentNames: uri {}", uri);
		ResponseEntity<CMSDocumentList> response = restTemplate.exchange(uri, HttpMethod.GET, null,
				new ParameterizedTypeReference<CMSDocumentList>() {
				});
		return response.getBody();
	}

}
