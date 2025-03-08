package org.example.sedol.common.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTProvider {
	private final String schema = "Bearer";
	private final String headser = "Authorization";
	private final SecretKey SECRET_KEY;
	private final int EXPIRE_TIME;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public JWTProvider(
		@Value("${jwt.secret}") String secretKey,
		@Value("${jwt.expiration_time}") int expireTime
	){
		this.SECRET_KEY = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));;
		this.EXPIRE_TIME = expireTime;
	}


	public String createToken(JWTClaim jwtClaims) {
		Date expiredTime = Date.from(ZonedDateTime.now().plusSeconds(EXPIRE_TIME).toInstant());
		Map jwtMap = objectMapper.convertValue(jwtClaims, Map.class);
		Claims claims = Jwts.claims().build();
		claims.putAll(jwtMap);

		return Jwts.builder()
			.subject("Authorization")

			.claims(claims)

			.issuedAt(Date.from(ZonedDateTime.now().toInstant()))
			.expiration(expiredTime)


			.signWith(SECRET_KEY)
			.compact();
	}

	public String createToken(JWTClaim jwtClaims, int ttl) {
		Date expiredTime = Date.from(ZonedDateTime.now().plusSeconds(ttl).toInstant());
		Map jwtMap = objectMapper.convertValue(jwtClaims, Map.class);
		Claims claims = Jwts.claims().build();
		claims.putAll(jwtMap);

		return Jwts.builder()
			.subject("Authorization")

			.claims(claims)

			.issuedAt(Date.from(ZonedDateTime.now().toInstant()))
			.expiration(expiredTime)


			.signWith(SECRET_KEY)
			.compact();
	}

	public Date getIsuuedAt(String jws) {
		try {
			return Jwts.parser().build().parseSignedClaims(jws).getPayload().getIssuedAt();
		} catch (ExpiredJwtException e) {
			log.info("ExpiredJWTException");
			return null;
		} catch (Exception e) {
			log.info("Parse Claim Error", e);
			return null;
		}
	}

	public String getJWSFromRequsetCookie(HttpServletRequest request, String keyName) {
		return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
			.filter(cookie -> cookie.getName().equals(keyName))
			.map(Cookie::getValue)
			.findFirst()
			.orElse(null);
	}

	public String getJWSFromRequestHeader(HttpServletRequest request, String keyName) {
		return request.getHeader(keyName);
	}

	public boolean validateToken(String jwtToken) {
		try {
			Jwts.parser().decryptWith(SECRET_KEY).build().parseSignedClaims(jwtToken);
			return true;
		}  catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token", e);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT Token", e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty.", e);
		}
		return false;
	}

	public <T> T parseClaim(String jwtToken, Class<T> clazz) {
		try {
			Claims claims = Jwts.parser().build().parseSignedClaims(jwtToken).getPayload();
			return objectMapper.convertValue(claims, clazz);
		} catch (ExpiredJwtException e) {
			log.info("ExpiredJWTException");
			return null;
		} catch (Exception e) {
			log.info("parse Claim Error", e);
			return null;
		}
	}

}
