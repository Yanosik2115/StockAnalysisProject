package com.yanosik.rcd;

import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.repository.StockDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaDatabaseConsumer {

		private static final Logger log = LoggerFactory.getLogger(KafkaDatabaseConsumer.class);
		private final StockDataParser stockDataParser;

		public KafkaDatabaseConsumer(StockDataParser stockDataParser, StockDataRepository stockDataRepository) {
				this.stockDataParser = stockDataParser;
				this.stockDataRepository = stockDataRepository;
		}

		private final StockDataRepository stockDataRepository;

		@KafkaListener(
				topics = "stock_data",
				groupId = "myGroup"
		)
		public void consume(String eventMessage) {
				log.info("Event message received: {}", eventMessage);
				StockData stockData = stockDataParser.parse(eventMessage);
				stockDataRepository.save(stockData);
		}

}
