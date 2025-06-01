import { configureStore } from '@reduxjs/toolkit';
import { stocksSlice } from './slices/stocksSlice';
import { portfolioSlice } from './slices/portfolioSlice';
import { watchlistSlice } from './slices/watchlistSlice';
import { uiSlice } from './slices/uiSlice';

export const store = configureStore({
    reducer: {
        stocks: stocksSlice.reducer,
        portfolio: portfolioSlice.reducer,
        watchlist: watchlistSlice.reducer,
        ui: uiSlice.reducer,
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
            serializableCheck: {
                ignoredActions: ['persist/PERSIST'],
            },
        }),
    devTools: process.env.NODE_ENV !== 'production',
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;