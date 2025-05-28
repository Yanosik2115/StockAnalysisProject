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

		@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
		@JoinColumn(name = "stock_id")
		private List<StockPrice> stockPrices = new ArrayList<>();

		@OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
		@JoinColumn(name = "stock_metadata_id", nullable = false, unique = true)
		private StockMetadata stockMetadata;

		@Override
		public String toString() {
				return "StockData{" +
						"id=" + id +
						", stockPrices=" + stockPrices +
						", stockMetadata=" + stockMetadata +
						'}';
		}
}
