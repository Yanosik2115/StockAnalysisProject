import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import { RootState } from '@store/store';

const SidebarContainer = styled.aside<{ isOpen: boolean }>`
  width: ${({ isOpen }) => (isOpen ? '280px' : '70px')};
  min-height: calc(100vh - 70px);
  background-color: ${({ theme }) => theme.colors.background.paper};
  border-right: 1px solid ${({ theme }) => theme.colors.border.light};
  transition: width ${({ theme }) => theme.transitions.base};
  overflow: hidden;
`;

const NavList = styled.nav`
  padding: ${({ theme }) => theme.spacing.lg} 0;
`;

const NavItem = styled(NavLink)<{ isOpen: boolean }>`
  display: flex;
  align-items: center;
  padding: ${({ theme }) => theme.spacing.md} ${({ theme }) => theme.spacing.lg};
  color: ${({ theme }) => theme.colors.text.secondary};
  text-decoration: none;
  transition: all ${({ theme }) => theme.transitions.fast};
  border-left: 3px solid transparent;
  
  &:hover {
    background-color: ${({ theme }) => theme.colors.background.tertiary};
    color: ${({ theme }) => theme.colors.text.primary};
  }

  &.active {
    background-color: ${({ theme }) => theme.colors.primary.main}10;
    color: ${({ theme }) => theme.colors.primary.main};
    border-left-color: ${({ theme }) => theme.colors.primary.main};
  }

  svg {
    width: 24px;
    height: 24px;
    margin-right: ${({ isOpen, theme }) => (isOpen ? theme.spacing.md : '0')};
    flex-shrink: 0;
  }

  span {
    font-weight: ${({ theme }) => theme.typography.fontWeight.medium};
    opacity: ${({ isOpen }) => (isOpen ? 1 : 0)};
    white-space: nowrap;
    transition: opacity ${({ theme }) => theme.transitions.fast};
  }
`;

const SectionTitle = styled.div<{ isOpen: boolean }>`
  padding: ${({ theme }) => theme.spacing.md} ${({ theme }) => theme.spacing.lg};
  font-size: ${({ theme }) => theme.typography.fontSize.xs};
  font-weight: ${({ theme }) => theme.typography.fontWeight.semibold};
  color: ${({ theme }) => theme.colors.text.tertiary};
  text-transform: uppercase;
  letter-spacing: 0.05em;
  opacity: ${({ isOpen }) => (isOpen ? 1 : 0)};
  transition: opacity ${({ theme }) => theme.transitions.fast};
  margin-top: ${({ theme }) => theme.spacing.lg};

  &:first-child {
    margin-top: 0;
  }
`;

const navigationItems = [
    {
        section: 'Main',
        items: [
            {
                path: '/',
                label: 'Dashboard',
                icon: (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                        <line x1="9" y1="9" x2="15" y2="9" />
                        <line x1="9" y1="15" x2="15" y2="15" />
                    </svg>
                ),
            },
        ],
    },
    {
        section: 'Trading',
        items: [
            {
                path: '/portfolio',
                label: 'Portfolio',
                icon: (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M21 16V8a2 2 0 0 0-1-1.73L14 4a2 2 0 0 0-2 0L6 6.27A2 2 0 0 0 5 8v8a2 2 0 0 0 1 1.73L12 20a2 2 0 0 0 2 0l6-2.27A2 2 0 0 0 21 16z" />
                        <polyline points="3.27,6.96 12,12.01 20.73,6.96" />
                        <line x1="12" y1="22.08" x2="12" y2="12" />
                    </svg>
                ),
            },
            {
                path: '/watchlist',
                label: 'Watchlist',
                icon: (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                        <circle cx="12" cy="12" r="3" />
                    </svg>
                ),
            },
        ],
    },
    {
        section: 'Analysis',
        items: [
            {
                path: '/screener',
                label: 'Stock Screener',
                icon: (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <circle cx="11" cy="11" r="8" />
                        <path d="m21 21-4.35-4.35" />
                    </svg>
                ),
            },
            {
                path: '/alerts',
                label: 'Price Alerts',
                icon: (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
                        <path d="M13.73 21a2 2 0 0 1-3.46 0" />
                    </svg>
                ),
            },
        ],
    },
    {
        section: 'Settings',
        items: [
            {
                path: '/settings',
                label: 'Settings',
                icon: (
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <circle cx="12" cy="12" r="3" />
                        <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1 1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
                    </svg>
                ),
            },
        ],
    },
];

export const Sidebar: React.FC = () => {
    const isOpen = useSelector((state: RootState) => state.ui.sidebarOpen);
    const location = useLocation();

    return (
        <SidebarContainer isOpen={isOpen}>
            <NavList>
                {navigationItems.map((section, sectionIndex) => (
                    <React.Fragment key={section.section}>
                        {sectionIndex > 0 && (
                            <SectionTitle isOpen={isOpen}>{section.section}</SectionTitle>
                        )}
                        {section.items.map((item) => (
                            <NavItem
                                key={item.path}
                                to={item.path}
                                isOpen={isOpen}
                                className={location.pathname === item.path ? 'active' : ''}
                            >
                                {item.icon}
                                <span>{item.label}</span>
                            </NavItem>
                        ))}
                    </React.Fragment>
                ))}
            </NavList>
        </SidebarContainer>
    );
};