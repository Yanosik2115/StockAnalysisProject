package com.yanosik.rcd.consumer;

import com.yanosik.rcd.dto.StockDataDto;
import com.yanosik.rcd.enums.AnalysisStatus;
import com.yanosik.rcd.model.*;
import com.yanosik.rcd.support.TimeSeriesPoint;
import com.yanosik.rcd.utils.Utilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SMAAnalysisService extends BaseAnalysisService {

		public SMAAnalysisService(RedisTemplate<String, Object> redisTemplate, KafkaTemplate<String, StockDataRequest> kafkaTemplate) {
				super(redisTemplate);
				this.kafkaTemplate = kafkaTemplate;
		}

		private final KafkaTemplate<String, StockDataRequest> kafkaTemplate;
		private final Map<String, StockDataResponseHolder> pendingRequests = new ConcurrentHashMap<>();


		@KafkaListener(topics = "stock_data_responses", groupId = "stockAnalysisGroup")
		public void handleStockDataResponse(StockDataResponse response) {
				StockDataResponseHolder holder = pendingRequests.remove(response.requestId());
				log.info("Received stock data response for requestId: {}", response.requestId());
				if (holder != null) {
						if ("SUCCESS".equals(response.status())) {
								holder.complete(response.stockData());
						} else {
								holder.completeExceptionally(response.status());
						}
				}
		}

		public StockDataDto requestStockDataSync(String symbol, LocalDate startDate, LocalDate endDate) throws InterruptedException {
				String requestId = Utilities.generateId();
				StockDataResponseHolder holder = new StockDataResponseHolder();
				pendingRequests.put(requestId, holder);

				StockDataRequest request = new StockDataRequest(
						requestId,
						symbol,
						startDate,
						endDate
				);
				kafkaTemplate.send("stock_data_request_internal", requestId, request);

				return holder.getResult(30, TimeUnit.SECONDS);
		}

		@KafkaListener(topics = "stock_analysis_sma", groupId = "stockAnalysisGroup")
		public void consume(AnalysisRequest analysisRequest) {
				log.info("SMA analysis request received: {}", analysisRequest);
				processAnalysis(analysisRequest);
		}

		@Override
		public AnalysisType getSupportedAnalysisType() {
				return AnalysisType.SMA;
		}

		@Override
		protected String getServiceName() {
				return "SMA_Analysis_Service";
		}

		@Override
		protected Analysis performAnalysis(AnalysisRequest analysisRequest) {
				log.info("Performing SMA analysis for request: {}", analysisRequest.requestId());
				long startTimeMs = System.currentTimeMillis();
				Map<String, String> parameters = analysisRequest.parameters();

				try {
						List<TimeSeriesPoint> smaPoints = calculateSMA(analysisRequest);
						long calculationTimeMs = System.currentTimeMillis() - startTimeMs;

						if (smaPoints.isEmpty()) {
								log.warn("No SMA points calculated for request: {}", analysisRequest.requestId());
								return TimeSeriesAnalysis.builder()
										.id(analysisRequest.requestId())
										.symbol(analysisRequest.symbol())
										.startDate(LocalDate.parse(parameters.get("startDate")))
										.calculatedBy(getServiceName())
										.analysisType(getSupportedAnalysisType())
										.parameters(parameters)
										.status(AnalysisStatus.FAILED)
										.calculationTimeMs(calculationTimeMs)
										.build();
						}

						return TimeSeriesAnalysis.builder()
								.id(analysisRequest.requestId())
								.symbol(analysisRequest.symbol())
								.startDate(LocalDate.parse(parameters.get("startDate")))
								.calculatedBy(getServiceName())
								.timeSeries(smaPoints)
								.analysisType(getSupportedAnalysisType())
								.parameters(parameters)
								.status(AnalysisStatus.COMPLETED)
								.calculationTimeMs(calculationTimeMs)
								.build();
				} catch (Exception ex) {
						long calculationTimeMs = System.currentTimeMillis() - startTimeMs;
						log.error("Error performing SMA analysis for request: {}", analysisRequest.requestId(), ex);
						return TimeSeriesAnalysis.builder()
								.id(analysisRequest.requestId())
								.symbol(analysisRequest.symbol())
								.startDate(LocalDate.parse(parameters.getOrDefault("startDate", LocalDate.now().toString())))
								.calculatedBy(getServiceName())
								.analysisType(getSupportedAnalysisType())
								.parameters(parameters)
								.status(AnalysisStatus.FAILED)
								.calculationTimeMs(calculationTimeMs)
								.build();
				}
		}

		@Override
		protected boolean validateSpecificParameters(AnalysisRequest analysisRequest) {
				Map<String, String> parameters = analysisRequest.parameters();

				if (!parameters.containsKey("startDate") || !parameters.containsKey("endDate") || !parameters.containsKey("period")) {
						log.warn("Missing required parameters for SMA analysis (startDate, endDate, period)");
						return false;
				}

				return true;
		}

		private List<TimeSeriesPoint> calculateSMA(AnalysisRequest analysisRequest) throws InterruptedException {
				LocalDate startDate = LocalDate.parse(analysisRequest.parameters().get("startDate"));
				LocalDate endDate = LocalDate.parse(analysisRequest.parameters().get("endDate"));
				int smaPeriod = Integer.parseInt(analysisRequest.parameters().get("period"));
				int additionalWeeksNeeded = Math.floorDiv(smaPeriod, 5) * 7;
				log.info("Additional weeks needed for SMA calculation: {}", additionalWeeksNeeded);
				LocalDate fetchStartDate = startDate.minusDays(additionalWeeksNeeded);

				StockDataDto stockDataDto = requestStockDataSync(analysisRequest.symbol(), fetchStartDate, endDate);

				if (stockDataDto == null || stockDataDto.getStockPrices() == null || stockDataDto.getStockPrices().isEmpty()) {
						log.warn("No stock data available for symbol: {}", analysisRequest.symbol());
						return Collections.emptyList();
				}

				if (stockDataDto.getStockPrices().size() < smaPeriod + startDate.until(endDate).getDays()) {
						log.warn("Not enough stock prices available for SMA calculation for symbol: {}. Required: {}, Available: {}",
								analysisRequest.symbol(), smaPeriod + startDate.until(endDate).getDays(), stockDataDto.getStockPrices().size());
						return Collections.emptyList();
				}

				List<StockDataDto.StockPriceDto> stockPrices = stockDataDto.getStockPrices();
				stockPrices.sort(Comparator.comparing(StockDataDto.StockPriceDto::getTimestamp));

				List<TimeSeriesPoint> smaPoints = new ArrayList<>();
				int indexOfStartDate = stockPrices.indexOf(
						stockPrices.stream()
								.filter(price -> price.getTimestamp().equals(startDate))
								.findFirst()
								.orElse(null)
				);

				for (int i = indexOfStartDate; i < stockPrices.size(); i++) {
						LocalDate currentDate = stockPrices.get(i).getTimestamp();
						double sum = 0.0;
						for (int j = i - smaPeriod + 1; j <= i; j++) {
								sum += stockPrices.get(j).getClose().doubleValue();
						}
						double smaValue = sum / smaPeriod;

						TimeSeriesPoint smaPoint = TimeSeriesPoint.builder()
								.timestamp(currentDate.atStartOfDay())
								.value(smaValue)
								.build();

						smaPoints.add(smaPoint);
						log.info("Calculated SMA for date {}: {}", currentDate, smaValue);
				}

				log.info("Calculated {} SMA points for symbol {} from {} to {}",
						smaPoints.size(), analysisRequest.symbol(), startDate, endDate);

				return smaPoints;
		}
}