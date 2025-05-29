package com.yanosik.rcd.controller;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.model.StockPrice;
import com.yanosik.rcd.repository.StockDataRepository;
import com.yanosik.rcd.parser.StockDataParser;
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
		private final StockDataRepository stockDataRepository;
		private final StockDataParser stockDataParser;

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

		@GetMapping("/get/between")
		public ResponseEntity<StockDataDto> getStockBetween(@RequestParam("symbol") String symbol,
		                                                    @RequestParam("startDate") LocalDate startDate,
		                                                    @RequestParam("endDate") LocalDate endDate) {
				StockData stockData = stockDataRepository.findStockDataWithFilteredPrices(symbol, startDate, endDate);
				if (stockData != null) {
						log.info("Stock data retrieved for symbol: {} between {} and {}", symbol, startDate, endDate);
						StockDataDto stockDataDto = stockDataParser.toDto(stockData);
						return ResponseEntity.ok(stockDataDto);
				} else {
						log.info("No stock data found for symbol: {}", symbol);
						return ResponseEntity.noContent().build();
				}
		}
}
