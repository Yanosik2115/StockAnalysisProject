package com.yanosik.rcd.model;

import com.yanosik.rcd.dto.StockDataDto;

import java.io.Serializable;

public record StockDataResponse(String requestId, String symbol, StockDataDto stockData,
                                String status) implements Serializable {
}
