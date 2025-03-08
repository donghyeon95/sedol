package org.example.sedol.domain.account.controller;

import org.example.sedol.domain.account.dto.request.LoginRequestDTO;
import org.example.sedol.domain.account.dto.response.LoginResponseDTO;
import org.example.sedol.domain.account.service.login.IntegrationLoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/login", produces = "application/json")
public class LoginController {

	private IntegrationLoginService integrationLoginService;

	@PostMapping()
	public ResponseEntity<LoginResponseDTO> login(@Valid LoginRequestDTO loginRequestDTO) throws Exception {
		String jws = integrationLoginService.login(loginRequestDTO);

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + jws)
			.build();
	}

	@GetMapping()
	public void hi(){
		System.out.println("hihi");
	}
}
