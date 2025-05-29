package com.yanosik.rcd.consumer;

import com.yanosik.rcd.model.Analysis;
import com.yanosik.rcd.model.AnalysisRequest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.CompleteAnalysisRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class SMA_Analysis {

		@KafkaListener(
				topics = "stock_analysis_sma",
				groupId = "stockAnalysisGroup"
		)
		public void consume(CompleteAnalysisRequest analysisRequest) {
				log.info("Event message received: {}", analysisRequest);
				performAnalysis(analysisRequest);
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
		}

}
