package org.example.sedol.domain.stremming.domain.entitiy;

import java.time.LocalDateTime;
import java.util.List;

import org.example.sedol.common.config.jpaConfig.BaseTimeEntity;
import org.example.sedol.domain.account.domain.entity.Account;
import org.hibernate.mapping.ToOne;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreamRoom extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private String title;

	@ElementCollection
	private List<String> tags;

	@Column(nullable = false)
	private Boolean adultOpt;
	@Column(nullable = false)
	private Boolean ageOpt;
	@Column(nullable = false)
	private Boolean exposedOPt;
	@Column(nullable = false)
	private Boolean adOpt;

	@Column(nullable = false)
	private Boolean passwordOpt;
	private String password;
	private LocalDateTime passwordUpdated;

	private String streamKey;

	// 방송 시작 시간
	private LocalDateTime StreamStartTime;

	// 방송 종료 시간
	private LocalDateTime streamEndTime;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = true)
	private Category category;

	private Boolean isLive;

	// 스트리머 정보
	@OneToOne
	private Account account;


	// 현재 시청자 수 ( 최종 시청자 수 )
	// -> 로그 성 데이터로 쌓아야할 필요가 있을 수도 있고,
	// ~~ 몇분에 몇명의 시청자가 있는 지 확인을 위해 (초단위로 집계를 하는 게 좋을 듯)
	private long viewerCnt;

	// 좋아요 수
	private long likeCnt;

	// 해상도
	private String resolution;

	// 화질
	private int videoQuality;

	// 섬네일 주소
	private String thumbnail;

	// Stream URL 주소 (m3u8 파일)
	// private String streamURL;

	// 방송 설정 (방송/채팅 차단 목록 등)
	// 이름 변경 시 => 몇시 몇분에 이름을 변경했다 정보

}


// 해당 설정의 경우 => redis에 저장을 해두었다가
// Stream이 시작이 되면 그때, 데이터베이스에 등록