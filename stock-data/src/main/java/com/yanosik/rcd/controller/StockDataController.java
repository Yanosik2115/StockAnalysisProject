package com.yanosik.rcd.controller;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.model.StockPrice;
import com.yanosik.rcd.repository.StockDataRepository;
import com.yanosik.rcd.parser.StockDataParser;
import com.yanosik.rcd.service.StockDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/stock-data")
public class StockDataController {
		private final StockDataService stockDataService;

		@GetMapping("/get")
		public ResponseEntity<StockDataDto> getStock(@RequestParam("symbol") String symbol) {
				StockDataDto stockData = stockDataService.fetchStockData(symbol);
				if (stockData != null) {
						log.info("Stock data retrieved for symbol: {}", symbol);
						return ResponseEntity.ok(stockData);
				} else {
						log.info("No stock data found for symbol: {}", symbol);
						return ResponseEntity.noContent().build();
				}
		}

		@GetMapping("/get/between")
		public ResponseEntity<StockDataDto> getStockBetween(@RequestParam("symbol") String symbol,
		                                                    @RequestParam("startDate") LocalDate startDate,
		                                                    @RequestParam("endDate") LocalDate endDate) {
				StockDataDto stockData = stockDataService.fetchStockDataBetween(symbol, startDate, endDate);
				if (stockData != null) {
						log.info("Stock data retrieved for symbol: {} between {} and {}", symbol, startDate, endDate);
						return ResponseEntity.ok(stockData);
				} else {
						log.info("No stock data found for symbol: {}", symbol);
						return ResponseEntity.noContent().build();
				}
		}
}
