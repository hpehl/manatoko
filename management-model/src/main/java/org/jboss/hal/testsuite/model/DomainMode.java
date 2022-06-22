/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.testsuite.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainMode {

    private static DomainMode instance = null;

    public static synchronized DomainMode instance() {
        if (instance == null) {
            instance = new DomainMode();
        }
        return instance;
    }

    private static final String DOMAIN_PROPERTIES = "domain.properties";
    private static final String DEFAULT_HOST = "default.host";
    private static final Logger logger = LoggerFactory.getLogger(DomainMode.class);

    private final Properties properties;

    private DomainMode() {
        properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream(DOMAIN_PROPERTIES)) {
            properties.load(resourceStream);
        } catch (IOException e) {
            logger.error("Unable to read {}: {}", DOMAIN_PROPERTIES, e.getMessage());
        }
    }

    public String defaultHost() {
        return getProperty(DEFAULT_HOST);
    }

    private String getProperty(String key) {
        if (!properties.containsKey(key)) {
            logger.error("Missing configuration for '{}' in '{}'", key, DOMAIN_PROPERTIES);
        }
        return properties.getProperty(key);
    }
}
