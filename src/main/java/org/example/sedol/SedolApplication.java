package org.example.sedol;

import java.net.URI;
import java.net.URISyntaxException;

import org.example.sedol.common.config.websocket.OBSWebsocketClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SedolApplication {

	public static void main(String[] args) throws URISyntaxException {

		ApplicationContext context = SpringApplication.run(SedolApplication.class, args);
		System.out.println("hihi");
		// Spring Context 외부에서 불림
		// OBSWebsocketClient obsWebsocketClient = new OBSWebsocketClient(new URI("ws://localhost:4455"));
		OBSWebsocketClient obsWebsocketClient = context.getBean("OBSWebsocketClient", OBSWebsocketClient.class);

		int retry = 0;
		boolean flag = false;

		while (!flag && retry < 15) {
			try {
				// 이미 연결되어 있는지 확인
				if (!obsWebsocketClient.isOpen()) {
					obsWebsocketClient.connect();
				}

				// 연결이 완료될 때까지 기다림
				int waitCount = 0;
				while (!obsWebsocketClient.isOpen() && waitCount < 10) {
					Thread.sleep(100); // 100ms 대기
					waitCount++;
				}

				// 인증이 완료되었는지 확인
				if (obsWebsocketClient.isAuthenticated()) {
					obsWebsocketClient.sendRequest("GetStats", "1");
					flag = true; // 요청 성공 시 반복 종료
				} else {
					System.out.println("Authentication not completed. Retrying...");
				}

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
				retry++;
			}
		}
		if (!flag) {
			System.out.println("Failed to connect and authenticate after " + retry + " retries.");
		}

	}

}
