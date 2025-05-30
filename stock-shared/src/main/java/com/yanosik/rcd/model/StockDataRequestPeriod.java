package com.yanosik.rcd.model;

import java.io.Serializable;
import java.time.LocalDate;


public record StockDataRequestPeriod(String requestId, String symbol, LocalDate startDate,
                                     int period) implements Serializable {
}