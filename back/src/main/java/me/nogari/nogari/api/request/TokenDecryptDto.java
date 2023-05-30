package me.nogari.nogari.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDecryptDto {
	private String notionToken;
	private String tistoryToken;
	private String githubToken;
}
