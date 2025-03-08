package org.example.sedol.common.aop.streamAuth;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.sedol.common.security.auth.stream.chain.StreamAuthenticationChain;
import org.example.sedol.common.security.auth.stream.chain.StreamAuthenticationChainFactory;
import org.example.sedol.domain.VO.StreamServiceType;
import org.example.sedol.domain.stremming.domain.entitiy.StreamRoom;
import org.example.sedol.domain.stremming.domain.repositoty.StreamRoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Aspect
@Component
public class StreamAuthenticationAspect {
	private final StreamAuthenticationChainFactory streamAuthenticationChainFactory;
	private final StreamRoomRepository streamRoomRepository;

	public StreamAuthenticationAspect (StreamAuthenticationChainFactory streamAuthenticationChainFactory, StreamRoomRepository streamRoomRepository){
		this.streamAuthenticationChainFactory = streamAuthenticationChainFactory;
		this.streamRoomRepository = streamRoomRepository;
	}

	@Around("@annotation(streamAuthCheck)")
	public Object checkAuthentication(ProceedingJoinPoint joinPoint, StreamAuthCheck streamAuthCheck) throws Throwable {
		HttpServletRequest request = getHttpServletRequest();

		// StreamKey 받아오기
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String[] paramNames = signature.getParameterNames();  // ✅ Reflection 없이 직접 접근
		Object[] args = joinPoint.getArgs();
		String streamKey = getStreamKeyFromArgs(args, paramNames);
		StreamRoom streamRoom = streamRoomRepository.findByStreamKey(streamKey).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Strem Room not found"));

		Set<StreamServiceType> serviceTypes = new HashSet<>(Arrays.asList(streamAuthCheck.serviceTypes()));
		StreamAuthenticationChain authenticationChain = streamAuthenticationChainFactory.createChain(serviceTypes);
		authenticationChain.authenticate(streamRoom, request);

		return joinPoint.proceed();
	}

	// 현재 스레드의 request를 가져온다.
	private HttpServletRequest getHttpServletRequest() {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

		if (attributes == null) {
			throw new IllegalStateException("No request context available");
		}

		return attributes.getRequest();
	}

	private HttpServletResponse getHttpServletResponse() {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

		if (attributes == null) {
			throw new IllegalStateException("No request context available");
		}

		return attributes.getResponse();

	}

	private String getStreamKeyFromArgs (Object[] args, String[] paramNames) {
		for (int i = 0; i < paramNames.length; i++) {
			if ("streamKey".equals(paramNames[i]) && args[i] instanceof String) {
				return (String) args[i];
			}
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing streamKey parameter");
	}
}
