package org.example.sedol.domain.stremming.domain.entitiy;

import java.time.LocalDateTime;

import org.example.sedol.common.config.jpaConfig.BaseTimeEntity;
import org.example.sedol.domain.account.domain.entity.Account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class StreamUserCountLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private LocalDateTime timeStamp;
	private long userCnt;

	@ManyToOne
	private StreamRoom streamRoom;
}
