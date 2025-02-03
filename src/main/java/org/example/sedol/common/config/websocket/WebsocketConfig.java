package org.example.sedol.common.config.websocket;

import org.example.sedol.domain.stremming.controller.VideoStreamWebSocketController;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

	private final VideoStreamWebSocketController videoStreamWebSocketController;

	public WebsocketConfig(VideoStreamWebSocketController videoStreamWebSocketController) {
		this.videoStreamWebSocketController = videoStreamWebSocketController;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(videoStreamWebSocketController, "/stream")
			.setAllowedOrigins("*");
	}
}