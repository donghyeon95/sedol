package org.example.sedol.common.security.auth.stream.chain;

import java.util.List;

import org.example.sedol.common.security.auth.stream.handler.StreamRoomAuthenticationHandler;
import org.example.sedol.domain.stremming.domain.entitiy.StreamRoom;

import jakarta.servlet.http.HttpServletRequest;

public class StreamAuthenticationChain {
	private List<StreamRoomAuthenticationHandler> handlerChain;

	public StreamAuthenticationChain(List<StreamRoomAuthenticationHandler> handlers) {
		this.handlerChain = handlers;
	}

	public boolean authenticate(StreamRoom streamRoom, HttpServletRequest request) {
		if (handlerChain.isEmpty()) {
			return true;
		}

		return handlerChain.get(0).handle(streamRoom, request);
	}
}
