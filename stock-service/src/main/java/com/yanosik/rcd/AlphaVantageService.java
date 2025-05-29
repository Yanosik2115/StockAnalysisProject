package com.yanosik.rcd;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.parser.StockDataDtoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class AlphaVantageService {
		private static final Logger log = LoggerFactory.getLogger(AlphaVantageService.class);

		private final KafkaTemplate<String, StockDataDto> kafkaTemplate;
		private final WebClient webClient;
		private final DiscoveryClient discoveryClient;


		@Value("${ALPHAVANTAGE_API_KEY}")
		private String apiKey;

		public AlphaVantageService(KafkaTemplate<String, StockDataDto> kafkaTemplate,
		                           WebClient webClient,
		                           DiscoveryClient discoveryClient) {
				this.kafkaTemplate = kafkaTemplate;
				this.webClient = webClient;
				this.discoveryClient = discoveryClient;
		}

		public Mono<StockDataDto> fetchStockData(String symbol) {
				log.info("Fetching stock data for symbol: {}", symbol);

				return getStockDataServiceInstance()
						.flatMap(serviceInstance -> fetchFromStockDataService(serviceInstance, symbol))
						.switchIfEmpty(fetchStockDataFromApi(symbol))
						.doOnError(error -> log.error("Error fetching stock data for {}: {}", symbol, error.getMessage()));
		}

		private Mono<ServiceInstance> getStockDataServiceInstance() {
				return Mono.fromCallable(() -> {
						var instances = discoveryClient.getInstances("stock-data");
						if (instances.isEmpty()) {
								throw new RuntimeException("No stock-data service instances available");
						}
						return instances.get(0);
				});
		}

		private Mono<StockDataDto> fetchFromStockDataService(ServiceInstance serviceInstance, String symbol) {
				String uri = UriComponentsBuilder.fromUri(serviceInstance.getUri())
						.path("/stock-data/get")
						.queryParam("symbol", symbol)
						.build()
						.toUriString();

				return webClient.get()
						.uri(uri)
						.retrieve()
						.onStatus(HttpStatusCode::isError, response ->
								Mono.error(new RuntimeException("Stock data service returned error: " + response.statusCode())))
						.onStatus(status -> status.value() == 204, response -> {
								log.info("No cached data found for symbol: {}, will fetch from API", symbol);
								return Mono.empty();
						})
						.bodyToMono(StockDataDto.class)
						.filter(Objects::nonNull);
		}

		private Mono<StockDataDto> fetchStockDataFromApi(String symbol) {
				log.info("Fetching stock data for symbol: {} from AlphaVantage API", symbol);

				return webClient.get()
						.uri(uriBuilder -> uriBuilder
								.scheme("https")
								.host("www.alphavantage.co")
								.path("/query")
								.queryParam("function", "TIME_SERIES_DAILY")
								.queryParam("outputsize", "full")
								.queryParam("symbol", symbol)
								.queryParam("apikey", apiKey)
								.build())
						.retrieve()
						.onStatus(HttpStatusCode::isError, response ->
								Mono.error(new RuntimeException("AlphaVantage API error: " + response.statusCode())))
						.bodyToMono(String.class)
						.doOnNext(jsonResponse -> {
								StockDataDto stockDataDto = StockDataDtoParser.parse(jsonResponse);
								log.info("Received response from AlphaVantage API for symbol: {}", symbol);
								CompletableFuture.runAsync(() -> {
										try {
												kafkaTemplate.send("stock_data", stockDataDto);
												log.info("Sent stock data to Kafka for symbol: {}", symbol);
										} catch (Exception e) {
												log.error("Failed to send stock data to Kafka for symbol: {}", symbol, e);
										}
								});
						})
						.map(StockDataDtoParser::parse)
						.switchIfEmpty(Mono.error(new RuntimeException("No data available from AlphaVantage")));
		}
}
