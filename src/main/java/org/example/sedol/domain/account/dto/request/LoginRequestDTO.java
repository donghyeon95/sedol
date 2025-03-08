package org.example.sedol.domain.account.dto.request;

import org.example.sedol.domain.account.dto.validator.Password;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDTO {
	@NotBlank(message = "아이디는 필수 입력값입니다.")
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "아이디 특수문자를 제외한 2~10자리여야 합니다.")
	private String userId;


	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	@Min(value = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
	@Max(value = 30, message = "비밀번호는 최대 30자 이하여야 합니다. ")
	@Password
	private String passWord;
}
