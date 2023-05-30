package me.nogari.nogari.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Swagger springdoc-ui 구성 파일
 */
@Configuration
@EnableWebMvc
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
			.title("Nogari API Document")
			.version("v1.0.0")
			.description("SSAFY 자율 프로젝트 Nogari의 API 명세서입니다.");

		// SecuritySecheme명
		String jwtSchemeName = "bearer-key";
		// API 요청헤더에 인증정보 포함
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
		// SecuritySchemes 등록
		Components components = new Components()
			.addSecuritySchemes(jwtSchemeName, new SecurityScheme()
				.name(jwtSchemeName)
				.type(SecurityScheme.Type.HTTP) // HTTP 방식
				.scheme("bearer")
				.bearerFormat("JWT")); // 토큰 형식을 지정하는 임의의 문자(Optional)

		return new OpenAPI()
			.addServersItem(new Server().url("http://localhost:8080/api/v1"))
			.addServersItem(new Server().url("https://www.nogari.me/api/v1"))
			.components(components)
			.addSecurityItem(securityRequirement)
			.info(info);
	}
}