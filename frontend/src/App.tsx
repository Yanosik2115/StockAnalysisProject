import React from 'react';
import { Routes, Route } from 'react-router-dom';
import styled from 'styled-components';
import { Header } from '@components/layout/Header';
import { Sidebar } from '@components/layout/Sidebar';
import { Dashboard } from '@pages/Dashboard';
import { StockDetails } from '@pages/StockDetails';
import { Portfolio } from '@pages/Portfolio';
import { Watchlist } from '@pages/Watchlist';

const AppContainer = styled.div`
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: ${({ theme }) => theme.colors.background.primary};
`;

const MainLayout = styled.div`
    display: flex;
    flex: 1;
`;

const ContentArea = styled.main`
    flex: 1;
    padding: ${({ theme }) => theme.spacing.lg};
    background-color: ${({ theme }) => theme.colors.background.secondary};
    min-height: calc(100vh - 70px);
`;

const App: React.FC = () => {
    return (
        <AppContainer>
            <Header />
            <MainLayout>
                <Sidebar />
                <ContentArea>
                    <Routes>
                        <Route path="/" element={<Dashboard />} />
                        <Route path="/stock/:symbol" element={<StockDetails />} />
                        <Route path="/portfolio" element={<Portfolio />} />
                        <Route path="/watchlist" element={<Watchlist />} />
                    </Routes>
                </ContentArea>
            </MainLayout>
        </AppContainer>
    );
};

export default App;