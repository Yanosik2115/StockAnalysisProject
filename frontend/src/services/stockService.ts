import axios from 'axios';
import { Stock, StockQuote, StockChart, TimeRange, StockNews, StockAnalysis } from '@/types/stock';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://api.example.com/v1';

const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.request.use((config) => {
    const apiKey = process.env.REACT_APP_API_KEY;
    if (apiKey) {
        config.headers['X-API-Key'] = apiKey;
    }
    return config;
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error);
        if (error.response?.status === 429) {
            throw new Error('Rate limit exceeded. Please try again later.');
        }
        if (error.response?.status >= 500) {
            throw new Error('Server error. Please try again later.');
        }
        throw error;
    }
);

class StockService {
    private readonly mockStocks: Stock[] = [
        { symbol: 'AAPL', name: 'Apple Inc.', exchange: 'NASDAQ', sector: 'Technology', industry: 'Consumer Electronics' },
        { symbol: 'GOOGL', name: 'Alphabet Inc.', exchange: 'NASDAQ', sector: 'Technology', industry: 'Internet Software & Services' },
        { symbol: 'MSFT', name: 'Microsoft Corporation', exchange: 'NASDAQ', sector: 'Technology', industry: 'Software' },
        { symbol: 'AMZN', name: 'Amazon.com Inc.', exchange: 'NASDAQ', sector: 'Consumer Discretionary', industry: 'E-commerce' },
        { symbol: 'TSLA', name: 'Tesla Inc.', exchange: 'NASDAQ', sector: 'Consumer Discretionary', industry: 'Electric Vehicles' },
    ];

    private generateMockQuote(symbol: string): StockQuote {
        const basePrice = Math.random() * 200 + 50;
        const change = (Math.random() - 0.5) * 10;
        const changePercent = (change / basePrice) * 100;

        return {
            symbol,
            name: this.mockStocks.find(s => s.symbol === symbol)?.name || `${symbol} Inc.`,
            price: Number(basePrice.toFixed(2)),
            change: Number(change.toFixed(2)),
            changePercent: Number(changePercent.toFixed(2)),
            volume: Math.floor(Math.random() * 10000000),
            avgVolume: Math.floor(Math.random() * 5000000),
            marketCap: Math.floor(Math.random() * 1000000000000),
            peRatio: Number((Math.random() * 30 + 5).toFixed(2)),
            high52Week: Number((basePrice * 1.5).toFixed(2)),
            low52Week: Number((basePrice * 0.7).toFixed(2)),
            dividendYield: Number((Math.random() * 5).toFixed(2)),
            eps: Number((Math.random() * 10 + 1).toFixed(2)),
            beta: Number((Math.random() * 2 + 0.5).toFixed(2)),
            previousClose: Number((basePrice - change).toFixed(2)),
            dayHigh: Number((basePrice * 1.05).toFixed(2)),
            dayLow: Number((basePrice * 0.95).toFixed(2)),
            openPrice: Number((basePrice * 0.98).toFixed(2)),
            lastUpdated: new Date().toISOString(),
        };
    }

    private generateMockChart(symbol: string, range: TimeRange): StockChart[] {
        const days = this.getRangeDays(range);
        const data: StockChart[] = [];
        let currentPrice = Math.random() * 200 + 50;

        for (let i = days; i >= 0; i--) {
            const date = new Date();
            date.setDate(date.getDate() - i);

            const volatility = 0.02;
            const change = (Math.random() - 0.5) * volatility * currentPrice;
            const open = currentPrice;
            const close = open + change;
            const high = Math.max(open, close) * (1 + Math.random() * 0.01);
            const low = Math.min(open, close) * (1 - Math.random() * 0.01);

            data.push({
                timestamp: date.toISOString(),
                open: Number(open.toFixed(2)),
                high: Number(high.toFixed(2)),
                low: Number(low.toFixed(2)),
                close: Number(close.toFixed(2)),
                volume: Math.floor(Math.random() * 10000000),
            });

            currentPrice = close;
        }

        return data;
    }

    private getRangeDays(range: TimeRange): number {
        switch (range) {
            case '1D': return 1;
            case '1W': return 7;
            case '1M': return 30;
            case '3M': return 90;
            case '1Y': return 365;
            case '5Y': return 1825;
            default: return 30;
        }
    }

    async getStockQuote(symbol: string): Promise<StockQuote> {
        try {
            // Mock implementation
            await new Promise(resolve => setTimeout(resolve, 500)); // Simulate API delay
            return this.generateMockQuote(symbol);
        } catch (error) {
            throw new Error(`Failed to fetch quote for ${symbol}: ${error instanceof Error ? error.message : 'Unknown error'}`);
        }
    }

    async getStockChart(symbol: string, range: TimeRange): Promise<StockChart[]> {
        try {
            // Mock implementation
            await new Promise(resolve => setTimeout(resolve, 300)); // Simulate API delay
            return this.generateMockChart(symbol, range);
        } catch (error) {
            throw new Error(`Failed to fetch chart for ${symbol}: ${error instanceof Error ? error.message : 'Unknown error'}`);
        }
    }

    async searchStocks(query: string): Promise<Stock[]> {
        try {
            // Mock implementation
            await new Promise(resolve => setTimeout(resolve, 200)); // Simulate API delay
            return this.mockStocks.filter(stock =>
                stock.symbol.toLowerCase().includes(query.toLowerCase()) ||
                stock.name.toLowerCase().includes(query.toLowerCase())
            );
        } catch (error) {
            throw new Error(`Failed to search stocks: ${error instanceof Error ? error.message : 'Unknown error'}`);
        }
    }

    async getStockNews(symbol: string): Promise<StockNews[]> {
        try {
            // Mock implementation
            await new Promise(resolve => setTimeout(resolve, 400));
            return [
                {
                    id: '1',
                    headline: `${symbol} Reports Strong Quarterly Earnings`,
                    summary: 'Company exceeds analyst expectations with strong revenue growth.',
                    source: 'Financial News',
                    publishedAt: new Date().toISOString(),
                    url: 'https://example.com/news/1',
                    sentiment: 'positive',
                    symbols: [symbol],
                },
            ];
        } catch (error) {
            throw new Error(`Failed to fetch news for ${symbol}: ${error instanceof Error ? error.message : 'Unknown error'}`);
        }
    }

    async getStockAnalysis(symbol: string): Promise<StockAnalysis> {
        try {
            // Mock implementation
            await new Promise(resolve => setTimeout(resolve, 300));
            return {
                symbol,
                recommendation: 'BUY',
                targetPrice: 150,
                analystCount: 25,
                strongBuy: 10,
                buy: 8,
                hold: 5,
                sell: 2,
                strongSell: 0,
                lastUpdated: new Date().toISOString(),
            };
        } catch (error) {
            throw new Error(`Failed to fetch analysis for ${symbol}: ${error instanceof Error ? error.message : 'Unknown error'}`);
        }
    }
}

export const stockService = new StockService();