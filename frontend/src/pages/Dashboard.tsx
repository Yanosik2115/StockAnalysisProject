import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState } from '@store/store';
import { fetchStockQuote } from '@store/slices/stocksSlice';

const DashboardContainer = styled.div`
  max-width: 1400px;
  margin: 0 auto;
`;

const PageHeader = styled.div`
  margin-bottom: ${({ theme }) => theme.spacing.xl};
`;

const PageTitle = styled.h1`
  font-size: ${({ theme }) => theme.typography.fontSize['3xl']};
  font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
  color: ${({ theme }) => theme.colors.text.primary};
  margin-bottom: ${({ theme }) => theme.spacing.sm};
`;

const PageSubtitle = styled.p`
  font-size: ${({ theme }) => theme.typography.fontSize.lg};
  color: ${({ theme }) => theme.colors.text.secondary};
`;

const GridContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: ${({ theme }) => theme.spacing.lg};
  margin-bottom: ${({ theme }) => theme.spacing.xl};
`;

const Card = styled.div`
  background-color: ${({ theme }) => theme.colors.background.paper};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing.xl};
  box-shadow: ${({ theme }) => theme.shadows.base};
  border: 1px solid ${({ theme }) => theme.colors.border.light};
`;

const CardHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: ${({ theme }) => theme.spacing.lg};
`;

const CardTitle = styled.h2`
  font-size: ${({ theme }) => theme.typography.fontSize.xl};
  font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
  color: ${({ theme }) => theme.colors.text.primary};
`;

const StatsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: ${({ theme }) => theme.spacing.lg};
`;

const StatCard = styled.div`
  background-color: ${({ theme }) => theme.colors.background.secondary};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  padding: ${({ theme }) => theme.spacing.lg};
  text-align: center;
`;

const StatValue = styled.div`
  font-size: ${({ theme }) => theme.typography.fontSize['2xl']};
  font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
  color: ${({ theme }) => theme.colors.text.primary};
  margin-bottom: ${({ theme }) => theme.spacing.xs};
`;

const StatLabel = styled.div`
  font-size: ${({ theme }) => theme.typography.fontSize.sm};
  color: ${({ theme }) => theme.colors.text.secondary};
`;

const QuoteList = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing.md};
`;

const QuoteItem = styled.div`
  display: flex;
  justify-content: between;
  align-items: center;
  padding: ${({ theme }) => theme.spacing.md};
  background-color: ${({ theme }) => theme.colors.background.secondary};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  transition: all ${({ theme }) => theme.transitions.fast};

  &:hover {
    background-color: ${({ theme }) => theme.colors.background.tertiary};
    cursor: pointer;
  }
`;

const QuoteSymbol = styled.div`
  font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
  color: ${({ theme }) => theme.colors.text.primary};
  flex: 1;
`;

const QuotePrice = styled.div`
  font-weight: ${({ theme }) => theme.typography.fontWeight.medium};
  color: ${({ theme }) => theme.colors.text.primary};
  margin-right: ${({ theme }) => theme.spacing.md};
`;

const QuoteChange = styled.div<{ positive: boolean }>`
  font-weight: ${({ theme }) => theme.typography.fontWeight.medium};
  color: ${({ positive, theme }) =>
    positive ? theme.colors.success.main : theme.colors.error.main};
  font-size: ${({ theme }) => theme.typography.fontSize.sm};
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

const watchedSymbols = ['AAPL', 'GOOGL', 'MSFT', 'AMZN', 'TSLA'];

export const Dashboard: React.FC = () => {
    const dispatch = useDispatch();
    const { quotes, loading } = useSelector((state: RootState) => ({
        quotes: state.stocks.quotes,
        loading: state.stocks.loading.quotes,
    }));

    useEffect(() => {
        // Fetch quotes for popular stocks
        watchedSymbols.forEach(symbol => {
            dispatch(fetchStockQuote(symbol) as any);
        });
    }, [dispatch]);

    return (
        <DashboardContainer>
            <PageHeader>
                <PageTitle>Dashboard</PageTitle>
                <PageSubtitle>
                    Track market movements and analyze your investment performance
                </PageSubtitle>
            </PageHeader>

            <GridContainer>
                <Card>
                    <CardHeader>
                        <CardTitle>Portfolio Overview</CardTitle>
                    </CardHeader>
                    <StatsGrid>
                        <StatCard>
                            <StatValue>$125,430</StatValue>
                            <StatLabel>Total Value</StatLabel>
                        </StatCard>
                        <StatCard>
                            <StatValue>+$8,240</StatValue>
                            <StatLabel>Total Gain/Loss</StatLabel>
                        </StatCard>
                        <StatCard>
                            <StatValue>+7.02%</StatValue>
                            <StatLabel>Return %</StatLabel>
                        </StatCard>
                        <StatCard>
                            <StatValue>12</StatValue>
                            <StatLabel>Holdings</StatLabel>
                        </StatCard>
                    </StatsGrid>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>Market Overview</CardTitle>
                    </CardHeader>
                    <QuoteList>
                        {watchedSymbols.map(symbol => {
                            const quote = quotes[symbol];
                            const isLoading = loading[symbol];

                            if (isLoading) {
                                return (
                                    <QuoteItem key={symbol}>
                                        <LoadingSpinner>
                                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                                <path d="M21 12a9 9 0 11-6.219-8.56" />
                                            </svg>
                                        </LoadingSpinner>
                                    </QuoteItem>
                                );
                            }

                            if (!quote) return null;

                            return (
                                <QuoteItem key={symbol}>
                                    <QuoteSymbol>{quote.symbol}</QuoteSymbol>
                                    <QuotePrice>${quote.price.toFixed(2)}</QuotePrice>
                                    <QuoteChange positive={quote.change >= 0}>
                                        {quote.change >= 0 ? '+' : ''}
                                        {quote.change.toFixed(2)} ({quote.changePercent.toFixed(2)}%)
                                    </QuoteChange>
                                </QuoteItem>
                            );
                        })}
                    </QuoteList>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>Recent Activity</CardTitle>
                    </CardHeader>
                    <QuoteList>
                        <QuoteItem>
                            <div>
                                <div style={{ fontWeight: 'bold' }}>Bought AAPL</div>
                                <div style={{ fontSize: '0.875rem', color: '#64748b' }}>
                                    10 shares at $175.50
                                </div>
                            </div>
                            <div style={{ fontSize: '0.875rem', color: '#64748b' }}>
                                2 hours ago
                            </div>
                        </QuoteItem>
                        <QuoteItem>
                            <div>
                                <div style={{ fontWeight: 'bold' }}>Price Alert Triggered</div>
                                <div style={{ fontSize: '0.875rem', color: '#64748b' }}>
                                    TSLA reached $250.00
                                </div>
                            </div>
                            <div style={{ fontSize: '0.875rem', color: '#64748b' }}>
                                1 day ago
                            </div>
                        </QuoteItem>
                        <QuoteItem>
                            <div>
                                <div style={{ fontWeight: 'bold' }}>Dividend Received</div>
                                <div style={{ fontSize: '0.875rem', color: '#64748b' }}>
                                    $45.20 from MSFT
                                </div>
                            </div>
                            <div style={{ fontSize: '0.875rem', color: '#64748b' }}>
                                3 days ago
                            </div>
                        </QuoteItem>
                    </QuoteList>
                </Card>
            </GridContainer>
        </DashboardContainer>
    );
};