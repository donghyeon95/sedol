package org.example.sedol.common.config.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OBSResponseMessage {
	private int op; // 작업 코드
	private Data d; // 응답 데이터

	public OBSResponseMessage() {}

	public OBSResponseMessage(int op, Data d) {
		this.op = op;
		this.d = d;
	}

	public int getOp() {
		return op;
	}

	public void setOp(int op) {
		this.op = op;
	}

	public Data getD() {
		return d;
	}

	public void setD(Data d) {
		this.d = d;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Data {
		@JsonProperty("rpcVersion")
		private Integer rpcVersion = 1;
		private String requestType;
		private String requestId;
		private Authentication authentication;
		private String eventName; // 이벤트 이름
		private Object eventData; // 이벤트 세부 데이터

		public Integer getRpcVersion() {
			return rpcVersion;
		}

		public void setRpcVersion(Integer rpcVersion) {
			this.rpcVersion = rpcVersion;
		}

		public String getRequestType() {
			return requestType;
		}

		public void setRequestType(String requestType) {
			this.requestType = requestType;
		}

		public String getRequestId() {
			return requestId;
		}

		public void setRequestId(String requestId) {
			this.requestId = requestId;
		}

		public Authentication getAuthentication() {
			return authentication;
		}

		public void setAuthentication(Authentication authentication) {
			this.authentication = authentication;
		}

		public String getEventName() {
			return eventName;
		}

		public void setEventName(String eventName) {
			this.eventName = eventName;
		}

		public Object getEventData() {
			return eventData;
		}

		public void setEventData(Object eventData) {
			this.eventData = eventData;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Authentication {
		private String challenge;
		private String salt;

		public String getChallenge() {
			return challenge;
		}

		public void setChallenge(String challenge) {
			this.challenge = challenge;
		}

		public String getSalt() {
			return salt;
		}

		public void setSalt(String salt) {
			this.salt = salt;
		}
	}
}
