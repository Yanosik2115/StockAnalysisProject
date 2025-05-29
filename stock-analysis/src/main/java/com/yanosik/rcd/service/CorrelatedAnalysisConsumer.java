package com.yanosik.rcd.service;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.AnalysisRequest;
import com.yanosik.rcd.model.CompleteAnalysisRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@AllArgsConstructor
public class CorrelatedAnalysisConsumer {
		private final RedisCacheService cacheService;
		private final KafkaTemplate<String, CompleteAnalysisRequest> kafkaTemplate;

		@KafkaListener(topics = "stock_analysis_requests", groupId = "stockAnalysisGroup")
		public void handleStockAnalysis(AnalysisRequest request) {
				log.info("Received stock analysis request: {}", request);

				CompletableFuture.runAsync(() -> {
						try {
								StockDataDto stockDataDto = awaitObjectCompletion(request.requestId());
								CompleteAnalysisRequest completeRequest = CompleteAnalysisRequest.builder().analysisType(request.analysisType())
										.requestId(request.requestId())
										.stockData(stockDataDto)
										.parameters(request.parameters())
										.build();
								kafkaTemplate.send(request.analysisType().getTopicName(), completeRequest);
						} catch (Exception e) {
								throw new RuntimeException(e);
						}
				});
		}

		private StockDataDto awaitObjectCompletion(String requestId)
				throws TimeoutException, InterruptedException {
				long startTime = System.currentTimeMillis();
				long timeoutMillis = 10 * 1000L;
				long pollIntervalMillis = 1000L;

				while (System.currentTimeMillis() - startTime < timeoutMillis) {
						Optional<StockDataDto> data = cacheService.getStockData(requestId);

						if (data.isPresent()) {
								return data.get();
						}

						Thread.sleep(pollIntervalMillis);
				}

				throw new TimeoutException("Request timeout");
		}

}
