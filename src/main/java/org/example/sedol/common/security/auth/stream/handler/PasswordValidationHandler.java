package org.example.sedol.common.security.auth.stream.handler;

import java.util.Set;

import org.example.sedol.common.security.jwt.JWTProvider;
import org.example.sedol.common.security.jwt.JWTStreamClaims;
import org.example.sedol.domain.VO.StreamServiceType;
import org.example.sedol.domain.stremming.domain.entitiy.StreamRoom;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PasswordValidationHandler extends StreamRoomAuthenticationHandler {
	private final StreamServiceType SERVICE_TYPE = StreamServiceType.PASSWORD_STREAMING;

	public PasswordValidationHandler(JWTProvider jwtProvider) {
		super(jwtProvider);
	}

	@Override
	protected boolean doHandler(StreamRoom streamRoom, JWTStreamClaims claims) {
		// 비밀 번호 인증 필요 없다면
		if (!streamRoom.getPasswordOpt())
			return true;

		if (!claims.getServiceTypes().get(StreamServiceType.PASSWORD_STREAMING))
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Need password authentication");

		return true;
	}

	@Override
	public boolean supports(Set<StreamServiceType> streamServiceTypes) {
		return streamServiceTypes.contains(SERVICE_TYPE);
	}
}
