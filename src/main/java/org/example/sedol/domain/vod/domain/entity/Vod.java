package org.example.sedol.domain.vod.domain.entity;

import java.util.List;

import org.example.sedol.common.config.jpaConfig.BaseTimeEntity;
import org.example.sedol.domain.account.domain.entity.Account;
import org.example.sedol.domain.stremming.domain.entitiy.Category;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.mapping.ToOne;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vod extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// 제목
	private String title;

	// 조회수
	@ColumnDefault("0")
	@Builder.Default
	private long viewerCnt = 0;

	// 재생 시간
	private long playTime;

	// 섬네일 위치
	private String thumbnail;

	// 영상 소스 위치
	private String source;

	// 영상 공개 여부
	private Boolean hide;

	@ManyToOne
	private Category category;

	@ElementCollection
	private List<String> tags;

	// 올린 사람
	@ManyToOne
	private Account account;



	// 댓글

	// 채팅

}
