package com.yanosik.rcd.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanosik.rcd.dto.StockDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class StockDataDtoParser {
		private static final Logger log = LoggerFactory.getLogger(StockDataDtoParser.class);

		private static final ObjectMapper objectMapper = new ObjectMapper();
		private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		/**
		 * Parses JSON string from AlphaVantage API into StockDataDto
		 *
		 * @param json JSON string from AlphaVantage API
		 * @return StockDataDto parsed from JSON
		 * @throws RuntimeException if parsing fails
		 */
		public static StockDataDto parse(String json) {
				try {
						JsonNode rootNode = objectMapper.readTree(json);
						log.info("Root node field names: {}", rootNode.fieldNames());
						rootNode.fieldNames().forEachRemaining(fieldName ->
								log.info("Field: {}", fieldName));

						JsonNode metaDataNode = rootNode.get("Meta Data");
						if (metaDataNode == null) {
								log.error("Meta Data node is missing in the JSON: {}", json);
								throw new RuntimeException("Meta Data node is missing");
						}
						StockDataDto.StockMetadataDto metadata = parseMetadata(metaDataNode);

						List<StockDataDto.StockPriceDto> stockPrices = parseTimeSeriesData(rootNode);

						return new StockDataDto(stockPrices, metadata);

				} catch (Exception e) {
						log.error("Failed to parse stock data from JSON: {}", json, e);
						throw new RuntimeException("Failed to parse stock data", e);
				}
		}

		/**
		 * Parses metadata from Meta Data node
		 */
		private static StockDataDto.StockMetadataDto parseMetadata(JsonNode metaDataNode) {
				try {
						String information = metaDataNode.path("1. Information").asText();
						String symbol = metaDataNode.path("2. Symbol").asText();
						String lastRefreshedStr = metaDataNode.path("3. Last Refreshed").asText();
						String interval = metaDataNode.path("4. Interval").asText();
						String outputSize = metaDataNode.path("5. Output Size").asText();

						LocalDateTime lastRefreshed = parseDateTime(lastRefreshedStr);

						log.debug("Parsed metadata - Symbol: {}, Information: {}, Last Refreshed: {}",
								symbol, information, lastRefreshed);

						return new StockDataDto.StockMetadataDto(
								information,
								symbol,
								lastRefreshed,
								interval,
								outputSize
						);

				} catch (Exception e) {
						log.error("Error parsing metadata: {}", e.getMessage());
						throw new RuntimeException("Failed to parse metadata", e);
				}
		}

		/**
		 * Parses time series data from Time Series node
		 */
		private static List<StockDataDto.StockPriceDto> parseTimeSeriesData(JsonNode rootNode) {
				List<StockDataDto.StockPriceDto> priceRecords = new ArrayList<>();

				JsonNode timeSeriesNode = rootNode.get("Time Series (Daily)");
				if (timeSeriesNode == null) {
						log.warn("Time Series (Daily) node is missing, returning empty price list");
						return priceRecords;
				}

				Iterator<String> fieldNames = timeSeriesNode.fieldNames();

				while (fieldNames.hasNext()) {
						String timestampStr = fieldNames.next();
						JsonNode priceNode = timeSeriesNode.get(timestampStr);

						try {
								StockDataDto.StockPriceDto priceRecord = parsePriceRecord(timestampStr, priceNode);
								priceRecords.add(priceRecord);

						} catch (Exception e) {
								log.error("Error parsing price record for timestamp {}: {}", timestampStr, e.getMessage());
						}
				}

				log.info("Parsed {} price records", priceRecords.size());
				return priceRecords;
		}

		/**
		 * Parses individual price record
		 */
		private static StockDataDto.StockPriceDto parsePriceRecord(String timestampStr, JsonNode priceNode) {
				try {
						LocalDate timestamp = LocalDate.parse(timestampStr, dateFormatter);

						BigDecimal open = parseBigDecimal(priceNode.path("1. open"));
						BigDecimal high = parseBigDecimal(priceNode.path("2. high"));
						BigDecimal low = parseBigDecimal(priceNode.path("3. low"));
						BigDecimal close = parseBigDecimal(priceNode.path("4. close"));
						BigDecimal volume = parseBigDecimal(priceNode.path("5. volume"));

						return new StockDataDto.StockPriceDto(
								timestamp,
								open,
								high,
								low,
								close,
								volume
						);

				} catch (Exception e) {
						log.error("Error parsing price values for timestamp {}: {}", timestampStr, e.getMessage());
						throw new RuntimeException("Failed to parse price record", e);
				}
		}

		/**
		 * Safely parses BigDecimal from JsonNode
		 */
		private static BigDecimal parseBigDecimal(JsonNode node) {
				if (node == null || node.isNull() || node.asText().trim().isEmpty()) {
						return BigDecimal.ZERO;
				}
				try {
						return new BigDecimal(node.asText());
				} catch (NumberFormatException e) {
						log.warn("Failed to parse BigDecimal from value: {}, using 0", node.asText());
						return BigDecimal.ZERO;
				}
		}


		private static LocalDateTime parseDateTime(String dateTimeStr) {
				if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
						log.warn("Empty datetime string, using current time");
						return LocalDateTime.now();
				}

				try {
						if (dateTimeStr.contains(" ") || dateTimeStr.contains("T")) {
								if (dateTimeStr.contains("T")) {
										return LocalDateTime.parse(dateTimeStr);
								} else {
										return LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
								}
						}
						else {
								LocalDate date = LocalDate.parse(dateTimeStr, dateFormatter);
								return date.atStartOfDay();
						}
				} catch (DateTimeParseException e) {
						log.warn("Failed to parse datetime: {}, using current time. Error: {}",
								dateTimeStr, e.getMessage());
						return LocalDateTime.now();
				}
		}

}