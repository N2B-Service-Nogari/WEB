package me.nogari.nogari.common.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import me.nogari.nogari.common.RedisUtil;

/**
 * Jwt가 유효성을 검증하는 Filter
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final RedisUtil redisUtil;

	public JwtAuthenticationFilter(JwtProvider jwtProvider, RedisUtil redisUtil) {
		this.jwtProvider = jwtProvider;
		this.redisUtil = redisUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String token = jwtProvider.resolveToken(request);

		if (token != null && jwtProvider.validateToken(token)) {
			// check access token
			token = token.split(" ")[1].trim();
			Authentication auth = jwtProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(auth);

			// blacklist 여부 확인
			if (redisUtil.hasKeyBlackList(token)) {
				return;
			}
		}

		filterChain.doFilter(request, response);
	}
}