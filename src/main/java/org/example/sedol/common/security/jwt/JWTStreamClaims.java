package org.example.sedol.common.security.jwt;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.example.sedol.domain.VO.StreamServiceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class JWTStreamClaims implements JWTClaim{
	private String userId;
	private String nickName;
	private String streamKey;

	@Builder.Default
	private Map<StreamServiceType, Boolean> serviceTypes = createDefault();

	public Map<StreamServiceType, Boolean> createDefault() {
		Map<StreamServiceType, Boolean> serviceTypes = new EnumMap<>(StreamServiceType.class);

		for (StreamServiceType s: StreamServiceType.values()) {
			serviceTypes.put(s, true);
		}

		return serviceTypes;
	}
}
