package org.example.sedol.domain.stremming.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class OBSStreamingService {
	// // Update FFmpeg command to ensure 1080p output
	// private static final String FFMPEG_COMMAND =
	// 	"wsl ffmpeg -i rtmp://localhost/live/213123 " +
	// 		"-vf scale=1920:1080 " +            // Scale to 1080p
	// 		"-f mpegts -codec:v mpeg1video -"; // Output format
	//
	// public void startStreaming(DataCallback callback) {
	// 	try {
	// 		Process process = Runtime.getRuntime().exec(FFMPEG_COMMAND);
	// 		InputStream inputStream = process.getInputStream();
	//
	// 		byte[] buffer = new byte[4096];
	// 		int bytesRead;
	// 		while ((bytesRead = inputStream.read(buffer)) != -1) {
	// 			callback.onData(buffer, bytesRead);
	// 		}
	// 	} catch (Exception e) {
	// 		e.printStackTrace();
	// 	}
	// }
	//
	// public interface DataCallback {
	// 	void onData(byte[] data, int length);
	// }
}
