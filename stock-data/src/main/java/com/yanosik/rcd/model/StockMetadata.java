package com.yanosik.rcd.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class StockMetadata {
		@Id
		@GeneratedValue(strategy = GenerationType.SEQUENCE)
		@Column(name = "id", nullable = false)
		private Long id;

		@JsonProperty("1. Information")
		private String information;
		@JsonProperty("2. Symbol")
		@Column(name = "symbol", unique = true, nullable = false)
		private String symbol;
		@JsonProperty("3. Last Refreshed")
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime lastRefreshed;
		@JsonProperty("4. Interval")
		@Column(name = "time_interval")
		private String interval;
		@JsonProperty("5. Output Size")
		private String outputSize;
		@JsonProperty("6. Time Zone")
		private String timeZone;

		@Override
		public String toString() {
				return "StockMetadata{" +
						"id=" + id +
						", information='" + information + '\'' +
						", symbol='" + symbol + '\'' +
						", lastRefreshed=" + lastRefreshed +
						'}';
		}
}
