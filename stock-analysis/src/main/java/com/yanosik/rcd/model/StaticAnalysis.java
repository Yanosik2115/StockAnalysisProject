package com.yanosik.rcd.model;

import com.yanosik.rcd.model.Analysis;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StaticAnalysis extends Analysis {

		private Double value;
		private String unit;
		private String interpretation;

		private Double confidenceLevel;
		private Double upperBound;
		private Double lowerBound;

		private Double previousValue;
		private Double marketAverage;

		@Override
		public boolean isValid() {
				return value != null &&
						!value.isNaN() &&
						!value.isInfinite() &&
						getSymbol() != null &&
						getAnalysisType() != null;
		}

		@Override
		public String getDisplayName() {
				return String.format("%s for %s: %.2f",
						getAnalysisType().name(),
						getSymbol(),
						value);
		}
}