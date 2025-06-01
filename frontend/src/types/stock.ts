export interface Stock {
    symbol: string;
    name: string;
    exchange: string;
    sector?: string;
    industry?: string;
}

export interface StockQuote {
    symbol: string;
    name: string;
    price: number;
    change: number;
    changePercent: number;
    volume: number;
    avgVolume: number;
    marketCap: number;
    peRatio: number;
    high52Week: number;
    low52Week: number;
    dividendYield: number;
    eps: number;
    beta: number;
    previousClose: number;
    dayHigh: number;
    dayLow: number;
    openPrice: number;
    lastUpdated: string;
}

export interface StockChart {
    timestamp: string;
    open: number;
    high: number;
    low: number;
    close: number;
    volume: number;
}

export type TimeRange = '1D' | '1W' | '1M' | '3M' | '1Y' | '5Y';

export interface MarketData {
    symbol: string;
    timestamp: string;
    price: number;
    volume: number;
}

export interface StockNews {
    id: string;
    headline: string;
    summary: string;
    source: string;
    publishedAt: string;
    url: string;
    sentiment: 'positive' | 'negative' | 'neutral';
    symbols: string[];
}

export interface StockAnalysis {
    symbol: string;
    recommendation: 'BUY' | 'SELL' | 'HOLD';
    targetPrice: number;
    analystCount: number;
    strongBuy: number;
    buy: number;
    hold: number;
    sell: number;
    strongSell: number;
    lastUpdated: string;
}