package org.example.sedol.domain.stremming.DTO;

import java.util.List;

import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StreamRoomRequestDTO {
	@NotBlank
	@Size(min=1, max=75, message="제목은 1~75 사이의 길이여야 합니다.")
	@Pattern(
		regexp = "^[가-힣a-zA-Z0-9\\[\\]{}_!?.,\\-()&+=%\\p{So} ]+$",
		message = "제목은 한글, 영어, 숫자, 특수문자([]{} _!?.,-()&+=%) 및 이모티콘만 포함 가능합니다."
	)
	private String title;

	@NotBlank
	private String category;

	@Size(min=0, max = 5)
	private List<String> tags;

	@NotBlank
	private Boolean ageOpt;
	@NotBlank
	private Boolean adultOpt;
	@NotBlank
	private Boolean exposedOpt;
	@NotBlank
	private Boolean adOpt;
	@NotBlank
	private Boolean passwordOpt;

	private Boolean password;

	@NotBlank
	private String streamKey;

	@NotBlank
	private String userId;
}
