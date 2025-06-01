package com.yanosik.rcd.controller;

import com.yanosik.rcd.AlphaVantageService;
import com.yanosik.rcd.dto.StockDataDto;
import jakarta.annotation.Nullable;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
		private final AlphaVantageService alphaVantageService;

		public StockController(AlphaVantageService alphaVantageService) {
				this.alphaVantageService = alphaVantageService;
		}

		@GetMapping("/get")
		public ResponseEntity<StockDataDto> getStockData(@RequestParam("symbol") String symbol,
		                                                 @Nullable @RequestParam("startDate") LocalDate startDate,
		                                                 @Nullable @RequestParam("endDate") LocalDate endDate) {
				return ResponseEntity.ok(alphaVantageService.fetchStockData(symbol, startDate, endDate));
		}

		@GetMapping("/get/quote")
		public ResponseEntity<StockDataDto> getStockDataQuote(@RequestParam("symbol") String symbol) {
				return ResponseEntity.ok(alphaVantageService.getStockDataQuote(symbol));
		}
}
