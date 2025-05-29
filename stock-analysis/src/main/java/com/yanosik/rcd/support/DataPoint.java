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
public class DataPoint {
		private Double x;
		private Double y;
		private LocalDateTime timestamp;
		private Map<String, Object> metadata;

		public boolean isValid() {
				return x != null && y != null &&
						!x.isNaN() && !y.isNaN() &&
						!x.isInfinite() && !y.isInfinite();
		}
}