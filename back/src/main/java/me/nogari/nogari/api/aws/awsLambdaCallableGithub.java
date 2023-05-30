package me.nogari.nogari.api.aws;
import me.nogari.nogari.api.request.TokenDecryptDto;
import okhttp3.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.nogari.nogari.api.request.PostNotionToGithubDto;
import me.nogari.nogari.entity.Github;
import me.nogari.nogari.entity.Member;

public class awsLambdaCallableGithub implements Callable<LambdaResponse> {
	private LambdaCallFunction lambdaCallFunction;

	private int index;
	private PostNotionToGithubDto post;
	private Github github;
	private TokenDecryptDto tokenDecryptDto;

	public awsLambdaCallableGithub(int index, PostNotionToGithubDto post, Github github, TokenDecryptDto tokenDecryptDto) {
		this.index = index;
		this.post = post;
		this.github = github;
		this.tokenDecryptDto = tokenDecryptDto;
	}

	public LambdaResponse githubAwsLambda(int index, PostNotionToGithubDto post, Github github,
		TokenDecryptDto tokenDecryptDto) throws Exception {
		LambdaResponse lambdaResponse = new LambdaResponse(index, github);

		// STEP2-1. AWS Lambda와 통신하는 과정
		Map<String, Object> data = awsLambdaResponse(post, tokenDecryptDto);
		String title = (String)data.get("title"); // github에 게시될 게시글 제목
		String content = (String)data.get("content"); // github에 게시될 게시글 내용

		//filePath 생성
		String fileDate = null;
		if (github.getStatus().equals("수정요청") || github.getStatus().equals("수정실패")) {
			String filePath =
				post.getGithubId() + "/" + post.getRepository() + "/contents/" + post.getCategoryName() + "/"
					+ post.getFilename();
			System.out.println("githubAwsLambda 수정요청 filePath : " + filePath);
			lambdaResponse.setFilePath(filePath);
		} else {
			//파일명 중복 방지를 위한 현재 날짜, 시간
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			Date nowDate = new Date();
			fileDate = simpleDateFormat.format(nowDate);

			String filePath =
				post.getGithubId() + "/" + post.getRepository() + "/contents/" + post.getCategoryName() + "/" + title
					+ "_" + fileDate + "." + post.getType();
			// String filePath =
			// 	member.getGithubId() + "/" + post.getRepository() + "/contents/" + post.getCategoryName() + "/" + title
			// 		+ "_" + fileDate + ".png";
			System.out.println("githubAwsLambda filePath : " + filePath);
			lambdaResponse.setFilePath(filePath);
		}
		// STEP2-2. Tistory API를 이용하여 Tistory 포스팅을 진행하기 위해 HttpEntity를 구성한다.
		try {
			HttpEntity<Map<String, String>> httpGithubRequest = getHttpLambdaRequest(title, content, github,
				post, tokenDecryptDto, fileDate);
			lambdaResponse.setGithubRequest(httpGithubRequest);
		}catch (Exception e){
			HttpHeaders headers = new HttpHeaders();
			headers.add("Accept", "ID_FAIL");
			Map<String, String> body = new LinkedHashMap<>();
			HttpEntity<Map<String, String>> httpGithubRequest = new HttpEntity<>(body, headers);
			lambdaResponse.setGithubRequest(httpGithubRequest);
		}
		return lambdaResponse;
	}

	private static byte[] downloadImage(String imageUrl) throws IOException {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(imageUrl).build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().bytes();
		}
	}
	public String uploadImageToGithub(
		String imageUrl, String title, Github github,
		PostNotionToGithubDto post, TokenDecryptDto tokenDecryptDto, String fileDate, int idx, HttpHeaders headers ) throws IOException {
		byte[] imageBytes = downloadImage(imageUrl);
		ResponseEntity<Map<String, Object>> response = null;
		Map<String, Object> responseBody = new HashMap<>();
		String githubRequestURL = "https://api.github.com/repos/";
		RestTemplate rt = new RestTemplate();
		String filePath = "";
		String updateImageSha = null;
		if (github.getStatus().equals("수정요청") || github.getStatus().equals("수정실패")) {
			String filename = post.getFilename();
			System.out.println("line98 filename : "  + filename);
			String[] split = filename.split("\\.");
			String splitFilename = split[0];
			filePath =
				post.getGithubId() + "/" + post.getRepository() + "/contents/" + post.getCategoryName() + "/imgs/"
					+ splitFilename + "_" + idx + ".png";;

			//수정 요청일 때 이미 업로드되어있던 이미지의 sha 가져오기
			HttpEntity<String> entity = new HttpEntity<>(null, headers);
			try {
				response = rt.exchange(
					githubRequestURL + filePath,
					HttpMethod.GET,
					entity,
					new ParameterizedTypeReference<Map<String, Object>>() {
					}
				);
				responseBody= response.getBody();
				if (responseBody != null) {
					updateImageSha = (String) responseBody.get("sha");
				} else {
					System.out.println("line 124 : 이미지 file의 sha 찾지 못함");
				}
			} catch (HttpClientErrorException.NotFound e){
				// 이미지 파일이 없는 경우에 대한 처리.
				// response와 responseBody는 null 상태이므로 여기서는 다루지 않는다.
				System.out.println("line 118 : 이미지 file 없음");
			}
		}
		else {
			filePath =
				post.getGithubId() + "/" + post.getRepository() + "/contents/" + post.getCategoryName() + "/imgs/"
					+ title + "_" + fileDate + "_" + idx + ".png";
		}

		String fileContent = Base64.getEncoder().encodeToString(imageBytes);

		Map<String, String> body = new LinkedHashMap<>();
		body.put("message", title + "_img_" + idx);
		body.put("content", fileContent);
		if(updateImageSha!=null){
			body.put("sha", updateImageSha);
		}

		HttpEntity<Map<String, String>> githubImgHttpRequest = new HttpEntity<>(body, headers);

		response = rt.exchange(
			// "https://api.github.com/repos/encoreKwang/PullRequestTest/contents" + filePath,
			githubRequestURL + filePath,
			HttpMethod.PUT,
			githubImgHttpRequest,
			new ParameterizedTypeReference<Map<String, Object>>() {}
		);

		responseBody= response.getBody();
		Map<String, String> content = (Map<String, String>) responseBody.get("content");
		String newImageUrl = content.get("download_url");

		return newImageUrl;
	}

	// STEP2-1. AWS Lambda와 통신하여 JSON 응답값을 받아오는 메소드
	public Map<String, Object> awsLambdaResponse(PostNotionToGithubDto post, TokenDecryptDto tokenDecryptDto) throws Exception {
		String response = ""; // Tistory에 발행할 Notion 페이지를 md로 파싱한 JSON 응답값

		// STEP2-1-1. AWS Lambda와 통신하는 과정
		lambdaCallFunction = new LambdaCallFunction(
			tokenDecryptDto.getNotionToken(),
			tokenDecryptDto.getGithubToken(),
			post.getRepository(),
			post.getRequestLink(),
			post.getType()
		);
		response = lambdaCallFunction.gitPost();

		// STEP2-1-2. JSON 데이터로 구성된 파싱 결과를 형식에 맞게 읽어들인다.
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> data = objectMapper.readValue(response, Map.class);
		return data;
	}

	// STEP2-2. Github API를 이용하여 Github 포스팅을 진행하기 위해 HttpEntity를 구성하는 메소드
	public HttpEntity<Map<String, String>> getHttpLambdaRequest(
		String title, String content, Github github, PostNotionToGithubDto post, TokenDecryptDto tokenDecryptDto, String fileDate) throws IOException {

		HttpHeaders headers = new HttpHeaders();

		headers.add("Accept", "application/vnd.github+json");
		headers.add("Authorization", "Bearer " + tokenDecryptDto.getGithubToken());
		headers.add("X-GitHub-Api-Version", "2022-11-28");
		headers.setContentType(MediaType.APPLICATION_JSON);

		Pattern imagePattern = Pattern.compile("!\\[.*?\\]\\((https://s3\\.us-west-2\\.amazonaws\\.com/secure\\.notion-static\\.com/[^)]+)\\)");
		StringBuilder newPostContent = new StringBuilder();
		Matcher matcher = imagePattern.matcher(content);

		int idx = 1;
		while (matcher.find()) {
			String imageUrl = matcher.group(1);
			String newImageUrl = uploadImageToGithub(imageUrl, title, github, post, tokenDecryptDto, fileDate, idx++, headers);
			matcher.appendReplacement(newPostContent, "![](" + newImageUrl + ")");
		}
		matcher.appendTail(newPostContent);

		content = newPostContent.toString();
		String fileContent = Base64.getEncoder().encodeToString(content.getBytes());

		Map<String, String> body = new LinkedHashMap<>();
		body.put("message", title);
		body.put("content", fileContent);
		if (github.getStatus().equals("수정요청")) {
			body.put("sha", github.getSha());
		}
		HttpEntity<Map<String, String>> httpLambdaRequest = new HttpEntity<>(body, headers);

		return httpLambdaRequest;
	}

	@Override
	public LambdaResponse call() throws Exception {
		return githubAwsLambda(index, post, github, tokenDecryptDto);
	}
}
