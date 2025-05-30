package com.yanosik.rcd.repository;


import com.yanosik.rcd.model.StockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface StockDataRepository extends JpaRepository<StockData, Long> {
		StockData findStockDataByStockMetadata_Symbol(String symbol);


		@Query("""
				SELECT DISTINCT sd FROM StockData sd\s
				LEFT JOIN FETCH sd.stockMetadata sm\s
				LEFT JOIN FETCH sd.stockPrices sp\s
				WHERE sm.symbol = :symbol\s
				AND sp.timestamp >= :startDate\s
				AND sp.timestamp <= :endDate\s
				ORDER BY sp.timestamp ASC\s
				""")
		StockData findStockDataWithFilteredPrices(
				@Param("symbol") String symbol,
				@Param("startDate") LocalDate startDate,
				@Param("endDate") LocalDate endDate
		);
}
