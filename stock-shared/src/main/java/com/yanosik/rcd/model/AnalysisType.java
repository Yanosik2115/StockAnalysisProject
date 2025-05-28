package com.yanosik.rcd.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum AnalysisType {
		TECHNICAL_ANALYSIS("technical_analysis"),
		RISK_ANALYSIS("risk_analysis"),
		TREND_ANALYSIS("trend_analysis"),
		VOLATILITY_ANALYSIS("volatility_analysis"),
		MOVING_AVERAGE("moving_average"),
		RSI_ANALYSIS("rsi_analysis"),
		MACD_ANALYSIS("macd_analysis");
		private final String topicName;
		AnalysisType(String topicName) {
				this.topicName = topicName;
		}
}