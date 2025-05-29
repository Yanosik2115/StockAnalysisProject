package com.yanosik.rcd.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDataRequest implements Serializable {
		private String requestId;
		private String symbol;
		private LocalDate startDate;
		private LocalDate endDate;
}