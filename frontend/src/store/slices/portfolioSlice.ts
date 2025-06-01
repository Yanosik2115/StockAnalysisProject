import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { PortfolioHolding, Transaction, TransactionType } from '@/types/portfolio';

interface PortfolioState {
    holdings: PortfolioHolding[];
    transactions: Transaction[];
    totalValue: number;
    totalCost: number;
    totalGainLoss: number;
    totalGainLossPercent: number;
}

const initialState: PortfolioState = {
    holdings: [],
    transactions: [],
    totalValue: 0,
    totalCost: 0,
    totalGainLoss: 0,
    totalGainLossPercent: 0,
};

export const portfolioSlice = createSlice({
    name: 'portfolio',
    initialState,
    reducers: {
        addTransaction: (state, action: PayloadAction<Omit<Transaction, 'id'>>) => {
            const transaction: Transaction = {
                ...action.payload,
                id: Date.now().toString(),
            };

            state.transactions.push(transaction);
            updateHoldings(state);
        },

        removeTransaction: (state, action: PayloadAction<string>) => {
            state.transactions = state.transactions.filter(t => t.id !== action.payload);
            updateHoldings(state);
        },

        updateHoldingPrice: (state, action: PayloadAction<{ symbol: string; currentPrice: number }>) => {
            const { symbol, currentPrice } = action.payload;
            const holding = state.holdings.find(h => h.symbol === symbol);

            if (holding) {
                holding.currentPrice = currentPrice;
                holding.marketValue = holding.shares * currentPrice;
                holding.gainLoss = holding.marketValue - holding.totalCost;
                holding.gainLossPercent = (holding.gainLoss / holding.totalCost) * 100;
            }

            calculateTotals(state);
        },

        setPortfolioData: (state, action: PayloadAction<Partial<PortfolioState>>) => {
            Object.assign(state, action.payload);
        },

        clearPortfolio: (state) => {
            state.holdings = [];
            state.transactions = [];
            state.totalValue = 0;
            state.totalCost = 0;
            state.totalGainLoss = 0;
            state.totalGainLossPercent = 0;
        },
    },
});

// Helper functions
function updateHoldings(state: PortfolioState) {
    const holdingsMap: Record<string, PortfolioHolding> = {};

    state.transactions.forEach(transaction => {
        const { symbol, type, shares, price, date } = transaction;

        if (!holdingsMap[symbol]) {
            holdingsMap[symbol] = {
                symbol,
                shares: 0,
                averagePrice: 0,
                totalCost: 0,
                currentPrice: 0,
                marketValue: 0,
                gainLoss: 0,
                gainLossPercent: 0,
                lastUpdated: new Date().toISOString(),
            };
        }

        const holding = holdingsMap[symbol];

        if (type === TransactionType.BUY) {
            const newTotalCost = holding.totalCost + (shares * price);
            const newShares = holding.shares + shares;
            holding.averagePrice = newTotalCost / newShares;
            holding.shares = newShares;
            holding.totalCost = newTotalCost;
        } else if (type === TransactionType.SELL) {
            holding.shares -= shares;
            holding.totalCost -= shares * holding.averagePrice;

            if (holding.shares <= 0) {
                delete holdingsMap[symbol];
                return;
            }
        }

        holding.marketValue = holding.shares * holding.currentPrice;
        holding.gainLoss = holding.marketValue - holding.totalCost;
        holding.gainLossPercent = holding.totalCost > 0 ? (holding.gainLoss / holding.totalCost) * 100 : 0;
    });

    state.holdings = Object.values(holdingsMap);
    calculateTotals(state);
}

function calculateTotals(state: PortfolioState) {
    state.totalValue = state.holdings.reduce((sum, holding) => sum + holding.marketValue, 0);
    state.totalCost = state.holdings.reduce((sum, holding) => sum + holding.totalCost, 0);
    state.totalGainLoss = state.totalValue - state.totalCost;
    state.totalGainLossPercent = state.totalCost > 0 ? (state.totalGainLoss / state.totalCost) * 100 : 0;
}