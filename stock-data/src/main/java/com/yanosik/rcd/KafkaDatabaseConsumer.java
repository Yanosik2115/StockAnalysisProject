package com.yanosik.rcd;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.repository.StockDataRepository;
import com.yanosik.rcd.parser.StockDataParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaDatabaseConsumer {
		private final StockDataRepository stockDataRepository;
		private final StockDataParser stockDataParser;

		@KafkaListener(
				topics = "stock_data",
				groupId = "myGroup"
		)
		public void consume(StockDataDto stockDataDto) {
				log.info("Event message received: {}", stockDataDto);
				StockData stockData = stockDataParser.toEntity(stockDataDto);
				stockDataRepository.save(stockData);
		}
}
