package org.example.sedol.common.config.webSecurityConfig;

import java.io.IOException;
import java.util.Arrays;

import org.example.sedol.common.security.jwt.JWTAccountClaims;
import org.example.sedol.common.security.jwt.JWTProvider;
import org.example.sedol.domain.account.domain.entity.Account;
import org.example.sedol.domain.account.domain.repository.AccountRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

	private JWTProvider jwtProvider;
	private AccountRepository accountRepository;

	private static final AntPathMatcher pathMatcher = new AntPathMatcher();
	private final String[] AUTH_WHITELIST;

	@Override
	@Transactional(readOnly=true)
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// filter whiteList 적용
		String requestURI = request.getRequestURI();
		System.out.println("filter In : " +  requestURI);
		if (isWhitelisted(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		// jwt Token 가져오기
		String authHeader = request.getHeader("Authorization");
		if (authHeader!=null && authHeader.startsWith("Bearer")) {
			String token = authHeader.substring(7);

			// TODO blackList 확인
			if (jwtProvider.validateToken(token)) {
				JWTAccountClaims jwtAccountClaims = jwtProvider.parseClaim(token, JWTAccountClaims.class);
				Account account = accountRepository.findByUserId(jwtAccountClaims.getUserId()).orElseThrow();
				CustomUserDetail userDetail = new CustomUserDetail(account);
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					userDetail, null, userDetail.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		} else {
			throw new IOException("Auth Token Error");
		}

	}

	private boolean isWhitelisted(HttpServletRequest request) {
		return Arrays.stream(AUTH_WHITELIST).map(AntPathRequestMatcher::new).anyMatch(matcher -> matcher.matches(request));
	}

}
