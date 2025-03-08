package org.example.sedol.common.config.webSecurityConfig;

import java.io.IOException;
import java.util.Arrays;

import org.example.sedol.domain.stremming.service.StreamKeyProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OBSFIlter extends OncePerRequestFilter {
	private String API_KEY;
	private final StreamKeyProvider streamKeyProvider;
	private final String[] BLACK_LIST;
	private final String STREM_KEY_PARAM = "name";
	private final String API_KEY_HEADER = "X-Api-Key";

	public OBSFIlter(String API_KEY, StreamKeyProvider streamKeyProvider, String[] obsBlackList) {
		this.API_KEY = API_KEY;
		this.streamKeyProvider = streamKeyProvider;
		this.BLACK_LIST = obsBlackList;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 해당 없다면
		if (!isBlacklisted(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		String apiKey = request.getHeader(API_KEY_HEADER);

		// API key 검증
		if (apiKey == null || !apiKey.equals(API_KEY)) {
			response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid API Key");
			return;
			// throw new OBSException("Invalid API Key");
		}
		// Stream Key 검증
		String streamKey = request.getParameter(STREM_KEY_PARAM);
		if (!streamKeyProvider.validateKey(streamKey)) {
			response.sendError(HttpStatus.UNAUTHORIZED.value(), "스트림키가 올바르지 않습니다.");
			return;
			// throw new OBSException("Invalid Stream Key");
		}

		filterChain.doFilter(request, response);
	}

	private boolean isBlacklisted(HttpServletRequest request) {
		return Arrays.stream(BLACK_LIST).map(AntPathRequestMatcher::new).anyMatch(matcher -> matcher.matches(request));
	}
}


