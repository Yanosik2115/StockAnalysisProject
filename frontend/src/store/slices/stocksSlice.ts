import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import {stockService} from '@/services/stockService';
import {Stock, StockChart, StockQuote, TimeRange} from '@/types/stock';

interface StocksState {
    quotes: Record<string, StockQuote>;
    charts: Record<string, Record<TimeRange, StockChart[]>>;
    searchResults: Stock[];
    loading: {
        quotes: Record<string, boolean>;
        charts: Record<string, boolean>;
        search: boolean;
    };
    error: {
        quotes: Record<string, string | null>;
        charts: Record<string, string | null>;
        search: string | null;
    };
}

const initialState: StocksState = {
    quotes: {},
    charts: {},
    searchResults: [],
    loading: {
        quotes: {},
        charts: {},
        search: false,
    },
    error: {
        quotes: {},
        charts: {},
        search: null,
    },
};

// Async Thunks
export const fetchStockQuote = createAsyncThunk(
    'stocks/fetchQuote',
    async (symbol: string, { rejectWithValue }) => {
        try {
            const quote = await stockService.getStockQuote(symbol);
            return { symbol, quote };
        } catch (error) {
            return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
        }
    }
);

export const fetchStockChart = createAsyncThunk(
    'stocks/fetchChart',
    async ({ symbol, range }: { symbol: string; range: TimeRange }, { rejectWithValue }) => {
        try {
            const chart = await stockService.getStockChart(symbol, range);
            return { symbol, range, chart };
        } catch (error) {
            return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
        }
    }
);

export const searchStocks = createAsyncThunk(
    'stocks/search',
    async (query: string, { rejectWithValue }) => {
        try {
            return await stockService.searchStocks(query);
        } catch (error) {
            return rejectWithValue(error instanceof Error ? error.message : 'Unknown error');
        }
    }
);

export const stocksSlice = createSlice({
    name: 'stocks',
    initialState,
    reducers: {
        clearSearchResults: (state) => {
            state.searchResults = [];
            state.error.search = null;
        },
        clearErrors: (state) => {
            state.error = {
                quotes: {},
                charts: {},
                search: null,
            };
        },
    },
    extraReducers: (builder) => {
        builder
            // Fetch Stock Quote
            .addCase(fetchStockQuote.pending, (state, action) => {
                state.loading.quotes[action.meta.arg] = true;
                state.error.quotes[action.meta.arg] = null;
            })
            .addCase(fetchStockQuote.fulfilled, (state, action) => {
                const { symbol, quote } = action.payload;
                state.loading.quotes[symbol] = false;
                state.quotes[symbol] = quote;
                state.error.quotes[symbol] = null;
            })
            .addCase(fetchStockQuote.rejected, (state, action) => {
                const symbol = action.meta.arg;
                state.loading.quotes[symbol] = false;
                state.error.quotes[symbol] = action.payload as string;
            })
            // Fetch Stock Chart
            .addCase(fetchStockChart.pending, (state, action) => {
                const { symbol } = action.meta.arg;
                state.loading.charts[symbol] = true;
                state.error.charts[symbol] = null;
            })
            .addCase(fetchStockChart.fulfilled, (state, action) => {
                const { symbol, range, chart } = action.payload;
                state.loading.charts[symbol] = false;
                if (!state.charts[symbol]) {
                    state.charts[symbol] = {} as Record<TimeRange, StockChart[]>;
                }
                state.charts[symbol][range] = chart;
                state.error.charts[symbol] = null;
            })
            .addCase(fetchStockChart.rejected, (state, action) => {
                const { symbol } = action.meta.arg;
                state.loading.charts[symbol] = false;
                state.error.charts[symbol] = action.payload as string;
            })
            // Search Stocks
            .addCase(searchStocks.pending, (state) => {
                state.loading.search = true;
                state.error.search = null;
            })
            .addCase(searchStocks.fulfilled, (state, action) => {
                state.loading.search = false;
                state.searchResults = action.payload;
            })
            .addCase(searchStocks.rejected, (state, action) => {
                state.loading.search = false;
                state.error.search = action.payload as string;
            });
    },
});