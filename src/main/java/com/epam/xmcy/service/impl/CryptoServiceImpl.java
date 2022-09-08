package com.epam.xmcy.service.impl;

import com.epam.xmcy.model.CryptoValue;
import com.epam.xmcy.model.Cryptocurrency;
import com.epam.xmcy.service.CryptoService;
import com.epam.xmcy.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The implementation of {@link CryptoService}.
 */
@Service
@Slf4j
public class CryptoServiceImpl implements CryptoService {

    @Value("${main.crypto.info.filename}")
    public String mainCryptoInfoFileName;

    @Value("${normalized.info.filename}")
    public String normalizedInfoFileName;

    @Override
    public void refreshCommonCryptoStats(Map<String, List<CryptoValue>> allData) {
        List<Cryptocurrency> cryptocurrencies = new ArrayList<>();

        // Calculates min, max, oldest, newest, normalized range for every crypto
        for (String shortName : allData.keySet()) {
            Cryptocurrency cryptocurrency = calculateStats(shortName, (LinkedList<CryptoValue>) allData.get(shortName));
            cryptocurrencies.add(cryptocurrency);
        }

        // Descendent sort by normalized range.
        cryptocurrencies.sort(Comparator.comparing(Cryptocurrency::getNormal).reversed());

        try {
            // Save file, so we don't need to calculate info each time
            // Some kind of cache :)
            FileUtils.saveAsJson(cryptocurrencies, mainCryptoInfoFileName);
        } catch (IOException e) {
            log.error("Failed to save " + mainCryptoInfoFileName + e.getMessage());
        }
    }

    @Override
    public void refreshCryptoStatsForDays(Map<String, List<CryptoValue>> allData) {
        Map<LocalDate, List<Cryptocurrency>> statsForDates = new HashMap<>();

        for (String shortName : allData.keySet()) {
            Map<LocalDate, List<CryptoValue>> dataForSingleDay = new HashMap<>();

            // First, find all data for each day
            // and collect it together by date.
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

            // Second, calculate min, max, normalized range
            // for each day
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

                List<Cryptocurrency> cryptocurrencies;

                // Third, collect all various cryptocurrencies together
                // by date
                if (statsForDates.containsKey(localDate)) {
                    cryptocurrencies = statsForDates.get(localDate);
                    cryptocurrencies.add(cryptocurrency);
                    cryptocurrencies.sort(Comparator.comparing(Cryptocurrency::getNormal).reversed());

                } else {
                    cryptocurrencies = new ArrayList<>();
                    cryptocurrencies.add(cryptocurrency);
                }
                statsForDates.put(localDate, cryptocurrencies);
            }
        }

        // Save file, so we don't need to calculate info each time
        try {
            FileUtils.saveAsJson(statsForDates, normalizedInfoFileName);
        } catch (IOException e) {
            log.error("Failed to save " + normalizedInfoFileName + e.getMessage());
        }
    }

    /**
     * Calculates min, max, oldest, newest, normalized range.
     *
     * @param shortName crypto short name.
     * @param cryptoValues crypto raw data.
     * @return cryptocurrency info object.
     */
    private Cryptocurrency calculateStats(String shortName, LinkedList<CryptoValue> cryptoValues) {
        Cryptocurrency crypto = new Cryptocurrency();
        crypto.setName(shortName);

        cryptoValues.sort(Comparator.comparing(CryptoValue::getDateTime));
        crypto.setOldest(cryptoValues.getFirst().getDateTime());
        crypto.setNewest(cryptoValues.getLast().getDateTime());

        cryptoValues.sort(Comparator.comparing(CryptoValue::getPrice));
        crypto.setMin(cryptoValues.getFirst().getPrice());
        crypto.setMax(cryptoValues.getLast().getPrice());

        crypto.setNormal((crypto.getMax() - crypto.getMin()) / crypto.getMin());

        return crypto;
    }

}
