package org.example.sedol.common.security.auth.stream.chain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.sedol.common.security.auth.stream.handler.StreamRoomAuthenticationHandler;
import org.example.sedol.domain.VO.StreamServiceType;
import org.springframework.stereotype.Component;

@Component
public class StreamAuthenticationChainFactory {
	private final List<StreamRoomAuthenticationHandler> allHandlers;

	public StreamAuthenticationChainFactory(List<StreamRoomAuthenticationHandler> handlers) {
		this.allHandlers = handlers;
	}

	public StreamAuthenticationChain createChain(Set<StreamServiceType> serviceTypes) {
		List<StreamRoomAuthenticationHandler> selectedHandlers = allHandlers.stream()
			.filter(handler -> handler.supports(serviceTypes))
			.collect(Collectors.toList());

		for (int i=0; i<selectedHandlers.size()-1; i++) {
			selectedHandlers.get(i).setNext(selectedHandlers.get(i + 1));
		}

		return new StreamAuthenticationChain(selectedHandlers);
	}
}
