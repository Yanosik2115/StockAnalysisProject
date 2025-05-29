package com.yanosik.rcd.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesPoint {
		private LocalDateTime timestamp;
		private Double value;
		private Double volume;
		private Map<String, Object> attributes;
}