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

import java.nio.file.Paths;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.DefaultRecordingFileFactory;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.VncRecordingContainer.VncRecordingFormat.MP4;

public class Browser extends BrowserWebDriverContainer<Browser> {

    private static Browser instance = null;
    private static final Logger logger = LoggerFactory.getLogger(BrowserWebDriverContainer.class);

    public static Browser instance() {
        return instance;
    }

    private RemoteWebDriver webDriver;

    public Browser() {
        super(DockerImageName.parse("quay.io/jbossqe-eap/selenium-images")
                .asCompatibleSubstituteFor("selenium/standalone-chrome"));
        this.withNetwork(Network.INSTANCE)
                .withNetworkAliases("selenium")
                .withCapabilities(new ChromeOptions())
                .withRecordingMode(VncRecordingMode.RECORD_ALL, Paths.get("target/recordings").toFile(), MP4)
                .withRecordingFileFactory(new DefaultRecordingFileFactory())
                .waitingFor(Wait.forLogMessage(".*Started Selenium Standalone.*", 1))
                .withStartupTimeout(Timeouts.BROWSER_STARTUP_TIMEOUT);
        Browser.instance = this;
    }

    @Override
    public String toString() {
        return "Browser{webDriver=" + webDriver + '}';
    }

    @Override
    public void start() {
        super.start();
        webDriver = getWebDriver();
        webDriver.manage().timeouts()
                .pageLoadTimeout(Timeouts.WEBDRIVER_PAGE_LOAD_TIMEOUT)
                .scriptTimeout(Timeouts.WEBDRIVER_SCRIPT_TIMEOUT)
                .implicitlyWait(Timeouts.WEBDRIVER_IMPLICIT_WAIT_TIMEOUT);
        logger.info("Browser started: {}", this);
    }

    @Override
    protected void configure() {
        super.configure();
        setCommand("/init");
        setEnv(List.of("VNC_PORT=5900"));
        setExposedPorts(List.of(4444, 5900));
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
