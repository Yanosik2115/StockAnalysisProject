package com.yanosik.rcd.redis;

import java.util.Optional;

public interface CacheService<T> {
		void save(String key, T data);
		Optional<T> get(String key);
		boolean exists(String key);
		void delete(String key);
}