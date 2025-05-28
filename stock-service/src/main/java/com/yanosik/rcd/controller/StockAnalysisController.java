package com.yanosik.rcd.controller;

import com.yanosik.rcd.model.AnalysisType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/v1/analysis")
public class StockAnalysisController {
		@PostMapping("/trigger/{symbol}")
		public ResponseEntity<Map<String, String>> triggerFullAnalysis(
				@PathVariable String symbol) {

				log.info("Triggering full analysis for symbol: {}", symbol);


//				String requestId = analysisOrchestrator.triggerFullAnalysis(symbol);

				return ResponseEntity.ok(Map.of(
//						"requestId", requestId,
						"symbol", symbol,
						"status", "TRIGGERED"
				));
		}

		@PostMapping("/trigger/{symbol}/{analysisType}")
		public ResponseEntity<Map<String, String>> triggerSpecificAnalysis(
				@PathVariable String symbol,
				@PathVariable AnalysisType analysisType) {

				log.info("Triggering {} analysis for symbol: {}", analysisType, symbol);

//				String requestId = analysisOrchestrator.triggerAnalysis(symbol, analysisType, parameters);

				return ResponseEntity.ok(Map.of(
//						"requestId", requestId,
						"symbol", symbol,
						"analysisType", analysisType.toString(),
						"status", "TRIGGERED"
				));
		}

		@GetMapping("/types")
		public ResponseEntity<AnalysisType[]> getAvailableAnalysisTypes() {
				return ResponseEntity.ok(AnalysisType.values());
		}

}
