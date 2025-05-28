package com.yanosik.rcd.parser;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.model.StockData;
import com.yanosik.rcd.model.StockMetadata;
import com.yanosik.rcd.model.StockPrice;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between StockData entity and StockDataDto
 */
@Component
public class StockDataParser {

		public StockDataDto toDto(StockData stockData) {
				if (stockData == null) {
						return null;
				}

				List<StockDataDto.StockPriceDto> stockPriceDtos = mapStockPricesToDtos(stockData.getStockPrices());
				StockDataDto.StockMetadataDto stockMetadataDto = mapStockMetadataToDto(stockData.getStockMetadata());

				return new StockDataDto(stockPriceDtos, stockMetadataDto);
		}

		public StockData toEntity(StockDataDto stockDataDto) {
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

		public StockData updateEntityFromDto(StockData stockData, StockDataDto stockDataDto) {
				if (stockData == null || stockDataDto == null) {
						return stockData;
				}

				if (stockDataDto.getStockMetadata() != null) {
						if (stockData.getStockMetadata() == null) {
								StockMetadata newMetadata = new StockMetadata();
								stockData.setStockMetadata(newMetadata);
						}
						updateStockMetadataFromDto(stockData.getStockMetadata(), stockDataDto.getStockMetadata());
				}

				// Update stock prices - replace existing list while maintaining relationships
				if (stockDataDto.getStockPrices() != null) {
						// Clear existing prices
						if (stockData.getStockPrices() != null) {
								stockData.getStockPrices().clear();
						} else {
								stockData.setStockPrices(new ArrayList<>());
						}

						// Add new prices with proper relationships
						List<StockPrice> updatedPrices = mapStockPriceDtosToEntities(stockDataDto.getStockPrices(), stockData);
						stockData.getStockPrices().addAll(updatedPrices);
				}

				return stockData;
		}


		private List<StockDataDto.StockPriceDto> mapStockPricesToDtos(List<StockPrice> stockPrices) {
				if (stockPrices == null) {
						return new ArrayList<>();
				}

				return stockPrices.stream()
						.map(this::mapStockPriceToDto)
						.collect(Collectors.toList());
		}


		private StockDataDto.StockPriceDto mapStockPriceToDto(StockPrice stockPrice) {
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


		private StockDataDto.StockMetadataDto mapStockMetadataToDto(StockMetadata stockMetadata) {
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


		private List<StockPrice> mapStockPriceDtosToEntities(List<StockDataDto.StockPriceDto> stockPriceDtos, StockData parentStockData) {
				if (stockPriceDtos == null) {
						return new ArrayList<>();
				}

				return stockPriceDtos.stream()
						.map(dto -> mapStockPriceDtoToEntity(dto, parentStockData))
						.collect(Collectors.toList());
		}

		/**
		 * FIXED: Converts StockPriceDto to StockPrice entity with proper parent reference
		 */
		private StockPrice mapStockPriceDtoToEntity(StockDataDto.StockPriceDto stockPriceDto, StockData parentStockData) {
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

		private StockMetadata mapStockMetadataDtoToEntity(StockDataDto.StockMetadataDto stockMetadataDto) {
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

		private void updateStockMetadataFromDto(StockMetadata stockMetadata, StockDataDto.StockMetadataDto stockMetadataDto) {
				if (stockMetadata == null || stockMetadataDto == null) {
						return;
				}

				stockMetadata.setInformation(stockMetadataDto.getInformation());
				stockMetadata.setSymbol(stockMetadataDto.getSymbol());
				stockMetadata.setLastRefreshed(stockMetadataDto.getLastRefreshed());
				stockMetadata.setInterval(stockMetadataDto.getInterval());
				stockMetadata.setOutputSize(stockMetadataDto.getOutputSize());
		}
}