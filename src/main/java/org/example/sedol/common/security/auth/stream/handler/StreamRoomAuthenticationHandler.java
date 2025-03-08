package org.example.sedol.common.security.auth.stream.handler;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.example.sedol.common.security.jwt.JWTProvider;
import org.example.sedol.common.security.jwt.JWTStreamClaims;
import org.example.sedol.domain.VO.StreamServiceType;
import org.example.sedol.domain.stremming.domain.entitiy.StreamRoom;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public abstract class StreamRoomAuthenticationHandler {
	private StreamRoomAuthenticationHandler nextHandler;
	protected final JWTProvider jwtProvider;

	protected StreamRoomAuthenticationHandler(JWTProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	public void setNext(StreamRoomAuthenticationHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	public final boolean handle(StreamRoom streamRoom, HttpServletRequest request) {
		String jws = Optional.ofNullable(jwtProvider.getJWSFromRequsetCookie(request, "RoomSession")).orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Jwt Key not found"));
		JWTStreamClaims claims = jwtProvider.parseClaim(jws, JWTStreamClaims.class);

		// stream Session Token이 다르다면
		if (!claims.getStreamKey().equals(streamRoom.getStreamKey())) return false;

		boolean result = doHandler(streamRoom, claims);

		// 인증 실패
		if (!result)
			return false;

		// 다음 핸들러 호출
		if (nextHandler != null) {
			return nextHandler.handle(streamRoom, request);
		}

		return true;
	}

	// handle
	protected abstract boolean doHandler(StreamRoom streamRoom, JWTStreamClaims claims);

	// 여러 개의 서비스 타입을 지원
	public abstract boolean supports(Set<StreamServiceType> streamServiceTypes);

}
