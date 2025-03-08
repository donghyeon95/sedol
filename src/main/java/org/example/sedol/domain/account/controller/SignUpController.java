package org.example.sedol.domain.account.controller;

import org.example.sedol.domain.account.dto.request.SignUpRequestDTO;
import org.example.sedol.domain.account.dto.response.SignUpResponseDTO;
import org.example.sedol.domain.account.service.signup.IntegratinSignUpService;
import org.example.sedol.domain.account.service.signup.SocialSignUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/signup", produces = "application/json")
public class SignUpController {
	private IntegratinSignUpService integratinSignUpService;
	private SocialSignUpService socialLoginService;

	public SignUpController(IntegratinSignUpService integratinSignUpService, SocialSignUpService socialLoginService) {
		this.integratinSignUpService = integratinSignUpService;
		this.socialLoginService = socialLoginService;
	}


	// 통합 회원가입
	@PostMapping("/")
	public ResponseEntity<SignUpResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO)  {
		SignUpResponseDTO signUpResponseDTO = integratinSignUpService.signUp("",signUpRequestDTO);
		return ResponseEntity.ok().body(null);
	}

	// 소셜 회원가입

}
