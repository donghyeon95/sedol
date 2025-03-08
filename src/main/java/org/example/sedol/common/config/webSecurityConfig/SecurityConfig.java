package org.example.sedol.common.config.webSecurityConfig;

import org.example.sedol.common.error.CustomAccessDeniedHandler;
import org.example.sedol.common.error.CustomAuthenticationEntryPoint;
import org.example.sedol.common.security.jwt.JWTProvider;
import org.example.sedol.domain.account.domain.repository.AccountRepository;
import org.example.sedol.domain.stremming.service.StreamKeyProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JWTProvider jwtProvider;
	private final StreamKeyProvider streamKeyProvider;
	private final AccountRepository accountRepository;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final CustomAccessDeniedHandler accessDeniedHandler;



	private static final String[] AUTH_WHITELIST = {
		"/stream/**", "/media/**", "/login/**", "/signup/**", "/swagger-ui/**", "/api-docs", "/swagger-ui-custom.html",
		"/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html", "/api/v1/auth/**"
	};

	@Value("${stream.api.key}")
	private String STREAM_KEY;
	private static final String[] OBS_BLACKLIST = {
		"/stream/start", "/stream/stop"
	};

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// csrf, cors 비활성화
		http.csrf(AbstractHttpConfigurer::disable);
		http.cors(Customizer.withDefaults());


		// 세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 X
		http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
			SessionCreationPolicy.STATELESS));

		//FormLogin, BasicHttp 비활성화
		http.formLogin(AbstractHttpConfigurer::disable);
		http.httpBasic(AbstractHttpConfigurer::disable);

		//JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
		http.addFilterBefore(new JWTFilter(jwtProvider, accountRepository, AUTH_WHITELIST), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(new OBSFIlter(STREAM_KEY, streamKeyProvider, OBS_BLACKLIST), JWTFilter.class);

		http.exceptionHandling((exceptionHandling) -> exceptionHandling
			.authenticationEntryPoint(authenticationEntryPoint)
			.accessDeniedHandler(accessDeniedHandler)
		);

		// 권한 규칙 작성
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/media/**").permitAll()
				.requestMatchers(AUTH_WHITELIST).permitAll()
				// //@PreAuthrization을 사용할 때에는 모든 경로에 대한 인증처리는 Pass
				.anyRequest().permitAll()
			// .anyRequest().authenticated()
		);

		return http.build();

	}
}
