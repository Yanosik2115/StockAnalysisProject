package com.yanosik.rcd.service;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.redis.CacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class RedisStockDataDtoCacheService implements CacheService<StockDataDto> {
		private RedisTemplate<String, StockDataDto> stockDataRedisTemplate;
		private static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour

		@Override
		public void save(String key, StockDataDto data) {
				try {
						stockDataRedisTemplate.opsForValue().set(key, data, Duration.ofSeconds(DEFAULT_TTL_SECONDS));
						log.info("Object saved for requestId: {}", key);
				} catch (Exception e) {
						log.error("Error saving object to Redis for requestId: {}", key, e);
						throw new RuntimeException("Failed to save object to cache", e);
				}
		}

		@Override
		public Optional<StockDataDto> get(String key) {
				try {
						StockDataDto result = stockDataRedisTemplate.opsForValue().get(key);
						log.info("StockData retrieved for requestId: {}", key);
						return Optional.of(result);
				} catch (Exception e) {
						log.error("Error retrieving StockData from Redis for requestId: {}", key, e);
						return Optional.empty();
				}
		}

		@Override
		public boolean exists(String requestId) {
				try {
						return Boolean.TRUE.equals(stockDataRedisTemplate.hasKey(requestId));
				} catch (Exception e) {
						log.error("Error checking existence for requestId: {}", requestId, e);
						return false;
				}
		}

		@Override
		public void delete(String key) {
				try {
						stockDataRedisTemplate.delete(key);
						log.info("Object deleted for requestId: {}", key);
				} catch (Exception e) {
						log.error("Error deleting object from Redis for requestId: {}", key, e);
				}
		}
}