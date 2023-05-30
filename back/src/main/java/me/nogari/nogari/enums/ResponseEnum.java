// package com.ssafy.tlens.enums;
//
// import lombok.AllArgsConstructor;
// import lombok.Getter;
//
// @Getter
// @AllArgsConstructor
// public enum ResponseEnum {
// 	AUTH_BAD_REQUEST(403, "bad request"),
// 	AUTH_INVALID_TOKEN(401, "invalid token"),
// 	AUTH_NOT_JOINED(405, "not joined user"),
// 	AUTH_ACCESS_EXPIRED(499, "AUTH_ACCESS_EXPIRED"),
// 	AUTH_REFRESH_EXPIRED(444, "AUTH_REFRESH_EXPIRED"),
// 	AUTH_AUTHORITY_DENIED(445, "AUTH_AUTHORITY_DENIED"),
//
// 	USER_USERNAME_CK_SUCCESS(200, "사용가능한 아이디입니다."),
// 	USER_JOIN_SUCCESS(200, "회원가입에 성공하였습니다."),
// 	USER_JOIN_FAIL(500, "다시 시도해주세요."),
//
// 	USER_MY_INFO_SUCCESS(200,"조회 성공"),
// 	USER_LOGOUT_SUCCESS(200,"로그아웃 성공"),
// 	USER_PROFILE_CHANGE_SUCCESS(200, "변경이 완료되었습니다"),
// 	PRODUCT_CATEGORY_SUCCESS(200,"조회에 성공하였습니다"),
// 	PRODUCT_SEARCH_SUCCESS(200,"조회에 성공하였습니다"),
// 	PRODUCT_SEARCH_FAIL(400,"조회에 실패하였습니다"),
//
// 	USER_DELETE_SUCCESS(200,"유저 성공"),
//
// 	REDIS_USER_NOT_FOUND(401,"Redis에 해당 USER가 존재하지 않습니다."),
// 	RTK_NOT_MATCHED(401,"Redis에 저장된 해당 USER의 RTK와 요청 받은 RTK가 일치하지 않습니다."),
// 	ATK_REISSUE_SUCCESS(200,"ATK 재발급 성공"),
// 	TOKEN_TYPE_NOT_FOUND(401,"토큰의 타입이 존재하지 않습니다"),
// 	FAIL_CONFIRM_PWD(500,"비밀번호를 다시 확인해주세요"),
// 	FAIL_DELETE_USER(500,"회원 삭제에 실패했습니다")
// 	;
//
//
// 	private final int code;
// 	private final String message;
//
// }