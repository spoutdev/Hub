package models.oauth;

import lombok.Getter;

public class OAuthResult {
	@Getter
	private final boolean success;
	@Getter
	private final String token;
	@Getter
	private final String token2;

	public OAuthResult(boolean success, String token) {
		this(success, token, null);
	}

	public OAuthResult(boolean success, String token, String token2) {
		this.success = success;
		this.token = token;
		this.token2 = token2;
	}
}
