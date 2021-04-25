package com.kdeyne.doc.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesLoader  {

    public Map<String, String> parseProperties(File file) throws IOException {
        final Map<String, String> map = new HashMap<>();

        try (InputStream inputStream = new FileInputStream(file)) {
            Properties properties = new Properties();
            properties.load(inputStream);

            for (String key : properties.stringPropertyNames()) {
                map.put(key, properties.getProperty(key));
            }
        }

        return map;
    }

    public Map<String, String> parseYML(File file) throws IOException {
        final Map<String, String> map = new HashMap<>();

        try (InputStream inputStream = new FileInputStream(file)) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> objectMap = mapper.readValue(inputStream, Map.class);
            map.putAll(ymlMap("", objectMap));
        }

        return map;
    }

    private Map<String, String> ymlMap(String root, Object valueMap) {
        final Map<String, String> map = new HashMap<>();

        if(valueMap instanceof String) {
            map.put(root, valueMap.toString());
        } else if (valueMap instanceof Map) {
            for(Map.Entry<String, Object> value : ((Map<String, Object>)valueMap).entrySet()) {
                final String key = ("".equals(root)) ? value.getKey() : root + "." + value.getKey();
                if(value.getValue() instanceof String) {
                    map.put(key, value.getValue().toString());
                } else if (value.getValue() instanceof Map) {
                    map.putAll(ymlMap(key, value.getValue()));
                }
            }
        }

        return map;
    }
}
