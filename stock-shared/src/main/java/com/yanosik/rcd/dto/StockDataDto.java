package com.yanosik.rcd.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.yanosik.rcd.model.StockData}
 */
@Value
public class StockDataDto implements Serializable {
		List<StockPriceDto> stockPrices;
		StockMetadataDto stockMetadata;

		/**
		 * DTO for {@link com.yanosik.rcd.model.StockPrice}
		 */
		@Value
		public static class StockPriceDto implements Serializable {
				LocalDateTime timestamp;
				BigDecimal open;
				BigDecimal high;
				BigDecimal low;
				BigDecimal close;
				BigDecimal volume;

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

		}
}