package org.example.sedol.domain.account.service.signup;

import org.example.sedol.domain.account.dto.request.SignUpRequestDTO;
import org.example.sedol.domain.account.dto.response.SignUpResponseDTO;

public interface SignUp {
	SignUpResponseDTO signUp(String id, SignUpRequestDTO signUpRequestDTO);
}
