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
package org.jboss.hal.manatoko.container;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Browser extends BrowserWebDriverContainer<Browser> {

    private static Browser currentInstance = null;

    public static Browser currentInstance() {
        return currentInstance;
    }

    public static Browser chrome() {
        currentInstance = new Browser().withCapabilities(new ChromeOptions());
        return currentInstance;
    }

    public static Browser firefox() {
        currentInstance = new Browser().withCapabilities(new FirefoxOptions());
        return currentInstance;
    }

    private Browser() {
        super();
        withNetwork(Network.INSTANCE)
                .waitingFor(Wait.forLogMessage(".*Started Selenium Standalone.*", 1))
                .withStartupTimeout(Duration.of(30, SECONDS));
    }

    public WebDriver webDriver() {
        RemoteWebDriver driver = getWebDriver();
        driver.manage().timeouts()
                .pageLoadTimeout(Duration.of(30, SECONDS))
                .scriptTimeout(Duration.of(20, SECONDS))
                .implicitlyWait(Duration.of(10, SECONDS));
        return driver;
    }
}
