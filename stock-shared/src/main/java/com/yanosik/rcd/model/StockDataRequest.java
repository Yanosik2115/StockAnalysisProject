package com.yanosik.rcd.model;

import java.io.Serializable;
import java.time.LocalDate;


public record StockDataRequest(String requestId, String symbol, LocalDate startDate,
                               LocalDate endDate) implements Serializable {
}