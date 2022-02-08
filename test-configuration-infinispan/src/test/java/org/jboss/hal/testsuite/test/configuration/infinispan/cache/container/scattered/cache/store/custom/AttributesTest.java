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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.store.custom;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.WildFlyContainer;
import org.jboss.hal.testsuite.page.configuration.ScatteredCachePage;
import org.jboss.hal.testsuite.test.Manatoko;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.container.WildFlyVersion._26;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.customStoreAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class AttributesTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE = "scattered-cache-" + Random.name();
    @Container static WildFlyContainer wildFly = WildFlyContainer.version(_26, FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and("transport", "jgroups"));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE));
        operations.headers(Values.of("allow-resource-service-restart", true))
                .add(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), Values.of("class", Random.name()));
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page ScatteredCachePage page;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE);
        console.verticalNavigation().selectPrimary("scattered-cache-store-item");
    }

    @Test
    void editClass() throws Exception {
        console.waitNoNotification();
        crudOperations.update(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getCustomStoreAttributesForm(),
                "class");
    }

    @Test
    void toggleFetchState() throws Exception {
        console.waitNoNotification();
        boolean fetchState = operations.readAttribute(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "fetch-state")
                .booleanValue(true);
        crudOperations.update(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getCustomStoreAttributesForm(),
                "fetch-state", !fetchState);
    }

    @Test
    void editMaxBatchSize() throws Exception {
        console.waitNoNotification();
        crudOperations.update(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getCustomStoreAttributesForm(),
                "max-batch-size", Random.number());
    }

    @Test
    void togglePassivation() throws Exception {
        console.waitNoNotification();
        crudOperations.update(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getCustomStoreAttributesForm(),
                "max-batch-size", Random.number());
    }

    @Test
    void togglePreload() throws Exception {
        console.waitNoNotification();
        boolean preload = operations.readAttribute(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "preload")
                .booleanValue(false);
        crudOperations.update(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getCustomStoreAttributesForm(),
                "preload", !preload);
    }

    @Test
    void editProperties() throws Exception {
        console.waitNoNotification();
        crudOperations.update(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getCustomStoreAttributesForm(),
                "properties", Random.properties());
    }

    @Test
    void togglePurge() throws Exception {
        console.waitNoNotification();
        boolean purge = operations.readAttribute(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "purge")
                .booleanValue(true);
        crudOperations.update(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getCustomStoreAttributesForm(),
                "purge", !purge);
    }

    @Test
    void toggleShared() throws Exception {
        console.waitNoNotification();
        boolean shared = operations.readAttribute(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), "shared")
                .booleanValue(false);
        crudOperations.update(customStoreAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getCustomStoreAttributesForm(),
                "shared", !shared);
    }
}
