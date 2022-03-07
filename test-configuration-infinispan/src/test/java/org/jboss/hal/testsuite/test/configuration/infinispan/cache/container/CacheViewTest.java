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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container;

import java.util.List;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.hal.meta.token.NameTokens;
import org.jboss.hal.resources.Ids;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.fragment.finder.ColumnFragment;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.jboss.arquillian.graphene.Graphene.waitGui;
import static org.jboss.hal.dmr.ModelDescriptionConstants.INFINISPAN;
import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fragment.finder.FinderFragment.configurationSubsystemPath;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class CacheViewTest {

    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);

    @Inject Console console;
    ColumnFragment cacheColumn;

    @BeforeEach
    void prepare() {
        cacheColumn = console.finder(NameTokens.CONFIGURATION,
                configurationSubsystemPath(INFINISPAN).append(Ids.CACHE_CONTAINER, Ids.cacheContainer("web")))
                .column("cache");
    }

    /**
     * Check if internal error is displayed when click on view on cache. See https://issues.redhat.com/browse/HAL-1627
     */
    @Test
    void checkNoErrorTest() {
        List<WebElement> elements = cacheColumn.getItems();
        elements.get(0).click();
        elements.get(0).findElement(By.className("clickable")).click();
        waitGui().until().element(By.className("back")).is().visible();
        assertTrue(console.verifyNoError(),
                "Internal error is displayed! See https://issues.redhat.com/browse/HAL-1627");
    }
}
