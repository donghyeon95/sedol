package org.example.sedol.domain.stremming.controller;

import org.example.sedol.common.config.websocket.MediaList;
import org.example.sedol.domain.stremming.StartTimeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController()
@RequestMapping(value = "streaming", produces = "application/json")
public class StreamingController {

	@Autowired
	private MediaList mediaList;

	@GetMapping("/key")
	public String getStreamCode(@RequestParam("userId") String user) {
		System.out.println(user);
		return "123-123-123";
	}

	@GetMapping("/start/time")
	public StartTimeResponse getStartTime() {
		System.out.println("ㅑ냐ㅜ");
		return new StartTimeResponse(mediaList.getMediaStartTime("213123"));
	}

}



