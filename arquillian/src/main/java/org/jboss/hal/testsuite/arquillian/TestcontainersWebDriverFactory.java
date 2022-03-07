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
package org.jboss.hal.testsuite.arquillian;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.drone.spi.Configurator;
import org.jboss.arquillian.drone.spi.Destructor;
import org.jboss.arquillian.drone.spi.DronePoint;
import org.jboss.arquillian.drone.spi.Instantiator;
import org.jboss.hal.testsuite.container.Browser;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public class TestcontainersWebDriverFactory implements
        Configurator<WebDriver, TestcontainersConfiguration>,
        Instantiator<WebDriver, TestcontainersConfiguration>,
        Destructor<WebDriver> {

    private static final Logger logger = LoggerFactory.getLogger(TestcontainersWebDriverFactory.class);

    @Override
    public TestcontainersConfiguration createConfiguration(final ArquillianDescriptor descriptor,
            final DronePoint<WebDriver> dronePoint) {
        return new TestcontainersConfiguration();
    }

    @Override
    public WebDriver createInstance(final TestcontainersConfiguration configuration) {
        logger.debug("Create web driver instance from configuration {}", configuration.getConfigurationName());
        if (Browser.instance() != null) {
            logger.debug("Browser container available. Trying to get remote web driver...");
            Stopwatch stopwatch = Stopwatch.createStarted();
            WebDriver driver = Browser.instance().webDriver();
            stopwatch.stop();
            logger.debug("Return web driver {} from browser container after {}", driver, stopwatch);
            return driver;
        } else {
            String message = "Unable to create web driver instance: browser container not ready!";
            logger.error(message);
            throw new IllegalStateException(message);
        }
    }

    @Override
    public void destroyInstance(final WebDriver instance) {
        // noop
    }

    @Override
    public int getPrecedence() {
        return 1;
    }
}
