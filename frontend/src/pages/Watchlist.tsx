import React from 'react';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState } from '@store/store';

const WatchlistContainer = styled.div`
  max-width: 1400px;
  margin: 0 auto;
`;

const PageHeader = styled.div`
  display: flex;
  justify-content: space-between;
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

const WatchlistTable = styled.div`
  background-color: ${({ theme }) => theme.colors.background.paper};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  border: 1px solid ${({ theme }) => theme.colors.border.light};
  overflow: hidden;
`;

const TableHeader = styled.div`
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr auto;
  padding: ${({ theme }) => theme.spacing.lg};
  background-color: ${({ theme }) => theme.colors.background.tertiary};
  font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
  color: ${({ theme }) => theme.colors.text.secondary};
  font-size: ${({ theme }) => theme.typography.fontSize.sm};
`;

const TableRow = styled.div`
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr auto;
  padding: ${({ theme }) => theme.spacing.lg};
  border-bottom: 1px solid ${({ theme }) => theme.colors.border.light};
  transition: background-color ${({ theme }) => theme.transitions.fast};
  align-items: center;

  &:hover {
    background-color: ${({ theme }) => theme.colors.background.secondary};
  }

  &:last-child {
    border-bottom: none;
  }
`;

const SymbolCell = styled.div`
  display: flex;
  flex-direction: column;
`;

const SymbolName = styled.div`
  font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
  color: ${({ theme }) => theme.colors.text.primary};
`;

const CompanyName = styled.div`
  font-size: ${({ theme }) => theme.typography.fontSize.sm};
  color: ${({ theme }) => theme.colors.text.secondary};
`;

const ActionButton = styled.button`
  padding: ${({ theme }) => theme.spacing.xs} ${({ theme }) => theme.spacing.sm};
  border: 1px solid ${({ theme }) => theme.colors.border.main};
  border-radius: ${({ theme }) => theme.borderRadius.sm};
  background-color: transparent;
  color: ${({ theme }) => theme.colors.text.secondary};
  font-size: ${({ theme }) => theme.typography.fontSize.xs};
  cursor: pointer;
  transition: all ${({ theme }) => theme.transitions.fast};

  &:hover {
    background-color: ${({ theme }) => theme.colors.background.tertiary};
    color: ${({ theme }) => theme.colors.text.primary};
  }
`;

const EmptyState = styled.div`
  text-align: center;
  padding: ${({ theme }) => theme.spacing['3xl']};
  color: ${({ theme }) => theme.colors.text.secondary};
`;

export const Watchlist: React.FC = () => {
    const watchlist = useSelector((state: RootState) => state.watchlist);

    return (
        <WatchlistContainer>
            <PageHeader>
                <PageTitle>Watchlist</PageTitle>
                <AddButton>Add Stock</AddButton>
            </PageHeader>

            <WatchlistTable>
                <TableHeader>
                    <div>Symbol</div>
                    <div>Price</div>
                    <div>Change</div>
                    <div>Change %</div>
                    <div>Volume</div>
                    <div>Actions</div>
                </TableHeader>

                {watchlist.items.length === 0 ? (
                    <EmptyState>
                        <p>Your watchlist is empty.</p>
                        <p>Add stocks to track their performance!</p>
                    </EmptyState>
                ) : (
                    watchlist.items.map((item) => (
                        <TableRow key={item.id}>
                            <SymbolCell>
                                <SymbolName>{item.symbol}</SymbolName>
                                <CompanyName>{item.name}</CompanyName>
                            </SymbolCell>
                            <div>${item.currentPrice.toFixed(2)}</div>
                            <div style={{
                                color: item.change >= 0 ? '#10b981' : '#ef4444',
                                fontWeight: 'bold'
                            }}>
                                {item.change >= 0 ? '+' : ''}
                                ${item.change.toFixed(2)}
                            </div>
                            <div style={{
                                color: item.changePercent >= 0 ? '#10b981' : '#ef4444',
                                fontWeight: 'bold'
                            }}>
                                {item.changePercent >= 0 ? '+' : ''}
                                {item.changePercent.toFixed(2)}%
                            </div>
                            <div>{(item.volume / 1e6).toFixed(2)}M</div>
                            <ActionButton>Remove</ActionButton>
                        </TableRow>
                    ))
                )}
            </WatchlistTable>
        </WatchlistContainer>
    );
};