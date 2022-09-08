package com.epam.xmcy.service;

import com.epam.xmcy.model.CryptoValue;

import java.util.List;
import java.util.Map;

/**
 * Interface to calculate and organize crypto stats.
 */
public interface CryptoService {

    /**
     * Recalculates and saves common info of all provided cryptocurrencies.
     *
     * @param allData map of raw crypto data.
     */
    void refreshCommonCryptoStats(Map<String, List<CryptoValue>> allData);

    /**
     * Recalculates and saves info for all existing dates from raw crypto data.
     *
     * @param allData map of raw crypto data.
     */
    void refreshCryptoStatsForDays(Map<String, List<CryptoValue>> allData);
}
