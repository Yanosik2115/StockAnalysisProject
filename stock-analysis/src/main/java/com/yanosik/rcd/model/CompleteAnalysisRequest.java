package com.yanosik.rcd.model;

import com.yanosik.rcd.dto.StockDataDto;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@Builder
@Jacksonized
public class CompleteAnalysisRequest {
		String requestId;
		AnalysisType analysisType;
		Map<String,String> parameters;
		StockDataDto stockData;
}
