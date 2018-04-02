package org.acumos.cds.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acumos.cds.util.EELFLoggerDelegate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

/**
 * Trivial extension of a standard Spring class to log the use of invalid
 * credentials when accessing a protected REST method.
 */
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate
			.getLogger(CustomBasicAuthenticationEntryPoint.class);

	/**
	 * Confusing name, this method is called for a request that resulted in an
	 * <code>AuthenticationException</code>.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
			throws IOException, ServletException {
		logger.warn(EELFLoggerDelegate.auditLogger, "{} on request for {} from address {}", authEx.getMessage(),
				request.getRequestURI(), request.getRemoteAddr());
		super.commence(request, response, authEx);
	}

}
