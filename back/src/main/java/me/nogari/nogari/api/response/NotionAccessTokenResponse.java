package me.nogari.nogari.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotionAccessTokenResponse {
	private String access_token;
	private String token_type;
	private int expires_in;
}
