package me.nogari.nogari.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAccessTokenResponse {
	private String access_token;
	private String token_type;

	private String refresh_token;

	private String expires_in;

	private String scope;

	private String refresh_token_expires_in;

}
