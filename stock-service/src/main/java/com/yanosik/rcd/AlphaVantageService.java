package com.yanosik.rcd;

import com.yanosik.rcd.dto.StockDataDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class AlphaVantageService {
		private static final Logger log = LoggerFactory.getLogger(AlphaVantageService.class);

		private final RestClient restClient;
		private final DiscoveryClient discoveryClient;

		public StockDataDto fetchStockData(String symbol, LocalDate startDate, LocalDate endDate) {
				log.info("Fetching stock data for symbol: {}", symbol);
				ServiceInstance stockDataService = getStockDataServiceInstance();
				if (stockDataService == null) {
						log.warn("No stock-data service instance available, fetching from API directly");
						throw new RuntimeException("No stock-data service instance available");
				}
				if (startDate == null || endDate == null) {
						log.warn("Start date or end date is null, fetching latest stock data for symbol: {}", symbol);
						return fetchFromStockDataService(stockDataService, symbol);
				}
				log.info("Fetching stock data for symbol: {} from {} to {}", symbol, startDate, endDate);
				return fetchFromStockDataServiceBetween(stockDataService, symbol, startDate, endDate);

		}

		private ServiceInstance getStockDataServiceInstance() {
				var instances = discoveryClient.getInstances("stock-data");
				if (instances.isEmpty()) {
						throw new RuntimeException("No stock-data service instances available");
				}
				return instances.get(0);
		}

		private StockDataDto fetchFromStockDataService(ServiceInstance serviceInstance, String symbol) {
				String uri = UriComponentsBuilder.fromUri(serviceInstance.getUri())
						.path("/stock-data/get")
						.queryParam("symbol", symbol)
						.build()
						.toUriString();
				return executeServiceCall(uri, symbol);
		}

		private StockDataDto fetchFromStockDataServiceBetween(ServiceInstance serviceInstance, String symbol, LocalDate startDate, LocalDate endDate) {
				String uri = UriComponentsBuilder.fromUri(serviceInstance.getUri())
						.path("/stock-data/get/between")
						.queryParam("symbol", symbol)
						.queryParam("startDate", startDate)
						.queryParam("endDate", endDate)
						.build()
						.toUriString();
				return executeServiceCall(uri, symbol);
		}

		private StockDataDto executeServiceCall(String uri, String symbol) {
				return restClient.get()
						.uri(uri)
						.retrieve()
						.onStatus(HttpStatusCode::isError, (response, request) ->
						{
								throw new RuntimeException("Stock data service returned error: " + request.getStatusCode());
						})
						.onStatus(status -> status.value() == 204, (response, request) -> {
								log.info("No cached data found for symbol: {}, will fetch from API", symbol);
								throw new RuntimeException("No cached data found for symbol: " + symbol);
						})
						.body(StockDataDto.class);
		}
}
