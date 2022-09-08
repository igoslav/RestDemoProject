package com.epam.xmcy.util;

import com.epam.xmcy.model.Cryptocurrency;
import com.fasterxml.jackson.core.type.TypeReference;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.testng.Assert.*;

public class FileUtilsTest {

    private final static String TEST_FILENAME = "test.json";

    private static final String NAME = "TEST";
    private static final Double MIN_VALUE = 0.0;
    private static final Double MAX_VALUE = 100.0;
    private static final LocalDateTime OLDEST_VALUE = LocalDateTime.of(2021, 1, 1, 1, 1);
    private static final LocalDateTime NEWEST_VALUE = LocalDateTime.of(2022, 1, 1, 1, 1);


    @Test
    public void testSaveAndRead() throws IOException {
        FileUtils.saveAsJson(createTestCrypto(), TEST_FILENAME);

        assertTrue(Files.exists(Paths.get(TEST_FILENAME)));

        Cryptocurrency result = FileUtils.readJson(TEST_FILENAME, new TypeReference<>() {
        });

        assertNotNull(result);
        assertEquals(result.getMin(), MIN_VALUE);
        assertEquals(result.getMax(), MAX_VALUE);
        assertEquals(result.getNewest(), NEWEST_VALUE);
        assertEquals(result.getOldest(), OLDEST_VALUE);
        assertEquals(result.getName(), NAME);

        Files.delete(Paths.get(TEST_FILENAME));
        assertFalse(Files.exists(Paths.get(TEST_FILENAME)));
    }

    private Cryptocurrency createTestCrypto() {
        Cryptocurrency cryptocurrency = new Cryptocurrency();
        cryptocurrency.setMin(MIN_VALUE);
        cryptocurrency.setMax(MAX_VALUE);
        cryptocurrency.setNewest(NEWEST_VALUE);
        cryptocurrency.setOldest(OLDEST_VALUE);
        cryptocurrency.setName(NAME);

        return cryptocurrency;
    }
}