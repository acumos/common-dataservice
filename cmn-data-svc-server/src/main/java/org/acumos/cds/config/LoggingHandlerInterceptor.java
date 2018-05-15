package org.acumos.cds.config;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.att.eelf.configuration.Configuration;

/**
 * Adds request details to the mapped diagnostic context (MDC) so they can be
 * logged. <BR/>
 * http://www.devgrok.com/2017/04/adding-mdc-headers-to-every-spring-mvc.html
 */
public class LoggingHandlerInterceptor extends HandlerInterceptorAdapter {

	/**
	 * Tracks the set of keys added to MDC.
	 */
	private ThreadLocal<Set<String>> storedKeys = ThreadLocal.withInitial(() -> new HashSet<>());

	/**
	 * Copies key-value pairs from HTTP request to MDC context. Unfortunately they
	 * use different conventions for key naming.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		addKey(Configuration.MDC_SERVER_IP_ADDRESS, InetAddress.getLocalHost().getHostAddress());
		addKey(Configuration.MDC_REMOTE_HOST, request.getRemoteAddr());
		final String sessionId = request.getHeader("X-Session-ID");
		if (sessionId != null)
			addKey("SessionId", sessionId); // this constant is not known in EELF
		final String requestId = request.getHeader("X-Request-ID");
		if (requestId != null)
			addKey(Configuration.MDC_KEY_REQUEST_ID, requestId);
		return true;
	}

	private void addKey(String key, String value) {
		MDC.put(key, value);
		storedKeys.get().add(key);
	}

	// request ended on current thread remove properties
	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		removeKeys();
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		removeKeys();
	}

	private void removeKeys() {
		for (String key : storedKeys.get())
			MDC.remove(key);

		storedKeys.remove();
	}
}