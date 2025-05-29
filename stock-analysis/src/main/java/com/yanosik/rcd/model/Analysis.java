package com.yanosik.rcd.model;

import com.yanosik.rcd.enums.AnalysisStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "analysisType")
@JsonSubTypes({
		@JsonSubTypes.Type(value = StaticAnalysis.class, name = "STATIC"),
		@JsonSubTypes.Type(value = PlotAnalysis.class, name = "PLOT"),
		@JsonSubTypes.Type(value = TimeSeriesAnalysis.class, name = "TIME_SERIES")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Analysis {

		// Core identification
		private String id;
		private String symbol;
		private AnalysisType analysisType;

		// Time period for analysis
		private LocalDate startDate;
		private LocalDate endDate;

		// Metadata
		private LocalDateTime calculatedAt;
		private String calculatedBy;
		private Map<String, Object> parameters;

		// Status and validation
		private AnalysisStatus status;
		private String errorMessage;

		// Performance metrics
		private Long calculationTimeMs;
		private Integer dataPointsUsed;

		// Abstract method to be implemented by subclasses
		public abstract boolean isValid();
		public abstract String getDisplayName();
}