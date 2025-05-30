package com.yanosik.rcd.model;

public interface AnalysisService {


		void processAnalysis(AnalysisRequest analysisRequest);


		AnalysisType getSupportedAnalysisType();

		boolean isValidRequest(AnalysisRequest analysisRequest);
}