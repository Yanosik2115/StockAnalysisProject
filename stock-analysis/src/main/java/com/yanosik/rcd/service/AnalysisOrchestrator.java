package com.yanosik.rcd.service;

import com.yanosik.rcd.model.AnalysisRequest;
import com.yanosik.rcd.model.AnalysisType;
import com.yanosik.rcd.model.StockDataRequest;
import com.yanosik.rcd.utils.Utilities;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@AllArgsConstructor
public class AnalysisOrchestrator {

		private final KafkaTemplate<String, StockDataRequest> kafkaStockDataRequestTemplate;
		private final KafkaTemplate<String, AnalysisRequest> kafkaAnalysisRequestTemplate;

		public String triggerAnalysis(@NonNull String symbol, @NonNull AnalysisType analysisType, Map<String, String> parameters) {
				String requestId = Utilities.generateId();
				AnalysisRequest analysisRequest = new AnalysisRequest(requestId, symbol, analysisType, LocalDateTime.now(), parameters);
				StockDataRequest stockDataRequest = new StockDataRequest(requestId, symbol, LocalDate.parse(parameters.get("startDate")), LocalDate.parse(parameters.get("endDate")));
				kafkaStockDataRequestTemplate.send("stock_data_request", stockDataRequest);
				kafkaAnalysisRequestTemplate.send("stock_analysis_requests", analysisRequest);
				return requestId;
		}
}
