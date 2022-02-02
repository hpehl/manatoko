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

import java.nio.file.Paths;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import static org.jboss.hal.manatoko.container.Timeouts.WEB_DRIVER_INIT_TIMEOUT;
import static org.jboss.hal.manatoko.container.Timeouts.WEB_DRIVER_INIT_TRIES;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING;
import static org.testcontainers.containers.VncRecordingContainer.VncRecordingFormat.MP4;

public class Browser extends BrowserWebDriverContainer<Browser> {

    private static Browser instance = null;
    private static final Logger logger = LoggerFactory.getLogger(BrowserWebDriverContainer.class);

    public static Browser instance() {
        if (instance == null) {
            instance = new Browser();
        }
        return instance;
    }

    private RemoteWebDriver webDriver;

    private Browser() {
        super();
        withNetwork(Network.INSTANCE)
                .withCapabilities(new ChromeOptions())
                .withRecordingMode(RECORD_FAILING, Paths.get("target/recordings").toFile(), MP4)
                .waitingFor(Wait.forLogMessage(".*Started Selenium Standalone.*", 1))
                .withStartupTimeout(Timeouts.BROWSER_STARTUP_TIMEOUT);
    }

    @Override
    public String toString() {
        return "Browser{webDriver=" + webDriver + '}';
    }

    @Override
    public void start() {
        super.start();
        webDriver = failSafeWebDriver();
        webDriver.manage().timeouts()
                .pageLoadTimeout(Timeouts.WEBDRIVER_PAGE_LOAD_TIMEOUT)
                .scriptTimeout(Timeouts.WEBDRIVER_SCRIPT_TIMEOUT)
                .implicitlyWait(Timeouts.WEBDRIVER_IMPLICIT_WAIT_TIMEOUT);
        logger.info("Browser started: {}", this);
    }

    private RemoteWebDriver failSafeWebDriver() {
        RemoteWebDriver webDriver = null;
        for (int i = 0; i < WEB_DRIVER_INIT_TRIES && webDriver == null; i++) {
            try {
                logger.debug("Try to initialize web driver #{}", i);
                webDriver = getWebDriver();
            } catch (Throwable t) {
                logger.error("Unable to initialize web driver: {} (try #{})", t.getMessage(), i);
                try {
                    Thread.sleep(WEB_DRIVER_INIT_TIMEOUT.toMillis());
                } catch (InterruptedException ignore) {}
            }
        }
        if (webDriver == null) {
            throw new IllegalStateException(
                    String.format("Unable to initialize web driver after %d tries", WEB_DRIVER_INIT_TRIES));
        }
        logger.info("Initialized web driver: {}", webDriver);
        return webDriver;
    }

    @Override
    public void stop() {
        super.stop();
        logger.info("Browser stopped: {}", this);
    }

    public WebDriver webDriver() {
        if (webDriver == null) {
            throw new IllegalStateException("Web driver not ready. Browser has not yet been started.");
        }
        return webDriver;
    }
}
