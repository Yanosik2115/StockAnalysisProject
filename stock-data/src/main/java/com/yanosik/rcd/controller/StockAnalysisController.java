package com.yanosik.rcd.controller;

import com.yanosik.rcd.model.StockAnalysisJob;
import com.yanosik.rcd.model.StockAnalysisJobStatus;
import com.yanosik.rcd.repository.StockAnalysisRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/stock-analysis")
public class StockAnalysisController {
		private final StockAnalysisRepository stockAnalysisRepository;
		@GetMapping("/status/{requestId}")
		public ResponseEntity<StockAnalysisJobStatus> getStock(@PathVariable("requestId") String requestId) {
				StockAnalysisJob stockAnalysisJob = stockAnalysisRepository.findStockAnalysisJobByJobId(requestId);
				if (stockAnalysisJob != null) {
						log.info("Stock analysis job retrieved for request id: {}", requestId);
						return ResponseEntity.ok(stockAnalysisJob.getStatus());
				} else {
						log.info("No stock analysis job found for request id: {}", requestId);
						return ResponseEntity.noContent().build();
				}
		}
}
