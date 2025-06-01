package com.yanosik.rcd;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.model.StockDataRequest;
import com.yanosik.rcd.model.StockDataResponse;
import com.yanosik.rcd.parser.StockDataParser;
import com.yanosik.rcd.repository.StockDataRepository;
import com.yanosik.rcd.service.RedisStockDataDtoCacheService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaDatabaseConsumer {
		private final StockDataRepository stockDataRepository;
		private final RedisStockDataDtoCacheService redisCacheService;
		private final KafkaTemplate<String, StockDataResponse> kafkaTemplate;


		@KafkaListener(
				topics = "stock_data",
				groupId = "myGroup"
		)
		public void consume(StockDataDto stockDataDto) {
				log.info("Event message received: {}", stockDataDto);
				StockData stockData = StockDataParser.toEntity(stockDataDto);
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
								stockDataRequest.symbol(),
								stockDataRequest.startDate(),
								stockDataRequest.endDate()
						);

						if (stockData == null) {
								log.warn("No stock data found for symbol: {}", stockDataRequest.symbol());
								return;
						}

						StockDataDto stockDataDto = StockDataParser.toDto(stockData);

						redisCacheService.save(stockDataRequest.requestId(), stockDataDto);

						log.info("Stock data for request ID {} saved to Redis with {} price records",
								stockDataRequest.requestId(),
								stockData.getStockPrices().size());

				} catch (Exception e) {
						log.error("Error processing stock data request for requestId: {}",
								stockDataRequest.requestId(), e);
						throw new RuntimeException("Failed to process stock data request", e);
				}
		}

		@KafkaListener(
				topics = "stock_data_request_internal",
				groupId = "stockAnalysisGroup"
		)
		@Transactional
		public void stockDataRequestInternalConsumer(StockDataRequest stockDataRequest) {
				log.info("Event message for stock data request received: {}", stockDataRequest);

				try {
						StockData stockData = stockDataRepository.findStockDataWithFilteredPrices(
								stockDataRequest.symbol(),
								stockDataRequest.startDate(),
								stockDataRequest.endDate()
						);

						if (stockData == null) {
								log.warn("No stock data found for symbol: {}", stockDataRequest.symbol());
								return;
						}

						StockDataDto stockDataDto = StockDataParser.toDto(stockData);
						StockDataResponse stockDataResponse = new StockDataResponse(
								stockDataRequest.requestId(),
								stockDataRequest.symbol(),
								stockDataDto,
								"SUCCESS"
						);

						log.info("Stock data for request ID {} found with {} price records",
								stockDataRequest.requestId(),
								stockData.getStockPrices().size());

						kafkaTemplate.send("stock_data_responses", stockDataRequest.requestId(), stockDataResponse);
				} catch (Exception e) {
						log.error("Error processing stock data request for requestId: {}",
								stockDataRequest.requestId(), e);
						throw new RuntimeException("Failed to process stock data request", e);
				}
		}
}
