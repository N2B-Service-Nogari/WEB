package me.nogari.nogari.common;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtil {
	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisTemplate<String, Object> redisBlackListTemplate;

	public void set(String key, Object o, int minutes) {
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(o.getClass()));
		redisTemplate.opsForValue().set(key, o, minutes, TimeUnit.MINUTES);
	}

	public Object get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public boolean delete(String key) {
		return redisTemplate.delete(key);
	}

	public boolean hasKey(String key) {
		return redisTemplate.hasKey(key);
	}

	public void setBlackList(String key, Object o, Long milliSeconds) {
		// Jackson 라이브러리를 사용하여 Java 객체를 JSON 문자열로 직렬화하고, 역직렬화할 때 JSON 문자열을 Java 객체로 변환
		redisBlackListTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(o.getClass()));
		redisBlackListTemplate.opsForValue().set(key, o, milliSeconds, TimeUnit.MILLISECONDS);
	}

	public Object getBlackList(String key) {
		return redisBlackListTemplate.opsForValue().get(key);
	}

	public boolean deleteBlackList(String key) {
		return redisBlackListTemplate.delete(key);
	}

	public boolean hasKeyBlackList(String key) {
		return redisBlackListTemplate.hasKey(key);
	}
}