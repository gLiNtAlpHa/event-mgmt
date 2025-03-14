package com.eventmgmt.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(".env")) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Failed to load .env file: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return properties.getProperty(key, System.getenv(key));
    }
}
