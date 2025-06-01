import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface UIState {
    sidebarOpen: boolean;
    theme: 'light' | 'dark';
    notifications: Notification[];
    modals: {
        addTransaction: boolean;
        editTransaction: boolean;
        addToWatchlist: boolean;
    };
    selectedTimeRange: '1D' | '1W' | '1M' | '3M' | '1Y' | '5Y';
    chartType: 'line' | 'candlestick';
}

interface Notification {
    id: string;
    type: 'success' | 'error' | 'warning' | 'info';
    title: string;
    message: string;
    timestamp: string;
    autoHide?: boolean;
}

const initialState: UIState = {
    sidebarOpen: true,
    theme: 'light',
    notifications: [],
    modals: {
        addTransaction: false,
        editTransaction: false,
        addToWatchlist: false,
    },
    selectedTimeRange: '1D',
    chartType: 'line',
};

export const uiSlice = createSlice({
    name: 'ui',
    initialState,
    reducers: {
        toggleSidebar: (state) => {
            state.sidebarOpen = !state.sidebarOpen;
        },

        setSidebarOpen: (state, action: PayloadAction<boolean>) => {
            state.sidebarOpen = action.payload;
        },

        toggleTheme: (state) => {
            state.theme = state.theme === 'light' ? 'dark' : 'light';
        },

        setTheme: (state, action: PayloadAction<'light' | 'dark'>) => {
            state.theme = action.payload;
        },

        addNotification: (state, action: PayloadAction<Omit<Notification, 'id' | 'timestamp'>>) => {
            const notification: Notification = {
                ...action.payload,
                id: Date.now().toString(),
                timestamp: new Date().toISOString(),
            };
            state.notifications.push(notification);
        },

        removeNotification: (state, action: PayloadAction<string>) => {
            state.notifications = state.notifications.filter(n => n.id !== action.payload);
        },

        clearNotifications: (state) => {
            state.notifications = [];
        },

        openModal: (state, action: PayloadAction<keyof UIState['modals']>) => {
            state.modals[action.payload] = true;
        },

        closeModal: (state, action: PayloadAction<keyof UIState['modals']>) => {
            state.modals[action.payload] = false;
        },

        closeAllModals: (state) => {
            Object.keys(state.modals).forEach(key => {
                state.modals[key as keyof UIState['modals']] = false;
            });
        },

        setSelectedTimeRange: (state, action: PayloadAction<UIState['selectedTimeRange']>) => {
            state.selectedTimeRange = action.payload;
        },

        setChartType: (state, action: PayloadAction<UIState['chartType']>) => {
            state.chartType = action.payload;
        },
    },
});