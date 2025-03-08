package org.example.sedol.domain.account.service.signup;

import org.example.sedol.domain.account.dto.request.SignUpRequestDTO;
import org.example.sedol.domain.account.dto.response.SignUpResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class SocialSignUpService implements SignUp {
	@Override
	public SignUpResponseDTO signUp(String id, SignUpRequestDTO signUpRequestDTO) {
		return null;
	}
}
