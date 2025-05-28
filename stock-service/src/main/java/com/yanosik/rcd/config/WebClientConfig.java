package com.yanosik.rcd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

		private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebClientConfig.class);

		@Bean
		public WebClient webClient() {
				ExchangeStrategies strategies = ExchangeStrategies.builder()
						.codecs(clientCodecConfigurer ->
								clientCodecConfigurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10 MB
						.build();
				return WebClient.builder()
						.exchangeStrategies(strategies).filter(logRequest())
						.clientConnector(new ReactorClientHttpConnector(HttpClient.create())).build();
		}

		ExchangeFilterFunction logRequest() {
				return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
						if (log.isDebugEnabled()) {
								StringBuilder sb = new StringBuilder("Request: \n");
								clientRequest
										.headers()
										.forEach((name, values) -> values.forEach(value -> sb.append(name).append(": ").append(value).append("\n")));
								log.debug(sb.toString());
						}
						return Mono.just(clientRequest);
				});
		}
}
