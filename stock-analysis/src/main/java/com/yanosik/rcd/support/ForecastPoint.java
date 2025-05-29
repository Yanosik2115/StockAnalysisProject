package com.yanosik.rcd.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastPoint {
		private LocalDateTime timestamp;
		private Double predictedValue;
		private Double confidenceInterval;
		private Double upperBound;
		private Double lowerBound;
}