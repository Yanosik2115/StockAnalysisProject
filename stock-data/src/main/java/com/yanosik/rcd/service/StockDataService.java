package com.yanosik.rcd.service;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.parser.StockDataParser;
import com.yanosik.rcd.repository.StockDataRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@AllArgsConstructor
public class StockDataService {

		private final StockDataRepository stockDataRepository;
		private final AlphaVantageService alphaVantageService;

		public StockDataDto fetchStockData(String symbol) {
				log.info("Fetching stock data for symbol: {}", symbol);
				StockData stockData = stockDataRepository.findStockDataByStockMetadata_Symbol(symbol);
				if (stockData == null) {
						log.warn("Stock data not found for symbol: {} in local repository", symbol);
						return fetchStockDataFromApi(symbol);
				}
				return StockDataParser.toDto(stockData);
		}

		public StockDataDto fetchStockDataBetween(String symbol, LocalDate startDate, LocalDate endDate) {
				log.info("Fetching stock data for symbol: {}", symbol);
				StockData stockData = stockDataRepository.findStockDataByStockMetadata_Symbol(symbol);
				if (stockData == null) {
						log.warn("Stock data not found for symbol: {} in local repository", symbol);
						fetchStockDataFromApi(symbol);
				}
				log.info("Fetching stock data for symbol: {} from {} to {}", symbol, startDate, endDate);
				return StockDataParser.toDto(stockDataRepository.findStockDataWithFilteredPrices(symbol, startDate, endDate));
		}

		private StockDataDto fetchStockDataFromApi(String symbol) {
				log.warn("Stock data not found for symbol: {}. Fetching from AlphaVantage API.", symbol);
				StockDataDto stockDataDto = alphaVantageService.fetchStockDataFromApi(symbol);
				if (stockDataDto == null) {
						log.error("Failed to fetch stock data for symbol: {} from AlphaVantage API", symbol);
						throw new RuntimeException("Failed to fetch stock data from AlphaVantage API for symbol: " + symbol);
				}
				return StockDataParser.toDto(stockDataRepository.save(StockDataParser.toEntity(stockDataDto)));
		}
}
