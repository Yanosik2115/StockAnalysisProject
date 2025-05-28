package com.yanosik.rcd.repository;

import com.yanosik.rcd.model.StockAnalysisJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockAnalysisRepository extends JpaRepository<StockAnalysisJob, Long> {

		StockAnalysisJob findStockAnalysisJobByJobId(String jobId);
}
