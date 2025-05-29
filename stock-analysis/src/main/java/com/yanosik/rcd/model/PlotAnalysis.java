package com.yanosik.rcd.model;

import com.yanosik.rcd.enums.LineStyle;
import com.yanosik.rcd.enums.PlotType;
import com.yanosik.rcd.support.DataPoint;
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
public class PlotAnalysis extends Analysis {

		private List<DataPoint> dataPoints;
		private PlotType plotType;
		private String chartTitle;
		private String xAxisLabel;
		private String yAxisLabel;

		// Visual configuration
		private String color;
		private LineStyle lineStyle;
		private Integer lineWidth;

		// Statistical summary
		private Double minValue;
		private Double maxValue;
		private Double averageValue;

		@Override
		public boolean isValid() {
				return dataPoints != null &&
						!dataPoints.isEmpty() &&
						getSymbol() != null &&
						getAnalysisType() != null &&
						dataPoints.stream().allMatch(DataPoint::isValid);
		}

		@Override
		public String getDisplayName() {
				return String.format("%s for %s (%d points)",
						getAnalysisType().name(),
						getSymbol(),
						dataPoints != null ? dataPoints.size() : 0);
		}
}