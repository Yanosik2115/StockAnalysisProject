package com.yanosik.rcd.repository;


import com.yanosik.rcd.model.StockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StockDataRepository extends JpaRepository<StockData, Long> {
		StockData findStockDataByStockMetadata_Symbol(String symbol);
}
