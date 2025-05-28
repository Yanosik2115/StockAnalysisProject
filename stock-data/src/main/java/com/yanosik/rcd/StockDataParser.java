package com.yanosik.rcd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.model.StockMetadata;
import com.yanosik.rcd.model.StockPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class StockDataParser {
		private static final Logger log = LoggerFactory.getLogger(StockDataParser.class);

		private final ObjectMapper objectMapper;
		private final DateTimeFormatter dateFormatter;

		public StockDataParser(ObjectMapper objectMapper) {
				this.objectMapper = objectMapper;
				this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		}

		public StockData parse(String json) {
				try {
						StockData stockData = new StockData();
						JsonNode rootNode = objectMapper.readTree(json);
						log.info("Root node field names: {}", rootNode.fieldNames());
						rootNode.fieldNames().forEachRemaining(fieldName ->
								log.info("Field: {}", fieldName));

						JsonNode metaDataNode = rootNode.get("Meta Data");
						if (metaDataNode == null) {
								log.error("Meta Data node is missing in the JSON: {}", json);
								throw new RuntimeException("Meta Data node is missing");
						}
						StockMetadata metadata = objectMapper.convertValue(metaDataNode, StockMetadata.class);
						List<StockPrice> stockPrices = parseTimeSeriesData(rootNode);
						stockData.setStockMetadata(metadata);
						stockData.setStockPrices(stockPrices);
						return stockData;
				} catch (Exception e) {
						log.error("Failed to parse stock data from JSON: {}", json, e);
						throw new RuntimeException("Failed to parse stock data", e);
				}
		}

		private List<StockPrice> parseTimeSeriesData(JsonNode rootNode) {
				List<StockPrice> priceRecords = new ArrayList<>();

				JsonNode timeSeriesNode = rootNode.get("Time Series (5min)");

				Iterator<Map.Entry<String, JsonNode>> fields = timeSeriesNode.fields();

				while (fields.hasNext()) {
						Map.Entry<String, JsonNode> entry = fields.next();
						String timestampStr = entry.getKey();
						JsonNode priceNode = entry.getValue();

						try {
								StockPrice priceRecord = objectMapper.convertValue(priceNode, StockPrice.class);
								priceRecord.setTimestamp(LocalDateTime.parse(entry.getKey(), dateFormatter));
								priceRecords.add(priceRecord);

						} catch (Exception e) {
								log.error("Error parsing price record for timestamp {}: {}", timestampStr, e.getMessage());
						}
				}

				return priceRecords;
		}

}
