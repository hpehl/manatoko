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
package org.jboss.hal.testsuite.test.configuration.infinispan.cache.container.scattered.cache.memory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.hal.testsuite.Console;
import org.jboss.hal.testsuite.CrudOperations;
import org.jboss.hal.testsuite.Random;
import org.jboss.hal.testsuite.container.Browser;
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

import static org.jboss.hal.testsuite.container.WildFlyConfiguration.FULL_HA;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.cacheContainerAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.offHeapMemoryAddress;
import static org.jboss.hal.testsuite.fixtures.InfinispanFixtures.scatteredCacheAddress;

@Manatoko
@Testcontainers
@Disabled // TODO Fix failing tests
class OffHeapMemoryTest {

    private static final String CACHE_CONTAINER = "cache-container-" + Random.name();
    private static final String SCATTERED_CACHE = "scattered-cache-" + Random.name();

    @Container static Browser browser = new Browser();
    @Container static WildFlyContainer wildFly = WildFlyContainer.standalone(FULL_HA);
    private static Operations operations;

    @BeforeAll
    static void setupModel() throws Exception {
        OnlineManagementClient client = wildFly.managementClient();
        operations = new Operations(client);
        operations.add(cacheContainerAddress(CACHE_CONTAINER));
        operations.add(cacheContainerAddress(CACHE_CONTAINER).and("transport", "jgroups"));
        operations.add(scatteredCacheAddress(CACHE_CONTAINER, SCATTERED_CACHE));
    }

    @Inject Console console;
    @Inject CrudOperations crudOperations;
    @Page ScatteredCachePage page;

    @BeforeEach
    void prepare() {
        page.navigate(CACHE_CONTAINER, SCATTERED_CACHE);
        console.verticalNavigation().selectPrimary("scattered-cache-memory-item");
    }

    @Test
    void editCapacity() throws Exception {
        crudOperations.update(offHeapMemoryAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getOffHeapMemoryForm(),
                "capacity",
                Random.number());
    }

    @Test
    void editEvictionType() throws Exception {
        console.waitNoNotification();
        String currentEvictionType = operations
                .readAttribute(offHeapMemoryAddress(CACHE_CONTAINER, SCATTERED_CACHE), "eviction-type")
                .stringValue("COUNT");
        List<String> evictionTypes = new ArrayList<>(Arrays.asList("COUNT", "MEMORY"));
        evictionTypes.remove(currentEvictionType);
        String evictionType = evictionTypes.get(0);
        crudOperations.update(offHeapMemoryAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getOffHeapMemoryForm(),
                formFragment -> formFragment.select("eviction-type", evictionType),
                resourceVerifier -> resourceVerifier.verifyAttribute("eviction-type", evictionType));
    }

    @Test
    void editSize() throws Exception {
        console.waitNoNotification();
        crudOperations.update(offHeapMemoryAddress(CACHE_CONTAINER, SCATTERED_CACHE), page.getOffHeapMemoryForm(),
                "size",
                (long) Random.number());
    }

}
