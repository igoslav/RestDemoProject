package com.epam.xmcy.service.impl;

import com.epam.xmcy.model.CryptoValue;
import com.epam.xmcy.model.Cryptocurrency;
import com.epam.xmcy.service.CsvService;
import com.epam.xmcy.service.strategies.CSVCryptoDataStrategy;
import com.epam.xmcy.service.strategies.CryptoDataStrategy;
import com.epam.xmcy.util.FileUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class CsvServiceImpl implements CsvService {

    public static final String MAIN_CRYPTO_INFO_FILE_NAME = "crypto.json";
    public static final String NORMALIZED_INFO_FILE_NAME = "normalized_for_dates.json";

    private final CryptoDataStrategy strategy = new CSVCryptoDataStrategy();

    @Override
    public List<Cryptocurrency> getAllCryptocurrencies() {
        try {
            return FileUtils.readJson(MAIN_CRYPTO_INFO_FILE_NAME, new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Failed to read JSON from file " + MAIN_CRYPTO_INFO_FILE_NAME + e.getMessage());
            return null;
        }
    }

    @Override
    public Cryptocurrency getCryptocurrencyByName(String shortName) {
        try {
            List<Cryptocurrency> cryptocurrencies = FileUtils.readJson(MAIN_CRYPTO_INFO_FILE_NAME, new TypeReference<>() {});

            return cryptocurrencies.stream()
                    .filter(c -> shortName.equalsIgnoreCase(c.getName()))
                    .findAny()
                    .orElse(null);
        } catch (IOException e) {
            log.error("Failed to read JSON from file " + MAIN_CRYPTO_INFO_FILE_NAME + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Cryptocurrency> getRecommendationByDate(LocalDate date) {
        try {
            Map<LocalDate, List<Cryptocurrency>> statsForDays = FileUtils.readJson(NORMALIZED_INFO_FILE_NAME,
                    new TypeReference<>() {});

            return statsForDays.getOrDefault(date, null);
        } catch (IOException e) {
            log.error("Failed to read JSON from file " + NORMALIZED_INFO_FILE_NAME + e.getMessage());
            return null;
        }
    }

    @Override
    public void refreshData() {
        Map<String, List<CryptoValue>> allData = readAll();

        refreshCommonCryptoStats(allData);
        refreshCryptoStatsForDays(allData);
    }

    private void refreshCommonCryptoStats(Map<String, List<CryptoValue>> allData) {
        List<Cryptocurrency> cryptocurrencies = new ArrayList<>();
        for (String shortName : allData.keySet()) {
            Cryptocurrency cryptocurrency = calculateStats(shortName, (LinkedList<CryptoValue>) allData.get(shortName));
            cryptocurrencies.add(cryptocurrency);
        }

        try {
            FileUtils.saveAsJson(cryptocurrencies, MAIN_CRYPTO_INFO_FILE_NAME);
        } catch (IOException e) {
            log.error("Failed to save " + MAIN_CRYPTO_INFO_FILE_NAME + e.getMessage());
        }
    }

    private void refreshCryptoStatsForDays(Map<String, List<CryptoValue>> allData) {
        Map<LocalDate, List<Cryptocurrency>> statsForDates = new HashMap<>();

        for (String shortName : allData.keySet()) {
            Map<LocalDate, List<CryptoValue>> dataForSingleDay = new HashMap<>();

            List<CryptoValue> cryptoValues = allData.get(shortName);
            for (CryptoValue cryptoValue : cryptoValues) {
                LocalDate localDate = cryptoValue.getDateTime().toLocalDate();

                if (dataForSingleDay.containsKey(localDate)) {
                    dataForSingleDay.get(localDate).add(cryptoValue);
                } else {
                    List<CryptoValue> cryptoValuesForSingleDay = new LinkedList<>();
                    cryptoValuesForSingleDay.add(cryptoValue);
                    dataForSingleDay.put(localDate, cryptoValuesForSingleDay);
                }
            }

            for (LocalDate localDate : dataForSingleDay.keySet()) {
                LinkedList<CryptoValue> values = (LinkedList<CryptoValue>) dataForSingleDay.get(localDate);
                values.sort(Comparator.comparing(CryptoValue::getPrice));

                Double min = values.getFirst().getPrice();
                Double max = values.getLast().getPrice();

                Cryptocurrency cryptocurrency = new Cryptocurrency();
                cryptocurrency.setName(shortName);
                cryptocurrency.setMin(min);
                cryptocurrency.setMax(max);
                cryptocurrency.setNormal((max - min) / min);

                if (statsForDates.containsKey(localDate)) {
                    statsForDates.get(localDate).add(cryptocurrency);
                } else {
                    List<Cryptocurrency> cryptocurrencies = new ArrayList<>();
                    cryptocurrencies.add(cryptocurrency);
                    statsForDates.put(localDate, cryptocurrencies);
                }
            }
        }

        try {
            FileUtils.saveAsJson(statsForDates, NORMALIZED_INFO_FILE_NAME);
        } catch (IOException e) {
            log.error("Failed to save " + NORMALIZED_INFO_FILE_NAME + e.getMessage());
        }
    }

    private Cryptocurrency calculateStats(String shortName, LinkedList<CryptoValue> cryptoValues) {
        Cryptocurrency crypto = new Cryptocurrency();
        crypto.setName(shortName);

        cryptoValues.sort(Comparator.comparing(CryptoValue::getDateTime));
        crypto.setNewest(cryptoValues.getFirst().getDateTime());
        crypto.setOldest(cryptoValues.getLast().getDateTime());

        cryptoValues.sort(Comparator.comparing(CryptoValue::getPrice));
        crypto.setMin(cryptoValues.getFirst().getPrice());
        crypto.setMax(cryptoValues.getLast().getPrice());

        crypto.setNormal((crypto.getMax() - crypto.getMin()) / crypto.getMin());

        return crypto;
    }

    private Map<String, List<CryptoValue>> readAll() {
        return strategy.read();
    }
}
