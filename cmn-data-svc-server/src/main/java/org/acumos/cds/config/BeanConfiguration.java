package org.acumos.cds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Adds logging handler interceptor.
 * <BR/>
 * http://www.devgrok.com/2017/04/adding-mdc-headers-to-every-spring-mvc.html
 */
@Configuration
public class BeanConfiguration {

	@Bean
	public LoggingHandlerInterceptor loggingHandlerInterceptor() {
		return new LoggingHandlerInterceptor();
	}

	@Bean
	public WebMvcConfigurerAdapter webConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(loggingHandlerInterceptor());
			}
		};
	}

}