package com.yanosik.rcd.controller;

import com.yanosik.rcd.model.Analysis;
import com.yanosik.rcd.model.AnalysisType;
import com.yanosik.rcd.service.AnalysisOrchestrator;
import com.yanosik.rcd.service.RedisCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/analysis")
public class StockAnalysisController {

		private final AnalysisOrchestrator analysisOrchestrator;
		private final RedisCacheService redisCacheService;

		@GetMapping("/{analysisId}")
		public ResponseEntity<Analysis> getAnalysis(@PathVariable String analysisId) {
				Optional<Analysis> analysis = redisCacheService.getAnalysisResult(analysisId);
				return analysis.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
		}

		@PostMapping("/trigger/{symbol}/{analysisType}")
		public ResponseEntity<Map<String, String>> triggerSpecificAnalysis(
				@PathVariable String symbol,
				@PathVariable AnalysisType analysisType, @RequestBody Map<String, String> parameters) {

				log.info("Triggering {} analysis for symbol: {}", analysisType, symbol);

				String requestId = analysisOrchestrator.triggerAnalysis(symbol, analysisType, parameters);

				return ResponseEntity.ok(Map.of(
						"requestId", requestId,
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
