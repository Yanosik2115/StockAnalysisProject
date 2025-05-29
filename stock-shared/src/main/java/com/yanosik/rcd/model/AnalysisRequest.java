package com.yanosik.rcd.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;


public record AnalysisRequest(String requestId, String symbol, AnalysisType analysisType,
                              LocalDateTime requestTime,
                              Map<String, String> parameters) implements Serializable {
}