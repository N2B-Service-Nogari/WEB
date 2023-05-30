package me.nogari.nogari.api.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nogari.nogari.common.JWTDto;
import me.nogari.nogari.entity.Authority;
import me.nogari.nogari.entity.Member;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignResponseDto {

	private Long memberId;
	private String email;
	private String password;
	private String notionToken;
	private List<Authority> roles = new ArrayList<>();
	private JWTDto token;

	public SignResponseDto(Member member) {
		this.memberId = member.getMemberId();
		this.email = member.getEmail();
		this.password = member.getPassword();
		this.notionToken = member.getNotionToken();
		this.roles = member.getRoles();
	}
}
