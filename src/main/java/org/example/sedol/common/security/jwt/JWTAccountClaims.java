package org.example.sedol.common.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JWTAccountClaims implements JWTClaim {
	private String userId;
	private String userName;
	private String nickName;
	private String sex;
}
