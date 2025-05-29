package com.yanosik.rcd.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

		@Bean
		public NewTopic topic() {
				return TopicBuilder.name("simple_moving_average").build();
		}

		@Bean
		public NewTopic stockDataAnalysisTopic() {
				return TopicBuilder.name("stock_analysis_data").build();
		}

		@Bean
		public NewTopic stockAnalysisRequestTopic() {
				return TopicBuilder.name("stock_analysis_req").build();
		}

}