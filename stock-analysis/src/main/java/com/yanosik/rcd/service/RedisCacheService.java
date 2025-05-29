package com.yanosik.rcd.service;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.Analysis;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class RedisCacheService {
		private RedisTemplate<String, StockDataDto> stockDataRedisTemplate;
		private RedisTemplate<String, Object> redisTemplate;
		private RedisTemplate<String, Analysis> analysisRedisTemplate;

		private static final String ANALYSIS_PREFIX = "analysis:";
		private static final String STATUS_PREFIX = "status:";
		private static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour

		public void saveStockData(String requestId, StockDataDto data) {
				try {
						stockDataRedisTemplate.opsForValue().set(requestId, data, Duration.ofSeconds(DEFAULT_TTL_SECONDS));
						log.info("Object saved for requestId: {}", requestId);
				} catch (Exception e) {
						log.error("Error saving object to Redis for requestId: {}", requestId, e);
						throw new RuntimeException("Failed to save object to cache", e);
				}
		}

		public Optional<StockDataDto> getStockData(String requestId) {
				try {
						StockDataDto result = stockDataRedisTemplate.opsForValue().get(requestId);
						log.info("StockData retrieved for requestId: {}", requestId);
						return Optional.of(result);
				} catch (Exception e) {
						log.error("Error retrieving StockData from Redis for requestId: {}", requestId, e);
						return Optional.empty();
				}
		}

		public Optional<Analysis> getAnalysisResult(String requestId) {
				try {
						String result = (String) redisTemplate.opsForValue().get(STATUS_PREFIX + requestId);
						switch (result) {
								case "PROCESSING" -> {
										log.info("Analysis is still processing for requestId: {}", requestId);
										return Optional.empty();
								}
								case "FAILED" -> {
										log.error("Analysis failed for requestId: {}", requestId);
										return Optional.empty();
								}
								case "COMPLETED" -> {
										log.info("Analysis completed for requestId: {}", requestId);
										Analysis analysis = analysisRedisTemplate.opsForValue().get(ANALYSIS_PREFIX + requestId);
										return Optional.of(analysis);
								}
								default -> {
										log.error("Unknown status for requestId: {}", requestId);
										return Optional.empty();
								}
						}
				} catch (Exception e) {
						log.error("Error retrieving StockData from Redis for requestId: {}", requestId, e);
						return Optional.empty();
				}
		}

		public boolean exists(String requestId) {
				try {
						return Boolean.TRUE.equals(stockDataRedisTemplate.hasKey(requestId));
				} catch (Exception e) {
						log.error("Error checking existence for requestId: {}", requestId, e);
						return false;
				}
		}

		public void deleteObject(String requestId) {
				try {
						stockDataRedisTemplate.delete(requestId);
						log.info("Object deleted for requestId: {}", requestId);
				} catch (Exception e) {
						log.error("Error deleting object from Redis for requestId: {}", requestId, e);
				}
		}
}