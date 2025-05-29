package com.yanosik.rcd.support;

import com.yanosik.rcd.enums.TrendDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendChangePoint {
		private LocalDateTime timestamp;
		private Double value;
		private TrendDirection fromTrend;
		private TrendDirection toTrend;
		private Double significance;
}