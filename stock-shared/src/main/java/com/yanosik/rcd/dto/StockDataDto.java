package com.yanosik.rcd.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.yanosik.rcd.model.StockData}
 */
@Value
public class StockDataDto implements Serializable {
		List<StockPriceDto> stockPrices;
		StockMetadataDto stockMetadata;

		@JsonCreator
		public StockDataDto(
				@JsonProperty("stockPrices") List<StockPriceDto> stockPrices,
				@JsonProperty("stockMetadata") StockMetadataDto stockMetadata) {
				this.stockPrices = stockPrices;
				this.stockMetadata = stockMetadata;
		}

		/**
		 * DTO for {@link com.yanosik.rcd.model.StockPrice}
		 */
		@Value
		public static class StockPriceDto implements Serializable {
				LocalDate timestamp;
				BigDecimal open;
				BigDecimal high;
				BigDecimal low;
				BigDecimal close;
				BigDecimal volume;

				@JsonCreator
				public StockPriceDto(
						@JsonProperty("timestamp") LocalDate timestamp,
						@JsonProperty("open") BigDecimal open,
						@JsonProperty("high") BigDecimal high,
						@JsonProperty("low") BigDecimal low,
						@JsonProperty("close") BigDecimal close,
						@JsonProperty("volume") BigDecimal volume) {
						this.timestamp = timestamp;
						this.open = open;
						this.high = high;
						this.low = low;
						this.close = close;
						this.volume = volume;
				}
		}

		/**
		 * DTO for {@link com.yanosik.rcd.model.StockMetadata}
		 */
		@Value
		public static class StockMetadataDto implements Serializable {
				String information;
				String symbol;
				LocalDateTime lastRefreshed;
				String interval;
				String outputSize;

				@JsonCreator
				public StockMetadataDto(
						@JsonProperty("information") String information,
						@JsonProperty("symbol") String symbol,
						@JsonProperty("lastRefreshed") LocalDateTime lastRefreshed,
						@JsonProperty("interval") String interval,
						@JsonProperty("outputSize") String outputSize) {
						this.information = information;
						this.symbol = symbol;
						this.lastRefreshed = lastRefreshed;
						this.interval = interval;
						this.outputSize = outputSize;
				}
		}
}