package org.example.sedol.common.config.websocket;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class OBSWebsocketClient extends WebSocketClient {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private String SERIAL_NUMBER = "213123";

	@Autowired
	private MediaList mediaList;

	public boolean isAuthenticated() {
		return isAuthenticated;
	}

	private boolean isAuthenticated = false;

	public OBSWebsocketClient() throws URISyntaxException {
		super(new URI("ws://localhost:4455"));
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("Connected to OBS WebSocket!");
	}

	@Override
	public void onMessage(String message) {
		System.out.println(System.currentTimeMillis() + " Received message: " + message);

		try {
			OBSResponseMessage response = objectMapper.readValue(message, OBSResponseMessage.class);

			switch (response.getOp()) {
				case 0: // 초기 메시지: 인증 정보 수신
					OBSResponseMessage.Authentication auth = response.getD().getAuthentication();
					if (auth != null) {
						String challenge = auth.getChallenge();
						String salt = auth.getSalt();
						sendAuthRequest("2M6CmRDvq2jINlBk", challenge, salt);
					}
					break;

				case 2: // 인증 성공
					System.out.println("Authentication successful!");
					isAuthenticated = true;
					break;

				case 5: // 이벤트 메시지 처리
					handleEvent(response.getD());
					
					break;

				case 7: // 일반 요청 응답
					handleGeneralResponse(response.getD());
					break;

				default:
					System.out.println("Unhandled op code: " + response.getOp());
			}
		} catch (Exception e) {
			System.err.println("Error processing message: " + e.getMessage());
			e.printStackTrace();
		}
	}


	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("Connection closed: " + reason);
	}

	@Override
	public void onError(Exception ex) {
		System.err.println("WebSocket error: " + ex.getMessage());
		ex.printStackTrace();
	}

	private void sendAuthRequest(String password, String challenge, String salt) {
		try {
			String authResponse = generateAuthenticationResponse(password, challenge, salt);

			OBSRequestMessage.Data requestData = new OBSRequestMessage.Data();
			requestData.setRpcVersion(1);
			requestData.setAuthentication(authResponse);

			OBSRequestMessage authRequest = new OBSRequestMessage(1, requestData);

			String jsonRequest = objectMapper.writeValueAsString(authRequest);
			System.out.println("Authentication request sent: " + jsonRequest);
			send(jsonRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendRequest(String requestType, String requestId) {
		if (!isAuthenticated) {
			System.err.println("Cannot send request before authentication is complete.");
			return;
		}
		try {
			OBSRequestMessage.Data requestData = new OBSRequestMessage.Data(requestType, requestId);
			OBSRequestMessage request = new OBSRequestMessage(6, requestData);

			String jsonRequest = objectMapper.writeValueAsString(request);
			System.out.println("Request sent: " + jsonRequest);
			send(jsonRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String generateAuthenticationResponse(String password, String challenge, String salt) throws Exception {
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		byte[] hashedPassword = sha256.digest((password + salt).getBytes(StandardCharsets.UTF_8));
		String hashedPasswordBase64 = Base64.getEncoder().encodeToString(hashedPassword);

		byte[] authResponse = sha256.digest((hashedPasswordBase64 + challenge).getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(authResponse);
	}

	private void handleGeneralResponse(OBSResponseMessage.Data data) {
		if (data.getRequestId() != null) {
			System.out.println("Response for requestId: " + data.getRequestId());
		}
	}

	private void handleEvent(OBSResponseMessage.Data data) throws NoSuchFieldException {
		if (data.getEventName() != null) {
			System.out.println("Event Name: " + data.getEventName());

			if (data.getEventData() != null) {
				System.out.println("Event Data: " + data.getEventData());

				// 특정 이벤트 처리 예시
				switch (data.getEventName()) {
					case "StreamStarted":
						System.out.println("Stream has started.");
						break;

					case "StreamStopped":
						System.out.println("Stream has stopped.");
						break;

					default:
						System.out.println("Unhandled event: " + data.getEventName());
				}
			} else {
				System.out.println("No event data available.");
			}
		} else {
			System.out.println("Event name is missing.");
		}

		JsonNode rootNode = objectMapper.valueToTree(data.getEventData());
		String outputState = rootNode.path("outputState").asText();

		// eveneData -> outputState를 확인!!()
		if (outputState.equals("OBS_WEBSOCKET_OUTPUT_STARTED")) {
			System.out.println("OBS Streaming Started!!! ");
			mediaList.setMediaStartTime(SERIAL_NUMBER, System.currentTimeMillis());
		}
	}
}
