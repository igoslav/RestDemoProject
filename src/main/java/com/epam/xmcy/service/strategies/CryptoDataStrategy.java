package com.epam.xmcy.service.strategies;

import com.epam.xmcy.model.CryptoValue;

import java.util.List;
import java.util.Map;

/**
 * Interface for all strategies.
 */
public interface CryptoDataStrategy {

    /**
     * Reads content from the resource (file, database, etc.).
     * @return map of crypto stats for a specific cryptocurrency.
     */
    Map<String, List<CryptoValue>> read();
}
