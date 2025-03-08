package org.example.sedol.domain.account.dto.request;

import java.time.LocalDateTime;

import org.example.sedol.domain.account.dto.validator.Password;
import org.example.sedol.domain.account.dto.validator.UniqueUserId;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequestDTO {
	@NotNull(message = "닉네임은 필수 입력값입니다.")
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
	// 중복 검사
	private String nickName;

	@NotNull(message = "아이디는 필수 입력값입니다.")
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{6,10}$", message = "아이디는 특수문자를 제외한 6~10자리여야 합니다.")
	@UniqueUserId
	private String userId;

	@NotNull(message = "비밀번호는 필수 입력값입니다.")
	@Size(min = 8, max = 30, message = "비밀번호는 최소 8자 이상, 최대 30자 이하여야 합니다.")
	@Pattern(regexp = "^[^\\s]+$", message = "비밀번호는 공백을 포함할 수 없습니다.")
	@Password
	private String passWord;

	@Past(message = "생일은 현재보다 과거여야 합니다.")
	@NotNull(message = "생일은 필수 입력값입니다.")
	private LocalDateTime birthDate;

	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
	@NotNull(message = "이메일은 필수 입력값입니다.")
	private String userEmail;

	@NotNull(message = "성별은 필수 입력값입니다.")
	private String sex;

	@NotBlank(message = "이름은 필수 입력값입니다.")
	@Pattern(regexp = "^[ㄱ-ㅎ가-힣]{2,5}$", message = "이름은 한글 2~5자리여야 합니다.")
	private String userName;

	@NotBlank(message = "전화번호는 필수 입력값입니다.")
	@Pattern(regexp = "^010\\d{8}$", message = "전화번호 형식이 올바르지 않습니다.")
	private String phoneNumber;
}
