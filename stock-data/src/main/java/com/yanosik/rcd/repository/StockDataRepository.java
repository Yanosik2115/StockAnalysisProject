package com.yanosik.rcd.repository;


import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StockDataRepository extends JpaRepository<StockData, Long> {
		StockData findStockDataByStockMetadata_Symbol(String symbol);

}
