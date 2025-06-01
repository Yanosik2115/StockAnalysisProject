import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import styled from 'styled-components';
import { uiSlice } from '@store/slices/uiSlice';
import { SearchBar } from '@components/common/SearchBar';

const HeaderContainer = styled.header`
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 ${({ theme }) => theme.spacing.lg};
    height: 70px;
    background-color: ${({ theme }) => theme.colors.background.paper};
    border-bottom: 1px solid ${({ theme }) => theme.colors.border.light};
    box-shadow: ${({ theme }) => theme.shadows.sm};
    position: sticky;
    top: 0;
    z-index: 100;
`;

const LeftSection = styled.div`
    display: flex;
    align-items: center;
    gap: ${({ theme }) => theme.spacing.lg};
`;

const Logo = styled.div`
    display: flex;
    align-items: center;
    gap: ${({ theme }) => theme.spacing.sm};
    font-size: ${({ theme }) => theme.typography.fontSize.xl};
    font-weight: ${({ theme }) => theme.typography.fontWeight.bold};
    color: ${({ theme }) => theme.colors.primary.main};
`;

const MenuButton = styled.button`
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    border-radius: ${({ theme }) => theme.borderRadius.md};
    background-color: transparent;
    color: ${({ theme }) => theme.colors.text.secondary};
    transition: all ${({ theme }) => theme.transitions.fast};

    &:hover {
        background-color: ${({ theme }) => theme.colors.background.tertiary};
        color: ${({ theme }) => theme.colors.text.primary};
    }

    svg {
        width: 20px;
        height: 20px;
    }
`;

const RightSection = styled.div`
    display: flex;
    align-items: center;
    gap: ${({ theme }) => theme.spacing.md};
`;

const UserMenu = styled.div`
    display: flex;
    align-items: center;
    gap: ${({ theme }) => theme.spacing.sm};
`;

const Avatar = styled.div`
    width: 36px;
    height: 36px;
    border-radius: ${({ theme }) => theme.borderRadius.full};
    background-color: ${({ theme }) => theme.colors.primary.main};
    display: flex;
    align-items: center;
    justify-content: center;
    color: ${({ theme }) => theme.colors.primary.contrast};
    font-weight: ${({ theme }) => theme.typography.fontWeight.medium};
    font-size: ${({ theme }) => theme.typography.fontSize.sm};
`;

const ThemeToggle = styled.button`
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    border-radius: ${({ theme }) => theme.borderRadius.md};
    background-color: transparent;
    color: ${({ theme }) => theme.colors.text.secondary};
    transition: all ${({ theme }) => theme.transitions.fast};

    &:hover {
        background-color: ${({ theme }) => theme.colors.background.tertiary};
        color: ${({ theme }) => theme.colors.text.primary};
    }

    svg {
        width: 20px;
        height: 20px;
    }
`;

export const Header: React.FC = () => {
    const dispatch = useDispatch();
    const [isDark, setIsDark] = useState(false);

    const handleToggleSidebar = () => {
        dispatch(uiSlice.actions.toggleSidebar());
    };

    const handleToggleTheme = () => {
        setIsDark(!isDark);
        dispatch(uiSlice.actions.toggleTheme());
    };

    return (
        <HeaderContainer>
            <LeftSection>
                <MenuButton onClick={handleToggleSidebar} aria-label="Toggle sidebar">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <line x1="3" y1="6" x2="21" y2="6" />
                        <line x1="3" y1="12" x2="21" y2="12" />
                        <line x1="3" y1="18" x2="21" y2="18" />
                    </svg>
                </MenuButton>

                <Logo>
                    <svg width="32" height="32" viewBox="0 0 32 32" fill="currentColor">
                        <path d="M4 20l4-4 4 4 8-8 8 8v8H4v-8z" />
                        <path d="M4 12l4-4 4 4 8-8 8 8" stroke="currentColor" strokeWidth="2" fill="none" />
                    </svg>
                    StockAnalyzer
                </Logo>

                <SearchBar />
            </LeftSection>

            <RightSection>
                <ThemeToggle onClick={handleToggleTheme} aria-label="Toggle theme">
                    {isDark ? (
                        <svg viewBox="0 0 24 24" fill="currentColor">
                            <circle cx="12" cy="12" r="5" />
                            <line x1="12" y1="1" x2="12" y2="3" />
                            <line x1="12" y1="21" x2="12" y2="23" />
                            <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
                            <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
                            <line x1="1" y1="12" x2="3" y2="12" />
                            <line x1="21" y1="12" x2="23" y2="12" />
                            <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
                            <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
                        </svg>
                    ) : (
                        <svg viewBox="0 0 24 24" fill="currentColor">
                            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
                        </svg>
                    )}
                </ThemeToggle>

                <UserMenu>
                    <Avatar>JD</Avatar>
                </UserMenu>
            </RightSection>
        </HeaderContainer>
    );
};