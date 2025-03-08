package org.example.sedol.domain.stremming.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
public class VideoStreamWebSocketController extends BinaryWebSocketHandler {

	private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
	private volatile boolean isStreaming = false;

	private final String MEDIA_PATH = "//wsl.localhost/Ubuntu/tmp/hls/1080/latest_213123.mp4";

	private static final int CHUNK_SIZE = 8192; // 청크 크기 (8KB)

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		System.out.println("[WebSocket] Connection established: " + session.getId());

		// FFmpeg 스트리밍 시작
		if (!isStreaming) {
			isStreaming = true;
			startFFmpegStream();
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		System.out.println("[WebSocket] Connection closed: " + session.getId());

		// 모든 클라이언트가 종료되면 스트리밍 중단
		if (sessions.isEmpty()) {
			isStreaming = false;
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.err.println("[WebSocket] Transport error: " + exception.getMessage());
		sessions.remove(session);
	}

	private void startFFmpegStream() throws InterruptedException {
		new Thread(() -> {
			Process ffmpegProcess = null;
			try {
				ProcessBuilder processBuilder = new ProcessBuilder(
					"wsl",
					"ffmpeg",
					"-i", "rtmp://localhost/live/213123", // RTMP 입력
					"-c:v", "libx264", // 비디오 재인코딩
					"-c:a", "aac", // 오디오 재인코딩
					"-movflags", "frag_keyframe+empty_moov+default_base_moof", // Fragmented MP4 형식
					"-use_wallclock_as_timestamps", "1",
					"-fflags", "+genpts",
					"-vsync", "2",
					"-vf", "fps=30",
					"-f", "mp4",
					"-segment_time", "0.00001", // 1초마다 fragment 생성
					"-segment_format", "mp4",
					"pipe:1" // FFmpeg 출력을 표준 출력으로 설정
				);

				processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
				ffmpegProcess = processBuilder.start();

				// FFmpeg 에러 로그 출력
				InputStream errorStream = ffmpegProcess.getErrorStream();
				new Thread(() -> {
					try (InputStream es = errorStream) {
						byte[] buffer = new byte[1024];
						int bytesRead;
						while ((bytesRead = es.read(buffer)) != -1) {
							System.out.write(buffer, 0, bytesRead); // 에러 로그 출력
							System.out.println("error");
						}
					} catch (IOException ignored) {
					}
				}).start();

				// FFmpeg 출력 데이터 처리
				InputStream inputStream = ffmpegProcess.getInputStream();
				byte[] buffer = new byte[8192];
				int bytesRead;

				boolean isInitializationSent = false;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);

					// 초기화 세그먼트 검증
					if (!isInitializationSent) {
						if (containsInitializationSegment(buffer, bytesRead)) {
							isInitializationSent = true;
							System.out.println("[FFmpeg] Initialization segment detected and sent.");
						} else {
							System.err.println("[FFmpeg] Initialization segment not found in the first chunk.");
							break;
						}
					}

					// WebSocket 클라이언트로 데이터 전송
					broadcast(byteBuffer);
				}

			} catch (Exception e) {
				System.err.println("[FFmpeg] Error: " + e.getMessage());
			} finally {
				if (ffmpegProcess != null) {
					ffmpegProcess.destroy();
				}
			}
		}).start();
		// Thread.sleep(33); // 30fps
	}

	private boolean containsInitializationSegment(byte[] buffer, int length) {
		// "ftyp" 박스 또는 "moov" 박스를 확인
		String header = new String(buffer, 0, Math.min(length, 64)); // 첫 64바이트만 확인
		System.out.println("[FFmpeg] Header content: " + header); // 헤더 로그 출력
		return header.contains("ftyp") || header.contains("moov");
	}

	private void broadcast(ByteBuffer data) {
		for (WebSocketSession session : sessions) {
			if (session.isOpen()) {
				synchronized (session) {
					try {
						session.sendMessage(new BinaryMessage(data));
						System.out.println("[WebSocket] Sent data size: " + data.remaining() + " bytes");
					} catch (IOException e) {
						System.err.println("[WebSocket] Broadcast error: " + e.getMessage());
					}
				}
			} else {
				System.err.println("[WebSocket] Session is closed: " + session.getId());
			}
		}
	}

}