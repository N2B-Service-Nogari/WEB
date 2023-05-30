package me.nogari.nogari.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.nogari.nogari.entity.Member;
import me.nogari.nogari.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Member member = memberRepository.findAllByEmail(username).orElseThrow(
			() -> new UsernameNotFoundException("Invalid authentication!")
		);

		return new CustomUserDetails(member);
	}
}
