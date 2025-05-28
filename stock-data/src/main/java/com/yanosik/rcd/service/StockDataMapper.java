package com.yanosik.rcd.service;
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
public class StockDataMapper {

		/**
		 * Converts StockData entity to StockDataDto
		 *
		 * @param stockData the entity to convert
		 * @return StockDataDto or null if input is null
		 */
		public StockDataDto toDto(StockData stockData) {
				if (stockData == null) {
						return null;
				}

				List<StockDataDto.StockPriceDto> stockPriceDtos = mapStockPricesToDtos(stockData.getStockPrices());
				StockDataDto.StockMetadataDto stockMetadataDto = mapStockMetadataToDto(stockData.getStockMetadata());

				return new StockDataDto(stockPriceDtos, stockMetadataDto);
		}

		/**
		 * Converts StockDataDto to StockData entity
		 *
		 * @param stockDataDto the DTO to convert
		 * @return StockData entity or null if input is null
		 */
		public StockData toEntity(StockDataDto stockDataDto) {
				if (stockDataDto == null) {
						return null;
				}

				StockData stockData = new StockData();

				List<StockPrice> stockPrices = mapStockPriceDtosToEntities(stockDataDto.getStockPrices());
				stockData.setStockPrices(stockPrices);

				StockMetadata stockMetadata = mapStockMetadataDtoToEntity(stockDataDto.getStockMetadata());
				stockData.setStockMetadata(stockMetadata);

				return stockData;
		}

		/**
		 * Updates existing StockData entity with data from StockDataDto
		 *
		 * @param stockData the entity to update
		 * @param stockDataDto the DTO containing new data
		 * @return updated StockData entity
		 */
		public StockData updateEntityFromDto(StockData stockData, StockDataDto stockDataDto) {
				if (stockData == null || stockDataDto == null) {
						return stockData;
				}

				// Update stock metadata
				if (stockDataDto.getStockMetadata() != null) {
						if (stockData.getStockMetadata() == null) {
								stockData.setStockMetadata(new StockMetadata());
						}
						updateStockMetadataFromDto(stockData.getStockMetadata(), stockDataDto.getStockMetadata());
				}

				// Update stock prices - replace existing list
				if (stockDataDto.getStockPrices() != null) {
						List<StockPrice> updatedPrices = mapStockPriceDtosToEntities(stockDataDto.getStockPrices());
						stockData.getStockPrices().clear();
						stockData.getStockPrices().addAll(updatedPrices);
				}

				return stockData;
		}

		/**
		 * Converts list of StockPrice entities to list of StockPriceDto
		 */
		private List<StockDataDto.StockPriceDto> mapStockPricesToDtos(List<StockPrice> stockPrices) {
				if (stockPrices == null) {
						return new ArrayList<>();
				}

				return stockPrices.stream()
						.map(this::mapStockPriceToDto)
						.collect(Collectors.toList());
		}

		/**
		 * Converts StockPrice entity to StockPriceDto
		 */
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

		/**
		 * Converts StockMetadata entity to StockMetadataDto
		 */
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

		/**
		 * Converts list of StockPriceDto to list of StockPrice entities
		 */
		private List<StockPrice> mapStockPriceDtosToEntities(List<StockDataDto.StockPriceDto> stockPriceDtos) {
				if (stockPriceDtos == null) {
						return new ArrayList<>();
				}

				return stockPriceDtos.stream()
						.map(this::mapStockPriceDtoToEntity)
						.collect(Collectors.toList());
		}

		/**
		 * Converts StockPriceDto to StockPrice entity
		 */
		private StockPrice mapStockPriceDtoToEntity(StockDataDto.StockPriceDto stockPriceDto) {
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

				return stockPrice;
		}

		/**
		 * Converts StockMetadataDto to StockMetadata entity
		 */
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

		/**
		 * Updates existing StockMetadata entity with data from StockMetadataDto
		 */
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

		/**
		 * Converts a list of StockData entities to a list of StockDataDto
		 *
		 * @param stockDataList list of entities to convert
		 * @return list of DTOs
		 */
		public List<StockDataDto> toDtoList(List<StockData> stockDataList) {
				if (stockDataList == null) {
						return new ArrayList<>();
				}

				return stockDataList.stream()
						.map(this::toDto)
						.collect(Collectors.toList());
		}

		/**
		 * Converts a list of StockDataDto to a list of StockData entities
		 *
		 * @param stockDataDtoList list of DTOs to convert
		 * @return list of entities
		 */
		public List<StockData> toEntityList(List<StockDataDto> stockDataDtoList) {
				if (stockDataDtoList == null) {
						return new ArrayList<>();
				}

				return stockDataDtoList.stream()
						.map(this::toEntity)
						.collect(Collectors.toList());
		}
}