package me.nogari.nogari.common;

import java.util.concurrent.TimeUnit;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@RedisHash("refreshToken")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JWT {

	@Id
	@JsonIgnore
	private Long id;

	private String refresh_token;

	@TimeToLive(unit = TimeUnit.DAYS)        // 토큰의 TTL 결정
	private Integer expiration;

	public void setExpiration(Integer expiration) {
		this.expiration = expiration;
	}
}
