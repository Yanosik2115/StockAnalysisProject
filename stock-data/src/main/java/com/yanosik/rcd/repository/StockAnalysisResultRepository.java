package com.yanosik.rcd.repository;

import com.yanosik.rcd.model.StockAnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockAnalysisResultRepository extends JpaRepository<StockAnalysisResult, Long> {

	StockAnalysisResult findStockAnalysisResultByJobId(String jobId);

}
