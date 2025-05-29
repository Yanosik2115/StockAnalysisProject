package com.yanosik.rcd.model;

import com.yanosik.rcd.support.TimeSeriesPoint;
import com.yanosik.rcd.support.TrendChangePoint;
import com.yanosik.rcd.enums.TimeSeriesType;
import com.yanosik.rcd.enums.TrendDirection;
import com.yanosik.rcd.support.ForecastPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TimeSeriesAnalysis extends Analysis {

		private List<TimeSeriesPoint> timeSeries;
		private TimeSeriesType seriesType;

		// Trend analysis
		private TrendDirection trend;
		private Double trendStrength;
		private List<TrendChangePoint> changePoints;

		// Seasonality
		private Boolean hasSeasonality;
		private Integer seasonalPeriod;

		// Forecasting
		private List<ForecastPoint> forecast;
		private Integer forecastHorizon;

		@Override
		public boolean isValid() {
				return timeSeries != null &&
						!timeSeries.isEmpty() &&
						getSymbol() != null &&
						getAnalysisType() != null;
		}

		@Override
		public String getDisplayName() {
				return String.format("%s Time Series for %s",
						getAnalysisType().name(),
						getSymbol());
		}
}