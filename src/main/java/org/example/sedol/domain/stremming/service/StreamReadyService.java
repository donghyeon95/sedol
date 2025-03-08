package org.example.sedol.domain.stremming.service;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.example.sedol.domain.account.domain.entity.Account;
import org.example.sedol.domain.account.domain.repository.AccountRepository;
import org.example.sedol.domain.stremming.DTO.StreamRoomRequestDTO;
import org.example.sedol.domain.stremming.domain.entitiy.StreamRoom;
import org.example.sedol.domain.stremming.domain.repositoty.StreamRoomRepository;
import org.example.sedol.domain.vod.domain.entity.Vod;
import org.example.sedol.domain.vod.domain.repository.VodRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StreamReadyService {
	@Value("${stream.api.key}")
	private String STREAM_API_KEY;
	private final StreamRoomRepository streamRoomRepository;
	private final AccountRepository accountRepository;
	private final StreamKeyProvider streamKeyProvider;
	private final ThumbnailService thumbnailService;
	// vod 레포지토리에서 하는 게 좋겠지? ( 이런 거 class 설계 하는 걸 확인 )
	private final VodRepository vodRepository;

	// 추후 FileStorage bean에서 가져올 수 있도록 ( s3, local 다 사용하게 )
	private final String thumbnailDomain = "http://localhost:8000/stream/thumbnail?streamKey=";


	// OBS 스트리밍 시작
	@Transactional
	public void startStream(String streamKey) throws Exception {
		// 이미 스트리밍 중인 Stream Key인지 확인
		StreamRoom streamRoom = streamRoomRepository.findByStreamKey(streamKey).orElseThrow();
		// TODO 만약 재접속인 경우는 어떻게 해야 될까?
		// TODO 제일 좋은 것은 이전의 방송은 revert하고 신규 방송만 허용 -> 이미 하고 있는 방송을 revert 할 수 있나?
		// 이것도  ffmpeg를 활용해서 가능  ( 트랜스 코딩 nginx에 삭제 요청을 하고 해당 응답을 받아서 활용)
		// if (Boolean.TRUE.equals(streamRoom.getIsLive())) throw new RuntimeException("이미 방송 중입니다.");

		// 스트리밍 시작 시간 확인
		streamRoom.setIsLive(true);
		streamRoom.setStreamStartTime(LocalDateTime.now());
		// TODO Stream 시작 알림 보내기 ()

	}

	// OBS 스트리밍 중지
	public void stopStream(String streamKey) {
		// streamKey 삭제
		// streamKeyProvider.deletKey(streamKey);

		// stream Room 정보 업데이트
		StreamRoom streamRoom = streamRoomRepository.findByStreamKey(streamKey).orElseThrow();
		streamRoom.setStreamKey(null);
		streamRoom.setIsLive(false);
		streamRoom.setStreamEndTime(LocalDateTime.now());
		streamRoomRepository.save(streamRoom);

		// 스트리밍 정지 신호 보내기 => 현재 보고 있는 사용자들에 대해
		// m3u8 활용하면 될 듯 -> ffmpeg에서 처리 완료 (ffmpeg 끝나면 #EXT-X-ENDLIST를 자동으로 찍도록 처리)
		// 근데 예외 상황으로 OBS는 종료 됐는 데, 이 함수가 안불려서 디비는 방송 중이라면?? ( 강제 종료라던지 하는 상황에서는 어떻게?, 서버 오류 등의 상황에서는 어떻게 처리? )
		// Batch로 ffmpeg 상태를 확인하거나, 하는 등의 방안이 필요 => 이거는 gRPC를 활용해서 트랜스 코딩 서버랑 통신

		// 다시 보기 생성
		// local Storage에 넣거나 s3로 넣거나
		// 넣을 때, 분산해서 넣는 것을 해보자.
		// 업로드 속도 향상 => s3 multipart upload 하는 걸로
		// 원래는 CDN에도 넣어야 한다. (알고리즘에 따라 -> 최신 순//조회  순)
		String source = "";
		// 재생 시간은 => 정확하게 영상에서 추출해야 한다.
		long playTime = streamRoom.getStreamEndTime().toEpochSecond(ZoneOffset.UTC) - streamRoom.getStreamStartTime()
			.toEpochSecond(ZoneOffset.UTC);

		// 영상 VOD 객체를 생성 => 옮기도록 처리
		Vod vod = Vod.builder()
			.title(streamRoom.getTitle())
			.playTime(playTime)
			.category(streamRoom.getCategory())
			.thumbnail(streamRoom.getThumbnail())
			.account(streamRoom.getAccount())
			.source(source)
			.build();

		vodRepository.save(vod);

		// 메타데이터 업데이트
	}

	// 방송 StreamRoom 생성 하기 (+ 방송 정보 만들기)
	@Transactional
	public StreamRoom generateStream(String userId) {
		// user 정보, stream 방 정보를 받아서 stream 키를 생성
		// 이미 생성이 된 방이 있는 지 확인
		Optional<StreamRoom> streamRoom = streamRoomRepository.findByAccount_UserId(userId);

		if (streamRoom.isPresent()) {
			if (streamRoom.get().getStreamKey() == null) streamRoom.get().setStreamKey(streamKeyProvider.generateKey());
			streamRoomRepository.save(streamRoom.get());

			return streamRoom.get();
		} else {
			Account account = accountRepository.findByUserId(userId).orElseThrow();
			String streamKey = streamKeyProvider.generateKey();

			StreamRoom newStreamRoom = StreamRoom.builder()
				.title("")
				.streamKey(streamKey)
				.account(account)
				.adOpt(false)
				.adultOpt(false)
				.ageOpt(false)
				.passwordOpt(false)
				.passwordUpdated(LocalDateTime.now())
				.exposedOPt(false)
				.thumbnail("http://localhost:8000/stream/thumbnail?streamKey=" + streamKey)
				.isLive(false)
				.build();

			streamRoomRepository.save(newStreamRoom);
			return newStreamRoom;
		}
	}

	// Stream Key (재)생성 하기
	@Transactional
	public String generateStreamKey(String userId) {
		// 키 생성 하기
		String streamKey = streamKeyProvider.generateKey();

		// 생성된 룸이 있다면 => 룸 정보를 수정
		Optional<StreamRoom> streamRoom = streamRoomRepository.findByAccount_UserId(userId);
		if (streamRoom.isPresent()) {
			streamRoom.get().setStreamKey(streamKey);
			streamRoom.get().setThumbnail(thumbnailDomain + streamKey);
			streamRoomRepository.save(streamRoom.get());
		}

		return streamKey;
	}

	public ResponseEntity<Resource> getThumnail(String streamKey) throws MalformedURLException {
		Resource resource = thumbnailService.getFile(streamKey);

		// Content-type 반환
		String contentType = thumbnailService.getContentType(resource);
		String encodedFileName = thumbnailService.getEncodedFileName(resource);

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
			.body(resource);
	}

	// 방송 정보 수정하기
	public StreamRoom modifyStreamInfo(StreamRoomRequestDTO streamRoomRequestDTO) {
		// 수정 항목 중
		// 수정 항목 별로 로직을 사용한다.
		// 방송 이름 / 태그 등 -> 시간 초를 구분
		// 다시보기 재생 시 해당 초를 넣어주거나
		// m3u8 파일에 넣는 것도 방법.
		// 정확한 영상의 시간을 어떻게 가져와야 할까?

		//방송 이름 변경을 했을 경우, StreamTitleLog에 저장

		return null;

	}

	// 유저 카운트 로그 갱신하기
	public void logUserCount() {
	}

	// 스트리밍 썸네일 갱신하기
	// 썸네일의 경우 ffmpeg에서 처리를 해주면 된다.

}
