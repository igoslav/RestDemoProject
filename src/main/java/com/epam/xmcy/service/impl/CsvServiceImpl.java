package com.epam.xmcy.service.impl;

import com.epam.xmcy.model.CryptoValue;
import com.epam.xmcy.model.Cryptocurrency;
import com.epam.xmcy.service.CryptoService;
import com.epam.xmcy.service.CsvService;
import com.epam.xmcy.service.strategies.CryptoDataStrategy;
import com.epam.xmcy.util.FileUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * The implementation of {@link CsvService}.
 */
@Service
@Slf4j
public class CsvServiceImpl implements CsvService {

    @Value("${main.crypto.info.filename}")
    public String mainCryptoInfoFileName;

    @Value("${normalized.info.filename}")
    public String normalizedInfoFileName;

    @Autowired
    @Qualifier("csvCryptoDataStrategy")
    private CryptoDataStrategy strategy;

    @Autowired
    private CryptoService cryptoService;

    @Override
    public List<Cryptocurrency> getAllCryptocurrencies() {
        try {
            return FileUtils.readJson(mainCryptoInfoFileName, new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Failed to read JSON from file " + mainCryptoInfoFileName + e.getMessage());
            return null;
        }
    }

    @Override
    public Cryptocurrency getCryptocurrencyByName(String shortName) {
        try {
            List<Cryptocurrency> cryptocurrencies = FileUtils.readJson(mainCryptoInfoFileName, new TypeReference<>() {});

            return cryptocurrencies.stream()
                    .filter(c -> shortName.equalsIgnoreCase(c.getName()))
                    .findAny()
                    .orElse(null);
        } catch (IOException e) {
            log.error("Failed to read JSON from file " + mainCryptoInfoFileName + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Cryptocurrency> getRecommendationByDate(LocalDate date) {
        try {
            Map<LocalDate, List<Cryptocurrency>> statsForDays = FileUtils.readJson(normalizedInfoFileName,
                    new TypeReference<>() {});

            return statsForDays.getOrDefault(date, null);
        } catch (IOException e) {
            log.error("Failed to read JSON from file " + normalizedInfoFileName + e.getMessage());
            return null;
        }
    }

    @Override
    public void refreshData() {
        Map<String, List<CryptoValue>> allData = readAll();

        cryptoService.refreshCommonCryptoStats(allData);
        cryptoService.refreshCryptoStatsForDays(allData);
    }

    /**
     * Method reads content from source based on strategy.
     * In this case, method reads data from CSV file.
     *
     * @return raw crypto data.
     */
    private Map<String, List<CryptoValue>> readAll() {
        return strategy.read();
    }

    public String getMainCryptoInfoFileName() {
        return mainCryptoInfoFileName;
    }

    public String getNormalizedInfoFileName() {
        return normalizedInfoFileName;
    }
}
