import React, { useState, useRef, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { RootState } from '@store/store';
import { searchStocks } from '@store/slices/stocksSlice';

const SearchContainer = styled.div`
    position: relative;
    width: 400px;
    max-width: 100%;
`;

const SearchInput = styled.input`
    width: 100%;
    padding: ${({ theme }) => theme.spacing.sm} ${({ theme }) => theme.spacing.md};
    padding-left: 40px;
    border: 1px solid ${({ theme }) => theme.colors.border.main};
    border-radius: ${({ theme }) => theme.borderRadius.lg};
    background-color: ${({ theme }) => theme.colors.background.secondary};
    font-size: ${({ theme }) => theme.typography.fontSize.sm};
    transition: all ${({ theme }) => theme.transitions.fast};

    &:focus {
        border-color: ${({ theme }) => theme.colors.primary.main};
        box-shadow: 0 0 0 3px ${({ theme }) => theme.colors.primary.main}20;
        background-color: ${({ theme }) => theme.colors.background.paper};
    }

    &::placeholder {
        color: ${({ theme }) => theme.colors.text.tertiary};
    }
`;

const SearchIcon = styled.div`
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: ${({ theme }) => theme.colors.text.tertiary};
    pointer-events: none;

    svg {
        width: 16px;
        height: 16px;
    }
`;

const ResultsContainer = styled.div<{ isVisible: boolean }>`
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background-color: ${({ theme }) => theme.colors.background.paper};
    border: 1px solid ${({ theme }) => theme.colors.border.main};
    border-radius: ${({ theme }) => theme.borderRadius.lg};
    box-shadow: ${({ theme }) => theme.shadows.lg};
    margin-top: 4px;
    max-height: 300px;
    overflow-y: auto;
    z-index: 1000;
    opacity: ${({ isVisible }) => (isVisible ? 1 : 0)};
    visibility: ${({ isVisible }) => (isVisible ? 'visible' : 'hidden')};
    transition: all ${({ theme }) => theme.transitions.fast};
`;

const ResultItem = styled.button`
    width: 100%;
    padding: ${({ theme }) => theme.spacing.md};
    text-align: left;
    background: none;
    border: none;
    cursor: pointer;
    transition: background-color ${({ theme }) => theme.transitions.fast};
    border-bottom: 1px solid ${({ theme }) => theme.colors.border.light};

    &:hover {
        background-color: ${({ theme }) => theme.colors.background.tertiary};
    }

    &:last-child {
        border-bottom: none;
    }
`;

const ResultSymbol = styled.div`
    font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
    color: ${({ theme }) => theme.colors.text.primary};
    font-size: ${({ theme }) => theme.typography.fontSize.sm};
`;

const ResultName = styled.div`
    color: ${({ theme }) => theme.colors.text.secondary};
    font-size: ${({ theme }) => theme.typography.fontSize.xs};
    margin-top: 2px;
`;

const ResultExchange = styled.span`
    color: ${({ theme }) => theme.colors.text.tertiary};
    font-size: ${({ theme }) => theme.typography.fontSize.xs};
    margin-left: ${({ theme }) => theme.spacing.sm};
`;

const NoResults = styled.div`
    padding: ${({ theme }) => theme.spacing.lg};
    text-align: center;
    color: ${({ theme }) => theme.colors.text.secondary};
    font-size: ${({ theme }) => theme.typography.fontSize.sm};
`;

const LoadingSpinner = styled.div`
    display: flex;
    align-items: center;
    justify-content: center;
    padding: ${({ theme }) => theme.spacing.lg};
    color: ${({ theme }) => theme.colors.text.secondary};

    svg {
        width: 20px;
        height: 20px;
        animation: spin 1s linear infinite;
    }
`;

export const SearchBar: React.FC = () => {
    const [query, setQuery] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const searchRef = useRef<HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { searchResults, loading } = useSelector((state: RootState) => ({
        searchResults: state.stocks.searchResults,
        loading: state.stocks.loading.search,
    }));

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    useEffect(() => {
        const searchTimeout = setTimeout(() => {
            if (query.trim()) {
                dispatch(searchStocks(query.trim()) as any);
                setIsOpen(true);
            } else {
                setIsOpen(false);
            }
        }, 300);

        return () => clearTimeout(searchTimeout);
    }, [query, dispatch]);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setQuery(e.target.value);
    };

    const handleSelectStock = (symbol: string) => {
        setQuery('');
        setIsOpen(false);
        navigate(`/stock/${symbol}`);
    };

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (e.key === 'Escape') {
            setIsOpen(false);
            inputRef.current?.blur();
        }
    };

    return (
        <SearchContainer ref={searchRef}>
            <SearchIcon>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <circle cx="11" cy="11" r="8" />
                    <path d="m21 21-4.35-4.35" />
                </svg>
            </SearchIcon>

            <SearchInput
                ref={inputRef}
                type="text"
                placeholder="Search stocks (e.g., AAPL, Apple)"
                value={query}
                onChange={handleInputChange}
                onKeyDown={handleKeyDown}
                onFocus={() => query.trim() && setIsOpen(true)}
            />

            <ResultsContainer isVisible={isOpen && query.trim().length > 0}>
                {loading ? (
                    <LoadingSpinner>
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                            <path d="M21 12a9 9 0 11-6.219-8.56" />
                        </svg>
                    </LoadingSpinner>
                ) : searchResults.length > 0 ? (
                    searchResults.map((stock) => (
                        <ResultItem
                            key={stock.symbol}
                            onClick={() => handleSelectStock(stock.symbol)}
                        >
                            <ResultSymbol>
                                {stock.symbol}
                                <ResultExchange>• {stock.exchange}</ResultExchange>
                            </ResultSymbol>
                            <ResultName>{stock.name}</ResultName>
                        </ResultItem>
                    ))
                ) : query.trim() && !loading ? (
                    <NoResults>No stocks found for "{query}"</NoResults>
                ) : null}
            </ResultsContainer>
        </SearchContainer>
    );
};