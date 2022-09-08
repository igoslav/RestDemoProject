package com.epam.xmcy.rest;

import com.epam.xmcy.model.Cryptocurrency;
import com.epam.xmcy.service.CsvService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class ResourceTest {

    private static final String NAME = "TEST";
    private static final Double MIN_VALUE = 0.0;
    private static final Double MAX_VALUE = 100.0;
    private static final LocalDateTime OLDEST_VALUE = LocalDateTime.of(2021, 1, 1, 1, 1);
    private static final LocalDateTime NEWEST_VALUE = LocalDateTime.of(2022, 1, 1, 1, 1);

    @Mock
    private CsvService csvService;

    @InjectMocks
    private Resource resource;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllCryptocurrencies() {
        Cryptocurrency cryptocurrency = createTestCrypto();
        when(csvService.getAllCryptocurrencies()).thenReturn(Collections.singletonList(cryptocurrency));
        List<Cryptocurrency> result = resource.getAllCryptocurrencies();

        assertFalse(CollectionUtils.isEmpty(result));
        assertTrue(result.size() > 0);
        assertEquals(result.get(0).getMin(), MIN_VALUE);
        assertEquals(result.get(0).getMax(), MAX_VALUE);
        assertEquals(result.get(0).getNewest(), NEWEST_VALUE);
        assertEquals(result.get(0).getOldest(), OLDEST_VALUE);
        assertEquals(result.get(0).getName(), NAME);
    }

    @Test
    void getCryptoStatistics() {
        Cryptocurrency cryptocurrency = createTestCrypto();
        when(csvService.getCryptocurrencyByName(NAME)).thenReturn(cryptocurrency);

        Cryptocurrency result = resource.getCryptoStatistics(NAME, null, null);

        assertNotNull(result);
        assertEquals(result.getMin(), MIN_VALUE);
        assertEquals(result.getMax(), MAX_VALUE);
        assertEquals(result.getNewest(), NEWEST_VALUE);
        assertEquals(result.getOldest(), OLDEST_VALUE);
        assertEquals(result.getName(), NAME);
    }

    @Test
    void getRecommendationByDate() {
        Cryptocurrency cryptocurrency = createTestCrypto();
        when(csvService.getRecommendationByDate(NEWEST_VALUE.toLocalDate(), OLDEST_VALUE.toLocalDate()))
                .thenReturn(Collections.singletonList(cryptocurrency));
        List<Cryptocurrency> result = resource.getRecommendationByDate(NEWEST_VALUE.toLocalDate(), OLDEST_VALUE.toLocalDate());

        assertFalse(CollectionUtils.isEmpty(result));
        assertTrue(result.size() > 0);
        assertEquals(result.get(0).getMin(), MIN_VALUE);
        assertEquals(result.get(0).getMax(), MAX_VALUE);
        assertEquals(result.get(0).getNewest(), NEWEST_VALUE);
        assertEquals(result.get(0).getOldest(), OLDEST_VALUE);
        assertEquals(result.get(0).getName(), NAME);
    }

    @Test
    void refreshStats() {
        resource.refreshStats();
        verify(csvService, only()).refreshData();
    }

    private Cryptocurrency createTestCrypto() {
        Cryptocurrency cryptocurrency = new Cryptocurrency();
        cryptocurrency.setMin(MIN_VALUE);
        cryptocurrency.setMax(MAX_VALUE);
        cryptocurrency.setNewest(NEWEST_VALUE);
        cryptocurrency.setOldest(OLDEST_VALUE);
        cryptocurrency.setName(NAME);

        return cryptocurrency;
    }
}