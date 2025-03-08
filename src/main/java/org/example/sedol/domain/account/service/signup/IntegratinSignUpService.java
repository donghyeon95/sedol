package org.example.sedol.domain.account.service.signup;

import org.example.sedol.domain.account.domain.entity.Account;
import org.example.sedol.domain.account.domain.repository.AccountRepository;
import org.example.sedol.domain.account.dto.request.SignUpRequestDTO;
import org.example.sedol.domain.account.dto.response.SignUpResponseDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class IntegratinSignUpService implements SignUp {

	private AccountRepository accountRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public IntegratinSignUpService(AccountRepository accountRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.accountRepository = accountRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	@Transactional
	public SignUpResponseDTO signUp(String id, SignUpRequestDTO signUpRequestDTO) {

		signUpRequestDTO.setPassWord(bCryptPasswordEncoder.encode(signUpRequestDTO.getPassWord()));
		Account account = new Account(signUpRequestDTO);

		accountRepository.save(account);

		return new SignUpResponseDTO();
	}

}
