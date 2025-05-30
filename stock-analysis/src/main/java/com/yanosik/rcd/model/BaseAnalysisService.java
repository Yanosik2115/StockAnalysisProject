package com.yanosik.rcd.model;

import com.yanosik.rcd.enums.AnalysisStatus;
import com.yanosik.rcd.utils.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class BaseAnalysisService implements AnalysisService {

		protected final RedisTemplate<String, Object> redisTemplate;

		protected static final String ANALYSIS_PREFIX = "analysis:";
		protected static final String STATUS_PREFIX = "status:";
		protected static final Duration DEFAULT_TTL = Duration.ofHours(24);
		protected static final Duration PROCESSING_TTL = Duration.ofMinutes(5);

		protected BaseAnalysisService(RedisTemplate<String, Object> redisTemplate) {
				this.redisTemplate = redisTemplate;
		}

		@Override
		public final void processAnalysis(AnalysisRequest analysisRequest) {
				log.info("Processing {} analysis for request: {}",
						getSupportedAnalysisType(), analysisRequest.requestId());

				if (!isValidRequest(analysisRequest)) {
						String error = "Invalid request parameters for " + getSupportedAnalysisType();
						log.error(error + " - requestId: {}", analysisRequest.requestId());
						storeFailedAnalysis(analysisRequest, error);
						return;
				}

				storeProcessingStatus(analysisRequest);

				performAnalysis(analysisRequest)
						.thenAccept(result -> {
								log.info("Analysis completed successfully for requestId: {}", analysisRequest.requestId());
								storeCompletedAnalysis(analysisRequest, result);
						})
						.exceptionally(ex -> {
								log.error("Analysis failed for requestId: {}", analysisRequest.requestId(), ex);
								storeFailedAnalysis(analysisRequest, ex.getMessage());
								return null;
						});
		}

		protected abstract CompletableFuture<Analysis> performAnalysis(AnalysisRequest analysisRequest);

		protected abstract boolean validateSpecificParameters(AnalysisRequest analysisRequest);

		protected abstract String getServiceName();

		@Override
		public boolean isValidRequest(AnalysisRequest analysisRequest) {
				if (analysisRequest == null || analysisRequest.symbol() == null || analysisRequest.symbol().isEmpty() ||
						analysisRequest.analysisType() == null || analysisRequest.requestId() == null || analysisRequest.requestId().isEmpty()) {
						return false;
				}

				Map<String, String> parameters = analysisRequest.parameters();
				if (parameters == null || parameters.isEmpty()) {
						return false;
				}

				return validateSpecificParameters(analysisRequest);
		}

		protected void storeProcessingStatus(AnalysisRequest analysisRequest) {
				String statusKey = STATUS_PREFIX + analysisRequest.requestId();
				redisTemplate.opsForValue().set(statusKey, "PROCESSING", PROCESSING_TTL);
				log.info("Stored processing status for requestId: {}", analysisRequest.requestId());
		}

		protected void storeCompletedAnalysis(AnalysisRequest analysisRequest, Analysis analysis) {
				String analysisKey = ANALYSIS_PREFIX + analysisRequest.requestId();
				String statusKey = STATUS_PREFIX + analysisRequest.requestId();

				redisTemplate.opsForValue().set(analysisKey, analysis, DEFAULT_TTL);
				redisTemplate.opsForValue().set(statusKey, "COMPLETED", DEFAULT_TTL);

				log.info("Analysis result stored for requestId: {}", analysisRequest.requestId());
		}

		protected void storeFailedAnalysis(AnalysisRequest analysisRequest, String errorMessage) {
				StaticAnalysis failedAnalysis = StaticAnalysis.builder()
						.id(Utilities.generateId())
						.calculatedAt(LocalDateTime.now())
						.analysisType(analysisRequest.analysisType())
						.symbol(analysisRequest.symbol())
						.parameters(analysisRequest.parameters())
						.status(AnalysisStatus.FAILED)
						.calculatedBy(getServiceName())
						.errorMessage(errorMessage)
						.build();

				String analysisKey = ANALYSIS_PREFIX + analysisRequest.requestId();
				String statusKey = STATUS_PREFIX + analysisRequest.requestId();

				redisTemplate.opsForValue().set(analysisKey, failedAnalysis, DEFAULT_TTL);
				redisTemplate.opsForValue().set(statusKey, "FAILED", DEFAULT_TTL);

				log.error("Failed analysis stored for requestId: {}", analysisRequest.requestId());
		}

		protected StaticAnalysis createBaseAnalysis(AnalysisRequest analysisRequest) {
				return StaticAnalysis.builder()
						.id(Utilities.generateId())
						.calculatedAt(LocalDateTime.now())
						.analysisType(analysisRequest.analysisType())
						.symbol(analysisRequest.symbol())
						.parameters(analysisRequest.parameters())
						.status(AnalysisStatus.COMPLETED)
						.calculatedBy(getServiceName())
						.build();
		}
}