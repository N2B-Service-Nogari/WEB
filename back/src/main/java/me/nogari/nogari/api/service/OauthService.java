package me.nogari.nogari.api.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import me.nogari.nogari.api.response.OAuthAccessTokenResponse;
import me.nogari.nogari.entity.Member;

@Service
public interface OauthService {

	String getTistoryAccessToken(String code, Member member);

	String getKakaoAccessToken(String code, Member member);

	OAuthAccessTokenResponse getGithubAccessToken(String code, Member member);

	String getNotionAccessToken(String code, Member member);

	HashMap<String,Boolean> checkIfTokenIsEmpty(Member member);
}
