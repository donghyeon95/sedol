package org.example.sedol.domain.stremming.DTO;

import org.example.sedol.domain.stremming.domain.entitiy.StreamRoom;
import org.example.sedol.domain.stremming.error.AuthErrorCode;

public class StreamRoomResponseDTO {
	String code;
	AuthErrorCode errorCode;
	StreamRoom streamRoom;


}
