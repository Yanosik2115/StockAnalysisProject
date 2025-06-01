export interface WatchlistItem {
    id: string;
    symbol: string;
    name: string;
    currentPrice: number;
    change: number;
    changePercent: number;
    volume: number;
    targetPrice?: number;
    notes?: string;
    addedAt: string;
    lastUpdated: string;
    alerts?: PriceAlert[];
}

export interface PriceAlert {
    id: string;
    type: 'above' | 'below';
    price: number;
    enabled: boolean;
    triggered: boolean;
    createdAt: string;
    triggeredAt?: string;
}

export interface WatchlistGroup {
    id: string;
    name: string;
    description?: string;
    items: WatchlistItem[];
    createdAt: string;
    updatedAt: string;
}