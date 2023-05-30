package me.nogari.nogari.common.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import me.nogari.nogari.common.RedisUtil;
import me.nogari.nogari.config.CorsConfig;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	private final JwtProvider jwtProvider;
	private final RedisUtil redisUtil;
	@Autowired
	private CorsConfig corsConfig;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.addFilter(corsConfig.corsFilter())
			// ID, Password 문자열을 Base64로 인코딩하여 전달하는 구조
			.httpBasic().disable()
			// 쿠키 기반이 아닌 JWT 기반이므로 사용하지 않음
			.csrf().disable()
			// CORS 설정
			//			.cors(c -> {
			//					CorsConfigurationSource source = request -> {
			//						// Cors 허용 패턴
			//						CorsConfiguration config = new CorsConfiguration();
			//						config.setAllowedOrigins(
			//							List.of("*")
			//						);
			//						config.setAllowedMethods(
			//							List.of("*")
			//						);
			//						return config;
			//					};
			//					c.configurationSource(source);
			//				}
			//			)
			// Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			// 조건별로 요청 허용/제한 설정
			.authorizeRequests()
			.antMatchers("/members/signup", "/members/login", "/members/duplicate", "/members/refresh").permitAll()
			.antMatchers("/admin/**").hasRole("ADMIN")
			.antMatchers("/user/**").hasRole("USER")
			.antMatchers("/members/logout").hasRole("USER")
			.antMatchers("/contents/tistory").hasRole("USER")
			.antMatchers("/members/user/get").hasRole("USER")
			.antMatchers("/members/admin/get").hasRole("ADMIN")
			.antMatchers("/oauth/**").hasRole("USER")
			.antMatchers("/contents/**").hasRole("USER")
			.antMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
			.anyRequest().denyAll()
			.and()
			// JWT 인증 필터 적용
			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider, redisUtil),
				UsernamePasswordAuthenticationFilter.class)
			// 에러 핸들링
			.exceptionHandling()
			.accessDeniedHandler(new AccessDeniedHandler() {
				@Override
				public void handle(HttpServletRequest request, HttpServletResponse response,
					AccessDeniedException accessDeniedException) throws IOException, ServletException {
					// 권한 문제가 발생했을 때 이 부분을 호출한다.
					response.setStatus(403);
					response.setCharacterEncoding("utf-8");
					response.setContentType("text/html; charset=UTF-8");
					response.getWriter().write("권한이 없는 사용자입니다.");
				}
			})
			.authenticationEntryPoint(new AuthenticationEntryPoint() {
				@Override
				public void commence(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException authException) throws IOException, ServletException {

					// System.out.println(request.getHeader("Authorization"));
					// 인증문제가 발생했을 때 이 부분을 호출한다.
					response.setStatus(401);
					response.setCharacterEncoding("utf-8");
					response.setContentType("text/html; charset=UTF-8");
					response.getWriter().write("인증되지 않은 사용자입니다.");
				}
			});

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers(
			/* swagger v2 */
			// "/v2/api-docs",
			// "/swagger-resources",
			// "/swagger-resources/**",
			// "/configuration/ui",
			// "/configuration/security",
			// "/swagger-ui.html",
			// "/webjars/**",
			/* swagger v3 */
			"/v3/api-docs/**",
			"/swagger-ui/**");
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(4);
	}

}