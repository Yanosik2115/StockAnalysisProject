package com.yanosik.rcd.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
public class StockData {
		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE)
		@Column(name = "id", nullable = false)
		private Long id;

		@OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
		@JoinColumn(name = "stock_metadata_id", nullable = false, unique = true)
		private StockMetadata stockMetadata;

		@OneToMany(mappedBy = "stockData", cascade = CascadeType.ALL, orphanRemoval = true)
		private List<StockPrice> stockPrices = new ArrayList<>();

		@Override
		public String toString() {
				return "StockData{" +
						"id=" + id +
						", stockPrices=" + stockPrices +
						", stockMetadata=" + stockMetadata +
						'}';
		}
}
