package com.yanosik.rcd.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanosik.rcd.dto.StockDataDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

		@Bean
		public RedisConnectionFactory redisConnectionFactory() {
				RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
				config.setPassword("eYVX7EwVmmxKPCDmwMtyKVge8oLd2t81");
				return new LettuceConnectionFactory(config);		}

		@Bean
		public RedisTemplate<String, Object> redisTemplate() {
				RedisTemplate<String, Object> template = new RedisTemplate<>();
				template.setConnectionFactory(redisConnectionFactory());
				template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
				template.setKeySerializer(new StringRedisSerializer());
				return template;
		}

		@Bean
		public RedisTemplate<String, StockDataDto> stockDataRedisTemplate(ObjectMapper objectMapper) {
				RedisTemplate<String, StockDataDto> template = new RedisTemplate<>();
				template.setConnectionFactory(redisConnectionFactory());

				Jackson2JsonRedisSerializer<StockDataDto> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, StockDataDto.class);

				template.setKeySerializer(new StringRedisSerializer());
				template.setValueSerializer(serializer);
				template.setHashKeySerializer(new StringRedisSerializer());
				template.setHashValueSerializer(serializer);

				template.afterPropertiesSet();
				return template;
		}
}