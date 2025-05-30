package com.yanosik.rcd.service;

import com.yanosik.rcd.model.AnalysisRequest;
import com.yanosik.rcd.model.AnalysisType;
import com.yanosik.rcd.utils.Utilities;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@AllArgsConstructor
public class AnalysisOrchestrator {

		private final KafkaTemplate<String, AnalysisRequest> kafkaAnalysisRequestTemplate;
		private final RedisTemplate<String, Object> redisTemplate;
		private static final String STATUS_PREFIX = "status:";


		public String triggerAnalysis(@NonNull String symbol, @NonNull AnalysisType analysisType, Map<String, String> parameters) {
				String requestId = Utilities.generateId();
				AnalysisRequest analysisRequest = new AnalysisRequest(requestId, symbol, analysisType, LocalDateTime.now(), parameters);
				kafkaAnalysisRequestTemplate.send(analysisType.getTopicName(), analysisRequest);
				String statusKey = STATUS_PREFIX + requestId;
				redisTemplate.opsForValue().set(statusKey, "TRIGGERED");
				return requestId;
		}
}
