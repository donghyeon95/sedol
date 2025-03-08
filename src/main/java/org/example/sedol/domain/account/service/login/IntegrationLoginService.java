package org.example.sedol.domain.account.service.login;

import org.example.sedol.common.security.jwt.JWTAccountClaims;
import org.example.sedol.common.security.jwt.JWTProvider;
import org.example.sedol.domain.account.domain.entity.Account;
import org.example.sedol.domain.account.domain.repository.AccountRepository;
import org.example.sedol.domain.account.dto.request.LoginRequestDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IntegrationLoginService implements Login {
	private AccountRepository accountRepository;
	private JWTProvider jwtProvider;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public IntegrationLoginService(AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
		JWTProvider jwtProvider) {
		this.accountRepository = accountRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.jwtProvider = jwtProvider;
	}

	@Transactional
	public String login(LoginRequestDTO loginRequestDTO) throws Exception {
		// 해당하는 ID가 있는 지 확인
		Account account = accountRepository.findByUserId(loginRequestDTO.getUserId())
			.orElseThrow();

		// password가 같은 지 확인
		if (!validatePassword(account.getPassWord(), loginRequestDTO.getPassWord())) {
			log.info("Invalid Password");
			throw new Exception("Invalid Password");
		}

		JWTAccountClaims jwtAccountClaims = JWTAccountClaims.builder()
			.userId(account.getUserId())
			.nickName(account.getNickName())
			.userName(account.getUserName())
			.sex(account.getSex())
			.build();

		// jwt Token 생성
		return jwtProvider.createToken(jwtAccountClaims);
	}

	private boolean validatePassword (String encryptedPassword, String rawPassword) {
		return encryptedPassword.equals(bCryptPasswordEncoder.encode(rawPassword));
	}

}
