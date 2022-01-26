/*
 *  Copyright 2022 Red Hat
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jboss.hal.manatoko.arquillian;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.drone.spi.Configurator;
import org.jboss.arquillian.drone.spi.Destructor;
import org.jboss.arquillian.drone.spi.DronePoint;
import org.jboss.arquillian.drone.spi.Instantiator;
import org.jboss.hal.manatoko.Browser;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestcontainersWebDriverFactory implements
        Configurator<WebDriver, TestcontainersConfiguration>,
        Instantiator<WebDriver, TestcontainersConfiguration>,
        Destructor<WebDriver> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestcontainersWebDriverFactory.class);

    @Override
    public TestcontainersConfiguration createConfiguration(final ArquillianDescriptor descriptor,
            final DronePoint<WebDriver> dronePoint) {
        LOGGER.debug("Create web driver configuration from drone point: " + dronePoint);
        return new TestcontainersConfiguration();
    }

    @Override
    public WebDriver createInstance(final TestcontainersConfiguration configuration) {
        LOGGER.debug("Create web driver newInstance from configuration {}", configuration.getConfigurationName());
        if (Browser.currentInstance() != null) {
            WebDriver driver = Browser.currentInstance().webDriver();
            LOGGER.debug("Return web driver from browser container: " + driver);
            return driver;
        } else {
            throw new IllegalStateException("Unable to create web driver newInstance: browser container not ready!");
        }
    }

    @Override
    public void destroyInstance(final WebDriver instance) {
        // noop
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }
}
