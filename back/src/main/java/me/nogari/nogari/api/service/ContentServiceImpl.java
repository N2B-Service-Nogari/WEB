package me.nogari.nogari.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jasypt.encryption.StringEncryptor;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.nogari.nogari.api.aws.LambdaCallFunction;
import me.nogari.nogari.api.request.PaginationDto;
import me.nogari.nogari.api.aws.LambdaResponse;
import me.nogari.nogari.api.aws.awsLambdaCallable;
import me.nogari.nogari.api.aws.awsLambdaCallableGithub;
import me.nogari.nogari.api.request.PostNotionToGithubDto;

import me.nogari.nogari.api.request.PostNotionToTistoryDto;
import me.nogari.nogari.api.request.TokenDecryptDto;
import me.nogari.nogari.api.response.GithubContentResponseDto;
import me.nogari.nogari.api.response.TistoryCateDto;
import me.nogari.nogari.api.response.TistoryContentResponseDto;
import me.nogari.nogari.config.JasyptConfig;
import me.nogari.nogari.entity.Github;
import me.nogari.nogari.entity.Member;
import me.nogari.nogari.entity.Tistory;
import me.nogari.nogari.repository.GithubRepository;
import me.nogari.nogari.repository.GithubRepositoryCust;
import me.nogari.nogari.repository.MemberRepository;
import me.nogari.nogari.repository.TistoryRepository;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.nogari.nogari.repository.TistoryRepositoryCust;


@Service
@Transactional
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

	// 초기 스레드 수 : 0, 코어 스레드 수 : 0, 최대 스레드 수 : Integer.MAX_VALUE
	// 추가된 스레드가 60초동안 아무 작업을 수행하지 않으면 ThreadPool에서 제거한다.
	private ExecutorService executorService;

	private final MemberRepository memberRepository;

	private final TistoryRepository tistoryRepository;
	private final GithubRepository githubRepository;
	private LambdaCallFunction lambdaCallFunction;

	private final TistoryRepositoryCust tistoryRepositoryCust;
	private final GithubRepositoryCust githubRepositoryCust;

	@Autowired
	private JasyptConfig jasyptConfig;

	@Override
	public List<String> getTistoryBlogName(List<String> blogNameList, Member member) {

		// 토큰에서 tistory accesstoken 받아오고 복호화
		StringEncryptor newStringEncryptor = jasyptConfig.createEncryptor();
		String accessToken = newStringEncryptor.decrypt(member.getToken().getTistoryToken());

		if (!"".equals(accessToken) && accessToken != null) {
			String blogInfoUrl = "https://www.tistory.com/apis/blog/info?"
				+ "access_token=" + accessToken
				+ "&output=json";

			try {
				URL url = new URL(blogInfoUrl);
				HttpURLConnection blogInfo = (HttpURLConnection)url.openConnection();

				// int responseCode = blogInfo.getResponseCode();
				// System.out.println("getBlogInfo responsecode = " + responseCode);

				BufferedReader blogInfoIn = new BufferedReader(new InputStreamReader(blogInfo.getInputStream()));

				String line;
				if ((line = blogInfoIn.readLine()) != null) {
					JSONObject item = new JSONObject(line)
						.getJSONObject("tistory")
						.getJSONObject("item");

					if(item.get("blogs")!=null){
						JSONArray blogInfoList = item.getJSONArray("blogs");

						int cnt = 0;
						while (blogInfoList.length() > cnt) {
							JSONObject blog = (JSONObject)blogInfoList.get(cnt++);
							blogNameList.add(blog.getString("name"));
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return blogNameList;
	}

	@Override
	public List<Object> getTistoryCates(List<String> blogNameList, List<Object> categoriesList, Member member) {

		// 토큰에서 tistory accesstoken 받아오고 복화화
		StringEncryptor newStringEncryptor = jasyptConfig.createEncryptor();
		String accessToken = newStringEncryptor.decrypt(member.getToken().getTistoryToken());

		if (!"".equals(accessToken) && accessToken != null) {

			try {
				// 각 블로그에 등록된 카테고리 리스트 저장 후 반환
				for (String blogName : blogNameList) {
					String blogInfoUrl = "https://www.tistory.com/apis/category/list?"
						+ "access_token=" + accessToken
						+ "&output=json"
						+ "&blogName=";

					blogInfoUrl += blogName;

					URL url = new URL(blogInfoUrl);
					HttpURLConnection blogInfo = (HttpURLConnection)url.openConnection();

					// int responseCode = blogInfo.getResponseCode();
					// System.out.println("getBlogInfo responsecode = " + responseCode);

					BufferedReader blogInfoIn = new BufferedReader(new InputStreamReader(blogInfo.getInputStream()));

					String line;
					if ((line = blogInfoIn.readLine()) != null) {

						JSONObject item = new JSONObject(line)
							.getJSONObject("tistory")
							.getJSONObject("item");

						// 블로그에 카테고리가 있는 경우
						if(item.get("categories")!=null){
							JSONArray cateInfoList = item.getJSONArray("categories");

							List<TistoryCateDto> cate = new ArrayList<>();

							int cnt = 0;
							while (cateInfoList.length() > cnt) {
								JSONObject category = (JSONObject)cateInfoList.get(cnt++);

								cate.add(new TistoryCateDto(
									category.getString("id").toString(),
									category.getString("name").toString(),
									category.getString("parent").toString(),
									category.getString("label").toString(),
									category.getString("entries").toString()
								));
							}
							categoriesList.add(cate);
						}
					}
				}

			} catch (JSONException x) {
				// throw new JSONException();
				List<TistoryCateDto> cate = new ArrayList<>();
				categoriesList.add(cate);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return categoriesList;
	}

	@Override
	public List<Object> getTistoryList(PaginationDto paginationDto, Member member) {

		Long lastTistoryId = paginationDto.getLastTistoryId();

		// 멤버의 블로그이름 리스트
		List<String> blogNameList = new ArrayList<>();

		// 블로그별 카테고리 리스트
		List<Object> categoriesList = new ArrayList<>();

		// 티스토리 발행 이력
		// List<TistoryResponseInterface> tistoryList = new ArrayList<>();

		// 첫 호출인 경우 블로그이름 리스트, 카테고리 리스트와 같이 반환
		if (lastTistoryId == -1) {
			lastTistoryId = null;
			blogNameList = getTistoryBlogName(blogNameList, member);
			categoriesList = getTistoryCates(blogNameList, categoriesList, member);
		}

		List<TistoryContentResponseDto> tistoryList = tistoryRepositoryCust.tistoryPaginationNoOffset(paginationDto, member);

		List<Object> rslt = new ArrayList<>();
		rslt.add(tistoryList);
		rslt.add(blogNameList);
		rslt.add(categoriesList);

		return rslt;
	}

	@Override
	public List<Object> getGithubList(PaginationDto paginationDto, Member member) {
		Long lastGithubId = paginationDto.getLastGithubId();

		// 멤버의 레포지토리 이름 리스트
		List<String> repositoryList = new ArrayList<>();

		// 레포지토리별 카테고리 리스트
		List<ArrayList> categoriesList = new ArrayList<ArrayList>();

		// 티스토리 발행 이력
		// List<TistoryResponseInterface> tistoryList = new ArrayList<>();

		// 첫 호출인 경우 블로그이름 리스트, 카테고리 리스트와 같이 반환
		if (lastGithubId == -1) {
			lastGithubId = null;
			repositoryList = getGithubRepository(repositoryList, member);
			categoriesList = getGithubCates(repositoryList, categoriesList, member);
		}

		List<GithubContentResponseDto> githubList = githubRepositoryCust.githubPaginationNoOffset(paginationDto, member);

		List<Object> rslt = new ArrayList<>();
		rslt.add(githubList);
		rslt.add(repositoryList);
		rslt.add(categoriesList);

		return rslt;

	}

	private List<ArrayList> getGithubCates(List<String> repositoryList, List<ArrayList> categoriesList, Member member) {

		// 토큰에서 github accesstoken 받아오고 복화화
		StringEncryptor newStringEncryptor = jasyptConfig.createEncryptor();
		String accessToken = newStringEncryptor.decrypt(member.getToken().getGithubToken());

		if (!"".equals(accessToken) && accessToken != null) {

			for(int i = 0; i < repositoryList.size(); i++){
				categoriesList.add(new ArrayList<Object>());
			}

			try {
				String githubRequestURL = "https://api.github.com/repos/" + member.getGithubId() + "/";
				// 각 블로그에 등록된 카테고리 리스트 저장 후 반환
				for (int i = 0; i < repositoryList.size(); i++) {
					String repository = repositoryList.get(i);
					ResponseEntity<List<Map<String, Object>>> response = null;
					Map<String, Object> responseBody = new HashMap<>();

					RestTemplate rt = new RestTemplate();
					String filePath = repository + "/" + "contents";

					HttpHeaders headers = new HttpHeaders();

					headers.add("Accept", "application/vnd.github+json");
					headers.add("Authorization", "Bearer " + accessToken);
					headers.add("X-GitHub-Api-Version", "2022-11-28");
					headers.setContentType(MediaType.APPLICATION_JSON);

					HttpEntity<String> entity = new HttpEntity<>(null, headers);
					try {

						response = rt.exchange(
							githubRequestURL + filePath,
							HttpMethod.GET,
							entity,
							new ParameterizedTypeReference<List<Map<String, Object>>>(){}
						);

						if (response != null && response.getBody() != null) {
							for (Map<String, Object> item : response.getBody()) {
								if ("dir".equals(item.get("type"))) {
									categoriesList.get(i).add(item);
								}
							}
						} else {
							System.out.println("해당 레포지토리는 카테고리가 없습니다.");
						}
					} catch (HttpClientErrorException.NotFound e){
						// 이미지 파일이 없는 경우에 대한 처리.
						// response와 responseBody는 null 상태이므로 여기서는 다루지 않는다.
						System.out.println("line 118 : 이미지 file 없음");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return categoriesList;
	}

	private List<String> getGithubRepository(List<String> repositoryList, Member member) {

		// 토큰에서 github accesstoken 받아오고 복화화
		StringEncryptor newStringEncryptor = jasyptConfig.createEncryptor();
		String accessToken = newStringEncryptor.decrypt(member.getToken().getGithubToken());

		ResponseEntity<List<Map<String, Object>>>  response = null;

		if (!"".equals(accessToken) && accessToken != null) {
			try {
				String githubRequestURL = "https://api.github.com/users/" + member.getGithubId() + "/repos";
					RestTemplate rt = new RestTemplate();
					HttpHeaders headers = new HttpHeaders();

					headers.add("Accept", "application/vnd.github+json");
					headers.add("Authorization", "Bearer " + accessToken);
					headers.add("X-GitHub-Api-Version", "2022-11-28");

					HttpEntity<String> entity = new HttpEntity<>(null, headers);
					try {
						response = rt.exchange(
							githubRequestURL,
							HttpMethod.GET,
							entity,
							new ParameterizedTypeReference<List<Map<String, Object>>>(){}
						);

						for (Map<String, Object> repository : response.getBody()) {
							Object name = repository.get("name");
							repositoryList.add(name.toString());
						}
						// 저장소 이름 리스트 사용 예시
						for (String repositoryName : repositoryList) {
							System.out.println("Repository Name: " + repositoryName);
						}

						// // Map<String, Object> body = response.getBody();
						// if (response != null && response.getBody() != null) {
						// 	for (Map<String, Object> item : response.getBody()) {
						// 		if ("dir".equals(item.get("type"))) {
						// 			categoriesList.get(i).add(item);
						// 		}
						// 	}
						// } else {
						// 	System.out.println("해당 레포지토리는 카테고리가 없습니다.");
						// }
					} catch (HttpClientErrorException.NotFound e){
						// 이미지 파일이 없는 경우에 대한 처리.
						// response와 responseBody는 null 상태이므로 여기서는 다루지 않는다.
						System.out.println("line 118 : 이미지 file 없음");
					}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return repositoryList;


		// GitHubClient client = new GitHubClient();
		// client.setOAuth2Token(ATK);
		//
		// // RepositoryService 생성
		// RepositoryService repoService = new RepositoryService(client);
		//
		// // 유저의 repositories 리스트 가져오기
		// try {
		// 	List<Repository> repositories = repoService.getRepositories();
		// 	for (Repository repo : repositories) {
		// 		repositoryList.add(repo.getName());
		// 	}
		// }catch (Exception e){
		// 	e.printStackTrace();
		// }
		//
		// return repositoryList;

	}

	// [Multi Thread] : 사용자가 N개의 발행 요청시, 작업이 동시에 수행되어 처리시간이 기존 N초에서 (1/N)초 수준으로 단축된다.

	// [Tistory Post]
	// 1. type 오류 : [발행실패] 입력 값이 올바르지 않습니다.
	// 2. blogName 불일치 : [발행실패] Tistory 게시물 작성중 에러가 발생했습니다. (페이지 접근 권한이 없거나, 블로그 이름이 일치하지 않습니다.)
	// 3. requestLink 형식 불일치 : [발행실패] 입력 값이 올바르지 않습니다.
	// 4. reponseLink 값 포함 : 무시하고 발행
	// 5. visibility 오류 : [발행실패] 입력 값이 올바르지 않습니다.
	// 6. status 오류 : [발행실패] 클라이언트에서 전달받은 요청의 상태 값이 올바르지 않습니다.
	// 7. 사용자가 권한을 허용하지 않은 Notion 페이지에 대한 발행요청 : [발행실패] Tistory 게시물 작성중 에러가 발생했습니다. (페이지 접근 권한이 없거나, 블로그 이름이 일치하지 않습니다.)

	// [Tistory Modify]
	// 1. type 오류 : [발행실패] 클라이언트에서 전달받은 입력 값이 올바르지 않습니다.
	// 2. blogName 불일치 : [발행실패] Tistory 게시물 수정중 에러가 발생했습니다. (이미 삭제된 게시물이거나, 블로그 이름이 일치하지 않습니다.)
	// 3. requestLink 형식 불일치 : [발행실패] 클라이언트에서 전달받은 입력 값이 올바르지 않습니다.
	// 4. reponseLink 데이터베이스 이력 부재 : [발행실패] 클라이언트에서 전달받은 입력 값이 올바르지 않습니다.
	// 5. visibility 오류 : [발행실패] 클라이언트에서 전달받은 입력 값이 올바르지 않습니다.
	// 6. status 오류 : [발행실패] 클라이언트에서 전달받은 요청의 상태 값이 올바르지 않습니다.
	// 7. 사용자가 이미 삭제한 게시글에 대한 수정요청 : [발행실패] Tistory 게시물 수정중 에러가 발생했습니다. (이미 삭제된 게시물이거나, 블로그 이름이 일치하지 않습니다.)
	// -> DB에는 있는 게시글이지만, Tistory Modify API에서 에러가 발생하는 것이다.
	// 8. 데이터베이스에 존재하지 않는 게시글에 대한 수정요청 : [발행실패] 클라이언트에서 전달받은 입력 값이 올바르지 않습니다.
	// -> DB에 없는 게시글로 인해, NullPointerException이 발생하는 것이다.

	@Override
	public Object postNotionToTistoryMultiThread(List<PostNotionToTistoryDto> postNotionToTistoryDtoList, Member member) {
		// 각 Request에 대한 Response 메시지를 저장하는 리스트
		List<Map<String, Object>> responseList = new ArrayList<>();

		// 초기 스레드 수 : 0, 코어 스레드 수 : 0, 최대 스레드 수 : Integer.MAX_VALUE
		// 추가된 스레드가 60초동안 아무 작업을 수행하지 않으면 ThreadPool에서 제거한다.
		executorService = Executors.newCachedThreadPool();

		// JAVA의 Future 인터페이스의 비동기 작업은 결과의 순서를 보장하지 않기 때문에, 완료된 작업의 결과를 순서대로 제공받기 위해 ExecutorCompletionService를 이용한다.
		ExecutorCompletionService<LambdaResponse> completionService = new ExecutorCompletionService<>(executorService);

		// 각 Thread별 실행결과를 반환받는 Future 리스트
		// Future 객체는 다른 스레드들의 연산 결과를 반환받기 위해 사용하는 지연 완료 객체 (Pending Completion Object)이다.
		List<Future<?>> futureList = new ArrayList<>();

		// 각 Thread별 조건검사 및 발행 결과를 반환받는 LamdaResponse 배열
		LambdaResponse[] lambdaResponses = new LambdaResponse[postNotionToTistoryDtoList.size()];

		// 멤버에 대한 토큰 암호화 및 복호화를 수행하는 Encryptor
		StringEncryptor newStringEncryptor = jasyptConfig.createEncryptor();

		for(int i=0; i<postNotionToTistoryDtoList.size(); i++){
			PostNotionToTistoryDto post = postNotionToTistoryDtoList.get(i);
			Map<String, Object> responseBody = new HashMap<>();
			// sSystem.out.println("★ 프론트로부터 전달된 값 "+post);

			// STEP1. 상태별 조건 검사를 수행한다.(조건검사에 따라 스레드 제출 여부를 검토한다.)
			Tistory tistory = null;
			boolean testFlag = false;
			boolean statusFlag = true;

			// 발행상태 변화도 : 발행실패 <-> 발행요청 -> 발행완료 <-> 수정요청 <-> 수정실패
			// 1. [발행요청]은 사용자가 신규로 발행요청을 했거나, [발행실패]이력에 대해 발행요청을 하는 경우이다.
			// (1). 클라이언트로부터 전달받은 tistoryId를 검사한다.
			// 1). tistoryId가 ""일 경우, 신규 발행요청으로 간주한다.
			// 2). tistoryId가 "숫자값"일 경우, 발행실패 이력에 대한 재발행요청으로 간주하고, Tistory 객체를 조회한다.
			//
			// 2. [발행실패]는 [발행요청]에 실패한 튜플로, 브라우저에서 사용자가 [발행요청]으로 요청상태를 변경해야한다.
			// (1). 아무 작업도 수행하지 않는다.
			//
			// 3. [발행완료]는 [발행요청] 및 [수정요청]에 대해 발행이 완료된 상태이다.
			// (1). 아무 작업도 수행하지 않는다.
			//
			// 4. [수정요청]은 사용자가 이미 발행했던 [발행완료] 혹은 [수정실패] 튜플에 대해서만 가능하다.
			// (1). [수정요청]은 사용자가 [발행완료]된 튜플에 수정요청을 했거나, [수정실패]된 튜플에 수정요청을 하는 경우이다.
			// (2). 클라이언트로부터 전달받은 tistoryId를 통해 Tistory 객체를 조회한다.
			// 1). 조회한 Tistory 객체의 상태(status)는 반드시 "발행완료" 혹은 "수정실패"여야한다.
			//
			// 5. [수정실패]는 [수정요청]에 실패한 튜플로, 브라우저에서 사용자가 [수정요청]으로 요청상태를 변경해야한다.
			// (1). 아무 작업도 수행하지 않는다.
			//
			// 브라우저에서 보이는 각 상태별 드롭다운 리스트
			// 1. 최초발행 튜플 : 발행요청
			// 2. 발행실패 튜플 : 발행실패, 발행요청
			// 3. 수정실패 튜플 : 수정실패, 수정요청
			// 4. 발행완료 튜플 : 발행완료, 수정요청

			if(post.getStatus().equals("발행요청")){
				// STEP2. 클라이언트로부터 전달받은 입력값을 검사한다.
				testFlag = conditionCheck(post);

				// STEP3-1. 조건검사 결과가 True인 경우, 최초 발행요청 및 재발행요청을 위한 발행 이력 상태를 검사한다.
				if(testFlag){
					try{
						tistory = tistoryRepository.findByTistoryId(Long.parseLong(post.getTistoryId()));
					} catch(NumberFormatException e){
						tistory = null;
					}

					// STEP4-1. 최초 발행요청(tistoryId="데이터베이스에 존재하지 않는 ID")에 해당하는 경우
					if(tistory==null){
						// STEP4-1-1. 클라이언트의 발행요청을 데이터베이스에 저장한다.
						tistory = Tistory.builder()
							.blogName(post.getBlogName())
							.requestLink(post.getRequestLink())
							.visibility(post.getVisibility())
							.categoryName(post.getCategoryName())
							.tagList(post.getTagList())
							.status("발행요청")
							.title("")
							.member(member)
							.build();
						tistoryRepository.save(tistory);
					}
					// STEP4-2. 발행실패 이력에 대한 재발행요청(tistoryId="데이터베이스에 존재하는 ID")에 해당하는 경우
					else{
						// STEP4-2-1. 데이터베이스에 저장되어 있는 기존 이력을 조회한다.
						try{
							// STEP4-2-2. 조회한 기존 이력의 상태가 [발행실패]가 아닌 경우, 잘못된 튜플에 대한 요청으로 간주한다.
							if(tistory.getStatus().equals("발행실패")){
								tistory.setBlogName(post.getBlogName());
								tistory.setCategoryName(post.getCategoryName());
								tistory.setRequestLink(post.getRequestLink());
								tistory.setTagList(post.getTagList());
								tistory.setVisibility(post.getVisibility());

								statusFlag = true;
								tistory.setStatus("발행요청");
							}
							else{
								statusFlag = false;

								responseBody.put("requestIndex", (i+1));
								responseBody.put("resultCode", 400);
								responseBody.put("resultMessage", "[발행실패] 입력 값이 올바르지 않습니다.");
								responseList.add(responseBody);

								// 해당 경우에 대해서는 tistory의 상태를 변경해서는 안된다.
								// tistory.setStatus("발행실패");
							}
						}catch(NullPointerException e){
							// NullPointerException : 데이터베이스에 존재하지 않는 tistoryId를 입력한 경우
							statusFlag = false;

							responseBody.put("requestIndex", (i+1));
							responseBody.put("resultCode", 400);
							responseBody.put("resultMessage", "[발행실패] 입력 값이 올바르지 않습니다.");
							responseList.add(responseBody);
						}
					}

					// STEP5-1. 조건검사와 상태검사를 모두 통과했다면 AWS Lambda를 호출하고 스레드에 제출한 뒤, FutureList에 스레드의 실행 결과를 추가한다.
					if(statusFlag){
						try{
							// 서브 스레드의 발행 요청을 수행하기 전, 복호화를 수행한다.
							TokenDecryptDto tokenDecryptDto = new TokenDecryptDto(
								newStringEncryptor.decrypt(member.getNotionToken()),
								newStringEncryptor.decrypt(member.getToken().getTistoryToken()),
								newStringEncryptor.decrypt(member.getToken().getGithubToken())
							);
							Callable<LambdaResponse> postLambdaCallable = new awsLambdaCallable(i, post, tistory, tokenDecryptDto);
							Future<?> future = completionService.submit(postLambdaCallable);
							futureList.add(future);
						} catch(Exception e){
							// Exception : 기타 예외의 경우
							e.printStackTrace();
						}
					}
				}
				// STEP5-2. 조건검사 결과가 False인 경우, AWS Lambda를 호출하지 않고 API 응답 결과에 Bad Request를 추가한다.
				else{
					responseBody.put("requestIndex", (i+1));
					responseBody.put("resultCode", 400);
					responseBody.put("resultMessage", "[발행실패] 입력 값이 올바르지 않습니다.");
					responseList.add(responseBody);

					tistory.setStatus("발행실패");
				}
			}
			else if(post.getStatus().equals("발행실패")){
				responseBody.put("requestIndex", (i+1));
				responseBody.put("resultCode", 200);
				responseBody.put("resultMessage", "[발행실패] 발행 실패된 페이지입니다. 요청에 대한 처리를 하지 않습니다. ");
				responseList.add(responseBody);
			}
			else if(post.getStatus().equals("발행완료")){
				responseBody.put("requestIndex", (i+1));
				responseBody.put("resultCode", 200);
				responseBody.put("resultMessage", "[발행완료] 발행 완료된 페이지입니다. 요청에 대한 처리를 하지 않습니다.");
				responseList.add(responseBody);
			}
			else if(post.getStatus().equals("수정요청")){
				// STEP2. 클라이언트로부터 전달받은 입력값을 검사한다.
				testFlag = conditionCheck(post);

				// STEP3-1. 조건검사 결과가 True인 경우, AWS Lambda를 호출하고 스레드에 제출한 뒤, FutureList에 스레드의 실행 결과를 추가한다.
				if(testFlag){
					try{
						tistory = tistoryRepository.findByTistoryId(Long.parseLong(post.getTistoryId()));

						// STEP4-1. 조회한 기존 이력의 상태가 [발행완료] 혹은 [수정실패] 아닌 경우, 잘못된 튜플에 대한 요청으로 간주한다.
						if(tistory.getStatus().equals("발행완료") || tistory.getStatus().equals("수정실패")){
							tistory.setBlogName(post.getBlogName());
							tistory.setCategoryName(post.getCategoryName());
							tistory.setRequestLink(post.getRequestLink());
							tistory.setTagList(post.getTagList());
							tistory.setVisibility(post.getVisibility());

							statusFlag = true;
							tistory.setStatus("수정요청");
						}
						else{
							statusFlag = false;

							responseBody.put("requestIndex", (i+1));
							responseBody.put("resultCode", 400);
							responseBody.put("resultMessage", "[수정실패] 입력 값이 올바르지 않습니다.");
							responseList.add(responseBody);

							// 해당 경우에 대해서는 tistory의 상태를 변경해서는 안된다.
							// tistory.setStatus("발행실패");
						}
					} catch(NullPointerException e){
						// NullPointerException : 데이터베이스에 존재하지 않는 tistoryId를 입력한 경우
						statusFlag = false;

						responseBody.put("requestIndex", (i+1));
						responseBody.put("resultCode", 400);
						responseBody.put("resultMessage", "[수정실패] 입력 값이 올바르지 않습니다.");
						responseList.add(responseBody);
					}

					// STEP4-2. 최초 수정요청 혹은 수정실패에 이력에 대한 수정요청에 해당하는 경우
					if(statusFlag){
						// 데이터베이스의 기존 발행 이력을 조회한 뒤, 클라이언트의 수정요청을 데이터베이스에 반영한다.
						try{
							// 서브 스레드의 발행 요청을 수행하기 전, 복호화를 수행한다.
							TokenDecryptDto tokenDecryptDto = new TokenDecryptDto(
								newStringEncryptor.decrypt(member.getNotionToken()),
								newStringEncryptor.decrypt(member.getToken().getTistoryToken()),
								newStringEncryptor.decrypt(member.getToken().getGithubToken())
							);
							Callable<LambdaResponse> awsLambdaCallable = new awsLambdaCallable(i, post, tistory, tokenDecryptDto);
							Future<?> future = completionService.submit(awsLambdaCallable);
							futureList.add(future);
						} catch(Exception e){
							// HttpClientErrorException : 티스토리에서 이미 삭제된 게시글에 대해 수정 요청을 하는 경우
							// JsonParseException : 입력값이 정상적으로 입력되지 않은 경우
							// NullPointerException : responseLink가 잘못 입력된 경우
							// Exception : 기타 예외의 경우
							responseBody.put("requestIndex", (i+1));
							responseBody.put("resultCode", 400);
							responseBody.put("resultMessage", "[수정실패] 클라이언트에서 전달받은 입력 값이 올바르지 않습니다.");
							responseList.add(responseBody);
						}
					}
				}
				// STEP3-2. 조건검사 결과가 False인 경우, AWS Lambda를 호출하지 않고 API 응답 결과에 Bad Request를 추가한다.
				else{
					try{
						responseBody.put("requestIndex", (i+1));
						responseBody.put("resultCode", 400);
						responseBody.put("resultMessage", "[수정실패] 클라이언트에서 전달받은 입력 값이 올바르지 않습니다.");
						responseList.add(responseBody);

						tistory.setStatus("수정실패");
					} catch(NullPointerException e){
						// NullPointerException : responseLink가 잘못 입력된 경우
					}
				}
			}
			else if(post.getStatus().equals("수정실패")){
				responseBody.put("requestIndex", (i+1));
				responseBody.put("resultCode", 200);
				responseBody.put("resultMessage", "[수정실패] 수정 실패된 페이지입니다. 요청에 대한 처리를 하지 않습니다. ");
				responseList.add(responseBody);
			}
			else{
				responseBody.put("requestIndex", (i+1));
				responseBody.put("resultCode", 400);
				responseBody.put("resultMessage", "[발행실패] 클라이언트에서 전달받은 요청의 상태 값이 올바르지 않습니다.");
				responseList.add(responseBody);
			}
		}
		System.out.println(executorService);

		// STEP3. ExecutorService 종료 및 모든 스레드의 실행이 종료될 때까지 대기한다.
		try {
			// ThreadPool에 대한 추가적인 Task 제출을 막는다.
			executorService.shutdown();

			// Nano Second 단위로, 현재 실행중인 Task들의 결과가 모두 반환될때까지 대기한다.
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// STEP4.
		// 각 발행 요청 스레드별 연산 결과를 모두 반환 받을때까지 대기하기 위해, future.get()을 사용한다.
		// 서브 스레드별 연산 결과를 모두 반환 받을때까지 메인 스레드의 실행 흐름을 잠시 Block한다.
		// 멀티스레드의 비동기 연산에 따른 무작위 발행을 막기 위해, 먼저 Http Response 패킷만 전부 받아두고, 순차적으로 발행 및 수정한다.
		for (Future<?> f : futureList) {
			Map<String, Object> responseBody = new HashMap<>();
			try {
				Future<LambdaResponse> completedFuture = completionService.take();
				lambdaResponses[completedFuture.get().getIndex()] = completedFuture.get();
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		// STEP5. 요청상태에 따라 순차적으로 Tistory 발행 및 수정을 수행한다.
		// AWS Lambda 파싱에 실패한 경우에 대해서는 오류 메시지를 기록한다.
		RestTemplate rt = new RestTemplate();
		for(int i=0; i<lambdaResponses.length; i++){
			Map<String, Object> responseBody = new HashMap<>();
			ResponseEntity<String> response = null;
			String tistoryRequestURL = "";

			// 조건검사에 위배되거나, 예외가 발생한 요청에 해당한다.
			if(lambdaResponses[i]==null){
				continue;
			}

			// STEP5-1. Tistory Post API
			if(lambdaResponses[i].getTistory().getStatus().equals("발행요청")){
				tistoryRequestURL = "https://www.tistory.com/apis/post/write";
				try{
					response = rt.exchange(
						tistoryRequestURL,
						HttpMethod.POST,
						lambdaResponses[i].getTistoryRequest(),
						String.class
					);

					// STEP6. [발행완료] Tistory 발행 상태 DB 갱신
					String tistoryResponse = response.toString(); // Tistory API의 응답
					Document doc = Jsoup.parse(tistoryResponse);
					lambdaResponses[i].getTistory().setTitle(lambdaResponses[i].getTistoryRequest().getBody().getFirst("title"));
					lambdaResponses[i].getTistory().setResponseLink(doc.select("url").text()); // Tistory에 게시된 게시글 링크
					lambdaResponses[i].getTistory().setPostId(Long.parseLong(doc.select("postId").text())); // Tistory에 게시된 게시글 번호
					lambdaResponses[i].getTistory().setStatus("발행완료");

					responseBody.put("requestIndex", i+1);
					responseBody.put("resultCode", 200);
					responseBody.put("resultMessage", "[발행완료] Tistory 게시물 발행이 정상적으로 완료되었습니다."
						+ lambdaResponses[i].getTistory().getPostId());
					responseList.add(responseBody);
				} catch(HttpClientErrorException e){
					// STEP6. [발행실패] Tistory 발행 상태 DB 갱신
					// Case1. BlogName Error (HttpClientErrorException)
					// Case2. Not Acceptable (HttpClientErrorException)
					e.printStackTrace();
					lambdaResponses[i].getTistory().setStatus("발행실패");

					responseBody.put("requestIndex", i+1);
					responseBody.put("resultCode", 400);
					responseBody.put("resultMessage", "[발행실패] Tistory 게시물 작성중 에러가 발생했습니다. (페이지 접근 권한이 없거나, 블로그 이름이 일치하지 않거나, 하루 최대 발행개수를 초과했습니다.)");
					responseList.add(responseBody);
				}
			}
			// STEP5-2. Tistory Modify API
			// [Exception] 사용자가 동일한 Tistory 게시글에 여러번 수정요청을 날리는 경우
			// 프론트엔드 테이블 구조상 하나의 Tistory 게시글에 대한 수정요청은 한번만 가능하므로, 해당 경우에 대해서는 고려하지 않아도 된다.
			// -> 최초 게시글에 대한 [수정요청] 이후 [발행완료] 상태가 되므로, 나머지 요청에 대해서는 Tistory Modify API에 전달되지 않는다.
			else if(lambdaResponses[i].getTistory().getStatus().equals("수정요청")){
				// Case1. RequestLink Error (수정의 경우 HttpClientErrorException이 발생하지 않으므로, 직접 처리한다.)
				if(lambdaResponses[i].getTistoryRequest().getBody().getFirst("title")==null
				&& lambdaResponses[i].getTistoryRequest().getBody().getFirst("content")==null){
					lambdaResponses[i].getTistory().setStatus("수정실패");

					responseBody.put("requestIndex", i+1);
					responseBody.put("resultCode", 400);
					responseBody.put("resultMessage", "[수정실패] Tistory 게시물 수정중 에러가 발생했습니다. (이미 삭제된 게시물이거나, 블로그 이름이 일치하지 않습니다.)"
						+ lambdaResponses[i].getTistory().getPostId());
					responseList.add(responseBody);
				}

				else{
					tistoryRequestURL = "https://www.tistory.com/apis/post/modify";
					try{
						response = rt.exchange(
							tistoryRequestURL,
							HttpMethod.POST,
							lambdaResponses[i].getTistoryRequest(),
							String.class
						);

						// STEP6. [발행완료] Tistory 발행 상태 DB 갱신
						lambdaResponses[i].getTistory().setTitle(lambdaResponses[i].getTistoryRequest().getBody().getFirst("title"));
						lambdaResponses[i].getTistory().setStatus("발행완료");

						responseBody.put("requestIndex", i+1);
						responseBody.put("resultCode", 200);
						responseBody.put("resultMessage", "[발행완료] Tistory 게시물 수정이 정상적으로 완료되었습니다."
							+ lambdaResponses[i].getTistory().getPostId());
						responseList.add(responseBody);
					} catch(HttpClientErrorException e){
						// STEP6. [수정실패] Tistory 발행 상태 DB 갱신
						// Case2. BlogName Error (HttpClientErrorException)
						lambdaResponses[i].getTistory().setStatus("수정실패");

						responseBody.put("requestIndex", i+1);
						responseBody.put("resultCode", 400);
						responseBody.put("resultMessage", "[수정실패] Tistory 게시물 수정중 에러가 발생했습니다. (이미 삭제된 게시물이거나, 블로그 이름이 일치하지 않습니다.)"
							+ lambdaResponses[i].getTistory().getPostId());
						responseList.add(responseBody);
					}
				}
			}
		}

		// for(int i=0; i<lambdaResponses.length; i++){
		// 	if(lambdaResponses[i]==null){
		// 		System.out.println((i+1) + " " + "에러가 발생한 게시글");
		// 	}
		// 	else{
		// 		System.out.println((i+1) + " " + lambdaResponses[i].getTistory().getResponseLink());
		// 	}
		// }

		// 입력값이 잘못 전달된 요청에 대한 응답이 정상 발행되는 요청의 응답보다 먼저 출력되는 문제를 해결하기 위해, requestIndex 오름차순으로 정렬한다.
		Collections.sort(responseList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int requestIndex1 = (int) o1.get("requestIndex");
				int requestIndex2 = (int) o2.get("requestIndex");
				return Integer.compare(requestIndex1, requestIndex2);
			}
		});

		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

	@Override
	public Object postNotionToGithubMultiThread(List<PostNotionToGithubDto> postNotionToGithubDtoList, Member member) {
		// 각 Request에 대한 Response 메시지를 저장하는 리스트
		List<Map<String, Object>> responseList = new ArrayList<>();

		// 초기 스레드 수 : 0, 코어 스레드 수 : 0, 최대 스레드 수 : Integer.MAX_VALUE
		// 추가된 스레드가 60초동안 아무 작업을 수행하지 않으면 ThreadPool에서 제거한다.
		executorService = Executors.newCachedThreadPool();

		// JAVA의 Future 인터페이스의 비동기 작업은 결과의 순서를 보장하지 않기 때문에, 완료된 작업의 결과를 순서대로 제공받기 위해 ExecutorCompletionService를 이용한다.
		ExecutorCompletionService<LambdaResponse> completionService = new ExecutorCompletionService<>(executorService);

		// 각 Thread별 실행결과를 반환받는 Future 리스트
		// Future 객체는 다른 스레드들의 연산 결과를 반환받기 위해 사용하는 지연 완료 객체 (Pending Completion Object)이다.
		List<Future<?>> futureList = new ArrayList<>();

		// 각 Thread별 조건검사 및 발행 결과를 반환받는 LamdaResponse 배열
		LambdaResponse[] lambdaResponses = new LambdaResponse[postNotionToGithubDtoList.size()];

		// 멤버에 대한 토큰 암호화 및 복호화를 수행하는 Encryptor
		StringEncryptor newStringEncryptor = jasyptConfig.createEncryptor();

		for(int i=0; i<postNotionToGithubDtoList.size(); i++){
			PostNotionToGithubDto post = postNotionToGithubDtoList.get(i);
			Map<String, Object> responseBody = new HashMap<>();

			// STEP1. 상태별 조건 검사를 수행한다.(조건검사에 따라 스레드 제출 여부를 검토한다.)
			Github github = null;
			boolean testFlag = false;
			boolean statusFlag = true;

			if(post.getStatus().equals("발행요청")){
				// STEP2. 클라이언트로부터 전달받은 입력값을 검사한다.
				testFlag = conditionCheckGithub(post);

				// STEP3-1. 조건검사 결과가 True인 경우, 최초 발행요청 및 재발행요청을 위한 발행 이력 상태를 검사한다.
				if(testFlag){
					try{
						github = githubRepository.findByGithubId(Long.parseLong(post.getGithubId()));
					} catch(NumberFormatException e){
						github = null;
					}
					// STEP4-1. 최초 발행요청(tistoryId="데이터베이스에 존재하지 않는 ID")에 해당하는 경우
					if(github == null){
						github = Github.builder()
							.repository(post.getRepository())
							.requestLink(post.getRequestLink())
							.categoryName(post.getCategoryName())
							.filename(post.getFilename())
							.status("발행요청")
							.member(member)
							.build();
						githubRepository.save(github);
					}
					// STEP4-2. 발행실패 이력에 대한 재발행요청(githubId="데이터베이스에 존재하는 ID")에 해당하는 경우
					else{
						// STEP4-2-1. 데이터베이스에 저장되어 있는 기존 이력을 조회한다.
						try {
							// STEP4-2-2. 조회한 기존 이력의 상태가 [발행실패]가 아닌 경우, 잘못된 튜플에 대한 요청으로 간주한다.
							if (github.getStatus().equals("발행실패")) {
								github.setRepository(post.getRepository());
								github.setCategoryName(post.getCategoryName());
								github.setRequestLink(post.getRequestLink());

								statusFlag = true;
								github.setStatus("발행요청");
							} else {
								statusFlag = false;
								responseBody.put("requestIndex", (i + 1));
								responseBody.put("resultCode", 400);
								responseBody.put("resultMessage", "[발행실패] 입력 값이 올바르지 않습니다.");
								responseList.add(responseBody);
							}
						}catch(NullPointerException e){
							// NullPointerException : 데이터베이스에 존재하지 않는 githubId를 입력한 경우
							statusFlag = false;

							responseBody.put("requestIndex", (i+1));
							responseBody.put("resultCode", 400);
							responseBody.put("resultMessage", "[발행실패] 입력 값이 올바르지 않습니다.");
							responseList.add(responseBody);
						}
					}
					// STEP5-1. 조건검사와 상태검사를 모두 통과했다면 AWS Lambda를 호출하고 스레드에 제출한 뒤, FutureList에 스레드의 실행 결과를 추가한다.
					if(statusFlag) {
						try {
							post.setGithubId(member.getGithubId());
							// 서브 스레드의 발행 요청을 수행하기 전, 복호화를 수행한다.
							TokenDecryptDto tokenDecryptDto = new TokenDecryptDto(
								newStringEncryptor.decrypt(member.getNotionToken()),
								newStringEncryptor.decrypt(member.getToken().getTistoryToken()),
								newStringEncryptor.decrypt(member.getToken().getGithubToken())
							);
							Callable<LambdaResponse> postLambdaCallable = new awsLambdaCallableGithub(i, post, github, tokenDecryptDto);
							Future<?> future = completionService.submit(postLambdaCallable);
							futureList.add(future);
						} catch (Exception e) {
							// Exception : 기타 예외의 경우
							e.printStackTrace();
						}
					}
				}
				// STEP5-2. 조건검사 결과가 False인 경우, AWS Lambda를 호출하지 않고 API 응답 결과에 Bad Request를 추가한다.
				else{
					responseBody.put("requestIndex", (i+1));
					responseBody.put("resultCode", 400);
					responseBody.put("resultMessage", "[발행실패] 입력 값이 올바르지 않습니다.");
					responseList.add(responseBody);

					github.setStatus("발행실패");
				}
			}
			///////////////////////////////////////
			else if(post.getStatus().equals("발행실패")){
				responseBody.put("requestIndex", (i+1));
				responseBody.put("resultCode", 200);
				responseBody.put("resultMessage", "[발행실패] 발행 실패된 페이지입니다. 요청에 대한 처리를 하지 않습니다. ");
				responseList.add(responseBody);
			}
			else if(post.getStatus().equals("발행완료")){
				responseBody.put("requestIndex", (i+1));
				responseBody.put("resultCode", 200);
				responseBody.put("resultMessage", "[발행실패] 발행 실패된 페이지입니다. 요청에 대한 처리를 하지 않습니다. ");
				responseList.add(responseBody);
			}
			else if(post.getStatus().equals("수정요청")){
				// STEP2. 클라이언트로부터 전달받은 입력값을 검사한다.
				testFlag = conditionCheckGithub(post);

				// STEP3-1. 조건검사 결과가 True인 경우, AWS Lambda를 호출하고 스레드에 제출한 뒤, FutureList에 스레드의 실행 결과를 추가한다.
				if(testFlag){
					try{
						github = githubRepository.findByGithubId(Long.parseLong(post.getGithubId()));

						// STEP4-1. 조회한 기존 이력의 상태가 [발행완료] 혹은 [수정실패] 아닌 경우, 잘못된 튜플에 대한 요청으로 간주한다.
						if(github.getStatus().equals("발행완료") || github.getStatus().equals("수정실패")){
							github.setRepository(post.getRepository());
							github.setCategoryName(post.getCategoryName());
							github.setRequestLink(post.getRequestLink());

							statusFlag = true;
							github.setStatus("수정요청");
						} else {
							statusFlag = false;
							responseBody.put("requestIndex", (i + 1));
							responseBody.put("resultCode", 400);
							responseBody.put("resultMessage", "[수정실패] 입력 값이 올바르지 않습니다.");
							responseList.add(responseBody);
						}
					}catch(NullPointerException e){
						// NullPointerException : 데이터베이스에 존재하지 않는 githubId를 입력한 경우
						statusFlag = false;

						responseBody.put("requestIndex", (i+1));
						responseBody.put("resultCode", 400);
						responseBody.put("resultMessage", "[수정실패] 입력 값이 올바르지 않습니다.");
						responseList.add(responseBody);
					}
					// STEP4-2. 최초 수정요청 혹은 수정실패에 이력에 대한 수정요청에 해당하는 경우
					if(statusFlag){
						post.setGithubId(member.getGithubId());
						// 데이터베이스의 기존 발행 이력을 조회한 뒤, 클라이언트의 수정요청을 데이터베이스에 반영한다.
						try{
							// 서브 스레드의 발행 요청을 수행하기 전, 복호화를 수행한다.
							TokenDecryptDto tokenDecryptDto = new TokenDecryptDto(
								newStringEncryptor.decrypt(member.getNotionToken()),
								newStringEncryptor.decrypt(member.getToken().getTistoryToken()),
								newStringEncryptor.decrypt(member.getToken().getGithubToken())
							);
							Callable<LambdaResponse> awsLambdaCallable = new awsLambdaCallableGithub(i, post, github, tokenDecryptDto);
							Future<?> future = completionService.submit(awsLambdaCallable);
							futureList.add(future);
						} catch(Exception e){
							// HttpClientErrorException : 티스토리에서 이미 삭제된 게시글에 대해 수정 요청을 하는 경우
							// JsonParseException : 입력값이 정상적으로 입력되지 않은 경우
							// NullPointerException : responseLink가 잘못 입력된 경우
							// Exception : 기타 예외의 경우
							responseBody.put("requestIndex", (i+1));
							responseBody.put("resultCode", 400);
							responseBody.put("resultMessage", "[수정실패] 클라이언트에서 전달받은 입력 값이 올바르지 않습니다.");
							responseList.add(responseBody);
						}
					}
				}
				// STEP3-2. 조건검사 결과가 False인 경우, AWS Lambda를 호출하지 않고 API 응답 결과에 Bad Request를 추가한다.
				else{
					try{
						responseBody.put("requestIndex", (i+1));
						responseBody.put("resultCode", 400);
						responseBody.put("resultMessage", "[수정실패] 클라이언트에서 전달받은 입력 값이 올바르지 않습니다.");
						responseList.add(responseBody);

						github.setStatus("수정실패");
					} catch(NullPointerException e){
						// NullPointerException : responseLink가 잘못 입력된 경우
					}
				}
			}
			else if(post.getStatus().equals("수정실패")){
				responseBody.put("requestIndex", (i+1));
				responseBody.put("resultCode", 200);
				responseBody.put("resultMessage", "[수정실패] 수정 실패된 페이지입니다. 요청에 대한 처리를 하지 않습니다. ");
				responseList.add(responseBody);
			}
			else{
				responseBody.put("requestIndex", (i+1));
				responseBody.put("resultCode", 400);
				responseBody.put("resultMessage", "[발행실패] 클라이언트에서 전달받은 요청의 상태 값이 올바르지 않습니다.");
				responseList.add(responseBody);
			}
		}
		System.out.println(executorService);

		// STEP3. ExecutorService 종료 및 모든 스레드의 실행이 종료될 때까지 대기한다.
		try {
			// ThreadPool에 대한 추가적인 Task 제출을 막는다.
			executorService.shutdown();

			// Nano Second 단위로, 현재 실행중인 Task들의 결과가 모두 반환될때까지 대기한다.
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// STEP4.
		// 각 발행 요청 스레드별 연산 결과를 모두 반환 받을때까지 대기하기 위해, future.get()을 사용한다.
		// 서브 스레드별 연산 결과를 모두 반환 받을때까지 메인 스레드의 실행 흐름을 잠시 Block한다.
		// 멀티스레드의 비동기 연산에 따른 무작위 발행을 막기 위해, 먼저 Http Response 패킷만 전부 받아두고, 순차적으로 발행 및 수정한다.
		for (Future<?> f : futureList) {
			// Map<String, Object> responseBody = new HashMap<>();
			try {
				Future<LambdaResponse> completedFuture = completionService.take();
				lambdaResponses[completedFuture.get().getIndex()] = completedFuture.get();
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		// STEP5. 요청상태에 따라 순차적으로 github 발행 및 수정을 수행한다.
		// AWS Lambda 파싱에 실패한 경우에 대해서는 오류 메시지를 기록한다.
		RestTemplate rt = new RestTemplate();
		for(int i=0; i<lambdaResponses.length; i++){
			Map<String, Object> responseBody = new HashMap<>();
			ResponseEntity<Map<String, Object>> response = null;
			String githubRequestURL = "https://api.github.com/repos/";
			rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

			// 조건검사에 위배되거나, 예외가 발생한 요청에 해당한다.
			if(lambdaResponses[i]==null){
				continue;
			}

			// STEP5-1. github Post API
			if(lambdaResponses[i].getGithub().getStatus().equals("발행요청")){
				try{
					response = rt.exchange(
						// "https://api.github.com/repos/encoreKwang/PullRequestTest/contents" + filePath,
						githubRequestURL + lambdaResponses[i].getFilePath(),
						HttpMethod.PUT,
						lambdaResponses[i].getGithubRequest(),
						new ParameterizedTypeReference<Map<String, Object>>() {}
					);
					responseBody= response.getBody();
					Map<String, String> content = (Map<String, String>) responseBody.get("content");
					String name = content.get("name");
					String sha = content.get("sha");
					String htmlUrl = content.get("html_url");

					// STEP6. [발행완료] Github 발행 상태 DB 갱신
					lambdaResponses[i].getGithub().setFilename(name);
					lambdaResponses[i].getGithub().setSha(sha);
					lambdaResponses[i].getGithub().setResponseLink(htmlUrl);
					lambdaResponses[i].getGithub().setStatus("발행완료");

					responseBody.put("requestIndex", i+1);
					responseBody.put("resultCode", 200);
					responseBody.put("resultMessage", "[발행완료] Github 게시물 발행이 정상적으로 완료되었습니다."
						+ lambdaResponses[i].getGithub().getSha());
					responseList.add(responseBody);
				} catch(Exception e){
					// STEP6. [발행실패] Github 발행 상태 DB 갱신
					// Case1. repository Error (HttpClientErrorException)
					// lambdaResponses[i].getTistory().setStatus("발행실패");
					lambdaResponses[i].getGithub().setStatus("발행실패");

					responseBody.put("requestIndex", i+1);
					responseBody.put("resultCode", 400);
					responseBody.put("resultMessage", "[발행실패] Github 게시물 작성중 에러가 발생했습니다. (페이지 접근 권한이 없거나, 레포지토리 이름이 일치하지 않습니다.)");
					responseList.add(responseBody);
				}
			}
			// STEP5-2. Github Modify API
			// [Exception] 사용자가 동일한 Github 게시글에 여러번 수정요청을 날리는 경우
			// 프론트엔드 테이블 구조상 하나의 Github 게시글에 대한 수정요청은 한번만 가능하므로, 해당 경우에 대해서는 고려하지 않아도 된다.
			// -> 최초 게시글에 대한 [수정요청] 이후 [발행완료] 상태가 되므로, 나머지 요청에 대해서는 Github Modify API에 전달되지 않는다.
			else if(lambdaResponses[i].getGithub().getStatus().equals("수정요청")){
				try{
					response = rt.exchange(
						// "https://api.github.com/repos/encoreKwang/PullRequestTest/contents" + filePath,
						githubRequestURL + lambdaResponses[i].getFilePath(),
						HttpMethod.PUT,
						lambdaResponses[i].getGithubRequest(),
						new ParameterizedTypeReference<Map<String, Object>>() {}
					);

					responseBody= response.getBody();
					Map<String, String> content = (Map<String, String>) responseBody.get("content");
					String sha = content.get("sha");

					// STEP6. [발행완료] Github 발행 상태 DB 갱신
					//수정일 땐 DB의 sha 필드값만 변경한다.
					lambdaResponses[i].getGithub().setSha(sha);
					lambdaResponses[i].getGithub().setStatus("발행완료");

					// STEP6. [발행완료] Github 발행 상태 DB 갱신
					lambdaResponses[i].getGithub().setStatus("발행완료");

					responseBody.put("requestIndex", i+1);
					responseBody.put("resultCode", 200);
					responseBody.put("resultMessage", "[발행완료] Github 게시물 발행이 정상적으로 완료되었습니다."
						+ lambdaResponses[i].getGithub().getSha());
					responseList.add(responseBody);
				} catch(HttpClientErrorException e){
					// STEP6. [수정실패] Github 발행 상태 DB 갱신
					lambdaResponses[i].getGithub().setStatus("수정실패");

					responseBody.put("requestIndex", i+1);
					responseBody.put("resultCode", 400);
					responseBody.put("resultMessage", "[수정실패] Github 게시물 수정중 에러가 발생했습니다. (이미 삭제된 게시물이거나, 레포지토리 이름이 일치하지 않습니다.)"
						+ lambdaResponses[i].getGithub().getSha());
					responseList.add(responseBody);
				}
			}
		}

		for(int i=0; i<lambdaResponses.length; i++){
			if(lambdaResponses[i]==null){
				System.out.println((i+1) + " " + "에러가 발생한 게시글");
			}
			else{
				System.out.println((i+1) + " " + lambdaResponses[i].getGithub().getResponseLink());
			}
		}

		// 입력값이 잘못 전달된 요청에 대한 응답이 정상 발행되는 요청의 응답보다 먼저 출력되는 문제를 해결하기 위해, requestIndex 오름차순으로 정렬한다.
		Collections.sort(responseList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int requestIndex1 = (int) o1.get("requestIndex");
				int requestIndex2 = (int) o2.get("requestIndex");
				return Integer.compare(requestIndex1, requestIndex2);
			}
		});

		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}

	public boolean conditionCheck(PostNotionToTistoryDto p){
		boolean testResult = false;

		if(p.getStatus().equals("발행요청") || p.getStatus().equals("발행실패")){
			// STEP1-1. 발행요청, 발행실패의 경우 blogName은 공백이 아니어야한다. tistoryId는 공백일수도 있다.
			if(!p.getBlogName().equals("")){
				// STEP2. requestLink는 'notion.so' 또는 'www.notion.so'를 포함한 링크여야한다.
				if(p.getRequestLink().contains("notion.so") || p.getRequestLink().contains("www.notion.so")){
					// STEP3-1. 발행요청, 발행실패의 경우 responseLink에 대해서는 검사하지 않는다.
					// STEP4. visibility는 비공개(0) 혹은 공개발행(3)이어야한다.
					if(p.getVisibility()==0 || p.getVisibility()==3){
						// STEP5. type은 'md' 혹은 'html'이어야한다.
						if(p.getType().equals("md") || p.getType().equals("html")){
							// STEP6. categoryName, tagList는 별도의 조건이 없다.
							testResult = true;
						}
					}
				}
			}
		}
		else if(p.getStatus().equals("수정요청") || p.getStatus().equals("수정실패")){
			// STEP1-2. 수정요청, 수정실패의 경우 title ,blogName, tistoryId는 공백이 아니어야한다.
			if(!p.getType().equals("") && !p.getBlogName().equals("") && p.getTistoryId()!=null && !p.getTistoryId().equals("")){
				// STEP2. requestLink는 'notion.so' 또는 'www.notion.so'를 포함한 링크여야한다.
				if(p.getRequestLink().contains("notion.so") || p.getRequestLink().contains("www.notion.so")){
					// STEP3. visibility는 비공개(0) 혹은 공개발행(3)이어야한다.
					if(p.getVisibility()==0 || p.getVisibility()==3){
						// STEP4. type은 'md' 혹은 'html'이어야한다.
						if(p.getType().equals("md") || p.getType().equals("html")){
							// STEP5. categoryName, tagList는 별도의 조건이 없다.
							testResult = true;
						}
					}
				}
			}
		}

		return testResult;
	}
	public boolean conditionCheckGithub(PostNotionToGithubDto p){
		boolean testResult = false;

		if(p.getStatus().equals("발행요청") || p.getStatus().equals("발행실패")){
			// STEP1-1. 발행요청, 발행실패의 경우 blogName은 공백이 아니어야한다. githubId는 공백일수도 있다.
			if(!p.getRepository().equals("")){
				// STEP2. requestLink는 'notion.so' 또는 'www.notion.so'를 포함한 링크여야한다.
				if(p.getRequestLink().contains("notion.so") || p.getRequestLink().contains("www.notion.so")){
					// STEP3-1. 발행요청, 발행실패의 경우 responseLink에 대해서는 검사하지 않는다.
						// STEP4. type은 'md' 혹은 'html'이어야한다.
						if(p.getType().equals("md") || p.getType().equals("html")){
							// STEP5. categoryName는 별도의 조건이 없다.
							testResult = true;
						}
				}
			}
		}
		else if(p.getStatus().equals("수정요청") || p.getStatus().equals("수정실패")){
			// STEP1-2. 수정요청, 수정실패의 경우 title ,repository, githubId은 공백이 아니어야한다.
			if(!p.getType().equals("") && !p.getRepository().equals("") && p.getGithubId()!=null && !p.getGithubId().equals("")){
				// STEP2. requestLink는 'notion.so' 또는 'www.notion.so'를 포함한 링크여야한다.
				if(p.getRequestLink().contains("notion.so") || p.getRequestLink().contains("www.notion.so")){
							// STEP3. type은 'md' 혹은 'html'이어야한다.
							if(p.getType().equals("md") || p.getType().equals("html")){
								// STEP4. categoryName은 별도의 조건이 없다.
								testResult = true;
							}
				}
			}
		}

		return testResult;
	}

	@Override
	public void upload(PostNotionToGithubDto githubPosting, Member member, String title, String content) throws IOException {
		System.out.println("upload 시작");
		System.out.println("upload title : " + title);

		RestTemplate rt = new RestTemplate();
		rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

		//파일명 중복 방지를 위한 현재 날짜, 시간
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Date nowDate = new Date();
		String fileDate = simpleDateFormat.format(nowDate);
		System.out.println("upload filedate : " + fileDate);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/vnd.github+json");
		headers.add("Authorization", "Bearer " + jasyptConfig.createEncryptor().decrypt(member.getToken().getGithubToken()));
		headers.add("X-GitHub-Api-Version", "2022-11-28");
		headers.setContentType(MediaType.APPLICATION_JSON);

		// String dirPath = "C:\\nogari-git-test\\git-clone-test\\upload.txt";
		// 	File gitDir = new File(dirPath);
		// 	// 업로드할 파일의 내용을 읽어옵니다.
		// 	byte[] fileBytes = Files.readAllBytes(gitDir.toPath());
		// 	String fileContent = Base64.getEncoder().encodeToString(fileBytes);

		String fileContent = Base64.getEncoder().encodeToString(content.getBytes());

		Map<String, String> body = new LinkedHashMap<>();
		body.put("message", "[UPLOAD] " + title);
		body.put("content", fileContent);

		//수정요청
		// if(githubPosting.getStatus().equals("수정요청")){
		// 	body.put("sha", githubPosting.getn);
		// }


		//committer 주석 처리
		// Map<String, String> address = new LinkedHashMap<>();
		// address.put("name", "encoreKwang");
		// address.put("email", "dnflrhkddyd@naver.com");
		// body.add("committer", address);

		HttpEntity<Map<String, String>> uploadRequest = new HttpEntity<>(body, headers);
		// String filePath = "C:\\nogari-git-test\\git-clone-test\\upload.txt";
		;
		String filePath = member.getGithubId() + "/" + githubPosting.getRepository() + "/contents/" +githubPosting.getCategoryName() +"/"+ title + "_" + fileDate + "."+ githubPosting.getType();
		System.out.println("filePath : " +  filePath );
		// String filePath = "/nogari2/titletmp2.txt";

		ResponseEntity<String> response = rt.exchange(
			// "https://api.github.com/repos/encoreKwang/PullRequestTest/contents" + filePath,
			"https://api.github.com/repos/" + filePath,
			HttpMethod.PUT,
			uploadRequest,
			String.class
		);

		String body1 = response.getBody();
		System.out.println(body1);
	}


	public String[] awsLambdaAndGithubPost(String notionToken, PostNotionToGithubDto githubPosting, Member member) {
		System.out.println("awsLambdaAndGithubPost 시작 =========");
		String title = ""; // Tistory에 게시될 게시글 제목
		String content = ""; // Tistory에 게시될 게시글 내용

		// STEP2-1. AWS Lambda와 통신하는 과정
		try{
			lambdaCallFunction = new LambdaCallFunction(
				notionToken,
				jasyptConfig.createEncryptor().decrypt(member.getToken().getGithubToken()),
				// member.getToken().getGithubToken(),
				githubPosting.getRepository(),
				githubPosting.getRequestLink(),
				githubPosting.getType()
			);
			content = lambdaCallFunction.gitPost();

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> data = objectMapper.readValue(content, Map.class);
				title = (String)data.get("title");
				content = (String)data.get("content");
			} catch(Exception e){
				e.printStackTrace();
				return null;
			}
		} catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("title : " + title);
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// STEP2-2. Github API를 이용하여 Github 포스팅을 진행한다.
		try {
			upload(githubPosting, member, title, content);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// RestTemplate rt = new RestTemplate();
		// HttpHeaders headers = new HttpHeaders();
		//
		// MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		// params.add("access_token", member.getToken().getTistoryToken());
		// params.add("output", "");
		// params.add("blogName", tistoryPosting.getBlogName()); // 블로그 이름
		// params.add("title", title); // 글 제목
		// params.add("content", content); // 글 내용
		// params.add("visibility", "3"); // 발행 상태 : 기본값(발행)
		// params.add("category", tistoryPosting.getCategoryName()); // 카테고리 아이디
		// params.add("published", ""); // 발행 시간
		// params.add("slogan", ""); // 문자 주소
		// params.add("tag", tistoryPosting.getTagList()); // 태그 리스트(','로 구분)
		// params.add("acceptComment", "1"); // 댓글 허용 :기본값(댓글 허용)
		// params.add("password", ""); // 보호글 비밀번호
		//
		// HttpEntity<MultiValueMap<String, String>> TistoryPostRequest = new HttpEntity<>(params, headers);
		//
		// // STEP2-3. Tistory API에 요청을 보내고, 응답 결과 중 Response URL을 DB에 반영한다.
		// ResponseEntity<String> response = rt.exchange(
		// 	"https://www.tistory.com/apis/post/write",
		// 	HttpMethod.POST,
		// 	TistoryPostRequest,
		// 	String.class
		// );
		//
		// String responseString = response.toString(); // Tistory API의 응답
		// String[] responseList = new String[3]; // responseLink와 postId를 함께 담아서 보낼 배열
		// String responseLink = ""; // Tistory에 게시된 게시글 링크
		// String postId = ""; // Tistory에 게시된 게시글 번호
		// Document doc = Jsoup.parse(responseString);
		// responseLink = doc.select("url").text();
		// postId = doc.select("postId").text();
		//
		// responseList[0] = responseLink;
		// responseList[1] = postId;
		// responseList[2] = title;
		// return responseList;
		return null;
	}

	// 	RestTemplate restTemplate = new RestTemplate();
	// 	restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
	// 	String dirPath = "C:\\nogari-git-test\\git-clone-test\\upload.txt";
	// 	File gitDir = new File(dirPath);
	// 	// 업로드할 파일의 내용을 읽어옵니다.
	// 	byte[] fileBytes = Files.readAllBytes(gitDir.toPath());
	// 	String fileContent = Base64.getEncoder().encodeToString(fileBytes);
	//
	// 	// 업로드할 파일의 경로를 지정합니다.
	// 	String filePath = "/path/to/upload.txt";
	//
	// 	// GitHub API 요청 URL을 생성합니다.
	// 	URI url = URI.create("https://api.github.com/repos/encoreKwang/PullRequestTest/contents/");
	// 	// url = UriComponentsBuilder.fromUri(url)
	// 	// 	.buildAndExpand("encoreKwang", "PullRequestTest", filePath)
	// 	// 	.toUri();
	//
	// 	// GitHub API 요청 본문을 생성합니다.
	// 	Map<String, String> request = new HashMap<>();
	// 	request.put("message", "upload file test");
	// 	request.put("content", fileContent);
	//
	// 	// GitHub API를 호출하여 파일을 업로드합니다.
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.setBearerAuth("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");
	// 	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
	// 	headers.setContentType(MediaType.APPLICATION_JSON);
	// 	HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
	// 	ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.PUT, entity, JsonNode.class);
	//
	// 	// GitHub API 응답 결과를 확인합니다.
	// 	if (response.getStatusCode().is2xxSuccessful()) {
	// 		System.out.println("File uploaded successfully.");
	// 	} else {
	// 		System.out.println("Failed to upload file: " + response.getBody().get("message").asText());
	// 	}
	// }

	// [Single Thread, Multi Thread 관련 Legacy Code]

	// [Single Thread] : 사용자가 N개의 발행 요청시, 작업이 순차적으로 수행되어 처리시간이 N개의 요청만큼 소요된다.
	// @Override
	// public Object postNotionToTistory(List<PostNotionToTistoryDto> postNotionToTistoryDtoList, Member member) {
	// 	String notionToken = member.getNotionToken();
	//
	// 	for(PostNotionToTistoryDto post : postNotionToTistoryDtoList){
	// 		String title = ""; // Tistory에 게시될 게시글 제목
	// 		String content = ""; // Tistory에 게시될 게시글 내용
	//
	// 		// [발행요청] DB 저장
	// 		Tistory tistory = Tistory.builder()
	// 			.blogName(post.getBlogName())
	// 			.requestLink(post.getRequestLink())
	// 			.visibility(post.getVisibility())
	// 			.categoryName(post.getCategoryName())
	// 			.tagList(post.getTagList())
	// 			.status("발행요청")
	// 			.title(post.getTitle())
	// 			.member(member)
	// 			.build();
	// 		tistoryRepository.save(tistory);
	//
	// 		// [변환진행] AWS Lambda와 통신하는 과정
	// 		try{
	// 			lambdaCallFunction = new LambdaCallFunction(
	// 				notionToken,
	// 				member.getToken().getTistoryToken(),
	// 				post.getBlogName(),
	// 				post.getRequestLink(),
	// 				post.getType()
	// 			);
	// 			content = lambdaCallFunction.post();
	//
	// 			try {
	// 				ObjectMapper objectMapper = new ObjectMapper();
	// 				Map<String, Object> data = objectMapper.readValue(content, Map.class);
	// 				title = (String)data.get("title");
	// 				content = (String)data.get("content");
	// 			} catch(Exception e){
	// 				e.printStackTrace();
	// 			}
	// 		} catch(IOException e){
	// 			e.printStackTrace();
	// 		}
	//
	// 		// [발행진행] Tistory API를 이용하여 Tistory 포스팅을 진행한다.
	// 		RestTemplate rt = new RestTemplate();
	// 		HttpHeaders headers = new HttpHeaders();
	//
	// 		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	// 		params.add("access_token", member.getToken().getTistoryToken());
	// 		params.add("output", "");
	// 		params.add("blogName", post.getBlogName()); // 블로그 이름
	// 		params.add("title", title); // 글 제목
	// 		params.add("content", content); // 글 내용
	// 		params.add("visibility", "3"); // 발행 상태 : 기본값(발행)
	// 		params.add("category", post.getCategoryName()); // 카테고리 아이디
	// 		params.add("published", ""); // 발행 시간
	// 		params.add("slogan", ""); // 문자 주소
	// 		params.add("tag", post.getTagList()); // 태그 리스트(','로 구분)
	// 		params.add("acceptComment", "1"); // 댓글 허용 :기본값(댓글 허용)
	// 		params.add("password", ""); // 보호글 비밀번호
	//
	// 		HttpEntity<MultiValueMap<String, String>> TistoryPostRequest = new HttpEntity<>(params, headers);
	//
	// 		ResponseEntity<String> response = rt.exchange(
	// 			"https://www.tistory.com/apis/post/write",
	// 			HttpMethod.POST,
	// 			TistoryPostRequest,
	// 			String.class
	// 		);
	// 		String responseString = response.getBody().toString();
	//
	// 		// [발행검증]
	// 		tistory.setStatus("발행완료");
	// 	}
	// 	return null;
	// }

	// public String[] awsLambdaAndTistoryModify(Long postId, String notionToken, PostNotionToTistoryDto post, Member member) throws Exception{
	// 	String title = ""; // Tistory에 게시될 게시글 제목
	// 	String content = ""; // Tistory에 게시될 게시글 내용
	//
	// 	// STEP2-1. AWS Lambda와 통신하는 과정
	// 	lambdaCallFunction = new LambdaCallFunction(
	// 		notionToken,
	// 		member.getToken().getTistoryToken(),
	// 		post.getBlogName(),
	// 		post.getRequestLink(),
	// 		post.getType()
	// 	);
	// 	content = lambdaCallFunction.post();
	//
	// 	ObjectMapper objectMapper = new ObjectMapper();
	// 	Map<String, Object> data = objectMapper.readValue(content, Map.class);
	// 	title = (String)data.get("title");
	// 	content = (String)data.get("content");
	//
	// 	// STEP2-2. Tistory API를 이용하여 Tistory 포스팅을 진행한다.
	// 	RestTemplate rt = new RestTemplate();
	// 	HttpHeaders headers = new HttpHeaders();
	//
	// 	MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	// 	params.add("access_token", member.getToken().getTistoryToken());
	// 	params.add("output", "");
	// 	params.add("blogName", post.getBlogName()); // 블로그 이름
	// 	params.add("postId", Long.toString(postId));
	// 	params.add("title", title); // 글 제목
	// 	params.add("content", content); // 글 내용
	// 	params.add("visibility", "3"); // 발행 상태 : 기본값(발행)
	// 	params.add("category", post.getCategoryName()); // 카테고리 아이디
	// 	params.add("published", ""); // 발행 시간
	// 	params.add("slogan", ""); // 문자 주소
	// 	params.add("tag", post.getTagList()); // 태그 리스트(','로 구분)
	// 	params.add("acceptComment", "1"); // 댓글 허용 :기본값(댓글 허용)
	// 	params.add("password", ""); // 보호글 비밀번호
	//
	// 	HttpEntity<MultiValueMap<String, String>> TistoryPostRequest = new HttpEntity<>(params, headers);
	//
	// 	// STEP2-3. Tistory API에 요청을 보내고, 응답 결과 중 Response URL을 DB에 반영한다.
	// 	ResponseEntity<String> response = rt.exchange(
	// 		"https://www.tistory.com/apis/post/modify",
	// 		HttpMethod.POST,
	// 		TistoryPostRequest,
	// 		String.class
	// 	);
	//
	// 	String responseString = response.toString(); // Tistory API의 응답
	// 	String[] responseList = new String[4]; // responseLink와 postId를 함께 담아서 보낼 배열
	// 	String responseLink = ""; // Tistory에 게시된 게시글 링크
	// 	String responsePostId = ""; // Tistory에 게시된 게시글 번호
	// 	Document doc = Jsoup.parse(responseString);
	// 	responseLink = doc.select("url").text();
	// 	responsePostId = doc.select("postId").text();
	//
	// 	responseList[0] = responseLink;
	// 	responseList[1] = responsePostId;
	// 	responseList[2] = title;
	//
	// 	for(int i=0; i<responseList.length; i++){
	// 		System.out.println("★★" + responseList[i]);
	// 	}
	//
	// 	return responseList;
	// }

	// public String[] awsLambdaAndTistoryPost(String notionToken, PostNotionToTistoryDto post, Member member) throws Exception {
	// 		String title = ""; // Tistory에 게시될 게시글 제목
	// 		String content = ""; // Tistory에 게시될 게시글 내용
	//
	// 		// STEP2-1. AWS Lambda와 통신하는 과정
	// 		try{
	// 			lambdaCallFunction = new LambdaCallFunction(
	// 				notionToken,
	// 				member.getToken().getTistoryToken(),
	// 				post.getBlogName(),
	// 				post.getRequestLink(),
	// 				post.getType()
	// 			);
	// 			content = lambdaCallFunction.post();
	//
	// 			try {
	// 				ObjectMapper objectMapper = new ObjectMapper();
	// 				Map<String, Object> data = objectMapper.readValue(content, Map.class);
	// 				title = (String)data.get("title");
	// 				content = (String)data.get("content");
	// 			} catch(Exception e){
	// 				e.printStackTrace();
	// 				return null;
	// 			}
	// 		} catch(IOException e){
	// 			e.printStackTrace();
	// 		}
	//
	// 		// STEP2-2. Tistory API를 이용하여 Tistory 포스팅을 진행한다.
	// 		RestTemplate rt = new RestTemplate();
	// 		HttpHeaders headers = new HttpHeaders();
	//
	// 		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
	// 		params.add("access_token", member.getToken().getTistoryToken());
	// 		params.add("output", "");
	// 		params.add("blogName", post.getBlogName()); // 블로그 이름
	// 		params.add("title", title); // 글 제목
	// 		params.add("content", content); // 글 내용
	// 		params.add("visibility", "3"); // 발행 상태 : 기본값(발행)
	// 		params.add("category", post.getCategoryName()); // 카테고리 아이디
	// 		params.add("published", ""); // 발행 시간
	// 		params.add("slogan", ""); // 문자 주소
	// 		params.add("tag", post.getTagList()); // 태그 리스트(','로 구분)
	// 		params.add("acceptComment", "1"); // 댓글 허용 :기본값(댓글 허용)
	// 		params.add("password", ""); // 보호글 비밀번호
	//
	// 		HttpEntity<MultiValueMap<String, String>> TistoryPostRequest = new HttpEntity<>(params, headers);
	//
	// 		// STEP2-3. Tistory API에 요청을 보내고, 응답 결과 중 Response URL을 DB에 반영한다.
	// 		ResponseEntity<String> response = rt.exchange(
	// 			"https://www.tistory.com/apis/post/write",
	// 			HttpMethod.POST,
	// 			TistoryPostRequest,
	// 			String.class
	// 		);
	//
	// 		String responseString = response.toString(); // Tistory API의 응답
	// 		String[] responseList = new String[3]; // responseLink와 postId를 함께 담아서 보낼 배열
	// 		String responseLink = ""; // Tistory에 게시된 게시글 링크
	// 		String postId = ""; // Tistory에 게시된 게시글 번호
	// 		Document doc = Jsoup.parse(responseString);
	// 		responseLink = doc.select("url").text();
	// 		postId = doc.select("postId").text();
	//
	// 		responseList[0] = responseLink;
	// 		responseList[1] = postId;
	// 		responseList[2] = title;
	//
	// 		return responseList;
	// }
}

