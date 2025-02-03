package org.example.sedol.common.config.websocket;

import java.util.HashMap;

import org.springframework.stereotype.Component;

@Component
public class MediaList {
	private HashMap<String, Long> mediaStartTime;

	MediaList() {
		mediaStartTime = new HashMap<>();
	}

	public void setMediaStartTime(String serialKey, long startTime) {
		mediaStartTime.put(serialKey, startTime);
	}

	public long getMediaStartTime(String serialKey) {
		return mediaStartTime.getOrDefault(serialKey, 1L);
	}
}
