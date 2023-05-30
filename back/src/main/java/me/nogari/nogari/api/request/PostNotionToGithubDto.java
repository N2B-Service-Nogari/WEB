package me.nogari.nogari.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostNotionToGithubDto {
	// AWS Lambda
	private String type;

	// Github
	private String githubId;
	private String repository;
	private String categoryName;
	private String status;
	private String requestLink;
	private String filename;
}
