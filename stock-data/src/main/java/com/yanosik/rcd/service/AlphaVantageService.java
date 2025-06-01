package com.yanosik.rcd.service;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.parser.StockDataDtoParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Slf4j
@Service
@AllArgsConstructor
public class AlphaVantageService {

//		@Value("${ALPHAVANTAGE_API_KEY}")
		private final String alphaVantageApiKey = "XG15WFEY5TQ2SDXM";

		private final RestClient restClient;

		public StockDataDto fetchStockDataFromApi(String symbol) {
				log.info("Fetching stock data for symbol: {} from AlphaVantage API", symbol);
				return StockDataDtoParser.parse(restClient.get()
						.uri(uriBuilder -> uriBuilder
								.scheme("https")
								.host("www.alphavantage.co")
								.path("/query")
								.queryParam("function", "TIME_SERIES_DAILY")
								.queryParam("outputsize", "full")
								.queryParam("symbol", symbol)
								.queryParam("apikey", alphaVantageApiKey)
								.build())
						.retrieve()
						.onStatus(HttpStatusCode::isError, (request, response) ->
								new RuntimeException("AlphaVantage API error: " + response.getStatusCode()))
						.body(String.class));

		}
}
