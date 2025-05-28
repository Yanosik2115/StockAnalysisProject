package com.yanosik.rcd.repository;

import com.yanosik.rcd.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

		List<StockPrice> findStockPricesByTimestampBetween(
				LocalDate timestamp, LocalDate timestamp2);

//		List<StockPrice> findStockPricesByTimestampBetween

}
