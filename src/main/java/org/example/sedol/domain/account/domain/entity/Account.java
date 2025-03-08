package org.example.sedol.domain.account.domain.entity;

import java.time.LocalDateTime;

import org.example.sedol.common.config.jpaConfig.BaseTimeEntity;
import org.example.sedol.domain.account.dto.request.SignUpRequestDTO;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseTimeEntity {
	public Account(SignUpRequestDTO signUpRequestDTO) {
		this.userId = signUpRequestDTO.getUserId();
		this.nickName = signUpRequestDTO.getNickName();
		this.userName = signUpRequestDTO.getUserName();
		this.passWord = signUpRequestDTO.getPassWord();
		this.birthDate = signUpRequestDTO.getBirthDate();
		this.sex = signUpRequestDTO.getSex();
		this.userEmail = signUpRequestDTO.getUserEmail();
		this.phoneNumber = signUpRequestDTO.getPhoneNumber();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(unique = true)
	private String userId;

	private String nickName;
	private String userName;
	private String passWord;
	private LocalDateTime birthDate;
	private String sex;
	private String userEmail;
	private String phoneNumber;
	private Boolean isAdult;
}
