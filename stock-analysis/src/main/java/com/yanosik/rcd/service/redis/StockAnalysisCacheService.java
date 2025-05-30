package com.yanosik.rcd.service.redis;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.Analysis;
import com.yanosik.rcd.redis.CacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class StockAnalysisCacheService implements CacheService<Analysis> {
		private RedisTemplate<String, Analysis> analysisRedisTemplate;
		private RedisTemplate<String, Object> analysisStatusRedisTemplate;

		private static final String ANALYSIS_PREFIX = "analysis:";
		private static final String STATUS_PREFIX = "status:";
		private static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour

		@Override
		public void save(String key, Analysis data) {
				try {
						analysisRedisTemplate.opsForValue().set(key, data, Duration.ofSeconds(DEFAULT_TTL_SECONDS));
						log.info("Object saved for requestId: {}", key);
				} catch (Exception e) {
						log.error("Error saving object to Redis for requestId: {}", key, e);
						throw new RuntimeException("Failed to save object to cache", e);
				}
		}

		@Override
		public Optional<Analysis> get(String key) {
				try {
						String result = (String) analysisStatusRedisTemplate.opsForValue().get(STATUS_PREFIX + key);
						switch (result) {
								case "PROCESSING" -> {
										log.info("Analysis is still processing for requestId: {}", key);
										return Optional.empty();
								}
								case "FAILED" -> {
										log.error("Analysis failed for requestId: {}", key);
										return Optional.empty();
								}
								case "COMPLETED" -> {
										log.info("Analysis completed for requestId: {}", key);
										Analysis analysis = analysisRedisTemplate.opsForValue().get(ANALYSIS_PREFIX + key);
										return Optional.of(analysis);
								}
								default -> {
										log.error("Unknown status for requestId: {}", key);
										return Optional.empty();
								}
						}
				} catch (Exception e) {
						log.error("Error retrieving StockData from Redis for requestId: {}", key, e);
						return Optional.empty();
				}
		}

		@Override
		public boolean exists(String requestId) {
				try {
						return Boolean.TRUE.equals(analysisRedisTemplate.hasKey(requestId));
				} catch (Exception e) {
						log.error("Error checking existence for requestId: {}", requestId, e);
						return false;
				}
		}

		@Override
		public void delete(String key) {
				try {
						analysisRedisTemplate.delete(key);
						log.info("Object deleted for requestId: {}", key);
				} catch (Exception e) {
						log.error("Error deleting object from Redis for requestId: {}", key, e);
				}
		}

}