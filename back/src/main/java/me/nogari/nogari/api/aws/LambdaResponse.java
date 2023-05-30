package me.nogari.nogari.api.aws;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

import lombok.Builder;
import me.nogari.nogari.entity.Github;
import me.nogari.nogari.entity.Tistory;

public class LambdaResponse {
	private int index;
	private Tistory tistory;
	private HttpEntity<MultiValueMap<String, String>> tistoryRequest;
	private Github github;
	private HttpEntity<Map<String, String>> githubRequest;
	private String filePath; //깃허브 포스팅 path


	public LambdaResponse() {
		this.index = 0;
		this.tistory = null;
		this.tistoryRequest = null;
		this.github = null;
		this.githubRequest = null;
		this.filePath = null;
	}

	public LambdaResponse(int index, Tistory tistory) {
		this.index = index;
		this.tistory = tistory;
		this.tistoryRequest = null;
		this.github = null;
		this.githubRequest = null;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public LambdaResponse(int index, Github github) {
		this.index = index;
		this.github = github;
		this.githubRequest = null;
		this.tistory = null;
		this.tistoryRequest = null;
	}

	public LambdaResponse(int index, Tistory tistory, HttpEntity<MultiValueMap<String, String>> tistoryRequest) {
		this.index = index;
		this.tistory = tistory;
		this.tistoryRequest = tistoryRequest;
	}

	public Github getGithub() {
		return github;
	}

	public void setGithub(Github github) {
		this.github = github;
	}

	public HttpEntity<Map<String, String>> getGithubRequest() {
		return githubRequest;
	}

	public void setGithubRequest(
		HttpEntity<Map<String, String>> githubRequest) {
		this.githubRequest = githubRequest;
	}

	public Tistory getTistory() {
		return tistory;
	}

	public void setTistory(Tistory tistory) {
		this.tistory = tistory;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public HttpEntity<MultiValueMap<String, String>> getTistoryRequest() {
		return tistoryRequest;
	}

	public void setTistoryRequest(HttpEntity<MultiValueMap<String, String>> tistoryRequest) {
		this.tistoryRequest = tistoryRequest;
	}
}
