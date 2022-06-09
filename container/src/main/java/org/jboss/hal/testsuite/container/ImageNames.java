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
package org.jboss.hal.testsuite.container;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ImageNames {

    private static ImageNames instance = null;

    static synchronized ImageNames instance() {
        if (instance == null) {
            instance = new ImageNames();
        }
        return instance;
    }

    private static final String CONTAINER_PROPERTIES = "container.properties";
    private static final String HAL_IMAGE_NAME_KEY = "hal.image";
    private static final String WILDFLY_DOMAIN_IMAGE_NAME_KEY = "wildfly.domain.image";
    private static final String WILDFLY_STANDALONE_IMAGE_NAME_KEY = "wildfly.standalone.image";
    private static final Logger logger = LoggerFactory.getLogger(ImageNames.class);

    private final Properties properties;

    private ImageNames() {
        properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream(CONTAINER_PROPERTIES)) {
            properties.load(resourceStream);
        } catch (IOException e) {
            logger.error("Unable to read {}: {}", CONTAINER_PROPERTIES, e.getMessage());
        }
    }

    String hal() {
        return getProperty(HAL_IMAGE_NAME_KEY);
    }

    String wildFlyDomain() {
        return getProperty(WILDFLY_DOMAIN_IMAGE_NAME_KEY);
    }

    String wildFlyStandalone() {
        return getProperty(WILDFLY_STANDALONE_IMAGE_NAME_KEY);
    }

    private String getProperty(String key) {
        if (!properties.containsKey(key)) {
            logger.error("Missing configuration for '{}' in '{}'", key, CONTAINER_PROPERTIES);
        }
        return properties.getProperty(key);
    }
}
