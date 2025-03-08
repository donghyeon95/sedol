package org.example.sedol.domain.stremming.controller;

import java.net.MalformedURLException;

import org.example.sedol.common.aop.streamAuth.StreamAuthCheck;
import org.example.sedol.common.config.websocket.MediaList;
import org.example.sedol.domain.VO.StreamServiceType;
import org.example.sedol.domain.stremming.DTO.StreamRoomRequestDTO;
import org.example.sedol.domain.stremming.domain.entitiy.StreamRoom;
import org.example.sedol.domain.stremming.service.StreamReadyService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping(value = "stream", produces = "application/json")
@RequiredArgsConstructor
public class StreamingController {
	private final MediaList mediaList;
	private final StreamReadyService streamReadyService;

	@GetMapping("/generate/skey")
	public ResponseEntity<String> getStreamKey(@RequestParam("userId") String user) {
		// user 별로 일자 별로? stream key를 발급
		// 재발급도 마찬가지 => 이미 발급된 key가 있다면
		String streamKey = streamReadyService.generateStreamKey(user);

		return ResponseEntity.ok(streamKey);
	}

	@PostMapping("/generate/room")
	@StreamAuthCheck(serviceTypes = {StreamServiceType.PASSWORD_STREAMING})
	public ResponseEntity<StreamRoom> makeStreamRoom (@RequestParam String userId) {
		// 맨 처음에 들어오면
		System.out.println(userId);
		StreamRoom streamRoom = streamReadyService.generateStream(userId);

		// TODO Response DTO 사용
		return ResponseEntity.ok(streamRoom);
	}

	@PatchMapping("/generate/room")
	public ResponseEntity<StreamRoom> patchStreamRoom(@RequestBody StreamRoomRequestDTO streamRoomRequestDTO) {
		StreamRoom streamRoom = streamReadyService.modifyStreamInfo(streamRoomRequestDTO);
		return ResponseEntity.ok(streamRoom);
	}

	@GetMapping("/thumbnail")
	public ResponseEntity<Resource> getThumnial(@RequestParam String streamKey) throws MalformedURLException {
		return streamReadyService.getThumnail(streamKey);
	}


	@PostMapping("/start")
	public ResponseEntity<String> startStream(@RequestParam("name") String streamKey, HttpServletRequest request) throws
		Exception {
		System.out.println("OBS Start");
		System.out.println(streamKey);
		streamReadyService.startStream(streamKey);

		return ResponseEntity.ok("Stream started successfully");
	}

	@PostMapping("/stop")
	public void stopStream(@RequestParam("name") String streamKey, HttpServletRequest request) {
		System.out.println("stream Key is " + streamKey);
		System.out.println("OBS Stop");

		streamReadyService.stopStream(streamKey);
	}

}



