package org.example.sedol.domain.stremming.error;

public enum AuthErrorCode {
	PASSWORD_CHANGED("AUTH_001", "비밀번호가 변경되었습니다. 다시 인증해주세요."),
	TOKEN_EXPIRED("AUTH_002", "세션이 만료되었습니다. 다시 로그인하세요."),
	IP_MISMATCH("AUTH_003", "로그인한 IP 주소가 다릅니다."),
	PERMISSION_DENIED("AUTH_004", "권한이 부족합니다."),
	INVALID_TOKEN("AUTH_005", "잘못된 토큰입니다."),
	ADULT_CHECKED("AUTH_006", "성인 인증이 필요합니다."),
	BAN("AUTH_007", "추방당한 사용자 입니다.");

	private final String code;
	private final String message;

	AuthErrorCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}