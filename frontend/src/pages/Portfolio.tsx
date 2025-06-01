import React from 'react';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState } from '@store/store';

const PortfolioContainer = styled.div`
  max-width: 1400px;
  margin: 0 auto;
`;

const PageHeader = styled.div`
  display: flex;
  justify-content: between;
  align-items: center;
  margin-bottom: ${({ theme }) => theme.spacing.xl};
`;

const PageTitle = styled.h1`
  font-size: ${({ theme }) => theme.typography.fontSize['3xl']};
  font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
  color: ${({ theme }) => theme.colors.text.primary};
`;

const AddButton = styled.button`
  padding: ${({ theme }) => theme.spacing.sm} ${({ theme }) => theme.spacing.lg};
  background-color: ${({ theme }) => theme.colors.primary.main};
  color: ${({ theme }) => theme.colors.primary.contrast};
  border: none;
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-weight: ${({ theme }) => theme.typography.fontWeight.medium};
  cursor: pointer;
  transition: background-color ${({ theme }) => theme.transitions.fast};

  &:hover {
    background-color: ${({ theme }) => theme.colors.primary.dark};
  }
`;

const SummaryCards = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: ${({ theme }) => theme.spacing.lg};
  margin-bottom: ${({ theme }) => theme.spacing.xl};
`;

const SummaryCard = styled.div`
  background-color: ${({ theme }) => theme.colors.background.paper};
  padding: ${({ theme }) => theme.spacing.xl};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  border: 1px solid ${({ theme }) => theme.colors.border.light};
`;

const SummaryValue = styled.div`
  font-size: ${({ theme }) => theme.typography.fontSize['2xl']};
  font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
  color: ${({ theme }) => theme.colors.text.primary};
  margin-bottom: ${({ theme }) => theme.spacing.xs};
`;

const SummaryLabel = styled.div`
  font-size: ${({ theme }) => theme.typography.fontSize.sm};
  color: ${({ theme }) => theme.colors.text.secondary};
`;

const HoldingsTable = styled.div`
  background-color: ${({ theme }) => theme.colors.background.paper};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  border: 1px solid ${({ theme }) => theme.colors.border.light};
  overflow: hidden;
`;

const TableHeader = styled.div`
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr;
  padding: ${({ theme }) => theme.spacing.lg};
  background-color: ${({ theme }) => theme.colors.background.tertiary};
  font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
  color: ${({ theme }) => theme.colors.text.secondary};
  font-size: ${({ theme }) => theme.typography.fontSize.sm};
`;

const TableRow = styled.div`
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1fr;
  padding: ${({ theme }) => theme.spacing.lg};
  border-bottom: 1px solid ${({ theme }) => theme.colors.border.light};
  transition: background-color ${({ theme }) => theme.transitions.fast};

  &:hover {
    background-color: ${({ theme }) => theme.colors.background.secondary};
  }

  &:last-child {
    border-bottom: none;
  }
`;

const EmptyState = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing['3xl']};
  color: ${({ theme }) => theme.colors.text.secondary};
`;

export const Portfolio: React.FC = () => {
    const portfolio = useSelector((state: RootState) => state.portfolio);

    return (
        <PortfolioContainer>
            <PageHeader>
                <PageTitle>Portfolio</PageTitle>
                <AddButton>Add Transaction</AddButton>
            </PageHeader>

            <SummaryCards>
                <SummaryCard>
                    <SummaryValue>${portfolio.totalValue.toLocaleString()}</SummaryValue>
                    <SummaryLabel>Total Value</SummaryLabel>
                </SummaryCard>
                <SummaryCard>
                    <SummaryValue>
                        {portfolio.totalGainLoss >= 0 ? '+' : ''}
                        ${portfolio.totalGainLoss.toLocaleString()}
                    </SummaryValue>
                    <SummaryLabel>Total Gain/Loss</SummaryLabel>
                </SummaryCard>
                <SummaryCard>
                    <SummaryValue>
                        {portfolio.totalGainLossPercent >= 0 ? '+' : ''}
                        {portfolio.totalGainLossPercent.toFixed(2)}%
                    </SummaryValue>
                    <SummaryLabel>Return %</SummaryLabel>
                </SummaryCard>
                <SummaryCard>
                    <SummaryValue>{portfolio.holdings.length}</SummaryValue>
                    <SummaryLabel>Holdings</SummaryLabel>
                </SummaryCard>
            </SummaryCards>

            <HoldingsTable>
                <TableHeader>
                    <div>Symbol</div>
                    <div>Shares</div>
                    <div>Avg Price</div>
                    <div>Current Price</div>
                    <div>Market Value</div>
                    <div>Gain/Loss</div>
                </TableHeader>

                {portfolio.holdings.length === 0 ? (
                    <EmptyState>
                        <p>No holdings in your portfolio yet.</p>
                        <p>Add your first transaction to get started!</p>
                    </EmptyState>
                ) : (
                    portfolio.holdings.map((holding) => (
                        <TableRow key={holding.symbol}>
                            <div style={{ fontWeight: 'bold' }}>{holding.symbol}</div>
                            <div>{holding.shares.toFixed(2)}</div>
                            <div>${holding.averagePrice.toFixed(2)}</div>
                            <div>${holding.currentPrice.toFixed(2)}</div>
                            <div>${holding.marketValue.toLocaleString()}</div>
                            <div style={{
                                color: holding.gainLoss >= 0 ? '#10b981' : '#ef4444',
                                fontWeight: 'bold'
                            }}>
                                {holding.gainLoss >= 0 ? '+' : ''}
                                ${holding.gainLoss.toFixed(2)}
                                <br />
                                <span style={{ fontSize: '0.875rem' }}>
                  ({holding.gainLossPercent.toFixed(2)}%)
                </span>
                            </div>
                        </TableRow>
                    ))
                )}
            </HoldingsTable>
        </PortfolioContainer>
    );
};