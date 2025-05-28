package com.yanosik.rcd.controller;

import com.yanosik.rcd.AlphaVantageService;
import com.yanosik.rcd.dto.StockDataDto;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
		private final AlphaVantageService alphaVantageService;
		private static final Logger log = LoggerFactory.getLogger(StockController.class);

		public StockController(AlphaVantageService alphaVantageService) {
				this.alphaVantageService = alphaVantageService;
		}

		@GetMapping("/get")
		public Mono<ResponseEntity<StockDataDto>> getStockData(@RequestParam("symbol") String symbol) {
				return alphaVantageService.fetchStockData(symbol)
						.map(data -> ResponseEntity.ok()
								.contentType(MediaType.APPLICATION_JSON)
								.body(data))
						.onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
		}
}
