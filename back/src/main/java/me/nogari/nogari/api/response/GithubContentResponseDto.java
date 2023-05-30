package me.nogari.nogari.api.response;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nogari.nogari.entity.Github;
import me.nogari.nogari.entity.Tistory;

@Getter
@NoArgsConstructor
@Builder
public class GithubContentResponseDto {
	private Long githubId;
	private String filename;
	private String repository;
	private String status;
	private String requestLink;
	private String responseLink;
	private String sha; //수정코드
	private String categoryName;
	private String modifiedDate;

	@QueryProjection
	public GithubContentResponseDto(Github github) {
		this.githubId = github.getGithubId();
		this.modifiedDate = github.getModifiedDate();
		this.repository = github.getRepository();
		this.filename = github.getFilename();
		this.categoryName = github.getCategoryName();
		this.status = github.getStatus();
		this.requestLink = github.getRequestLink();
		this.responseLink = github.getResponseLink();
		this.sha = github.getSha();
	}
	@QueryProjection
	public GithubContentResponseDto(Long githubId, String repository, String filename, String categoryName,
		String sha, String status, String requestLink, String responseLink, String modifiedDate) {
		this.githubId = githubId;
		this.modifiedDate = modifiedDate;
		this.repository = repository;
		this.filename = filename;
		this.categoryName = categoryName;
		this.sha = sha;
		this.status = status;
		this.requestLink = requestLink;
		this.responseLink = responseLink;
	}

}
