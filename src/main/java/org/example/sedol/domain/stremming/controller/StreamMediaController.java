package org.example.sedol.domain.stremming.controller;

import java.awt.*;
import java.io.File;

import org.example.sedol.common.config.websocket.MediaList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/media", produces = "application/json")
public class StreamMediaController {

	String FILE_PATH = "//wsl.localhost/Ubuntu/tmp/hls";

	@Autowired
	private MediaList mediaList;


	@GetMapping("/hls/{quality}/{fileName}")
	public ResponseEntity<Resource> getTsFile( @PathVariable String quality, @PathVariable String fileName) {
		// return ts File
		System.out.println("Is IN");
		File hlsFile = Paths.get(FILE_PATH, quality, fileName).toFile();
		Resource resource = new FileSystemResource(hlsFile);
		String contentType = fileName.endsWith(".m3u8") ?
			"application/vnd.apple.mpegurl" :
			"video/mp2t";



		if (!resource.exists()) {
			return ResponseEntity.notFound().build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Broadcast-Start-Time", String.valueOf(mediaList.getMediaStartTime("213123")));
		// headers.add("Cache-Control", "no-cache, no-store, must-revalidate"); // 캐싱 방지
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);

		return ResponseEntity.ok()
			.headers(headers)
			.body(resource);
	}
}
