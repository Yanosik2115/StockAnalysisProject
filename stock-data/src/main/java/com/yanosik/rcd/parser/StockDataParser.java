package com.yanosik.rcd.parser;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.model.StockMetadata;
import com.yanosik.rcd.model.StockPrice;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockDataParser {

		public static StockDataDto toDto(StockData stockData) {
				if (stockData == null) {
						return null;
				}

				List<StockDataDto.StockPriceDto> stockPriceDtos = mapStockPricesToDtos(stockData.getStockPrices());
				StockDataDto.StockMetadataDto stockMetadataDto = mapStockMetadataToDto(stockData.getStockMetadata());
				return new StockDataDto(stockPriceDtos, stockMetadataDto);
		}

		public static StockData toEntity(StockDataDto stockDataDto) {
				if (stockDataDto == null) {
						return null;
				}

				StockData stockData = new StockData();

				// Set up metadata first
				StockMetadata stockMetadata = mapStockMetadataDtoToEntity(stockDataDto.getStockMetadata());
				if (stockMetadata == null) {
						throw new IllegalArgumentException("StockMetadata cannot be null");
				}

				stockData.setStockMetadata(stockMetadata);

				List<StockPrice> stockPrices = mapStockPriceDtosToEntities(stockDataDto.getStockPrices(), stockData);
				stockData.setStockPrices(stockPrices);

				return stockData;
		}


		private static List<StockDataDto.StockPriceDto> mapStockPricesToDtos(List<StockPrice> stockPrices) {
				if (stockPrices == null) {
						return new ArrayList<>();
				}

				return stockPrices.stream()
						.map(StockDataParser::mapStockPriceToDto)
						.collect(Collectors.toList());
		}


		private static StockDataDto.StockPriceDto mapStockPriceToDto(StockPrice stockPrice) {
				if (stockPrice == null) {
						return null;
				}

				return new StockDataDto.StockPriceDto(
						stockPrice.getTimestamp(),
						stockPrice.getOpen(),
						stockPrice.getHigh(),
						stockPrice.getLow(),
						stockPrice.getClose(),
						stockPrice.getVolume()
				);
		}


		private static StockDataDto.StockMetadataDto mapStockMetadataToDto(StockMetadata stockMetadata) {
				if (stockMetadata == null) {
						return null;
				}

				return new StockDataDto.StockMetadataDto(
						stockMetadata.getInformation(),
						stockMetadata.getSymbol(),
						stockMetadata.getLastRefreshed(),
						stockMetadata.getInterval(),
						stockMetadata.getOutputSize()
				);
		}


		private static List<StockPrice> mapStockPriceDtosToEntities(List<StockDataDto.StockPriceDto> stockPriceDtos, StockData parentStockData) {
				if (stockPriceDtos == null) {
						return new ArrayList<>();
				}

				return stockPriceDtos.stream()
						.map(dto -> mapStockPriceDtoToEntity(dto, parentStockData))
						.collect(Collectors.toList());
		}


		private static StockPrice mapStockPriceDtoToEntity(StockDataDto.StockPriceDto stockPriceDto, StockData parentStockData) {
				if (stockPriceDto == null) {
						return null;
				}

				StockPrice stockPrice = new StockPrice();
				stockPrice.setTimestamp(stockPriceDto.getTimestamp());
				stockPrice.setOpen(stockPriceDto.getOpen());
				stockPrice.setHigh(stockPriceDto.getHigh());
				stockPrice.setLow(stockPriceDto.getLow());
				stockPrice.setClose(stockPriceDto.getClose());
				stockPrice.setVolume(stockPriceDto.getVolume());
				stockPrice.setStockData(parentStockData);

				return stockPrice;
		}

		private static StockMetadata mapStockMetadataDtoToEntity(StockDataDto.StockMetadataDto stockMetadataDto) {
				if (stockMetadataDto == null) {
						return null;
				}

				StockMetadata stockMetadata = new StockMetadata();
				stockMetadata.setInformation(stockMetadataDto.getInformation());
				stockMetadata.setSymbol(stockMetadataDto.getSymbol());
				stockMetadata.setLastRefreshed(stockMetadataDto.getLastRefreshed());
				stockMetadata.setInterval(stockMetadataDto.getInterval());
				stockMetadata.setOutputSize(stockMetadataDto.getOutputSize());

				return stockMetadata;
		}

}