package com.epam.xmcy.service;

import com.epam.xmcy.model.Cryptocurrency;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface to work with cryptocurrencies using CSV files.
 */
public interface CsvService {

    /**
     * Gets all cryptocurrencies info sorted by normalized range.
     *
     * @return list of cryptocurrencies.
     */
    List<Cryptocurrency> getAllCryptocurrencies();

    /**
     * Gets cryptocurrency info by short name.
     *
     * @param shortName crypto short name (BTC, ETH, etc.).
     * @return cryptocurrency info object.
     */
    Cryptocurrency getCryptocurrencyByName(String shortName);

    /**
     * Gets all cryptocurrencies sorted by normalized range for a particular day.
     *
     * @param date date.
     * @return list of cryptocurrencies.
     */
    List<Cryptocurrency> getRecommendationByDate(LocalDate date);

    /**
     * Recalculates all crypto stats based on CSV files.
     * Stores calculated info in JSON files.
     */
    void refreshData();
}
