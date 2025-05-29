package com.yanosik.rcd;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.model.StockDataRequest;
import com.yanosik.rcd.parser.StockDataParser;
import com.yanosik.rcd.repository.StockDataRepository;
import com.yanosik.rcd.service.RedisCacheService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaDatabaseConsumer {
		private final StockDataRepository stockDataRepository;
		private final StockDataParser stockDataParser;
		private final RedisCacheService redisCacheService;

		@KafkaListener(
				topics = "stock_data",
				groupId = "myGroup"
		)
		public void consume(StockDataDto stockDataDto) {
				log.info("Event message received: {}", stockDataDto);
				StockData stockData = stockDataParser.toEntity(stockDataDto);
				stockDataRepository.save(stockData);
		}

		@KafkaListener(
				topics = "stock_data_request",
				groupId = "stockAnalysisGroup"
		)
		@Transactional
		public void stockDataRequestConsumer(StockDataRequest stockDataRequest) {
				log.info("Event message for stock data request received: {}", stockDataRequest);

				try {
						StockData stockData = stockDataRepository.findStockDataWithFilteredPrices(
								stockDataRequest.getSymbol(),
								stockDataRequest.getStartDate(),
								stockDataRequest.getEndDate()
						);

						if (stockData == null) {
								log.warn("No stock data found for symbol: {}", stockDataRequest.getSymbol());
								return;
						}

						StockDataDto stockDataDto = stockDataParser.toDto(stockData);

						redisCacheService.saveStockData(stockDataRequest.getRequestId(), stockDataDto);

						log.info("Stock data for request ID {} saved to Redis with {} price records",
								stockDataRequest.getRequestId(),
								stockData.getStockPrices().size());

				} catch (Exception e) {
						log.error("Error processing stock data request for requestId: {}",
								stockDataRequest.getRequestId(), e);
						throw new RuntimeException("Failed to process stock data request", e);
				}
		}
}
