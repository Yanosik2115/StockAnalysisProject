package com.yanosik.rcd.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "stock_results")
public class StockAnalysisResult {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@Column(nullable = false)
		private String jobId;

		@Column(nullable = false)
		private String symbol;

		@Column
		private Double price;

		@Column
		private Double priceToEarnings;

		@Column
		private Double priceToBookValue;

		@Column
		private Double shillerPriceToEarnings;

		@Column
		private Double returnOnEquity;

		@Column
		private LocalDateTime analysisTime;

		@Column(columnDefinition = "TEXT")
		private String additionalData;
}
