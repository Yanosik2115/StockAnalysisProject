export interface PortfolioHolding {
    symbol: string;
    shares: number;
    averagePrice: number;
    totalCost: number;
    currentPrice: number;
    marketValue: number;
    gainLoss: number;
    gainLossPercent: number;
    lastUpdated: string;
}

export enum TransactionType {
    BUY = 'BUY',
    SELL = 'SELL',
    DIVIDEND = 'DIVIDEND',
}

export interface Transaction {
    id: string;
    symbol: string;
    type: TransactionType;
    shares: number;
    price: number;
    fee: number;
    date: string;
    notes?: string;
}

export interface PortfolioMetrics {
    totalValue: number;
    totalCost: number;
    totalGainLoss: number;
    totalGainLossPercent: number;
    dayChange: number;
    dayChangePercent: number;
    diversification: {
        sectors: Record<string, number>;
        holdings: Record<string, number>;
    };
}

export interface PortfolioSummary {
    holdings: PortfolioHolding[];
    metrics: PortfolioMetrics;
    topGainers: PortfolioHolding[];
    topLosers: PortfolioHolding[];
    recentTransactions: Transaction[];
}