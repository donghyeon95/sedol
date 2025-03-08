package org.example.sedol.domain.stremming.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.example.sedol.domain.account.domain.entity.Account;
import org.example.sedol.domain.stremming.DTO.StreamRoomRequestDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class StreamKeyProvider {
	// 전역에서 항상 유일해야 한다.
	// 10자리 + 숫자 + 연속적이지 않아야 한다.
	// 시퀀스에서 가져와야
	// 당장은 방법이 없기 때문에 일단 연속적인 숫자 생성하도록 할 것 (현재 활성화된 방송에서 cache hit 하면 되는 거 아닌가?)
	// 지금은 그냥 redis INCR를 사용해서 가져오도록 할 것. 초기 값은 1000001로

	private final long initialNum = 10000000000L;
	private final String REDIS_KEY = "STREAM_KEY";
	private final String STREAM_KEY = "STREAM_KEY_SET";
	private final String STREAM_TTL_KEY = "STREAM_TTL";
	private final int TTL = 10 * 24 * 60 * 60; // 10 Day
	private RedisTemplate<String, Object> redisTemplate;

	public StreamKeyProvider(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// 키 TTL (10 DAY)
	public String generateKey() {
		// FIXME 재생성이 되더라도 계속 값이 증가하는 문제 <안쓴 번호는 안쓴걸로 -> bloom filter?>
		// TODO 비연속적인 key 발급이 가능해야
		Long incrementedValue =  Optional.ofNullable(redisTemplate.opsForValue().increment(REDIS_KEY))
			.orElseThrow(() -> new RuntimeException("❌ Redis INCR 연산 실패: REDIS_KEY가 존재하지 않거나, Redis 연결 문제 발생!"));
		String stream_key = String.valueOf(initialNum + incrementedValue);

		// 스트림 키 확인을 위해 redis set을 사용
		long expiredTime = LocalDateTime.now().plusSeconds(TTL).toEpochSecond(ZoneOffset.UTC);
		redisTemplate.opsForSet().add(STREAM_KEY, stream_key);
		redisTemplate.opsForZSet().add(STREAM_TTL_KEY, stream_key, expiredTime);

		return stream_key;
	}

	// TODO redis랑 postgresql의 transaction 관리
	// 사용하지 않는 key 삭제
	public void deletKey(String streamKey) {
		redisTemplate.opsForZSet().remove(STREAM_TTL_KEY, streamKey);
		redisTemplate.opsForSet().remove(STREAM_KEY, streamKey);
	}

	public boolean validateKey(String streamKey) {
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(STREAM_KEY, streamKey));
	}


	// 스트림 배치를 해야 하나?
	@Scheduled(fixedRate = 60 * 60 * 1000)
	public void removeExpiredKey() {
		System.out.println("key Delete");
		// FIXME 지금 방송 중이라면? -> pub/sub으로 알려서 처리 => 사용자에 알림을 주도록. (방송 자동 종료)
		redisTemplate.opsForZSet()
			.removeRangeByScore(STREAM_TTL_KEY, 0, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
	}
}
