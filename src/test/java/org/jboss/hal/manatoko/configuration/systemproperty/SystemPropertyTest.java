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
package org.jboss.hal.manatoko.configuration.systemproperty;

import java.io.File;

import org.jboss.hal.manatoko.Browser;
import org.jboss.hal.manatoko.HalContainer;
import org.jboss.hal.manatoko.WildFlyContainer;
import org.jboss.hal.resources.Ids;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.dmr.ModelDescriptionConstants.VALUE;
import static org.jboss.hal.manatoko.WildFlyVersion._26;
import static org.jboss.hal.manatoko.configuration.systemproperty.SystemPropertyFixtures.READ_NAME;
import static org.jboss.hal.manatoko.configuration.systemproperty.SystemPropertyFixtures.READ_VALUE;
import static org.jboss.hal.manatoko.configuration.systemproperty.SystemPropertyFixtures.systemPropertyAddress;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;
import static org.testcontainers.containers.VncRecordingContainer.VncRecordingFormat.MP4;

@Testcontainers
class SystemPropertyTest {

    @TempDir
    static File recordings;

    @Container
    static WildFlyContainer wildFly = WildFlyContainer.version(_26);

    @Container
    static HalContainer console = HalContainer.instance();

    @Container
    static Browser chrome = Browser.chrome()
            .withRecordingMode(RECORD_ALL, recordings, MP4);

    @BeforeAll
    public static void beforeAll() throws Exception {
        console.connectTo(wildFly);
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);
        operations.add(systemPropertyAddress(READ_NAME), Values.empty().and(VALUE, READ_VALUE));
    }

    @AfterAll
    public static void afterAll() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        Operations operations = new Operations(client);

        // try-with-resource does not work with impsort-maven-plugin
        // noinspection TryFinallyCanBeTryWithResources
        try {
            operations.removeIfExists(systemPropertyAddress(READ_NAME));
        } finally {
            client.close();
        }
    }

    @Test
    void read() {
        WebDriver driver = chrome.driver();
        driver.get(console.url("index.html", "system-properties"));

        var table = driver.findElement(By.id(Ids.SYSTEM_PROPERTY_TABLE));
        var tds = table.findElements(By.cssSelector("tbody td"))
                .stream()
                .map(WebElement::getText)
                .toList();
        assertTrue(tds.contains(READ_NAME));
        assertTrue(tds.contains(READ_VALUE));
    }
}
