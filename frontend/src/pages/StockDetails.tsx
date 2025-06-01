import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import styled from 'styled-components';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { RootState } from '@store/store';
import { fetchStockQuote, fetchStockChart } from '@store/slices/stocksSlice';
import { TimeRange } from '@/types/stock';

const StockDetailsContainer = styled.div`
    max-width: 1400px;
    margin: 0 auto;
`;

const StockHeader = styled.div`
    margin-bottom: ${({ theme }) => theme.spacing.xl};
`;

const StockTitle = styled.h1`
    font-size: ${({ theme }) => theme.typography.fontSize['3xl']};
    font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
    color: ${({ theme }) => theme.colors.text.primary};
    margin-bottom: ${({ theme }) => theme.spacing.sm};
`;

const PriceSection = styled.div`
    display: flex;
    align-items: baseline;
    gap: ${({ theme }) => theme.spacing.md};
    margin-bottom: ${({ theme }) => theme.spacing.lg};
`;

const CurrentPrice = styled.div`
    font-size: ${({ theme }) => theme.typography.fontSize['4xl']};
    font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
    color: ${({ theme }) => theme.colors.text.primary};
`;

const PriceChange = styled.div<{ positive: boolean }>`
    font-size: ${({ theme }) => theme.typography.fontSize.xl};
    font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
    color: ${({ positive, theme }) =>
            positive ? theme.colors.success.main : theme.colors.error.main};
`;

const MetricsGrid = styled.div`
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: ${({ theme }) => theme.spacing.lg};
    margin-bottom: ${({ theme }) => theme.spacing.xl};
`;

const MetricCard = styled.div`
    background-color: ${({ theme }) => theme.colors.background.paper};
    padding: ${({ theme }) => theme.spacing.lg};
    border-radius: ${({ theme }) => theme.borderRadius.lg};
    border: 1px solid ${({ theme }) => theme.colors.border.light};
`;

const MetricLabel = styled.div`
    font-size: ${({ theme }) => theme.typography.fontSize.sm};
    color: ${({ theme }) => theme.colors.text.secondary};
    margin-bottom: ${({ theme }) => theme.spacing.xs};
`;

const MetricValue = styled.div`
    font-size: ${({ theme }) => theme.typography.fontSize.lg};
    font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
    color: ${({ theme }) => theme.colors.text.primary};
`;

const ChartContainer = styled.div`
    background-color: ${({ theme }) => theme.colors.background.paper};
    border-radius: ${({ theme }) => theme.borderRadius.lg};
    padding: ${({ theme }) => theme.spacing.xl};
    margin-bottom: ${({ theme }) => theme.spacing.xl};
    border: 1px solid ${({ theme }) => theme.colors.border.light};
`;

const ChartHeader = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: ${({ theme }) => theme.spacing.lg};
`;

const ChartTitle = styled.h3`
  font-size: ${({ theme }) => theme.typography.fontSize.xl};
  font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
  color: ${({ theme }) => theme.colors.text.primary};
  margin: 0;
`;

const TimeRangeButtons = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing.sm};
`;

const TimeRangeButton = styled.button<{ active: boolean }>`
  padding: ${({ theme }) => theme.spacing.xs} ${({ theme }) => theme.spacing.md};
  border: 1px solid ${({ theme }) => theme.colors.border.main};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  background-color: ${({ active, theme }) =>
    active ? theme.colors.primary.main : 'transparent'};
  color: ${({ active, theme }) =>
    active ? theme.colors.primary.contrast : theme.colors.text.secondary};
  font-size: ${({ theme }) => theme.typography.fontSize.sm};
  font-weight: ${({ theme }) => theme.typography.fontWeight.medium};
  cursor: pointer;
  transition: all ${({ theme }) => theme.transitions.fast};

  &:hover {
    background-color: ${({ active, theme }) =>
    active ? theme.colors.primary.dark : theme.colors.background.tertiary};
  }
`;

const LoadingSpinner = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  padding: ${({ theme }) => theme.spacing.xl};
  color: ${({ theme }) => theme.colors.text.secondary};

  svg {
    width: 24px;
    height: 24px;
    animation: spin 1s linear infinite;
  }
`;

const ChartLoadingContainer = styled.div`
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: ${({ theme }) => theme.colors.text.secondary};
`;

const ErrorMessage = styled.div`
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: ${({ theme }) => theme.colors.error.main};
  text-align: center;
`;

const timeRanges: TimeRange[] = ['1D', '1W', '1M', '3M', '1Y', '5Y'];

// Custom tooltip component for the chart
const CustomTooltip = ({ active, payload, label }: any) => {
    if (active && payload && payload.length) {
        const data = payload[0].payload;
        return (
            <div style={{
                backgroundColor: 'white',
                padding: '12px',
                border: '1px solid #e2e8f0',
                borderRadius: '8px',
                boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)'
            }}>
                <p style={{ margin: '0 0 8px 0', fontSize: '14px', color: '#64748b' }}>
                    {new Date(data.timestamp).toLocaleDateString()}
                </p>
                <p style={{ margin: '0', fontSize: '16px', fontWeight: 'bold', color: '#1e293b' }}>
                    ${data.close.toFixed(2)}
                </p>
                <p style={{ margin: '4px 0 0 0', fontSize: '12px', color: '#64748b' }}>
                    Volume: {(data.volume / 1e6).toFixed(2)}M
                </p>
            </div>
        );
    }
    return null;
};

export const StockDetails: React.FC = () => {
    const { symbol } = useParams<{ symbol: string }>();
    const dispatch = useDispatch();
    const [selectedRange, setSelectedRange] = useState<TimeRange>('1M');

    const { quote, chartData, loading, chartLoading, error, chartError } = useSelector((state: RootState) => ({
        quote: symbol ? state.stocks.quotes[symbol] : undefined,
        chartData: symbol ? state.stocks.charts[symbol]?.[selectedRange] : undefined,
        loading: symbol ? state.stocks.loading.quotes[symbol] : false,
        chartLoading: symbol ? state.stocks.loading.charts[symbol] : false,
        error: symbol ? state.stocks.error.quotes[symbol] : null,
        chartError: symbol ? state.stocks.error.charts[symbol] : null,
    }));

    useEffect(() => {
        if (symbol) {
            dispatch(fetchStockQuote(symbol) as any);
            dispatch(fetchStockChart({ symbol, range: selectedRange }) as any);
        }
    }, [symbol, selectedRange, dispatch]);

    const handleTimeRangeChange = (range: TimeRange) => {
        setSelectedRange(range);
    };

    if (!symbol) {
        return <div>Invalid stock symbol</div>;
    }

    if (loading) {
        return (
            <StockDetailsContainer>
                <LoadingSpinner>
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M21 12a9 9 0 11-6.219-8.56" />
                    </svg>
                </LoadingSpinner>
            </StockDetailsContainer>
        );
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    if (!quote) {
        return <div>No data available</div>;
    }

    // Format chart data for Recharts
    const formattedChartData = chartData?.map(item => ({
        ...item,
        date: new Date(item.timestamp).toLocaleDateString(),
        timestamp: item.timestamp
    })) || [];

    return (
        <StockDetailsContainer>
            <StockHeader>
                <StockTitle>{quote.name} ({quote.symbol})</StockTitle>
                <PriceSection>
                    <CurrentPrice>${quote.price.toFixed(2)}</CurrentPrice>
                    <PriceChange positive={quote.change >= 0}>
                        {quote.change >= 0 ? '+' : ''}
                        {quote.change.toFixed(2)} ({quote.changePercent.toFixed(2)}%)
                    </PriceChange>
                </PriceSection>
            </StockHeader>

            <MetricsGrid>
                <MetricCard>
                    <MetricLabel>Market Cap</MetricLabel>
                    <MetricValue>${(quote.marketCap / 1e9).toFixed(2)}B</MetricValue>
                </MetricCard>
                <MetricCard>
                    <MetricLabel>P/E Ratio</MetricLabel>
                    <MetricValue>{quote.peRatio.toFixed(2)}</MetricValue>
                </MetricCard>
                <MetricCard>
                    <MetricLabel>Volume</MetricLabel>
                    <MetricValue>{(quote.volume / 1e6).toFixed(2)}M</MetricValue>
                </MetricCard>
                <MetricCard>
                    <MetricLabel>52W High</MetricLabel>
                    <MetricValue>${quote.high52Week.toFixed(2)}</MetricValue>
                </MetricCard>
                <MetricCard>
                    <MetricLabel>52W Low</MetricLabel>
                    <MetricValue>${quote.low52Week.toFixed(2)}</MetricValue>
                </MetricCard>
                <MetricCard>
                    <MetricLabel>Dividend Yield</MetricLabel>
                    <MetricValue>{quote.dividendYield.toFixed(2)}%</MetricValue>
                </MetricCard>
            </MetricsGrid>

            <ChartContainer>
                <ChartHeader>
                    <ChartTitle>Price Chart</ChartTitle>
                    <TimeRangeButtons>
                        {timeRanges.map((range) => (
                            <TimeRangeButton
                                key={range}
                                active={selectedRange === range}
                                onClick={() => handleTimeRangeChange(range)}
                            >
                                {range}
                            </TimeRangeButton>
                        ))}
                    </TimeRangeButtons>
                </ChartHeader>

                {chartLoading ? (
                    <ChartLoadingContainer>
                        <LoadingSpinner>
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                <path d="M21 12a9 9 0 11-6.219-8.56" />
                            </svg>
                        </LoadingSpinner>
                    </ChartLoadingContainer>
                ) : chartError ? (
                    <ErrorMessage>
                        Failed to load chart data: {chartError}
                    </ErrorMessage>
                ) : formattedChartData.length > 0 ? (
                    <ResponsiveContainer width="100%" height={400}>
                        <LineChart data={formattedChartData}>
                            <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                            <XAxis
                                dataKey="date"
                                stroke="#64748b"
                                fontSize={12}
                                tickLine={false}
                            />
                            <YAxis
                                stroke="#64748b"
                                fontSize={12}
                                tickLine={false}
                                domain={['dataMin - 5', 'dataMax + 5']}
                                tickFormatter={(value) => `${value.toFixed(0)}`}
                            />
                            <Tooltip content={<CustomTooltip />} />
                            <Line
                                type="monotone"
                                dataKey="close"
                                stroke="#2563eb"
                                strokeWidth={2}
                                dot={false}
                                activeDot={{ r: 4, stroke: '#2563eb', strokeWidth: 2 }}
                            />
                        </LineChart>
                    </ResponsiveContainer>
                ) : (
                    <ChartLoadingContainer>
                        No chart data available
                    </ChartLoadingContainer>
                )}
            </ChartContainer>
        </StockDetailsContainer>
    );
};