package com.kdeyne.doc;

import com.kdeyne.doc.loader.PropertiesLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Map;

@Execution(ExecutionMode.CONCURRENT)
class PropertiesLoaderTest extends AbstractTest {

    private final PropertiesLoader loader = new PropertiesLoader();

    @ParameterizedTest
    @ValueSource(strings = {"properties/example.properties"})
    void testLoadProperties(String fileName) throws IOException {
        Map<String, String> parsedProperties = loader.parseProperties(getFile(fileName));

        Assertions.assertEquals(12, parsedProperties.size());
        Assertions.assertEquals("admin", parsedProperties.get("spring.datasource.username"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"properties/example.yml"})
    void testLoadYML(String fileName) throws IOException {
        Map<String, String> parsedProperties = loader.parseYML(getFile(fileName));

        System.out.println(parsedProperties);

        Assertions.assertEquals(5, parsedProperties.size());
        Assertions.assertEquals("SearcherWeatherDatabasePool", parsedProperties.get("spring.datasource.hikari.poolName"));
    }
}

