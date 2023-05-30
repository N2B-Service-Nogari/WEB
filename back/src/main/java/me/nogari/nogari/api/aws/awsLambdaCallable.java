package me.nogari.nogari.api.aws;

import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.nogari.nogari.api.request.PostNotionToTistoryDto;
import me.nogari.nogari.api.request.TokenDecryptDto;
import me.nogari.nogari.entity.Member;
import me.nogari.nogari.entity.Tistory;

public class awsLambdaCallable implements Callable<LambdaResponse> {
	private LambdaCallFunction lambdaCallFunction;

	private int index;
	private PostNotionToTistoryDto post;
	private Tistory tistory;

	private TokenDecryptDto tokenDecryptDto;

	public awsLambdaCallable(int index, PostNotionToTistoryDto post, Tistory tistory, TokenDecryptDto tokenDecryptDto) {
		this.index = index;
		this.post = post;
		this.tistory = tistory;
		this.tokenDecryptDto = tokenDecryptDto;
	}

	public LambdaResponse tistoryAwsLambda(int index, PostNotionToTistoryDto post, Tistory tistory,
		TokenDecryptDto tokenDecryptDto) throws Exception {
		LambdaResponse lambdaResponse = new LambdaResponse(index, tistory);

		// STEP2-1. AWS Lambda와 통신하는 과정
		Map<String, Object> data = awsLambdaResponse(post, tokenDecryptDto);
		String title = (String)data.get("title"); // Tistory에 게시될 게시글 제목
		String content = (String)data.get("content"); // Tistory에 게시될 게시글 내용

		// STEP2-2. Tistory API를 이용하여 Tistory 포스팅을 진행하기 위해 HttpEntity를 구성한다.
		HttpEntity<MultiValueMap<String, String>> httpTistoryRequest = getHttpLambdaRequest(title, content, tistory,
			post, tokenDecryptDto);
		lambdaResponse.setTistoryRequest(httpTistoryRequest);

		return lambdaResponse;
	}

	// STEP2-1. AWS Lambda와 통신하여 JSON 응답값을 받아오는 메소드
	public Map<String, Object> awsLambdaResponse(PostNotionToTistoryDto post, TokenDecryptDto tokenDecryptDto) throws Exception {
		String response = ""; // Tistory에 발행할 Notion 페이지를 html로 파싱한 JSON 응답값

		// STEP2-1-1. AWS Lambda와 통신하는 과정
		lambdaCallFunction = new LambdaCallFunction(
			tokenDecryptDto.getNotionToken(),
			tokenDecryptDto.getTistoryToken(),
			post.getBlogName(),
			post.getRequestLink(),
			post.getType()
		);
		response = lambdaCallFunction.post();

		// STEP2-1-2. JSON 데이터로 구성된 파싱 결과를 형식에 맞게 읽어들인다.
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> data = objectMapper.readValue(response, Map.class);

		return data;
	}

	// STEP2-2. Tistory API를 이용하여 Tistory 포스팅을 진행하기 위해 HttpEntity를 구성하는 메소드
	public HttpEntity<MultiValueMap<String, String>> getHttpLambdaRequest(
		String title, String content, Tistory tistory, PostNotionToTistoryDto post, TokenDecryptDto tokenDecryptDto) {

		HttpHeaders headers = new HttpHeaders();

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("access_token", tokenDecryptDto.getTistoryToken());
		params.add("output", "");
		params.add("blogName", post.getBlogName()); // 블로그 이름
		params.add("title", title); // 글 제목
		params.add("content", content); // 글 내용
		params.add("visibility", Integer.toString(post.getVisibility())); // 발행 상태 : 기본값(발행)
		params.add("category", post.getCategoryName()); // 카테고리 아이디
		params.add("published", ""); // 발행 시간
		params.add("slogan", ""); // 문자 주소
		params.add("tag", post.getTagList()); // 태그 리스트(','로 구분)
		params.add("acceptComment", "1"); // 댓글 허용 :기본값(댓글 허용)
		params.add("password", ""); // 보호글 비밀번호

		if (tistory.getStatus().equals("수정요청")) {
			params.add("postId", Long.toString(tistory.getPostId()));
		}

		HttpEntity<MultiValueMap<String, String>> httpLambdaRequest = new HttpEntity<>(params, headers);
		return httpLambdaRequest;
	}

	@Override
	public LambdaResponse call() throws Exception {
		return tistoryAwsLambda(index, post, tistory, tokenDecryptDto);
	}
}
