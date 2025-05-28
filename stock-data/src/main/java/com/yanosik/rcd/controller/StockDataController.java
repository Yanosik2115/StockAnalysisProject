package com.yanosik.rcd.controller;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.repository.StockDataRepository;
import com.yanosik.rcd.parser.StockDataParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock-data")
public class StockDataController {
		private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StockDataController.class);
		private final StockDataRepository stockDataRepository;
		private final StockDataParser stockDataParser;

		public StockDataController(StockDataRepository stockDataRepository, StockDataParser stockDataParser) {
				this.stockDataRepository = stockDataRepository;
				this.stockDataParser = stockDataParser;
		}

		@GetMapping("/get")
		public ResponseEntity<StockDataDto> getStock(@RequestParam("symbol") String symbol) {
				StockData stockData = stockDataRepository.findStockDataByStockMetadata_Symbol(symbol);

				if (stockData != null) {
						log.info("Stock data retrieved for symbol: {}", symbol);
						StockDataDto stockDataDto = stockDataParser.toDto(stockData);
						return ResponseEntity.ok(stockDataDto);
				} else {
						log.info("No stock data found for symbol: {}", symbol);
						return ResponseEntity.noContent().build();
				}
		}
}
