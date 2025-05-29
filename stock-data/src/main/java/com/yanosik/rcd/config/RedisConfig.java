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
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

		@Bean
		public RedisConnectionFactory redisConnectionFactory() {
				RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
				config.setPassword("eYVX7EwVmmxKPCDmwMtyKVge8oLd2t81");
				return new LettuceConnectionFactory(config);
		}

		@Bean
		public RedisTemplate<String, StockDataDto> redisStockDataTemplate(ObjectMapper objectMapper) {
				RedisTemplate<String, StockDataDto> template = new RedisTemplate<>();
				template.setConnectionFactory(redisConnectionFactory());

				GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

				template.setDefaultSerializer(serializer);
				template.setKeySerializer(new StringRedisSerializer());
				template.setValueSerializer(serializer);
				template.setHashKeySerializer(new StringRedisSerializer());
				template.setHashValueSerializer(serializer);

				template.afterPropertiesSet();
				return template;
		}
}