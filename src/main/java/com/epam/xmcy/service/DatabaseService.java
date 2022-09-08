package com.epam.xmcy.service;

import com.epam.xmcy.model.Cryptocurrency;

import java.time.LocalDate;
import java.util.List;

/**
 * Example of database service.
 *
 * Check {@link CsvService} for details.
 */
public interface DatabaseService {

    List<Cryptocurrency> getAllCryptocurrencies();

    Cryptocurrency getCryptocurrencyByName(String shortName);

    List<Cryptocurrency> getRecommendationByDate(LocalDate date);
}
