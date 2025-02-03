package org.example.sedol.common.config.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebsocketHandler extends TextWebSocketHandler {
	private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

	//websocket handshake가 완료되어 연결이 수립될 때
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("connection established, session id ={}", session.getId());
		sessionMap.putIfAbsent(session.getId(), session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("connection closed, session id = {}, close status = {}", session.getId(), status);
		sessionMap.remove(session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		log.info("received message, session id={}, message = {}", session.getId(), payload);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.error("connection closed, session id = {}, close status = {}", session.getId(), exception.getMessage());
		sessionMap.remove(session.getId());
	}
}
