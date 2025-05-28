package com.yanosik.rcd.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "analysis_jobs")
public class StockAnalysisJob {
		@Id
		private String jobId;

		@Column(nullable = false)
		@Enumerated(EnumType.STRING)
		private StockAnalysisJobStatus status;

		@Column(columnDefinition = "TEXT")
		private String symbols;

		@Column
		private LocalDateTime startTime;

		@Column
		private LocalDateTime endTime;

		@Column(columnDefinition = "TEXT")
		private String errors;
}
