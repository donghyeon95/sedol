package org.example.sedol.domain.stremming.domain.entitiy;

import java.time.LocalDateTime;

import org.example.sedol.common.config.jpaConfig.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class StreamTitleLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String oldName;
	private String newName;

	// 변경된 시간 TimeLine
	private LocalDateTime changedAt;

	@ManyToOne
	private StreamRoom streamRoom;
}
