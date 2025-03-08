package org.example.sedol.domain.stremming.controller;

import java.io.File;

import org.example.sedol.common.config.websocket.MediaList;
import org.example.sedol.domain.stremming.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/media", produces = "application/json")
public class StreamMediaController {

	private final StreamingService streamingService;

	@GetMapping("/hls/{quality}/{fileName}")
	public ResponseEntity<Resource> getTsFile(@PathVariable String quality, @PathVariable String fileName, @PathParam("stremKey") String streamKey) {
		// return ts File
		System.out.println("Is IN");
		return streamingService.streamHLS(quality, fileName, streamKey);
	}
}
