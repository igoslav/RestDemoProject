package com.epam.xmcy.service;

import com.epam.xmcy.model.Cryptocurrency;

import java.time.LocalDate;
import java.util.List;

public interface CsvService {

    List<Cryptocurrency> getAllCryptocurrencies();

    Cryptocurrency getCryptocurrencyByName(String shortName);

    List<Cryptocurrency> getRecommendationByDate(LocalDate date);

    void refreshData();
}
