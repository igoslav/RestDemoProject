package com.epam.xmcy.service.impl;

import com.epam.xmcy.service.CryptoService;
import com.epam.xmcy.service.strategies.CryptoDataStrategy;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class CsvServiceImplTest {

    @Mock
    private CryptoDataStrategy strategy;

    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private CsvServiceImpl service;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRefreshData() {
        service.refreshData();

        verify(strategy, times(1)).read();
        verify(cryptoService, times(1)).refreshCommonCryptoStats(anyMap());
        verify(cryptoService, times(1)).refreshCryptoStatsForDays(anyMap());
    }
}