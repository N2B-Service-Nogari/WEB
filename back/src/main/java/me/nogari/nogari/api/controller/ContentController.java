package me.nogari.nogari.api.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.nogari.nogari.api.request.PaginationDto;
import me.nogari.nogari.api.request.PostNotionToGithubDto;
import me.nogari.nogari.api.request.PostNotionToTistoryDto;
import me.nogari.nogari.api.response.BaseResponse;
import me.nogari.nogari.api.service.ContentServiceImpl;
import me.nogari.nogari.common.security.CustomUserDetails;
import me.nogari.nogari.config.JasyptConfig;
import me.nogari.nogari.entity.Member;
import me.nogari.nogari.entity.Token;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentController {
	@Autowired
	private ContentServiceImpl contentService;

	@Autowired
	private JasyptConfig jasyptConfig;

	@ResponseBody
	@PostMapping("/tistory")
	@Operation(summary = "티스토리 무한스크롤 발행 내역 리스트 검색 및 조회")
	public BaseResponse<Object> getTistoryListByFilter(@RequestBody PaginationDto paginationDto,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		// security session에 있는 유저 정보를 가져온다
		Member member;
		try {
			member = customUserDetails.getMember();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("로그인된 사용자가 없습니다!")
				.build();
		}

		try {
			return BaseResponse.builder()
				.result(contentService.getTistoryList(paginationDto, member))
				.resultCode(HttpStatus.OK.value())
				.resultMsg("정상적으로 티스토리 발행 내역 조회 성공")
				.build();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("티스토리 발행 내역 조회 실패")
				.build();
		}
	}

	@ResponseBody
	@PostMapping("/github")
	@Operation(summary = "깃허브 무한스크롤 발행 내역 리스트 검색 및 조회")
	public BaseResponse<Object> getGithubListByFilter(@RequestBody PaginationDto paginationDto,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		// security session에 있는 유저 정보를 가져온다
		Member member;
		try {
			member = customUserDetails.getMember();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("로그인된 사용자가 없습니다!")
				.build();
		}
		try {
			return BaseResponse.builder()
				.result(contentService.getGithubList(paginationDto, member))
				.resultCode(HttpStatus.OK.value())
				.resultMsg("정상적으로 깃허브 발행 내역 조회 성공")
				.build();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("깃허브 발행 내역 조회 실패")
				.build();
		}
	}


	@ResponseBody
	@GetMapping("/git/clone")
	@Operation(summary = "github repository clone")
	public void gitCloneRepo(@RequestParam  String repositoryName,
		@AuthenticationPrincipal CustomUserDetails customUserDetails){
		//매개변수로 레포지토리 name, github email 받아야할듯
		//github id
		//


		//String ATK = "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";

		StringEncryptor newStringEncryptor = jasyptConfig.createEncryptor();
		Member member = customUserDetails.getMember();
		Token memberToken = member.getToken();
		String ATK = newStringEncryptor.decrypt(memberToken.getGithubToken());

		//create git folder
		// File gitDir = new File("C:\\nogari-git-test\\git-clone-test");
		File gitDir = new File("/home/" + repositoryName);
		if (gitDir.exists()) {
			try {
				FileUtils.deleteDirectory(gitDir);
			}catch (Exception e){
				System.out.println("deleteDirectory ERROR");
			}
		}
		System.out.println("Current working directory: " + System.getProperty("user.dir"));


		if (gitDir.mkdirs()) {
			System.out.println("dir create success");
		}else {
			System.out.println("dir create failed: " + gitDir.getAbsolutePath());
		}

		//set username, access token
		CredentialsProvider credentialsProvider
			= new UsernamePasswordCredentialsProvider(
			"dnflrhkddyd@naver.com"
			, ATK); //access token


		try {
			//clone
			Git git = Git.cloneRepository()
				.setURI("https://github.com/encoreKwang/PullRequestTest")
				.setCredentialsProvider(credentialsProvider)
				.setDirectory(gitDir)
				.call();
			git.close();
			System.out.println("Git clone completed: " + gitDir.getAbsolutePath());

		} catch (Exception e){
			e.printStackTrace();
			System.out.println("git clone REPO ERROR");
		}

		//		contentService.githubConnectionTest();

	}

	@ResponseBody
	@GetMapping("/git/add")
	@Operation(summary = "github add")
	public void gitAdd() throws GitAPIException, IOException {
		//git repo path
		String dirPath = "C:\\nogari-git-test\\git-clone-test";
		File gitDir = new File(dirPath);

		//create temp file
		String fileName = UUID.randomUUID().toString();
		File file = new File(dirPath + "\\" + fileName + ".txt");
		FileUtils.writeStringToFile(file, "testing it...", StandardCharsets.UTF_8);

		//add
		Git git = Git.open(gitDir);

		AddCommand add = git.add();
		add.addFilepattern(fileName + ".txt").call();

		git.close();
	}

	@ResponseBody
	@GetMapping("/git/upload")
	@Operation(summary = "github upload")
	public void gitUpload() throws GitAPIException, IOException {
		// contentService.upload();
	}

	@ResponseBody
	@GetMapping("/git/commit")
	@Operation(summary = "github commit")
	public void gitCommit() throws GitAPIException, IOException {
		//git repo path
		String dirPath = "C:\\nogari-git-test\\git-clone-test";
		File gitDir = new File(dirPath);

		//commit
		Git git = Git.open(gitDir);
		git.commit().setMessage("JGIT commit test").call();

		git.close();
	}

	@ResponseBody
	@GetMapping("/git/push")
	@Operation(summary = "github push")
	public void gitPush() throws GitAPIException, IOException {
		String ATK = "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";

		//git repo path
		String dirPath = "C:\\nogari-git-test\\git-clone-test";
		File gitDir = new File(dirPath);

		//set username, access token
		CredentialsProvider credentialsProvider
			= new UsernamePasswordCredentialsProvider(
			"dnflrhkddyd@naver.com"
			, ATK); //access token

		//push
		Git git = Git.open(gitDir);
		git.push()
			.setCredentialsProvider(credentialsProvider)
			.setRemote("origin")
			.setRefSpecs(new RefSpec("master"))
			.call();

		git.close();
	}

	@ResponseBody
	@GetMapping("/git/repoList")
	@Operation(summary = "get github repo list")
	public List<String> gitRepoList(
		@AuthenticationPrincipal CustomUserDetails customUserDetails) throws GitAPIException, IOException {

		StringEncryptor newStringEncryptor = jasyptConfig.createEncryptor();
		Member member = customUserDetails.getMember();
		String ATK = newStringEncryptor.decrypt(member.getToken().getGithubToken());

		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(ATK);

		// RepositoryService 생성
		RepositoryService repoService = new RepositoryService(client);

		List<String> repositoryList = new ArrayList<>();
		// 유저의 repositories 리스트 가져오기
		List<Repository> repositories = repoService.getRepositories();
		for (Repository repo : repositories) {
			System.out.println(repo.getName());
			repositoryList.add(repo.getName());
		}

		return repositoryList;
	}
	@ResponseBody
	@PostMapping("/post")
	@Operation(summary = "노션 게시글 티스토리 발행")
	public BaseResponse<Object> postNotionToTistory(
		@RequestBody List<PostNotionToTistoryDto> postNotionToTistoryDtoList,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		// security session에 있는 유저 정보를 가져온다
		Optional<Member> member;
		try {
			member = Optional.ofNullable(customUserDetails.getMember());
		} catch (Exception e) {
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("로그인된 사용자가 없습니다.")
				.build();
		}
		try {
			return BaseResponse.builder()
				.result(contentService.postNotionToTistoryMultiThread(postNotionToTistoryDtoList, member.get()))
				.resultCode(HttpStatus.OK.value())
				.resultMsg("Nogari Tistory POST API가 정상적으로 실행되었습니다.")
				.build();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("Nogari Tistory POST API가 실행되는 과정에서 오류가 발생했습니다.")
				.build();
		}
	}

	@ResponseBody
	@PostMapping("/git/post")
	@Operation(summary = "깃허브 게시글 티스토리 발행")
	public BaseResponse<Object> postNotionToGithub(
		@RequestBody List<PostNotionToGithubDto> postNotionToGithubDtoList,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {

		Optional<Member> member;
		try {
			member = Optional.ofNullable(customUserDetails.getMember());
		} catch (Exception e) {
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("로그인된 사용자가 없습니다.")
				.build();
		}
		try {
			return BaseResponse.builder()
				.result(contentService.postNotionToGithubMultiThread(postNotionToGithubDtoList, member.get()))
				.resultCode(HttpStatus.OK.value())
				.resultMsg("노션 게시글을 깃허브로 정상적으로 발행했습니다.")
				.build();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("노션 게시글을 깃허브로 발행하는데 실패했습니다.")
				.build();
		}
	}
}
