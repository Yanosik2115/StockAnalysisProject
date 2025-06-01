import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { WatchlistItem } from '@/types/watchlist';

interface WatchlistState {
    items: WatchlistItem[];
    loading: boolean;
    error: string | null;
}

const initialState: WatchlistState = {
    items: [],
    loading: false,
    error: null,
};

export const watchlistSlice = createSlice({
    name: 'watchlist',
    initialState,
    reducers: {
        addToWatchlist: (state, action: PayloadAction<Omit<WatchlistItem, 'id' | 'addedAt'>>) => {
            const existingItem = state.items.find(item => item.symbol === action.payload.symbol);

            if (!existingItem) {
                const newItem: WatchlistItem = {
                    ...action.payload,
                    id: Date.now().toString(),
                    addedAt: new Date().toISOString(),
                };
                state.items.push(newItem);
            }
        },

        removeFromWatchlist: (state, action: PayloadAction<string>) => {
            state.items = state.items.filter(item => item.symbol !== action.payload);
        },

        updateWatchlistPrice: (state, action: PayloadAction<{ symbol: string; currentPrice: number; change: number; changePercent: number }>) => {
            const { symbol, currentPrice, change, changePercent } = action.payload;
            const item = state.items.find(item => item.symbol === symbol);

            if (item) {
                item.currentPrice = currentPrice;
                item.change = change;
                item.changePercent = changePercent;
                item.lastUpdated = new Date().toISOString();
            }
        },

        updateWatchlistItem: (state, action: PayloadAction<{ symbol: string; updates: Partial<WatchlistItem> }>) => {
            const { symbol, updates } = action.payload;
            const item = state.items.find(item => item.symbol === symbol);

            if (item) {
                Object.assign(item, updates);
            }
        },

        setWatchlistItems: (state, action: PayloadAction<WatchlistItem[]>) => {
            state.items = action.payload;
        },

        clearWatchlist: (state) => {
            state.items = [];
            state.error = null;
        },

        setWatchlistLoading: (state, action: PayloadAction<boolean>) => {
            state.loading = action.payload;
        },

        setWatchlistError: (state, action: PayloadAction<string | null>) => {
            state.error = action.payload;
        },

        sortWatchlist: (state, action: PayloadAction<'symbol' | 'currentPrice' | 'change' | 'changePercent'>) => {
            const sortKey = action.payload;
            state.items.sort((a, b) => {
                if (sortKey === 'symbol') {
                    return a.symbol.localeCompare(b.symbol);
                }
                // Handle the numeric fields properly
                const aValue = sortKey === 'currentPrice' ? a.currentPrice :
                    sortKey === 'change' ? a.change :
                        sortKey === 'changePercent' ? a.changePercent : 0;
                const bValue = sortKey === 'currentPrice' ? b.currentPrice :
                    sortKey === 'change' ? b.change :
                        sortKey === 'changePercent' ? b.changePercent : 0;
                return bValue - aValue;
            });
        },
    },
});