package me.nogari.nogari.api.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import me.nogari.nogari.api.request.CodeRequestDto;
import me.nogari.nogari.api.response.BaseResponse;
import me.nogari.nogari.api.response.OAuthAccessTokenResponse;
import me.nogari.nogari.api.service.OauthService;
import me.nogari.nogari.common.security.CustomUserDetails;
import me.nogari.nogari.entity.Member;

@RestController
@AllArgsConstructor
@RequestMapping("/oauth")
@Tag(name = "OAuthController", description = "플랫폼 인증 서비스")
public class OAuthController {

	@Autowired
	private OauthService oauthService;

	@ResponseBody
	@PostMapping("/tistory")
	@Operation(summary = "티스토리 토큰 발급")
	public BaseResponse<Object> tistoryCallBack(@RequestBody CodeRequestDto codeDto,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		String code = codeDto.getCode();
		// security session에 있는 유저 정보를 가져온다
		Optional<Member> member;
		// System.out.println(customUserDetails);
		try {
			member = Optional.ofNullable(customUserDetails.getMember());
		} catch (Exception e) {
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("로그인된 사용자가 없습니다.")
				.build();
		}

		// 인가코드 받기
		// System.out.println("code: " + code);

		// 엑세스토큰 (access token) 받기
		try {
			return BaseResponse.builder()
				.result(oauthService.getTistoryAccessToken(code, member.get()))
				.resultCode(HttpStatus.OK.value())
				.resultMsg("정상적으로 티스토리 엑세스 토큰 얻기 성공")
				.build();

		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				// .result(oauthService.getTistoryAccessToken(code, member.get()))
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("티스토리 엑세스 토큰 얻기에 실패")
				.build();
		}
	}

	@ResponseBody
	@PostMapping("/git")
	@Operation(summary = "깃허브 토큰 발급")
	public BaseResponse<Object> getGithubAccessToken(@RequestBody CodeRequestDto codeDto,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		String code = codeDto.getCode();
		// System.out.println("code: " + code);

		// 깃허브 서버에 엑세스토큰 (access token) 받기
		try {
			Member member = customUserDetails.getMember();
			OAuthAccessTokenResponse tokenResponse = oauthService.getGithubAccessToken(code, member);
			String ATK = tokenResponse.getAccessToken();
			// System.out.println("ATK : " + ATK);
			return BaseResponse.builder()
				.result(ATK)
				.resultCode(HttpStatus.OK.value())
				.resultMsg("정상적으로 깃허브 엑세스 토큰 얻기 성공")
				.build();
		}catch (Exception e){
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("깃허브 엑세스 토큰 얻기에 실패")
				.build();
		}
	}

	@ResponseBody
	@PostMapping("/notion")
	@Operation(summary = "노션 토큰 발급")
	public BaseResponse<Object> getNotionAccessToken(@RequestBody CodeRequestDto codeDto,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		String code = codeDto.getCode();
		// System.out.println("notion code: " + code);

		try {
			Member member = customUserDetails.getMember();
			String ATK = oauthService.getNotionAccessToken(code, member);
			// System.out.println("ATK : " + ATK);
			return BaseResponse.builder()
				.result(ATK)
				.resultCode(HttpStatus.OK.value())
				.resultMsg("정상적으로 노션 엑세스 토큰 얻기 성공")
				.build();
		} catch (Exception e) {
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("노션 엑세스 토큰 얻기에 실패")
				.build();
		}
	}

	@ResponseBody
	@GetMapping("/check")
	@Operation(summary = "노션, 티스토리, 깃헙 토큰 유무 확인")
	public BaseResponse<Object> checkIfTokenIsEmpty(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		try {
			Member member = customUserDetails.getMember();

			return BaseResponse.builder()
				.result(oauthService.checkIfTokenIsEmpty(member))
				.resultCode(HttpStatus.OK.value())
				.resultMsg("정상적으로 토큰 유무 성공")
				.build();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("토큰 유무 확인 실패")
				.build();
		}
	}

}
