package com.epam.xmcy.service.strategies;

import com.epam.xmcy.model.CryptoValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The implementation of {@link CryptoDataStrategy}.
 * Class reads CSV files from directory, converts to {@link CryptoValue}
 * and returns as Map<String, List<CryptoValue>>.
 */
@Component("csvCryptoDataStrategy")
@Slf4j
public class CSVCryptoDataStrategy implements CryptoDataStrategy {

    /**
     * Path to directory with CSV files.
     */
    @Value("${path.to.csv.dir}")
    private String pathToCsvDir;

    /**
     * Defines in which order values should be read.
     *
     * Initialized as default order.
     */
    private List<String> readOrder = Arrays.asList("timestamp", "symbol", "price");

    @Override
    public Map<String, List<CryptoValue>> read() {
        List<String> cryptoCsvFiles;

        // Get all files names from the directory.
        try {
            cryptoCsvFiles = getCryptoFileNames();
        } catch (IOException e) {
            log.error("Failed attempt to get names of the files at path: " + pathToCsvDir);
            log.error(e.getMessage());
            return null;
        }

        Map<String, List<CryptoValue>> all = new HashMap<>();

        // Read every file and parse rows to object.
        for (String fileName : cryptoCsvFiles) {
            List<CryptoValue> stats = readCsvDataFromFile(fileName);

            if (!CollectionUtils.isEmpty(stats)) {
                // Get crypto short name from the file name.
                String symbol = fileName.replaceAll("_values\\.csv", "");
                all.put(symbol, stats);
            }
        }

        return all;
    }

    /**
     * Read CSV file and parse all rows to CryptoValue object.
     *
     * @param fileName name of the file, f.e. BTC_values.csv
     * @return list of all crypto data.
     */
    private List<CryptoValue> readCsvDataFromFile(String fileName) {
        List<CryptoValue> records = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(pathToCsvDir + fileName))) {
            String line = br.readLine();
            if (line == null || "".equals(line)) {
                return Collections.emptyList();
            }

            // Read first line which contains headers.
            String[] headers = line.split(",");

            // Compare with default order and update (if necessary).
            checkReadOrder(headers);

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Parse CSV row to an object.
                CryptoValue value = parseCsvToObject(values);

                records.add(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }

    /**
     * Compare read order from CSV file with the default order.
     *
     * TODO: Update order not supported yet.
     * @param headers headers from the CSV file.
     */
    private void checkReadOrder(String[] headers) {
        List<String> newOrder = Arrays.asList(headers);

        if (!readOrder.equals(newOrder)) {
            throw new UnsupportedOperationException("Not supported yet");
        }
    }

    /**
     * Parse CSV row to a CryptoValue object.
     *
     * @param values CSV row.
     * @return parsed object.
     */
    private CryptoValue parseCsvToObject(String[] values) {
        CryptoValue value = new CryptoValue();

        for (int i = 0; i < readOrder.size(); i++) {

            // Read values with right order.
            switch (readOrder.get(i)) {
                case "timestamp":
                    long epoch = Long.parseLong(values[i]);
                    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
                    value.setDateTime(dateTime);
                    break;
                case "symbol":
                    value.setName(values[i]);
                    break;
                case "price":
                    Double price = Double.valueOf(values[i]);
                    value.setPrice(price);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown header: " + readOrder.get(i));
            }
        }

        return value;
    }

    /**
     * Gets all file names in directory.
     *
     * @return list of file names.
     */
    private List<String> getCryptoFileNames() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(pathToCsvDir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }
}
