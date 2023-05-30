package me.nogari.nogari.api.aws;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import me.nogari.nogari.api.request.PostNotionToTistoryDto;

public class LambdaCallFunction {
	private String functionURL = "https://bvazzmfbka4sn7abpl5csefrgy0cnqtm.lambda-url.ap-northeast-2.on.aws/"; // AWS Lambda 함수 URL
	private String notionToken; // 회원 Notion Token
	private String url; // 변환할 Notion 페이지 링크
	private String type; // 변환할 형태(.md, .html, .tistory)
	private String tistoryToken;
	private String blogName;
	private String responseString; // AWS Lambda Response

	public LambdaCallFunction(String notionToken, String tistoryToken, String blogName, String url, String type) throws IOException {
		this.notionToken = notionToken;
		this.url = url;
		this.type = type;
		this.tistoryToken = tistoryToken;
		this.blogName = blogName;
	}

	public String post() {
		String requestBody = String.format("{\n"
			+ "  \"notion\": {\n"
			+ "    \"notionToken\": \"" + notionToken + "\",\n"
			+ "    \"page_url\": \""+ url + "\"\n"
			+ "  },\n"
			+ "  \"type\": \"" + type + "\",\n"
			+ "  \"tistory\": {\n"
			+ "    \"access_token\": \""+ this.tistoryToken +"\",\n"
			+ "    \"blogName\": \""+ this.blogName +"\"\n"
			+ "  }\n"
			+ "}"
		);

		// AWS Lambda에 등록된 함수 URL로 POST를 요청한다.
		HttpPost httpPost = new HttpPost(functionURL);

		// 요청 헤더 설정
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Authorization", "Bearer access_token");

		// 요청 바디 설정
		httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

		try (CloseableHttpClient httpClient = HttpClients.createDefault();
			 CloseableHttpResponse response = httpClient.execute(httpPost)
		) {
			HttpEntity entity = response.getEntity();
			// System.out.println("★ response : " + response);
			// System.out.println("★ response.status : " + response.getStatusLine());
			responseString = EntityUtils.toString(entity, "UTF-8");
			// System.out.println("★ responseString : " + responseString);
		} catch(Exception e){
			// System.out.println("★ DataIntegrity~");
			e.printStackTrace();
		}
		return responseString;
	}

	public String gitPost() {
		String requestBody = String.format("{\n"
			+ "  \"notion\": {\n"
			+ "    \"notionToken\": \"" + notionToken + "\",\n"
			+ "    \"page_url\": \""+ url + "\"\n"
			+ "  },\n"
			+ "  \"type\": \"" + type + "\"\n"
			// + "  \"tistory\": {\n"
			// + "    \"access_token\": \""+ this.tistoryToken +"\",\n"
			// + "    \"blogName\": \""+ this.blogName +"\"\n"
			// + "  }\n"
			+ "}"
		);

		// AWS Lambda에 등록된 함수 URL로 POST를 요청한다.
		HttpPost httpPost = new HttpPost(functionURL);

		// 요청 헤더 설정
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Authorization", "Bearer access_token");

		// 요청 바디 설정
		httpPost.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
			 CloseableHttpResponse response = httpClient.execute(httpPost)
		) {
			HttpEntity entity = response.getEntity();
			System.out.println(response);
			System.out.println(response.getStatusLine());
			responseString = EntityUtils.toString(entity, "UTF-8");
			System.out.println(responseString);
		} catch(Exception e){
			e.printStackTrace();
		}
		return responseString;
	}
}
