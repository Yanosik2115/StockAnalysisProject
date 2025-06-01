package com.yanosik.rcd.model;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class StockQuote {
		private String symbol;
		private String name;
		private BigDecimal price;
		private Double change;
		private Double changePercent;
		private Long volume;
		private Double avgVolume;
		private Long marketCap;
		private Double peRatio;
		private Double high52Week;
		private Double low52Week;
		private Double dividendYield;
		private Double eps;
		private Double beta;
		private Double previousClose;
		private BigDecimal dayHigh;
		private BigDecimal dayLow;
		private BigDecimal openPrice;
		private LocalDate lastUpdated;

}
