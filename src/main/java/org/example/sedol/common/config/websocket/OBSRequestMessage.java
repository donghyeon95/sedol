package org.example.sedol.common.config.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OBSRequestMessage {
	private int op; // 작업 코드
	private Data d; // 요청 데이터

	public OBSRequestMessage() {}

	public OBSRequestMessage(int op, Data d) {
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
		private String authentication; // 요청 시에는 String 형태

		public Data() {}

		public Data(String requestType, String requestId) {
			this.requestType = requestType;
			this.requestId = requestId;
		}

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

		public String getAuthentication() {
			return authentication;
		}

		public void setAuthentication(String authentication) {
			this.authentication = authentication;
		}
	}
}
