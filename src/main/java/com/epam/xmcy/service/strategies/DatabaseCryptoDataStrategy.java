package com.epam.xmcy.service.strategies;

import com.epam.xmcy.model.CryptoValue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * The implementation of {@link CryptoDataStrategy}.
 *
 * Example of other possible strategies.
 */
@Component
public class DatabaseCryptoDataStrategy implements CryptoDataStrategy {

    @Override
    public Map<String, List<CryptoValue>> read() {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
