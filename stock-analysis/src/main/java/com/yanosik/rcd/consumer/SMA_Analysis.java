package com.yanosik.rcd.consumer;

import com.yanosik.rcd.enums.AnalysisStatus;
import com.yanosik.rcd.model.Analysis;
import com.yanosik.rcd.model.AnalysisRequest;
import com.yanosik.rcd.model.StaticAnalysis;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.CompleteAnalysisRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class SMA_Analysis {

		private final RedisTemplate<String, Object> redisTemplate;

		private static final String ANALYSIS_PREFIX = "analysis:";
		private static final String STATUS_PREFIX = "status:";

		@KafkaListener(
				topics = "stock_analysis_sma",
				groupId = "stockAnalysisGroup"
		)
		public void consume(CompleteAnalysisRequest analysisRequest) {
				log.info("Event message received: {}", analysisRequest);
				storeProcessingRequest(analysisRequest);

				try {
						performAnalysis(analysisRequest);
				} catch (Exception e) {
						log.error("Analysis failed for requestId: {}", analysisRequest.getRequestId(), e);
						storeFailedAnalysis(analysisRequest, e.getMessage());
				}
		}

		private void storeProcessingRequest(CompleteAnalysisRequest analysisRequest) {
				String statusKey = STATUS_PREFIX + analysisRequest.getRequestId();
				redisTemplate.opsForValue().set(statusKey, "PROCESSING", Duration.ofMinutes(5));

				log.info("Stored processing request for requestId: {}", analysisRequest.getRequestId());
		}

		private boolean isValidRequest(AnalysisRequest analysisRequest) {
				Map<String, String> parameters = analysisRequest.parameters();
				return parameters != null &&
						!parameters.isEmpty() &&
						parameters.containsKey("startDate") &&
						parameters.containsKey("endDate") &&
						analysisRequest.symbol() != null &&
						!analysisRequest.symbol().isEmpty();
		}

		private void performAnalysis(CompleteAnalysisRequest analysisRequest) {
				log.info("Performing SMA analysis for request: {}", analysisRequest);

				double smaValue = 0.0;

				StaticAnalysis staticAnalysis = StaticAnalysis.builder()
						.id(UUID.randomUUID().toString()) // Separate ID for analysis
						.calculatedAt(LocalDateTime.now())
						.analysisType(analysisRequest.getAnalysisType())
						.symbol(analysisRequest.getStockData().getStockMetadata().getSymbol())
						.parameters(analysisRequest.getParameters())
						.status(AnalysisStatus.COMPLETED)
						.calculatedBy("SMA_Analysis_Service")
						.value(smaValue)
						.unit("Price")
						.interpretation("DUPA")
						.build();

				String analysisKey = ANALYSIS_PREFIX + analysisRequest.getRequestId();
				String statusKey = STATUS_PREFIX + analysisRequest.getRequestId();

				redisTemplate.opsForValue().set(analysisKey, staticAnalysis, Duration.ofHours(24));
				redisTemplate.opsForValue().set(statusKey, "COMPLETED", Duration.ofHours(24));

				log.info("Analysis result stored/overridden for requestId: {}", analysisRequest.getRequestId());
		}

		private void storeFailedAnalysis(CompleteAnalysisRequest analysisRequest, String errorMessage) {
				StaticAnalysis failedAnalysis = StaticAnalysis.builder()
						.id(UUID.randomUUID().toString())
						.calculatedAt(LocalDateTime.now())
						.analysisType(analysisRequest.getAnalysisType())
						.symbol(analysisRequest.getStockData().getStockMetadata().getSymbol())
						.parameters(analysisRequest.getParameters())
						.status(AnalysisStatus.FAILED)
						.calculatedBy("SMA_Analysis_Service")
						.errorMessage(errorMessage)
						.build();

				String analysisKey = ANALYSIS_PREFIX + analysisRequest.getRequestId();
				String statusKey = STATUS_PREFIX + analysisRequest.getRequestId();

				redisTemplate.opsForValue().set(analysisKey, failedAnalysis, Duration.ofHours(24));
				redisTemplate.opsForValue().set(statusKey, "FAILED", Duration.ofHours(24));
		}

}
