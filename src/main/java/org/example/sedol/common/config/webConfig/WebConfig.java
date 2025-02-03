package org.example.sedol.common.config.webConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:5500")
			.allowedHeaders("Range", "X-Broadcast-Start-Time")
			.exposedHeaders("Content-Length", "Content-Range", "X-Broadcast-Start-Time")
			.allowedMethods(HttpMethod.GET.name(), HttpMethod.OPTIONS.name(), HttpMethod.POST.name())
			.maxAge(3600);
	}
}
