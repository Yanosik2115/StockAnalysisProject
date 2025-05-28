package com.yanosik.rcd.model;

import com.yanosik.rcd.dto.StockDataDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @param callbackTopic Optional: for response routing
 */
public record AnalysisRequest(String requestId, String symbol, AnalysisType analysisType,
                              LocalDateTime requestTime,
                              Map<String, String> parameters,
                              String callbackTopic) implements Serializable {
}