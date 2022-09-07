package com.epam.xmcy.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Util class to work with files.
 */
@Component
public final class FileUtils {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private FileUtils() {

    }

    /**
     * Save object as JSON into file.
     *
     * @param object object to save.
     * @param fileName name of the file.
     */
    public static void saveAsJson(Object object, String fileName) throws IOException {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        String json = objectMapper.writeValueAsString(object);

        Files.write(path, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    /**
     * Read JSON from file and convert into object.
     *
     * @param fileName name of the file.
     * @param typeReference type of the converted object.
     * @param <T> object class.
     *
     * @return deserialized object.
     */
    public static <T> T readJson(String fileName, TypeReference<T> typeReference) throws IOException {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            throw new NoSuchFileException("No such file: " + fileName);
        }

        String content = Files.readString(path);

        return objectMapper.readValue(content, typeReference);
    }
}
