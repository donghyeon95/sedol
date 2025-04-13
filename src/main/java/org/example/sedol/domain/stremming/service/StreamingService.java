package org.example.sedol.domain.stremming.service;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

import javax.swing.text.html.parser.Entity;

import org.example.sedol.common.aop.streamAuth.StreamAuthCheck;
import org.example.sedol.common.security.jwt.JWTProvider;
import org.example.sedol.common.security.jwt.JWTStreamClaims;
import org.example.sedol.domain.VO.StreamServiceType;
import org.example.sedol.domain.account.domain.entity.Account;
import org.example.sedol.domain.account.domain.repository.AccountRepository;
import org.example.sedol.domain.stremming.domain.entitiy.StreamRoom;
import org.example.sedol.domain.stremming.domain.repositoty.StreamRoomRepository;
import org.example.sedol.domain.stremming.error.AuthErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class StreamingService {
	private String ROOT_FILE_PATH;
	private final JWTProvider jwtProvider;
	private final StreamRoomRepository streamRoomRepository;
	private final AccountRepository accountRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public StreamingService (@Value("${media.file.path}") String filePath, JWTProvider jwtProvider, StreamRoomRepository streamRoomRepository,
		AccountRepository accountRepository,
		BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.ROOT_FILE_PATH = filePath;
		this.jwtProvider = jwtProvider;
		this.streamRoomRepository = streamRoomRepository;
		this.accountRepository = accountRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public ResponseEntity<StreamRoom> getStreamRoom(String userId, String streamKey, HttpServletRequest request) {
		StreamRoom streamRoom = streamRoomRepository.findByStreamKey(streamKey).orElseThrow();
		Account account = accountRepository.findByUserId(userId).orElseThrow();
		String jws = jwtProvider.getJWSFromRequestHeader(request, "RoomSession");

		// Session이 이미 있는 지 확인
		if (jws == null) {
			JWTStreamClaims jwtStreamClaims = JWTStreamClaims.builder()
			.userId(userId)
			.streamKey(streamKey)
			.build();

		// FIXME 인증 config를 객체화 해야겠다. 일일이 쓰지 않도록
		// 성인 인증 여부 확인
		if (streamRoom.getAdultOpt()) {
			jwtStreamClaims.getServiceTypes().put(StreamServiceType.ADULT_STREAMING, account.getIsAdult());
		}

		// 비밀 번호 확인
		if (streamRoom.getPasswordOpt())
			jwtStreamClaims.getServiceTypes().put(StreamServiceType.PASSWORD_STREAMING, false);

		jws = jwtProvider.createToken(jwtStreamClaims, 60); // 유효기간 1분
	}


		return ResponseEntity.ok().header("RoomSession", jws).body(streamRoom);
	}


	// 10초마다 session 확인
	public ResponseEntity<StreamRoom> checkSteramRoom(String userId, String streamKey, HttpServletRequest request, HttpServletResponse response) {
					StreamRoom streamRoom = streamRoomRepository.findByStreamKey(streamKey).orElseThrow();
					String jws = Optional.ofNullable(jwtProvider.getJWSFromRequestHeader(request, "RoomSession")).orElseThrow();

					JWTStreamClaims jwtStreamClaims = jwtProvider.parseClaim(jws, JWTStreamClaims.class);

					// 비밀 번호 업데이트 확인
					// jws 발급 시간이랑 비교해서 비밀번호 인증 여부 확인
					Date passwordUpdate = Date.from(streamRoom.getPasswordUpdated().toInstant(ZoneOffset.UTC));
					if (jwtProvider.getIsuuedAt(jws).before(passwordUpdate)) {
						// 비밀번호 인증 여부 OFF
						jwtStreamClaims.getServiceTypes().put(StreamServiceType.PASSWORD_STREAMING, false);
						response.addHeader("RoomSession", jwtProvider.createToken(jwtStreamClaims, 60));
						throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, AuthErrorCode.PASSWORD_CHANGED + "");
					}

					// 성인 인증 여부 확인
					if (streamRoom.getAdOpt() && !jwtStreamClaims.getServiceTypes().get(StreamServiceType.ADULT_STREAMING)) {
						Account account = accountRepository.findByUserId(userId).orElseThrow();
						// account 불러와서 확인
						if (account.getIsAdult()) {
							jwtStreamClaims.getServiceTypes().put(StreamServiceType.ADULT_STREAMING, true);
							response.addHeader("RoomSession", jwtProvider.createToken(jwtStreamClaims, 60));
						} else {
							jwtStreamClaims.getServiceTypes().put(StreamServiceType.ADULT_STREAMING, false);
							response.addHeader("RoomSession", jwtProvider.createToken(jwtStreamClaims, 60));
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, AuthErrorCode.ADULT_CHECKED + "");
			}
		}

		return ResponseEntity.ok().body(streamRoom);
		// 밴 여부 확인
		//

	}


	public ResponseEntity<StreamRoom> authRoomPassword (String streamKey, String password, HttpServletRequest request) {
		// 비밀번호 방인 지 여부 => 비밀번호 방이라면 비밀번호 인증
		// 해당 요청에 인증을 부여하려면 => 확인을 받아야 함
		// 1. client 방 정보 요청 -> 2. 비밀번호 권한 요청 -> 3. 비밀번호 인증 -> 4. 권한 확인
		// -> 5. (해당 세션 혹은 client에 방송 볼 수 있는 권한) 권한 부여 (cookie 혹은 세션으로 관리) -> 6. 매 요청 마다 인가를 확인
		StreamRoom streamRoom = streamRoomRepository.findByStreamKey(streamKey).orElseThrow();

		// 세션에서 Key를 가져오기
		String jws = Optional.ofNullable(jwtProvider.getJWSFromRequestHeader(request, "RoomSession")).orElseThrow(()-> new ResponseStatusException(
			HttpStatus.BAD_REQUEST, "Jwt cookie not found"));
		JWTStreamClaims jwtStreamClaims = jwtProvider.parseClaim(jws, JWTStreamClaims.class);

		// 해당 방의 토큰이 맞는 지 확인
		if (!streamRoom.getStreamKey().equals(jwtStreamClaims.getStreamKey()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token");

		// 비밀번호 인증
		if (streamRoom.getPasswordOpt() && streamRoom.getPassword().equals(bCryptPasswordEncoder.encode(password))) {
			jwtStreamClaims.getServiceTypes().put(StreamServiceType.PASSWORD_STREAMING, true);
		}

		String updatedToken = jwtProvider.createToken(jwtStreamClaims);
		return ResponseEntity.ok().header("RoomSession", updatedToken).body(streamRoom);
	}

	// TODO 추방당한 사람인 지 확인
	// TODO 성인 인증 확인
	// TODO 설정 변경 시 속성 변경 (비밀번호 변경/비밀번호 설정, 보고 있는데 성인인증 설정 등)
	// @StreamAuthCheck(serviceTypes = {StreamServiceType.PASSWORD_STREAMING})
	public ResponseEntity<Resource> streamHLS(String quality, String fileName, String streamKey, HttpServletRequest request) {
		// jwt 인증
		String jws = Optional.ofNullable(jwtProvider.getJWSFromRequestHeader(request, "RoomSesion")).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, AuthErrorCode.INVALID_TOKEN+""));

		JWTStreamClaims jwtStreamClaims = jwtProvider.parseClaim(jws, JWTStreamClaims.class);
		for (Boolean auth: jwtStreamClaims.getServiceTypes().values()) {
			if (Boolean.FALSE.equals(auth))  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, AuthErrorCode.PERMISSION_DENIED+"");
		}

		// FIXME Stream으로 읽어서 Stream으로 보내주도록.
		File hlsFile = Paths.get(ROOT_FILE_PATH, quality, fileName).toFile();
		Resource resource = new FileSystemResource(hlsFile);
		String contentType = fileName.endsWith(".m3u8") ?
			"application/vnd.apple.mpegurl" :
			"video/mp2t";


		if (!resource.exists()) {
			return ResponseEntity.notFound().build();
		}

		HttpHeaders headers = new HttpHeaders();
		// headers.add("Cache-Control", "no-cache, no-store, must-revalidate"); // 캐싱 방지
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);

		return ResponseEntity.ok()
			.headers(headers)
			.body(resource);
	}
}
