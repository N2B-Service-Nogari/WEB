package me.nogari.nogari.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.nogari.nogari.api.request.LoginRequestDto;
import me.nogari.nogari.api.request.SignRequestDto;
import me.nogari.nogari.api.response.BaseResponse;
import me.nogari.nogari.api.response.SignResponseDto;
import me.nogari.nogari.api.service.MemberService;
import me.nogari.nogari.common.JWTDto;
import me.nogari.nogari.common.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/login")
	@Operation(summary = "회원 로그인")
	public BaseResponse<Object> login(@RequestBody LoginRequestDto request) throws Exception {

		return BaseResponse.builder()
			.result(memberService.login(request))
			.resultCode(HttpStatus.OK.value())
			.resultMsg("로그인 성공")
			.build();
	}

	@PostMapping("/logout")
	@Operation(summary = "회원 로그아웃")
	public BaseResponse<Object> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@RequestBody JWTDto jwtDto) {

		return BaseResponse.builder()
			.result(memberService.logout(customUserDetails.getMember().getMemberId(), jwtDto))
			.resultCode(HttpStatus.OK.value())
			.resultMsg("로그아웃 성공")
			.build();
	}

	@PostMapping("/signup")
	@Operation(summary = "회원가입")
	public BaseResponse<Object> signup(@RequestBody SignRequestDto request) throws Exception {

		try {
			return BaseResponse.builder()
				.result(memberService.signup(request))
				.resultCode(HttpStatus.OK.value())
				.resultMsg("회원가입 성공")
				.build();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.BAD_REQUEST.value())
				.resultMsg("중복된 email입니다")
				.build();
		}
	}

	@GetMapping("/user/get")
	@Operation(summary = "회원 정보 조회")
	public ResponseEntity<SignResponseDto> getUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) throws
		Exception {
		return new ResponseEntity<>(memberService.getMember(customUserDetails.getMember().getEmail()), HttpStatus.OK);
	}

	@GetMapping("/admin/get")
	@Operation(summary = "관리자 정보 조회")
	public ResponseEntity<SignResponseDto> getUserForAdmin(
		@AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception {
		return new ResponseEntity<>(memberService.getMember(customUserDetails.getMember().getEmail()), HttpStatus.OK);
	}

	@GetMapping("/duplicate")
	@Operation(summary = "이메일 중복검사")
	public BaseResponse<Object> checkEmailDuplicate(@RequestParam String email) {
		boolean status = memberService.checkEmailDuplicate(email);
		if (status) {
			return BaseResponse.builder()
				.result(status)
				.resultCode(HttpStatus.OK.value())
				.resultMsg("사용할 수 없는 이메일입니다.")
				.build();
		}
		return BaseResponse.builder()
			.result(status)
			.resultCode(HttpStatus.OK.value())
			.resultMsg("사용할 수 있는 이메일입니다.")
			.build();

	}

	@PostMapping("/refresh")
	@Operation(summary = "토큰 재발급")
	public BaseResponse<Object> refresh(@RequestBody JWTDto jwt) throws Exception {
		try {
			return BaseResponse.builder()
				.result(memberService.refreshAccessToken(jwt))
				.resultCode(HttpStatus.OK.value())
				.resultMsg("refresh 토큰이 만료되지 않아 access 토큰이 재발급 되었습니다.")
				.build();
		} catch (Exception e) {
			return BaseResponse.builder()
				.result(null)
				.resultCode(HttpStatus.REQUEST_TIMEOUT.value())
				.resultMsg("refresh 토큰이 만료되었습니다. login을 다시 해주세요")
				.build();
		}
	}
}
