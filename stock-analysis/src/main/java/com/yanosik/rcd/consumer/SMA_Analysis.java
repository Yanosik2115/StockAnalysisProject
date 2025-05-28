package com.yanosik.rcd.consumer;

import com.yanosik.rcd.model.AnalysisRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class SMA_Analysis {
		@KafkaListener(
				topics = "stock_analysis_sma",
				groupId = "myGroup"
		)
		public void consume(AnalysisRequest analysisRequest) {
				log.info("Event message received: {}", analysisRequest);
				Map<String, String> parameters = analysisRequest.parameters();
				if (parameters == null || parameters.isEmpty() || !parameters.containsKey("period")
						|| analysisRequest.symbol() == null || analysisRequest.symbol().isEmpty()) {
						log.warn("No parameters provided for SMA analysis");
						return;
				}
				performAnalysis(analysisRequest);
		}

		private void performAnalysis(AnalysisRequest analysisRequest) {

		}
}
